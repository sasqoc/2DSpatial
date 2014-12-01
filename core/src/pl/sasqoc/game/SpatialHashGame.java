package pl.sasqoc.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import pl.sasqoc.gui.StageGui;

public class SpatialHashGame extends ApplicationAdapter {

    World world;
    WorldRenderer worldRenderer;
    StageGui stageGui;
    
    InputMultiplexer inputMultiplexer;
    Controller controller;        

    @Override
    public void create() {
        world = new World();
        worldRenderer = new WorldRenderer(world);
        stageGui = new StageGui(worldRenderer.stage, world);
        
        inputMultiplexer = new InputMultiplexer();
        controller = new Controller(world, stageGui, worldRenderer.camera);
        inputMultiplexer.addProcessor(controller);
        inputMultiplexer.addProcessor(worldRenderer.stage);        
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        world.createObjects();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stageGui.update();
        world.update();        
        worldRenderer.render();        
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.resize(width, height);
        stageGui.resize();
    }

}
