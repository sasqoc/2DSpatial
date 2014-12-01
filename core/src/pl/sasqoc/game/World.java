package pl.sasqoc.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

/**
 *
 * @author amadela
 */
public class World {

    public int staringObjectsNumber = 120;
    public int objectSize = 28;
    public int gridCellSize = 40;

    public Rectangle selectRectangle;

    public boolean movingObjects = true;
    public boolean drawGrid = true;
    public boolean drawBoundingBoxes = true;

    public int width, heigth;
    FunctionMode fMode = FunctionMode.SPATIAL_ALL_ALL;
    Array<Sprite> gameObjects;
    Array<Vector2> gameObjVectors;
    float deltaTimeDirection = 0;

    SpatialIndex<Sprite> objectsIndex;

    public World() {
        gameObjects = new Array<Sprite>(10000);
        gameObjVectors = new Array<Vector2>(10000);

        selectRectangle = new Rectangle();
        objectsIndex = new SpatialIndex<Sprite>(gridCellSize);
    }

    public void resizeWorld(int w, int h) {
        this.width = w;
        this.heigth = h;
    }

    public void createObjects() {
        gameObjects.clear();

        addRandomObjects(staringObjectsNumber);
        genNewDirectionVectors(staringObjectsNumber);
    }

    void genNewDirectionVectors(int count) {
        gameObjVectors.clear();
        Random gen = new Random();

        for (int n = 0; n < count; n++) {
            Vector2 v = new Vector2();
            v.set(gen.nextFloat() - gen.nextFloat(), gen.nextFloat() - gen.nextFloat());            
            

            gameObjVectors.add(v);
        }
    }

    void addRandomObjects(int count) {
        Random gen = new Random();

        for (int n = 0; n < count; n++) {
            Sprite s = new Sprite(Assets.logo);
            s.setSize(objectSize, objectSize);
            s.setPosition(gen.nextFloat() * width, gen.nextFloat() * heigth);

            gameObjects.add(s);
        }
    }

    void update() {

        if (movingObjects) {
            moveObjects();
        }

        Statistics.reset();
        resetColoring();
        indexUpdate();

        Statistics.functionName = fMode.getName();
        switch (fMode) {
            case SIMPLE_ALL:
                simpleCheckAll();
                break;
            case SPATIAL_CELLS:
                spatialIndexInCells();
                break;
            case SPATIAL:
                spatialIndex();
                break;
            case SIMPLE_ALL_ALL:
                simpleAllAll();
                break;
            case SPATIAL_ALL_ALL:
                spatialIndexAllAll();
                break;
        }

    }

    private void spatialIndexInCells() {
        for (Sprite s : objectsIndex.getAllInCells(selectRectangle.x, selectRectangle.y, selectRectangle.width, selectRectangle.height)) {
            Statistics.checksCount++;
            s.setColor(Color.BLUE);
        }
    }

    private void spatialIndex() {
        for (Sprite s : objectsIndex.get(selectRectangle.x, selectRectangle.y, selectRectangle.width, selectRectangle.height)) {
            Statistics.checksCount++;
            s.setColor(Color.BLUE);
        }
    }

    private void simpleCheckAll() {
        for (Sprite s : gameObjects) {
            Statistics.checksCount++;
            if (s.getBoundingRectangle().overlaps(selectRectangle)) {
                s.setColor(Color.BLUE);
            }
        }
    }

    private void simpleAllAll() {

        for (int s1 = 0; s1 < gameObjects.size; s1++) {
            for (int s2 = 0; s2 < gameObjects.size; s2++) {
                Sprite sprite1 = gameObjects.get(s1);
                Sprite sprite2 = gameObjects.get(s2);
                Statistics.checksCount++;

                if ((sprite1.getBoundingRectangle().overlaps(sprite2.getBoundingRectangle())) && (sprite1 != sprite2)) {
                    sprite1.setColor(Color.RED);
                    sprite2.setColor(Color.RED);
                }
            }
        }

    }

    private void spatialIndexAllAll() {

        for (int s1 = 0; s1 < gameObjects.size; s1++) {
            Sprite sprite1 = gameObjects.get(s1);
            for (Sprite sprite2 : objectsIndex.getAllInCells(sprite1.getX(), sprite1.getY(), sprite1.getWidth(), sprite1.getHeight())) {
                Statistics.checksCount++;

                if ((sprite1.getBoundingRectangle().overlaps(sprite2.getBoundingRectangle())) && (sprite1 != sprite2)) {
                    sprite1.setColor(Color.RED);
                    sprite2.setColor(Color.RED);
                }
            }
        }

    }

    private void resetColoring() {
        for (Sprite s : gameObjects) {
            s.setColor(Color.WHITE);
        }
    }

    private void indexUpdate() {
        objectsIndex.clear();
        for (Sprite s : gameObjects) {
            objectsIndex.put(s, s.getX(), s.getY(), s.getWidth(), s.getHeight());
        }

    }

    public void updateSettings() {
        createObjects();
        objectsIndex = new SpatialIndex<Sprite>(gridCellSize);
        for (Sprite s : gameObjects) {
            s.setSize(objectSize, objectSize);
        }
    }

    private void moveObjects() {
        float lTime = Gdx.app.getGraphics().getDeltaTime();
        float speed = 60;
        deltaTimeDirection += lTime;
        for (int n = 0; n < gameObjects.size; n++) {
            Sprite s = gameObjects.get(n);
            Vector2 v = gameObjVectors.get(n);
            s.setPosition(s.getX() + v.x * lTime * speed, s.getY() + v.y * lTime * speed);
        }

        if (deltaTimeDirection > 5) {
            deltaTimeDirection -= 5;
            genNewDirectionVectors(gameObjects.size);
        }
    }

}
