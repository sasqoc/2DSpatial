package pl.sasqoc.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import static pl.sasqoc.game.Assets.logo;

/**
 *
 * @author amadela
 */
public class WorldRenderer {

    OrthographicCamera camera;
    ExtendViewport extendedViewport;
    Stage stage;
    Table table;

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    World world;

    WorldRenderer(World w) {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        extendedViewport = new ExtendViewport(960, 480, 960, 720, camera);
        camera.update();

        stage = new Stage(extendedViewport, batch);

        this.world = w;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());     
    }

    public final void resize(int width, int height) {
        extendedViewport.update(width, height, true);
        world.resizeWorld((int) extendedViewport.getWorldWidth(), (int) extendedViewport.getWorldHeight());
    }

    public void renderObjects() {
        batch.begin();
        for (Sprite s : world.gameObjects) {
            s.draw(batch);
        }
        batch.end();

        if(!world.drawBoundingBoxes) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (Sprite s : world.gameObjects) {
            Rectangle spriteRec = s.getBoundingRectangle();
            shapeRenderer.rect(spriteRec.x, spriteRec.y, spriteRec.width, spriteRec.height);
        }
        shapeRenderer.end();
    }

    public void drawGrid() {
        if(!world.drawGrid) return;
        int x = 0, y = 0;
        int height = (int) extendedViewport.getWorldHeight();
        int width = (int) extendedViewport.getWorldWidth();
        int rowCount = height / world.gridCellSize;
        int columnCount = width / world.gridCellSize;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        for (int row = 0; row <= rowCount; row++) {
            shapeRenderer.line(x, y, width, y);
            y += world.gridCellSize;
        }
        y = 0;
        for (int column = 0; column <= columnCount; column++) {
            shapeRenderer.line(x, y, x, height);
            x += world.gridCellSize;
        }
        shapeRenderer.end();
    }

    public void drawSelectBox() {
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(world.selectRectangle.x, world.selectRectangle.y, world.selectRectangle.width, world.selectRectangle.height);
        shapeRenderer.end();
    }

    void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(logo, 0, 0, world.gridCellSize, world.gridCellSize);
        batch.end();

        drawGrid();
        renderObjects();        
        drawSelectBox();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

}
