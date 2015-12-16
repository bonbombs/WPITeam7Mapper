package edu.wpi.off.by.one.errors.code.controller;

import java.io.IOException;

import edu.wpi.off.by.one.errors.code.controller.menupanes.devtoolspanes.NodeDevToolPane;
import edu.wpi.off.by.one.errors.code.controller.other.EmailPane;
import edu.wpi.off.by.one.errors.code.controller.other.SMSPane;
import edu.wpi.off.by.one.errors.code.model.Coordinate;
import edu.wpi.off.by.one.errors.code.model.Display;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.Duration;


/**
 * The controller that manages the logic for most of the application view:
 * 		- Map
 * 		- Menu
 *		- Directions
 * 
 */
public class MainPane extends StackPane {
    private BooleanProperty isDevModeOn;


	Window window;
	@FXML private Slider floorSlider;
	@FXML private StackPane mapContainer;
	@FXML private Button rotateLeftButton;
	@FXML private Button rotateRightButton;
	@FXML private Button zoomInButton;
	@FXML private Button zoomOutButton;
    @FXML private Button openNavigationPaneButton;
	@FXML private ScrollPane mapScrollPane;
	@FXML private MenuPane menuPane;
	@FXML private MapRootPane mapRootPane;
	@FXML private NavigationPane navigationPane;
	@FXML private StackPane smsEmailContainer;
	@FXML private EmailPane emailPane;
	@FXML private SMSPane smsPane;

	public Coordinate dropStartC;
	public	Coordinate dropEndC; 
	public float dropStartR = 0.0f;
	public float dropEndR = 0.0f;
	public float dropStartS = 1.0f;
	public float dropEndS = 1.0f;
	public float dropTime = 0.0f;

	static float smoothstep(float edge0, float edge1, float x){
		x = ((x - edge0) / (edge1 - edge0));
		if(x > 1.0f) x = 1.0f; else if (x< 0.0f) x = 0.0f;
		return x * x * (3.0f - 2.0f * x);
	}
	static float smootherstep(float edge0, float edge1, float x){
		x = ((x - edge0) / (edge1 - edge0));
		if(x > 1.0f) x = 1.0f; else if (x< 0.0f) x = 0.0f;
		return x*x*x*(x*(x*6.0f - 15.0f) + 10.0f);
	}
	static float dosmooth(float edge0, float edge1, float x){

		if(edge0 > edge1){
			float t = edge1;
			edge1 = edge0;
			edge0 = t;
			x = 1.0f - x;
		} else if( edge0 == edge1){
			return edge0;
		}

		float inbete = edge0 * (1.0f - x) + edge1 * x;
		float retty = smootherstep(edge0, edge1, inbete);
		return edge0 * (1.0f - retty) + edge1 * retty;
	}
	
	public Timeline dropzoom = new Timeline(new KeyFrame(
			Duration.millis(33.3),
			ae -> {
				float sx = dropStartC.getX();
				float sy = dropStartC.getY();
				float sz = dropStartC.getZ();
				float ex = dropEndC.getX();
				float ey = dropEndC.getY();
				float ez = dropEndC.getZ();
				//float inbetx = sx* (1.0f - dropTime) + ex * dropTime;
				//float inbety = sy* (1.0f - dropTime) + ey * dropTime;
				//float inbetz = sz* (1.0f - dropTime) + ez * dropTime;
				float inbetx = dosmooth(sx, ex, dropTime);
				float inbety = dosmooth(sy, ey, dropTime);
				float inbetz = dosmooth(sz, ez, dropTime);
				mapRootPane.translate.setAll(inbetx, inbety, inbetz);
				
				//mapRootPane.rot = dropStartR * (1.0f - dropTime) + dropEndR * dropTime;
				//mapRootPane.zoom = dropStartS * (1.0f - dropTime) + dropEndS * dropTime;

				mapRootPane.rot = dosmooth(dropStartR, dropEndR, dropTime);
				mapRootPane.zoom = dosmooth(dropStartS, dropEndS, dropTime);
			//	System.out.println(dosmooth(2.0f, 10.0f, dropTime) + " " + dropTime);
				dropTime+= 0.05;
				if(dropTime >= 1.0) stopdrop();
				mapRootPane.render();
			}));
	private void stopdrop(){ dropzoom.stop(); dropTime = 0.0f;
		mapRootPane.translate.setAll(dropEndC.getX(), dropEndC.getY(), dropEndC.getZ());
		mapRootPane.rot = dropEndR;
		mapRootPane.zoom = dropEndS;
	}
	
