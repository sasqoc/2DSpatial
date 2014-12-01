package pl.sasqoc.game;


import java.util.Comparator;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Keys;
import com.badlogic.gdx.utils.Pool;

/**
 * Efficiently store and retrieve items by area occupied within a plane.
 *
 * Zero de-allocation, so safe for games.
 * 
 * From http://code.google.com/p/spatial-index-for-libgdx/
 */
public class SpatialIndex<T>
{
        private static final ItemDetailsPool itemDetailsPool = new ItemDetailsPool();
       
        private float _cellSize;
        private final IntMap<Array<T>> _cells = new IntMap<Array<T>>();
        private final ObjectMap<Object, ItemDetails> _contents = new ObjectMap<Object, ItemDetails>();
        private final Array<T> _result = new Array<T>();
        private int _lookupKey = 0;
        private int _lookupNum;
        private boolean _zOrdered;       
       
        private static class ItemDetails
        {
                public int resultNum;
                public float x, y, w, h;
                public int z;
               
                private ItemDetails() { }
               
                public ItemDetails set(float x, float y, int z, float w, float h)
                {
                        this.x = x;
                        this.y = y;
                        this.z = z;
                        this.w = w;
                        this.h = h;
                        this.resultNum = 0;
                       
                        return this;
                }
        }
       
        private static class ItemDetailsPool extends Pool<ItemDetails>
        {
                public int numInUse = 0;
               
                @Override
                protected ItemDetails newObject()
                {
                        return new ItemDetails();
                }      
               
                @Override
                public ItemDetails obtain()
                {
                        numInUse++;
                        return super.obtain();
                }
               
                @Override
                public void free(ItemDetails object)
                {
                        super.free(object);
                        numInUse --;
                }
        }
       
        public int numInUse()
        {
                return itemDetailsPool.numInUse;
        }
       
        /**
         * Construct a new SpatialIndex
         *
         * @cellSize for best performance choose a cell size that is about the same as the shortest side of your typical
         * query area. If your stored items are densely packed you may benefit from using smaller cells.
         *
         * With this constructor, z-ordering is turned off.
         */
        public SpatialIndex(float cellSize)
        {
                this(cellSize, false);
        }
       
        /**
         * Construct a new SpatialIndex
         *
         * @cellSize for best performance choose a cell size that is about the same as the shortest side of your typical
         * query area. If your stored items are densely packed you may benefit from using smaller cells.
         *
         * @zOrdering if true, you can specify the z-index of entered items and retrieved items will be returned
         * with preserved z order.
         */
        public SpatialIndex(float cellSize, boolean zOrdered)
        {
                _cellSize = cellSize;
                _zOrdered = zOrdered;
        }
       
        /**
         * Store the specified item at the specified area.
         * @param obj The item to store.
         * @param x The lower x bound of the item.
         * @param y The lower y bound of the item.
         * @param w The width of the item. May be 0.
         * @param h The height of the item. May be 0.
         */
        public void put(T obj, float x, float y, float w, float h)
        {
                put(obj, x, y, 0, w, h);
        }
       
        /**
         * Store the specified item at the specified area.
         * @param obj The item to store.
         * @param x The lower x bound of the item.
         * @param y The lower y bound of the item.
         * @param z The z-index of the item.
         * @param w The width of the item. May be 0.
         * @param h The height of the item. May be 0.
         */
        public void put(T obj, float x, float y, int z, float w, float h)
        {
                _contents.put(obj, itemDetailsPool.obtain().set(x, y, z, w, h));
                for(int cy = toCell(y); cy <= toCell(y + h); cy++)
                {
                        for(int cx = toCell(x); cx <= toCell(x + w); cx++)
                        {
                                _lookupKey = toHashKey(cx, cy);
                                Array<T> cell = _cells.get(_lookupKey);
                                if(cell == null)
                                {
                                        cell = new Array<T>();
                                        _cells.put(_lookupKey, cell);
                                }

                                cell.add(obj);                          
                        }
                }
        }
       
        /**
         * Remove the specified item.
         *
         * Runtime is linear with the number of cells occupied by the item.
         */
        public void remove(T e)
        {
                ItemDetails dets = _contents.get(e);
                for(int cy = toCell(dets.y); cy <= toCell(dets.y + dets.h); cy++)
                {
                        for(int cx = toCell(dets.x); cx <= toCell(dets.x + dets.w); cx++)
                        {              
                                _lookupKey = toHashKey(cx, cy);
                                Array<T> cell = _cells.get(_lookupKey);
                                if(cell != null)
                                        cell.removeValue(e, true);                                      
                        }
                }
               
                itemDetailsPool.free(dets);
                _contents.remove(e);
        }
       
        /**
         * Move the specified item to the newly specified area.
         *
         * @param e The item to move.
         * @param x The new lower x bound of the item.
         * @param y The new lower y bound of the item.
         * @param w The new width of the item.
         * @param h The new height of the item.
         *
         * With this overload, the z-index will remain unchanged.
         */
        public void move(T e, float x, float y, float w, float h)
        {              
                int z = 0;
                if(_zOrdered)
                        z = _contents.get(e).z;
                remove(e);
                put(e, x, y, z, w, h);
        }
       
