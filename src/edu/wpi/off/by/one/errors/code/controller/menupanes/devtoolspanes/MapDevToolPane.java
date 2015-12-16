package edu.wpi.off.by.one.errors.code.controller.menupanes.devtoolspanes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import edu.wpi.off.by.one.errors.code.controller.ControllerSingleton;
import edu.wpi.off.by.one.errors.code.controller.MainPane;
import edu.wpi.off.by.one.errors.code.controller.MapRootPane;
import edu.wpi.off.by.one.errors.code.controller.customcontrols.ClearableTextField;
import edu.wpi.off.by.one.errors.code.model.Coordinate;
import edu.wpi.off.by.one.errors.code.model.Display;
import edu.wpi.off.by.one.errors.code.model.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Created by jules on 11/30/2015.
 */
public class MapDevToolPane extends VBox {

	Display currentDisplay;
	Map currentMap;

	MainPane mainPane;
	HashMap<String,Display> displayList;

	@FXML public ChoiceBox<String> mapChoiceBox;
	@FXML ClearableTextField nameTextField;
	@FXML TextField xTextField;
	@FXML TextField yTextField;
	@FXML TextField zTextField;
	@FXML TextField rotationTextField;
	@FXML TextField scaleTextField;
	@FXML ClearableTextField mapTagTextField;
	@FXML Label pathLabel;
	@FXML Button changeImageButton;
	@FXML ToggleButton selectColorButton;
	@FXML CheckBox autoEdgeSelector;
	@FXML ColorPicker walkableColorSelector;
	
	int eyedroppedColor = 0;
	
	private ArrayList<Map> mapList = new ArrayList<Map>();
	private Map selectedMap;