	Timeline lttl = new Timeline(new KeyFrame(
			Duration.millis(33.3),
			ae -> {
				mapRootPane.rot -= 1.0;
				//mapRootPane.translate.setAll(mapRootPane.translate.getX() + 1.0f, mapRootPane.translate.getY(), mapRootPane.translate.getZ());
				mapRootPane.render();
			}));
	Timeline rttl = new Timeline(new KeyFrame(
			Duration.millis(33.3),
			ae -> {
				mapRootPane.rot += 1.0;
				mapRootPane.render();
			}));
	Timeline zitl = new Timeline(new KeyFrame(
			Duration.millis(33.3),
			ae -> {
				mapRootPane.zoom *= 1.05;
				mapRootPane.render();
			}));
	Timeline zotl = new Timeline(new KeyFrame(
			Duration.millis(33.3),
			ae -> {
				mapRootPane.zoom *= 0.95;
				mapRootPane.render();
			}));
    public MainPane(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/MainPane.fxml"));

        loader.setRoot(this);
        loader.setController(this);
        try{
            loader.load();
        }catch(IOException excpt){
            throw new RuntimeException(excpt);
        }
        ControllerSingleton.getInstance().registerMainPane(this);
        mapRootPane = ControllerSingleton.getInstance().getMapRootPane();
        lttl.setCycleCount(Timeline.INDEFINITE);
        rttl.setCycleCount(Timeline.INDEFINITE);
        zitl.setCycleCount(Timeline.INDEFINITE);
        zotl.setCycleCount(Timeline.INDEFINITE);
        dropzoom.setCycleCount(Timeline.INDEFINITE);
        addListeners();
        
        this.getStylesheets().add(getClass().getResource("/edu/wpi/off/by/one/errors/code/resources/stylesheets/MainPaneStyleSheet.css").toExternalForm());
		this.navigationPane.setIsExpandedProperty(false);

    }

	private void addListeners(){
        openNavigationPaneButton.visibleProperty().bind(navigationPane.getIsExpandedProperty().not());
    	rotateLeftButton.setOnMousePressed(e -> lttl.play());
    	rotateLeftButton.setOnMouseReleased(e -> lttl.stop());
    	rotateRightButton.setOnMousePressed(e -> rttl.play());
    	rotateRightButton.setOnMouseReleased(e -> rttl.stop() );
    	zoomInButton.setOnMousePressed(e -> zitl.play());
    	zoomInButton.setOnMouseReleased(e -> zitl.stop() );
    	zoomOutButton.setOnMousePressed(e -> zotl.play());
    	zoomOutButton.setOnMouseReleased(e -> zotl.stop() );
    	ControllerSingleton.getInstance().getMenuPane().searchMenuPane.updateMapList(ControllerSingleton.getInstance().getMapRootPane().getDisplay().getMaps());

    }

    @FXML private void onOpenNavigationPaneButtonClick(){
        navigationPane.expand();
    }
    public void setWindow(Window window) { 
    	this.window = window; 
    	mapScrollPane.setVmax(0);
    	mapScrollPane.setHmax(0);

    	mapContainer.heightProperty().addListener(e -> {
    		mapRootPane.updateCanvasSize(mapContainer.getWidth(), mapContainer.getHeight());
    	});
    	
    	mapContainer.widthProperty().addListener(e -> {
    		mapRootPane.updateCanvasSize(mapContainer.getWidth(), mapContainer.getHeight());
    	});
    }
    public Window getWindow() { return this.window; }
    public NodeDevToolPane getNodeTool() { return this.menuPane.getDevToolsMenuPane().getNodeDevToolPane(); }

    
    public void setFloorSlider(int val){
    	floorSlider.setValue(val);
    }
    
    /**
     * Listener for when mouse is clicked on the floor slider
     */
    @FXML private void onFloorSliderMouseCLicked(){
        int floor = (int) floorSlider.getValue();
        mapRootPane.currentLevel.set(floor);;
        mapRootPane.getMapRootPane().translate.setAll(mapRootPane.translate.getX(), mapRootPane.translate.getY(), floor);
        mapRootPane.getMapRootPane().render();
    }

	/**
	 * shows the sms pane
	 */
	public void showSMSPane(boolean visible){
		smsEmailContainer.visibleProperty().set(visible);
		smsPane.setVisible(visible);
		emailPane.setVisible(!visible);

	}

	/**
	 * Shows email pane
	 */
	public void showEmailPane(boolean visible){
		smsEmailContainer.visibleProperty().set(visible);
		emailPane.setVisible(visible);
		smsPane.setVisible(!visible);
	}
}