        /**
         * Move the specified item to the newly specified area.
         *
         * @param e The item to move.
         * @param x The new lower x bound of the item.
         * @param y The new lower y bound of the item.
         * @param z The new z-index of the item.
         * @param w The new width of the item.
         * @param h The new height of the item.
         */
        public void move(T e, float x, float y, int z, float w, float h)
        {
                remove(e);
                put(e, x, y, z, w, h);
        }
       
        /**
         * Get all stored objects, regardless of location/size.
         */
        @SuppressWarnings("unchecked")
        public Array<T> getAll()
        {
                _result.clear();
                Keys<Object> keys = _contents.keys();
                while(keys.hasNext())
                        _result.add((T)keys.next());
               
                if(_zOrdered)
                        _result.sort(zIndexComparator);
               
                return _result;
        }
        
        /**
         * Get all the items that overlap cells from specified area.
         *
         * @param x The lower x bound of the query area.
         * @param y The lower y bound of the query area.
         * @param w The width of the query area.
         * @param h The height of the query area.
         * @return All the stored items that overlap cells from specified area.
         * The returned array is a single instance shared across all calls to get(...) and getAll(...)
         */        
        public Array<T> getAllInCells(float x, float y, float w, float h)
        {
                _result.clear();
               
                _lookupNum++;
               
                for(int cy = toCell(y); cy <= toCell(y + h); cy++)
                {
                        for(int cx = toCell(x); cx <= toCell(x + w); cx++)
                        {
                                //_lookupKey.set(toHashKey(cx, cy));
                        		_lookupKey = toHashKey(cx, cy);
                               
                                Array<T> items = _cells.get(_lookupKey);
                                if(items != null)
                                {
                                        for(T t : items)
                                        {
                                                
                                                ItemDetails dets = _contents.get(t);
                                                if(dets.resultNum < _lookupNum)
                                                {                                                    
                                                    //Statistics.checksCount++;
                                                    _result.add(t);
//                                                        if(dets.x <= x + w
//                                                                        && dets.y <= y + h
//                                                                        && dets.x + dets.w >= x
//                                                                        && dets.y + dets.h >= y)
//                                                        {                                                      
//                                                                _result.add(t);
//                                                                dets.resultNum = _lookupNum;
//                                                        }
                                                }
                                        }
                                }
                        }
                }
               
                if(_zOrdered)
                        _result.sort(zIndexComparator);
       
                return _result;            
        }
       
        /**
         * Get all the items that overlap the specified area.
         *
         * @param x The lower x bound of the query area.
         * @param y The lower y bound of the query area.
         * @param w The width of the query area.
         * @param h The height of the query area.
         * @return All the stored items that overlap the specified area.
         * The returned array is a single instance shared across all calls to get(...) and getAll(...)
         */
        public Array<T> get(float x, float y, float w, float h)
        {
                _result.clear();
               
                _lookupNum++;
               
                for(int cy = toCell(y); cy <= toCell(y + h); cy++)
                {
                        for(int cx = toCell(x); cx <= toCell(x + w); cx++)
                        {
                                //_lookupKey.set(toHashKey(cx, cy));
                        		_lookupKey = toHashKey(cx, cy);
                               
                                Array<T> items = _cells.get(_lookupKey);
                                if(items != null)
                                {
                                        for(T t : items)
                                        {                                                
                                                ItemDetails dets = _contents.get(t);
                                                if(dets.resultNum < _lookupNum)
                                                {
                                                        //Statistics.checksCount++;
                                                        if(dets.x <= x + w
                                                                        && dets.y <= y + h
                                                                        && dets.x + dets.w >= x
                                                                        && dets.y + dets.h >= y)
                                                        {                                                      
                                                                _result.add(t);
                                                                dets.resultNum = _lookupNum;
                                                        }
                                                }
                                        }
                                }
                        }
                }
               
                if(_zOrdered)
                        _result.sort(zIndexComparator);
       
                return _result;
        }
       
        /**
         * Remove all items from the index.
         */
        public void clear()
        {
                for(Array<T> item : _cells.values())
                        item.clear();  
                for(ItemDetails dets : _contents.values())
                        itemDetailsPool.free(dets);
                _contents.clear();
        }
       
        private int toCell(float v)
        {
                return (int)Math.floor(v / _cellSize);
        }
       
        private int toHashKey(int cx, int cy)
        {
                return cx + (cy << 16);
        }
       
        private final Comparator<T> zIndexComparator = new Comparator<T>()
        {
                @Override
                public int compare(T arg0, T arg1)
                {
                        int z1 = _contents.get(arg0).z;
                        int z2 = _contents.get(arg1).z;
                       
                        if(z1 > z2)
                                return 1;
                        else if(z1 < z2)
                                return -1;
                        else return 0;
                }
        };
}

