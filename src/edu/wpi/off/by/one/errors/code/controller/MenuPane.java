package edu.wpi.off.by.one.errors.code.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Created by jules on 11/28/2015.
 */
public class MenuPane extends HBox {

    //region FXML file attributes
    @FXML
    private ToggleButton hamburgerToggleButton;

    @FXML
    private AnchorPane detailsMenuContainerAnchorPane;
    //endregion

    //region Constructors
    /**
     * Default Constructor
     * This Constructor will get the associated FXML file, set this class as the root node/layout and the controller for that FXML file and then try to load the fxml file.
     * Finally it will set/add all the listeners needed for all the Nodes/controls/layouts that make up this pane.
     * @exception RuntimeException Thrown if file cannot be loaded.
     */
    public MenuPane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/MenuPane.fxml"));

        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException excpt) {
            throw new RuntimeException(excpt);
        }

        addListeners();
    }
    //endregion

    //region Listener Methods
    /**
     * adds all the listeners needed
     */
    private void addListeners(){
        //listener to compact or extend this pane once the hamburgerToggleButton is clicked(the selected value is changed)
        hamburgerToggleButton.selectedProperty().addListener((v, oldValue, newValue) -> {
            if (newValue){
                this.setPrefWidth(this.getMaxWidth());
                //detailsMenuContainerAnchorPane.setVisible(true);
            }
            else{
                this.setPrefWidth(this.getMinWidth());
                //detailsMenuContainerAnchorPane.setVisible(false);
            }
        });



    }
    //endregion

    //region Helper Methods
    /**
     * Extends the pane by selecting the toggle button
     */
    public void extend(){
        if(!hamburgerToggleButton.isSelected())
            hamburgerToggleButton.setSelected(true);
    }

    /**
     * Compacts the pane by deselection the toggle button
     */
    public void compact(){
        if(hamburgerToggleButton.isSelected())
            hamburgerToggleButton.setSelected(false);
    }
    //endregion



}
