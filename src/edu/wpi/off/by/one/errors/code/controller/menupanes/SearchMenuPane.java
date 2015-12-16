package edu.wpi.off.by.one.errors.code.controller.menupanes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.off.by.one.errors.code.controller.ControllerSingleton;
import edu.wpi.off.by.one.errors.code.controller.MainPane;
import edu.wpi.off.by.one.errors.code.controller.MapRootPane;
import edu.wpi.off.by.one.errors.code.controller.customcontrols.AutoCompleteTextField;
import edu.wpi.off.by.one.errors.code.controller.customcontrols.ClearableTextField;
import edu.wpi.off.by.one.errors.code.model.Coordinate;
import edu.wpi.off.by.one.errors.code.model.Graph;
import edu.wpi.off.by.one.errors.code.model.Id;
import edu.wpi.off.by.one.errors.code.model.Map;
import edu.wpi.off.by.one.errors.code.model.Matrix;
import edu.wpi.off.by.one.errors.code.model.Node;
import edu.wpi.off.by.one.errors.code.model.TagMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * Created by jules on 11/28/2015.
 */
public class SearchMenuPane extends BorderPane {

	@FXML ClearableTextField searchField;
	@FXML Button toButton;
	@FXML Button fromButton;
	@FXML Button searchLocationButton;
	@FXML ComboBox<String> buildingChoiceBox;
	@FXML ListView<String> locationResultListView;
	HashMap<String, Node> resultReference = new HashMap<String, Node>();
	String selectedNode;
	int currentLevel;
	
    public SearchMenuPane(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/menupanes/SearchMenuPane.fxml"));

        loader.setRoot(this);
        loader.setController(this);
        try{
            loader.load();
            
        }catch(IOException excpt){
            throw new RuntimeException(excpt);
        }
        this.getStylesheets().add(getClass().getResource("/edu/wpi/off/by/one/errors/code/resources/stylesheets/menupanes/SearchPaneStyleSheet.css").toExternalForm());
        setListeners();
		searchField.setTagsSet(TagMap.getTagMap().getTags());
        //SortedSet<String> entries = new TreeSet<String>();
        /*
        for(Map m : ControllerSingleton.getInstance().getMapRootPane().getDisplay().getMaps()){
        	String name = (m.getName() == null) ? m.getImgUrl() : m.getName();
        	//buildingChoiceBox.getItems().add(name);
        }*/
    }
	
	public void updateMapList(ArrayList<Map> maps){
		buildingChoiceBox.getItems().clear();
		for(Map m : ControllerSingleton.getInstance().getMapRootPane().getDisplay().getMaps()){
			String name = (m.getName() == null) ? m.getImgUrl() : m.getName();
			buildingChoiceBox.getItems().add(name);
        }
		buildingChoiceBox.setValue("Campus Map");
	}
	
	@FXML private void setDirectionsTo(){
		//get selected place from search
		if(resultReference == null || selectedNode == null) return;
		Node n = resultReference.get(selectedNode);
		if(n != null) {
			ControllerSingleton.getInstance().getMenuPane().getDirectionsMenuPane().setDirectionsToNode(n);
			ControllerSingleton.getInstance().getMapRootPane().placeMarker(n);
			ControllerSingleton.getInstance().getMenuPane().showDirections();
		}
		//ControllerSingleton.getInstance().getMenuPane().getDirectionsMenuPane().setDirectionsToNode(/*NODE*/);
	}
	
	@FXML private void setDirectionsFrom(){
		//ControllerSingleton.getInstance().getMenuPane().getDirectionsMenuPane().setDirectionsToNode(/*NODE*/);
		if(resultReference == null || selectedNode == null) return;
		Node n = resultReference.get(selectedNode);
		if(n != null) {
			ControllerSingleton.getInstance().getMenuPane().getDirectionsMenuPane().setDirectionsFromNode(n);
			ControllerSingleton.getInstance().getMapRootPane().placeMarker(n);
			ControllerSingleton.getInstance().getMenuPane().showDirections();
		}
	}
	
	private void showResults(ArrayList<Id> results){
		// should take in whatever search spits out
		if(results == null) return;
		if(results.size() == 0) return; // if empty, display a message for user saying that there's no result
		resultReference = new HashMap<String, Node>();
		// Should be called after search is complete
		// Possibly concatenate tags for each node
		// i.e. Fuller Labs, Level 2, Room 222; Salisbury, Floor 3, Women's Bathroom
		//populate locationResultListView
		for(Id id : results) {
			Graph g = ControllerSingleton.getInstance().getMapRootPane().getDisplay().getGraph();
			Node n = g.returnNodeById(id);
			if(n == null) continue;
			System.out.println(n.getName());
			if(!n.getName().trim().isEmpty()) resultReference.put(n.getName(), n);
		}
		locationResultListView.getItems().clear();
		locationResultListView.getItems().addAll(resultReference.keySet());
		//TODO tell render to draw markers of results on map
	}
	
	private void search(String tag){
		ArrayList<Id> results = TagMap.getTagMap().findTag(tag);
		showResults(results);
	}

