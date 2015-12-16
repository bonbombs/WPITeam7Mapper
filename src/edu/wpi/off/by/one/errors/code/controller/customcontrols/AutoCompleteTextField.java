package edu.wpi.off.by.one.errors.code.controller.customcontrols;

import java.io.IOException;
import java.util.*;

import edu.wpi.off.by.one.errors.code.controller.ControllerSingleton;
import edu.wpi.off.by.one.errors.code.model.TagMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
/**
 * Original code by Caleb Brinkman (floralvikings)
 * source: https://gist.github.com/floralvikings/10290131
 *
 */
public class AutoCompleteTextField extends TextField{
    private Set<String> tags;
	private final SortedSet<String> entries;
	private ContextMenu entriesPopup;
	
	public AutoCompleteTextField(){
        tags = Collections.emptySet();
		setListeners();
		entries = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		entriesPopup = new ContextMenu();
		this.setOnMouseClicked(e -> {
			//update();
            entries.addAll(tags);
		});
		
		textProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				if(getText().length() == 0 || tags.isEmpty()) entriesPopup.hide();
				else{
					LinkedList<String> result = new LinkedList<>();
					result.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
					if(entries.size() > 0){
						populatePopup(result);
						if(!entriesPopup.isShowing()) entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
					}
					else {
						entriesPopup.hide();
					}
				}
			}
		});
		
		this.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				// TODO Auto-generated method stub
                entriesPopup.hide();
			}
			
		});
		//update();
        entries.addAll(tags);
	}
	
	private void setListeners(){
		
	}
	
	public SortedSet<String> getEntries() { return entries; }
	
	/*public void update(){
		Set<String> tags = TagMap.getTagMap().getTags();
		entries.addAll(tags);
	}*/
	protected void add(Set<String> tags){
		entries.addAll(tags);
	}
	
	private void populatePopup(List<String> result){
		List<CustomMenuItem> menuItems = new LinkedList<>();
		int maxEntries = 10;
		int count = Math.min(result.size(), maxEntries);
		for(int i = 0; i < count; i++){
			final String searchresult = result.get(i);
			Label entryLabel = new Label(searchresult);
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);
			item.setOnAction(e -> {
				setText(searchresult);
				entriesPopup.hide();
			});
			menuItems.add(item);
		}
		entriesPopup.getItems().clear();
		entriesPopup.getItems().addAll(menuItems);
	}

	public void setTagsSet(Set<String> tags){
		this.tags = tags;
	}
}
