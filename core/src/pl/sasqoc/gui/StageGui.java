package pl.sasqoc.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pl.sasqoc.game.Assets;
import pl.sasqoc.game.Statistics;
import pl.sasqoc.game.World;


/**
 *
 * @author amadela
 */
public class StageGui {
    
    private final Stage uiStage;
    private final World world;
            
    private Label functionName;
    private Label functionCount;
    private Label fpsLabel;
    
    private Window wSettings;
    private boolean wHidden = true;
    private final float windowPadding = 60;    
    private final Vector2 windowPosShow = new Vector2();
    private final Vector2 windowPosHide = new Vector2();    
    
    TextField gridCellSize;
    TextField objectSize;
    TextField staringObjectsNumner;
    
    
    public StageGui(Stage stage, World world){
        this.uiStage = stage;
        this.world = world;
        
        createUI();        
    }
    
    public void update(){               
        updateInfoLabels();
    }

    private void createUI() {
        
        createInfoLabels();        
        
        wSettings =  new Window("Settings", Assets.skin);
        wSettings.setSize(350, 250);
        wSettings.setPosition(windowPosHide.x,  world.heigth -windowPadding -wSettings.getHeight());
        wSettings.setMovable(false);            
        wSettings.setKeepWithinStage(false);
        
        uiStage.addActor(wSettings);        
        
        Button applayButton = new TextButton("Apply", Assets.skin);

        applayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gridCellSize.getText().length()>0) world.gridCellSize  = Integer.parseInt(gridCellSize.getText());
                if (objectSize.getText().length()>0) world.objectSize  = Integer.parseInt(objectSize.getText());
                if (staringObjectsNumner.getText().length()>0) world.staringObjectsNumber  = Integer.parseInt(staringObjectsNumner.getText());
                world.updateSettings();
            }
        });
        
        
        Table table = new Table();
        table.setFillParent(true);
        
        DigitsOnlyFilter digits =  new DigitsOnlyFilter();
        
        Label label1 = new Label("Grid cell size:", Assets.skin);
        gridCellSize = new TextField(Integer.toString(world.gridCellSize), Assets.skin);
        gridCellSize.setTextFieldFilter(digits);
        gridCellSize.setMaxLength(3);
        
        Label label2 = new Label("Object size:", Assets.skin);
        objectSize = new TextField(Integer.toString(world.objectSize), Assets.skin);
        objectSize.setTextFieldFilter(digits);        
        objectSize.setMaxLength(3);
        
        Label label3 = new Label("Objects count:", Assets.skin);
         staringObjectsNumner = new TextField(Integer.toString(world.staringObjectsNumber), Assets.skin);
        staringObjectsNumner.setTextFieldFilter(digits);                
        staringObjectsNumner.setMaxLength(5);        

        
        table.add(label1);
        table.add(gridCellSize);
        table.add().row();
        table.add(label2);
        table.add(objectSize);
        table.add().row();
        table.add(label3);
        table.add(staringObjectsNumner);        
        
        Table tableBR = new Table();
        tableBR.pad(10);
        tableBR.setFillParent(true);
        tableBR.bottom().right().add(applayButton);
        
        wSettings.addActor(table);
        wSettings.addActor(tableBR);
    }
    
    public void changeWindowVisbility(){
        if (wHidden) wSettings.addAction(Actions.moveTo(windowPosShow.x, windowPosShow.y, 0.4f, Interpolation.exp10));
        else  wSettings.addAction(Actions.moveTo(windowPosHide.x, windowPosHide.y, 0.4f, Interpolation.exp10));
        
        wHidden = !wHidden;                
    }

    private void createInfoLabels() {
        LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = Assets.skin.getFont("default-font");
        //labelStyle.background = Assets.skin.getDrawable("btn_default_focused");
        Pixmap pxmBlack = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pxmBlack.setColor(Color.BLACK);
        pxmBlack.fill();        
        Drawable drawable =  new TextureRegionDrawable(new TextureRegion(new Texture(pxmBlack)));
        labelStyle.background = drawable;
        
        
        
        functionName = new Label("Operacja", labelStyle);                
        functionCount = new Label("0", labelStyle);      
        fpsLabel = new Label("0", labelStyle);      
        
        Table tableTL = new Table();
        tableTL.setFillParent(true);
        
        Table tableTR = new Table();
        tableTR.setFillParent(true);
        
        Table tableBR = new Table();
        tableBR.setFillParent(true);
        
        tableTL.top().left().add(functionName);
        tableTR.top().right().add(functionCount);
        tableBR.bottom().right().add(fpsLabel);
        
        uiStage.addActor(tableTL);             
        uiStage.addActor(tableTR);
        uiStage.addActor(tableBR);
    }
    
    private void updateInfoLabels() {
        functionName.setText("Function : " +Statistics.functionName);
        functionCount.setText(Integer.toString(Statistics.checksCount));         
        fpsLabel.setText("FPS:" + Integer.toString(Gdx.app.getGraphics().getFramesPerSecond()));
    }   
    
    public void resize(){
        windowPosShow.x = windowPadding;
        windowPosHide.x = -400;
        windowPosShow.y = world.heigth -windowPadding -wSettings.getHeight();
        windowPosHide.y = world.heigth -windowPadding -wSettings.getHeight();
        if (wHidden) wSettings.setPosition(windowPosHide.x,  windowPosHide.y);        
        else wSettings.setPosition(windowPosShow.x,  windowPosShow.y);        
    }
    
}
