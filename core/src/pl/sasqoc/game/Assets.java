package pl.sasqoc.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 *
 * @author amadela
 */
public class Assets {
    
    static final Texture img;
    static final Texture logo;
    public static final Skin skin;
    
    static{
        boolean mipMaps = true;
        img = new Texture("badlogic.jpg");
        logo = new Texture(Gdx.files.internal("site-logo.png"), mipMaps);
        //logo.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);                
        logo.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.MipMap);               
        skin = new Skin(Gdx.files.internal("UI/Holo.json"));        
    }
    
}