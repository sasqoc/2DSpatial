package pl.sasqoc.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import pl.sasqoc.gui.StageGui;

/**
 *
 * @author amadela
 */
public class Controller implements InputProcessor {

    private final World world;
    private final StageGui gui;
    private final Camera camera;
    private final Vector2 p1, p2;

    public Controller(World w, StageGui gui, Camera c) {
        this.world = w;
        this.gui = gui;
        this.camera = c;
        this.p1 = new Vector2();
        this.p2 = new Vector2();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.Q) {
            world.fMode = FunctionMode.SIMPLE_ALL;
        }
        if (keycode == Keys.W) {
            world.fMode = FunctionMode.SPATIAL_CELLS;
        }
        if (keycode == Keys.E) {
            world.fMode = FunctionMode.SPATIAL;
        }
        if (keycode == Keys.R) {
            world.fMode = FunctionMode.SIMPLE_ALL_ALL;
        }
        if (keycode == Keys.T) {
            world.fMode = FunctionMode.SPATIAL_ALL_ALL;
        }
        if (keycode == Keys.A) {
            world.createObjects();
        }
        if (keycode == Keys.M) {
            world.movingObjects = !world.movingObjects;
        }
        if (keycode == Keys.G) {
            world.drawGrid = !world.drawGrid;
        }
        if (keycode == Keys.B) {
            world.drawBoundingBoxes = !world.drawBoundingBoxes;
        }

        if (keycode == Keys.ESCAPE) {
            gui.changeWindowVisbility();
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
        Vector3 position = camera.unproject(clickCoordinates);

        p1.x = position.x;
        p1.y = position.y;
        world.selectRectangle.x = position.x;
        world.selectRectangle.y = position.y;
        world.selectRectangle.width = 0;
        world.selectRectangle.height = 0;

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
        Vector3 position = camera.unproject(clickCoordinates);

        p2.x = position.x;
        p2.y = position.y;

        world.selectRectangle.x = p1.x > p2.x ? p2.x : p1.x;
        world.selectRectangle.y = p1.y > p2.y ? p2.y : p1.y;
        world.selectRectangle.width = Math.abs(p1.x - p2.x);
        world.selectRectangle.height = Math.abs(p1.y - p2.y);

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}