	public MapDevToolPane() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/menupanes/devtoolspanes/MapDevToolPane.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException excpt) {
			throw new RuntimeException(excpt);
		}
		ControllerSingleton cs = ControllerSingleton.getInstance();
		setListeners();
		cs.registerMapDevToolPane(this);
	}

	private void setListeners(){  	
		
		this.mapChoiceBox.setOnAction(e -> {
			//should be change map info
			changeDisplay();
		});
		
		this.mapChoiceBox.setOnContextMenuRequested(e->{
			MapRootPane mapRoot = ControllerSingleton.getInstance().getMapRootPane();
			mapChoiceBox.getItems().clear();
			this.mapList = mapRoot.getDisplay().getMaps(); 
			for(Map m : mapRoot.getDisplay().getMaps()){
				String name = (m.getName() == null) ? m.getImgUrl() : m.getName();
				mapChoiceBox.getItems().add(name);
			}
		});
		this.nameTextField.setOnKeyPressed(e -> {
			MapRootPane mapRoot = ControllerSingleton.getInstance().getMapRootPane();
			String s = nameTextField.getText();
			s = s.trim();
			currentMap.setName(s);
			mapRoot.render();
		});

		this.rotationTextField.setOnKeyPressed(e -> {
			MapRootPane mapRoot = ControllerSingleton.getInstance().getMapRootPane();
			String s = rotationTextField.getText();
			currentMap.setRotation(toFloat(s));
			mapRoot.render();
		});

		this.scaleTextField.setOnKeyPressed(e -> {
			MapRootPane mapRoot = ControllerSingleton.getInstance().getMapRootPane();
			String s = scaleTextField.getText();
			currentMap.setScale(toFloat(s));
			mapRoot.render();
		});

		this.xTextField.setOnKeyPressed(e -> {
			MapRootPane mapRoot = ControllerSingleton.getInstance().getMapRootPane();
			String s = xTextField.getText();
			Coordinate currentc = currentMap.getCenter();
			currentMap.setCenter(new Coordinate(toFloat(s),
					currentc.getY(), currentc.getZ()));
			mapRoot.render();
		});

		this.yTextField.setOnKeyPressed(e -> {
			MapRootPane mapRoot = ControllerSingleton.getInstance().getMapRootPane();
			String s = yTextField.getText();
			Coordinate currentc = currentMap.getCenter();
			currentMap.setCenter(new Coordinate(currentc.getX(),
					toFloat(s), currentc.getZ()));
			mapRoot.render();
		});

		this.zTextField.setOnKeyPressed(e -> {
			MapRootPane mapRoot = ControllerSingleton.getInstance().getMapRootPane();
			String s = zTextField.getText();
			Coordinate currentc = currentMap.getCenter();
			currentMap.setCenter(new Coordinate(currentc.getX(),
					currentc.getY(), toFloat(s)));
			mapRoot.render();
		});
	}

	public void setMap(Map m) { 
		this.currentMap = m;
		if(m.getName() != null) this.nameTextField.setText(m.getName());
		else this.nameTextField.clear();
		this.xTextField.setText(Float.toString(m.getCenter().getX()));
		this.yTextField.setText(Float.toString(m.getCenter().getY()));
		this.zTextField.setText(Float.toString(m.getCenter().getZ()));
		this.scaleTextField.setText(Float.toString(m.getScale()));
		this.rotationTextField.setText(Float.toString(m.getRotation()));
		this.pathLabel.setText(m.getImgUrl());
	}

	@FXML public void changeImage(){
		mainPane = ControllerSingleton.getInstance().getMainPane();
		Display display = ControllerSingleton.getInstance().getMapRootPane().getDisplay();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Map File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Image Files", "*.png"),
				new ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(mainPane.getWindow());
		if (selectedFile != null) {
			String inpath = selectedFile.getName();
			Map newmap = new Map();
			newmap.setImgUrl(inpath);
			ControllerSingleton.getInstance().getMapRootPane().updateDisplay(display, "NEW");
		}
	}

	public void changeDisplay(){
		MapRootPane maproot = ControllerSingleton.getInstance().getMapRootPane();
		int index = mapChoiceBox.getItems().indexOf(mapChoiceBox.getSelectionModel().getSelectedItem());
		this.selectedMap = this.mapList.get(index);
		ArrayList<Map> selectedMaps = maproot.getSelectedMaps();
		selectedMaps.clear();
		selectedMaps.add(this.selectedMap);
		maproot.setSelectedMaps(selectedMaps);
		setMap(this.selectedMap);
		maproot.render();
		ControllerSingleton.getInstance().getMenuPane().getSearchMenuPane().spinnyZoom(index);
	}

	public void updateMapList(ArrayList<Map> maps){
		if(!this.mapList.containsAll(maps)){
			this.mapList = maps;
			mapChoiceBox.getItems().clear();
			for(Map m : maps){
				String name = (m.getName() == null) ? ((m.getImgUrl() == null) ? "unnamed" : m.getImgUrl() ): m.getName();
				mapChoiceBox.getItems().add(name);
			}
		}
	}
	
	@FXML private void assignMapTag(){
		//TODO do something to map
	}
	
	@FXML private void reaffiliateNodes(){
		//TODO do something to map
	}
	
	@FXML private void disaffiliateNodes(){
		//TODO do something to map
	}
	

//	@FXML private void toggleAutoEdge(){
//		autoEdgeSelector.isSelected();
//		//TODO do stuff w this
//	}
//	
//	@FXML private void setWalkableColor(){
//		Color color = walkableColorSelector.getValue();
//		//TODO do stuff w this
//	}
//	
//	@FXML private void selectColorOnMap(){
//		ControllerSingleton.getInstance().getMapRootPane().isEyedrop = selectColorButton.isSelected() ? true : false;
//	}
//	
//	public void setEyedroppedColor(int color){
//		this.eyedroppedColor = color;
//		System.out.println(color);
//		//walkableColorSelector.setValue();
//	}
//	
	/**
	 * Parses/cleans input string to only contain
	 * numerical values
	 * TODO program still flips out at certain characters
	 * try-catching is the current backup to this
	 * @param s input string
	 * @return cleaned string
	 */
	private float toFloat(String s){
		String regexString = "^-?(?:\\d+|\\d{1,3}(?:,\\d{3})+)?(?:\\.\\d+)?$";


		float newNum = 0;

		if(s.matches(regexString)){
			if(s == "" || s == null || s.isEmpty()) s = "0";

			try {
				newNum = Float.valueOf(s);

			} catch (NumberFormatException ex){
				newNum = 0;


			}
		}
		else newNum = 0;

		return newNum;
	}
}
