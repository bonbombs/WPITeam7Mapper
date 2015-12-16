package edu.wpi.off.by.one.errors.code.controller.menupanes.devtoolspanes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.wpi.off.by.one.errors.code.controller.customcontrols.ClearableTextField;
import edu.wpi.off.by.one.errors.code.application.NodeDisplay;
import edu.wpi.off.by.one.errors.code.controller.ControllerSingleton;
import edu.wpi.off.by.one.errors.code.controller.MainPane;
import edu.wpi.off.by.one.errors.code.controller.MapRootPane;
import edu.wpi.off.by.one.errors.code.model.Coordinate;
import edu.wpi.off.by.one.errors.code.model.Display;
import edu.wpi.off.by.one.errors.code.model.Graph;
import edu.wpi.off.by.one.errors.code.model.Id;
import edu.wpi.off.by.one.errors.code.model.Node;
import edu.wpi.off.by.one.errors.code.model.TagMap;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

/**
 * Created by jules on 11/30/2015.
 */
public class NodeDevToolPane extends VBox {

	MainPane mainPane;
	Display currentDisplay;
	NodeDisplay currentNd;
	@FXML ClearableTextField nodeNameTextField;
	@FXML Label nodeIdLabel;
	@FXML TextField xTextField;
	@FXML TextField yTextField;
	@FXML TextField zTextField;
	@FXML ClearableTextField tagTextField;
	@FXML ListView<String> tagListView;
	//@FXML ListView<Id> edgeListView;
	@FXML Button addTagButton;
	@FXML CheckBox accessibleCheckbox;
	@FXML CheckBox foodCheckbox;
	@FXML CheckBox mensCheckbox;
	@FXML CheckBox womensCheckbox;
	@FXML CheckBox genderNeutralCheckbox;
	@FXML CheckBox stairsCheckbox;
	@FXML CheckBox elevatorCheckbox;
	
	
    public NodeDevToolPane(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/menupanes/devtoolspanes/NodeDevToolPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
            
        }catch(IOException excpt){
            throw new RuntimeException(excpt);
        }
        setListeners();
        //currentDisplay = ControllerSingleton.getInstance().getMapRootPane().getDisplay();
    }
    
    public void displayNodeInfo(NodeDisplay nd){
    	this.currentNd = nd;
    	currentDisplay = ControllerSingleton.getInstance().getMapRootPane().getDisplay();
    	Graph g = currentDisplay.getGraph();
    	Node n = g.returnNodeById(nd.getNode());
    	Coordinate c = g.returnNodeById(nd.getNode()).getCoordinate();
    	nodeNameTextField.setText(n.getName());
    	nodeIdLabel.setText(nd.getNode().toString());
    	xTextField.setText(Float.toString(c.getX()));
    	yTextField.setText(Float.toString(c.getY()));
    	zTextField.setText(Float.toString(c.getZ()));
    	tagListView.getItems().clear();
    	//edgeListView.getItems().clear();
    	tagListView.getItems().addAll((n.GetTags() != null) ? n.GetTags() : new ArrayList<String>());
    	//edgeListView.getItems().addAll((n.getEdgelist() != null) ? n.getEdgelist() : new ArrayList<Id>());
    	tagTextField.clear();
    	accessibleCheckbox.setSelected(g.returnNodeById(nd.getNode()).isAccessible());
    	foodCheckbox.setSelected(g.returnNodeById(nd.getNode()).isFood());
    	mensCheckbox.setSelected(g.returnNodeById(nd.getNode()).isMens());
    	womensCheckbox.setSelected(g.returnNodeById(nd.getNode()).isWomens());
    	genderNeutralCheckbox.setSelected(g.returnNodeById(nd.getNode()).isGenderNeutral());
    	stairsCheckbox.setSelected(g.returnNodeById(nd.getNode()).isStairs());
    	elevatorCheckbox.setSelected(g.returnNodeById(nd.getNode()).isElevator());
    }
    
    private void setListeners(){
    	
    	this.visibleProperty().addListener(e -> {
    		if(this.isVisible()){
    			//mainPane.getMapRootPane().isNodeEditor = true;
    		} else {
    			//mainPane.getMapRootPane().isNodeEditor = false;
    		}
    		
    	});
    	
    	this.accessibleCheckbox.selectedProperty().addListener(e -> {
    		Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    		n.setAccessible(this.accessibleCheckbox.isSelected());
    	});
    	
    	//do sth to adjust node display on map as well
    	this.xTextField.setOnKeyPressed(e -> {
    		MapRootPane maproot = ControllerSingleton.getInstance().getMapRootPane();
    		String s = xTextField.getText();
    		Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    		Coordinate currentc = n.getCoordinate();
    		n.setCoordinate(new Coordinate(toFloat(s),
    				currentc.getY(), currentc.getZ()));
    		maproot.render();
    	});
    
    	this.yTextField.setOnKeyPressed(e -> {
    		MapRootPane maproot = ControllerSingleton.getInstance().getMapRootPane();
    		String s = yTextField.getText();
    		Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    		Coordinate currentc = n.getCoordinate();
    		n.setCoordinate(new Coordinate(currentc.getX(),
    				toFloat(s), currentc.getZ()));
    		maproot.render();
    	});
    	
    	this.zTextField.setOnKeyPressed(e -> {
    		MapRootPane maproot = ControllerSingleton.getInstance().getMapRootPane();
    		String s = zTextField.getText();
    		Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    		Coordinate currentc = n.getCoordinate();
    		
    		n.setCoordinate(new Coordinate(currentc.getX(),
    				currentc.getY(), toFloat(s)));
    		maproot.render();
    	});
    	this.tagTextField.setOnAction(e -> {
    		addTag();
    	});
    	this.addTagButton.setOnAction(e -> {
    		addTag();
    	});
    	this.nodeNameTextField.setOnAction(e -> {
    		MapRootPane maproot = ControllerSingleton.getInstance().getMapRootPane();
    		String s = nodeNameTextField.getText();
    		if(currentNd != null) {
    			Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    			n.setName(s);
    		}
    		
    	});
    	this.tagListView.setOnMouseClicked(e -> {
    		if(e.getButton() == MouseButton.SECONDARY){
    			String toRemove = tagListView.getSelectionModel().getSelectedItem();
    			TagMap.getTagMap().removeTag(toRemove, currentNd.getNode());
    			tagListView.getItems().remove(toRemove);
    		}
		});
    }
    

    @FXML private void toggleIsAccessible() {
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.setAccessible(accessibleCheckbox.isSelected() ? true : false);
    }
    @FXML private void toggleIsStairs() {
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.setStairs(stairsCheckbox.isSelected() ? true : false);
    }
    @FXML private void toggleIsElevator() {
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.setElevator(elevatorCheckbox.isSelected() ? true : false);
    }
    @FXML private void toggleIsFood() {
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.setFood(foodCheckbox.isSelected() ? true : false);
    }
    @FXML private void toggleIsMens() {
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.setMens(mensCheckbox.isSelected() ? true : false);
    }
    @FXML private void toggleIsWomens() {
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.setWomens(womensCheckbox.isSelected() ? true : false);
    }
    @FXML private void toggleIsGenderNeutral() {
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.setGenderNeutral(genderNeutralCheckbox.isSelected() ? true : false);
    }
    
    /**
     * Retrieves text input from field and adds it to
     * the selected node's taglist
     * Also adds the tag string into the GUI view
     * TODO sanitize string before adding it into taglist
     */
    private void addTag(){
    	String s = tagTextField.getText();
    	s = s.trim();
    	Node n = currentDisplay.getGraph().returnNodeById(currentNd.getNode());
    	n.addTag(s);
    	tagListView.getItems().clear();
    	tagListView.getItems().addAll((n.GetTags() != null) ? n.GetTags() : new ArrayList<String>());
    	tagTextField.clear();
    }
    
    /**
     * Parses/cleans input string to only contain
     * numerical values
     * TODO program still flips out at certain characters
     * try-catching is the current backup to this
     * @param s input string
     * @return cleaned string
     */
    private float toFloat(String s){
    	String regexString = "/^(-)?((\\d+(\\.\\d*)?)|(\\.\\d+))$/";
       	Pattern regex = Pattern.compile(regexString);
    	Matcher parser = regex.matcher(s);
    	if(parser.matches()) s = parser.group(0);
    	else s = "0";
    	if(s == "" || s == null || s.isEmpty()) s = "0";
    	float newNum;
		try {
			newNum = Float.parseFloat(s);
		} catch (NumberFormatException ex){
			newNum = 0;
		}
		return newNum;
    }
}