	private void setListeners(){
		/*this.buildingChoiceBox.setOnContextMenuRequested(e -> {
			//TODO update map list
		});*/
		this.searchField.setOnAction(e -> search(searchField.getText()));
		this.searchLocationButton.setOnAction(e -> search(searchField.getText()));
		this.locationResultListView.setOnMouseClicked(e -> {
			if(locationResultListView.getSelectionModel().isEmpty()){
				toButton.setDisable(true);
				fromButton.setDisable(true);
				return;
			}
			toButton.setDisable(false);
			fromButton.setDisable(false);
			selectedNode = locationResultListView.getSelectionModel().getSelectedItem();
			System.out.println(selectedNode);
			Node n = resultReference.get(selectedNode);
			//TODO tell render to draw a selected point on map
			//ControllerSingleton.getInstance().getMapRootPane().zoomTo
			//spinnyZoom(n);
			if(e.getClickCount() == 2) ControllerSingleton.getInstance().getMapRootPane().placeStartMarker(n);
			else ControllerSingleton.getInstance().getMapRootPane().placeMarker(n);
		});
		this.buildingChoiceBox.setOnAction(e -> {
			spinnyZoom(buildingChoiceBox.getItems().indexOf(buildingChoiceBox.getSelectionModel().getSelectedItem()));
		});
		
	}
	/*
	public void spinnyZoom(Node n) {
		MainPane mainPane = ControllerSingleton.getInstance().getMainPane();
		MapRootPane m = ControllerSingleton.getInstance().getMapRootPane();
		ControllerSingleton.getInstance().getMapRootPane().currentLevel = (int) n.getCoordinate().getZ();
		mainPane.dropStartC = ControllerSingleton.getInstance().getMapRootPane().translate;
		//mainPane.dropStartR = ControllerSingleton.getInstance().getMapRootPane().rot;
		mainPane.dropStartS = ControllerSingleton.getInstance().getMapRootPane().zoom;
		
		//Matrix matrix = new Matrix(m.getRotation(),0,0,1).scale(m.getScale());
		Coordinate coord = new Coordinate((float)m.canvas.getWidth()/2, (float)m.canvas.getHeight()/2, 0);
		coord = m.view.transform(coord);
		Coordinate c = n.getCoordinate();
		mainPane.dropEndC = new Coordinate(-c.getX()-coord.getX(), -c.getY()-coord.getY(), c.getZ());
		float mx = (float)(ControllerSingleton.getInstance().getMapRootPane().canvas.getWidth());
		float my = (float)(ControllerSingleton.getInstance().getMapRootPane().canvas.getHeight());
		mainPane.dropEndS = mx < my ? mx : my;
		if(mainPane.dropEndS <= 0.05f) mainPane.dropEndS = 1.0f;
		mainPane.dropzoom.play();
		//ControllerSingleton.getInstance().getMapRootPane().translate = mainPane.dropEndC;
	//	ControllerSingleton.getInstance().getMapRootPane().rot = mainPane.dropEndR;
	//	ControllerSingleton.getInstance().getMapRootPane().translate = mainPane.dropEndC;
		
		ControllerSingleton.getInstance().getMapRootPane().render();
	}
	*/
	public void spinnyZoom(int in){
		MainPane mainPane = ControllerSingleton.getInstance().getMainPane();
		int index = in;

		if(index == -1){return;}
		Map m = ControllerSingleton.getInstance().getMapRootPane().getDisplay().getMaps().get(index);
		if(m == null) return;
		ControllerSingleton.getInstance().getMapRootPane().currentLevel.setValue(m.getCenter().getZ());
		mainPane.dropStartC = ControllerSingleton.getInstance().getMapRootPane().translate;
		mainPane.dropStartR = ControllerSingleton.getInstance().getMapRootPane().rot;
		mainPane.dropStartS = ControllerSingleton.getInstance().getMapRootPane().zoom;
		
		//float zx = (float) (m.getCenter().getX() + m.getImage().getWidth() * 0.5f * m.getScale());
		//float zy = (float) (m.getCenter().getY() + m.getImage().getHeight() * 0.5f * m.getScale());
	
		Matrix matrix = new Matrix(m.getRotation(),0,0,1).scale(m.getScale());
		Coordinate coord = new Coordinate((float)m.getImage().getWidth()/2, (float)m.getImage().getHeight()/2, 0);
		coord = matrix.transform(coord);
			
		mainPane.dropEndC = new Coordinate(-m.getCenter().getX()-coord.getX(), -m.getCenter().getY()-coord.getY(), m.getCenter().getZ());
		
		mainPane.dropEndR = -m.getRotation();
		float mx = (float)(ControllerSingleton.getInstance().getMapRootPane().canvas.getWidth()/(m.getImage().getWidth() * m.getScale()) );
		float my = (float)(ControllerSingleton.getInstance().getMapRootPane().canvas.getHeight()/(m.getImage().getHeight() * m.getScale()));
		mainPane.dropEndS = mx < my ? mx : my;
		if(mainPane.dropEndS <= 0.05f) mainPane.dropEndS = 1.0f;
		mainPane.dropzoom.play();
		//ControllerSingleton.getInstance().getMapRootPane().translate = mainPane.dropEndC;
	//	ControllerSingleton.getInstance().getMapRootPane().rot = mainPane.dropEndR;
	//	ControllerSingleton.getInstance().getMapRootPane().translate = mainPane.dropEndC;
		ControllerSingleton.getInstance().getMapRootPane().render();
		//buildingChoiceBox.getSelectionModel().clearSelection();
	}
}
