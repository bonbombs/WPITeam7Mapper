package edu.wpi.off.by.one.errors.code.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import edu.wpi.off.by.one.errors.code.controller.customcontrols.IconedLabel;
import edu.wpi.off.by.one.errors.code.model.Node;
import edu.wpi.off.by.one.errors.code.model.Step;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * Created by jules on 11/28/2015.
 */
public class NavigationPane extends GridPane{
    //region FXML attributes
	@FXML protected Label instructionLabel;
    //endregion

    //region Properties
    /**
     * Property to keep track of whether or not this pane is expanded or is collapsed/compacted
     */
    private BooleanProperty isExpandedProperty;
    //endregion

    //region Other attributes
    /**
     * List that contains the list of directions/Step
     */
    private ArrayList<Step> directionsList;

    /**
     * Keeps track of which instruction is currently showing
     */
    private int currentInstructionIndex;

    /**
     * Animation object to collapse this pane by setting its height to 0
     */
    private final Animation collapseAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(100));
        }
        protected void interpolate(double frac) {
            final double currentHeight = getMaxHeight() * (1.0 - frac);
            setPrefHeight(currentHeight);
            setTranslateY(getMaxHeight() + currentHeight);
        }
    };

    /**
     * Animation object to expand this pane by setting its height to 0
     */
    private final Animation expandAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(100));
        }
        protected void interpolate(double frac) {
            final double currentHeight = getMaxHeight() * frac;
            setPrefHeight(currentHeight);
            setTranslateY(getMaxHeight() - currentHeight);
        }
    };
    //endregion

    //region Constructor

    /**
     * Constructor to initialize attributes and this object
     */
    public NavigationPane(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/NavigationPane.fxml"));

        loader.setRoot(this);
        loader.setController(this);
        try{
            loader.load();
            this.getStylesheets().add(getClass().getResource("/edu/wpi/off/by/one/errors/code/resources/stylesheets/NavigationPaneStyleSheet.css").toExternalForm());
        }catch(IOException excpt){
            throw new RuntimeException(excpt);
        }
        ControllerSingleton.getInstance().registerNavigationPane(this);
        this.currentInstructionIndex = 0;
        directionsList = new ArrayList<Step>();
        this.directionsList.add(new Step());
        this.isExpandedProperty = new SimpleBooleanProperty(true);
        setListeners();

    }
    //endregion

    //region Getters

    /**
     * Gets the isExpandedProperty
     * @return The isExpandedProperty
     */
    public BooleanProperty getIsExpandedProperty(){
        return this.isExpandedProperty;
    }
    //endregion

    //region setters

    /**
     * Sets the isExpandedProperty value
     * @param newValue The new boolean value for the isExpectedProperty
     */
    public void setIsExpandedProperty(boolean newValue){
        isExpandedProperty.set(newValue);
    }

    /**
     * Sets the list of directions
     * @param directionsList The new list of directions
     */
    public void setDirectionsList(ArrayList<Step> directionsList){
        this.directionsList = directionsList;
    }
    //endregion

    //region Listener Methods

    /**
     * Collapses this pane when the user clicks the close pane button
     */
    @FXML protected void onClosePaneButtonClick(){
        collapseAnimation.play();
    }

    /**
     * Listener for previous Button
     * Shows the previous instruction
     */
    @FXML protected void onPreviousButtonClick(){
        currentInstructionIndex -= 1;
        if (currentInstructionIndex >= 0 && currentInstructionIndex< directionsList.size())
            instructionLabel.setText(directionsList.get(currentInstructionIndex).toString());
    }

    /**
     * Listener for Next Button
     * Shows the Next instruction
     */
    @FXML protected void onNextButtonClick(){
        currentInstructionIndex += 1;
        if (currentInstructionIndex >= 0 && currentInstructionIndex< directionsList.size())
            instructionLabel.setText(directionsList.get(currentInstructionIndex).toString());
    }

    /**
     * adds listeners to the necessary controls.
     */
    private void setListeners(){
        expandAnimation.onFinishedProperty().set(e -> isExpandedProperty.set(true));
        collapseAnimation.onFinishedProperty().set(e -> isExpandedProperty.set(false));
    }
    //endregion

    //region Helper Methods

    /**
     * Expands the pane by playing the animation to set the size of this pane to its max height
     */
    public void expand(){
        expandAnimation.play();
    }

    /**
     * Clears the list of directions
     */
    public void clearInstructions(){
        this.directionsList.clear();
        this.currentInstructionIndex = 0;
        this.directionsList.add(new Step());
    }

    /**
     * Initializes the navigation system
     */
    public void start(ArrayList<Step> directionsList){
        if(!isExpandedProperty.get())
            expandAnimation.play();
        this.directionsList = directionsList;
        currentInstructionIndex = 0;
        instructionLabel.setText(this.directionsList.get(currentInstructionIndex).toString());
    }
    //endregion





}
