package edu.wpi.off.by.one.errors.code.controller.customcontrols;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Set;

/**
 * Created by jules on 12/5/2015.
 */
public class ClearableTextField extends BorderPane {

    //region FXML attributes
    @FXML protected BorderPane textFieldButtonContainer;
    @FXML protected AutoCompleteTextField textField;
    @FXML protected Button clearButton;
    @FXML protected Label errorLabel;
    //endregion

    //region constructor/s
    public ClearableTextField(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/customcontrols/ClearableTextField.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try{
            loader.load();
            this.getStylesheets().add(getClass().getResource("/edu/wpi/off/by/one/errors/code/resources/stylesheets/customcontrols/ClearableTextFieldStyleSheet.css").toExternalForm());
        }
        catch(IOException excpt){
            throw new RuntimeException(excpt);
        }
        this.getStyleClass().add("clearable-text-field");
        bind();
    }
    //endregion

    //region Getters, Setters
    public StringProperty textProperty() {
        return textField.textProperty();
    }

    public String getText(){
        return textProperty().get();
    }

    public void setText(String text){
        textProperty().set(text);
    }

    public StringProperty promptTextProperty() {
        return textField.promptTextProperty();
    }

    public String getPromptText(){
        return promptTextProperty().get();
    }

    public void setPromptText(String text){
        promptTextProperty().set(text);
    }

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty(){
        return textField.onActionProperty();
    }

    public EventHandler<ActionEvent> getOnAction(){
        return onActionProperty().get();
    }

    public void setOnAction(EventHandler<ActionEvent> actionEvent){
        onActionProperty().set(actionEvent);
    }

    //endregion

    //region listener Methods
    @FXML private void onClearTextBoxButtonClick(){
        textField.clear();
        textField.requestFocus();
    }
    //endregion

    //region helper Methods

    /**
     * Binds various properties together
     */
    private void bind(){
        clearButton.disableProperty().bind(textField.textProperty().isEmpty());
    }

    /**
     * Shows the error label by setting the size of the error label to max height (25) and sets the text of the label
     * @param message The error message to be display
     */
    public void showErrorLabel(String message){
        errorLabel.setText(message);
        errorLabel.setPrefHeight(errorLabel.getMaxHeight());
        textFieldButtonContainer.getStyleClass().add("textFieldButtonContainerError");

    }

    /**
     * Hides the error label by setting its height to 0 and sets the text to an empty string;
     */
    public void hideErrorLabel(){
        errorLabel.setPrefHeight(errorLabel.getMinHeight());
        errorLabel.setText("");
        textFieldButtonContainer.getStyleClass().remove("textFieldButtonContainerError");
    }

    /**
     * Sets the tags list for autocomplete
     * @param tags
     */
    public void setTagsSet(Set<String> tags){
        this.textField.setTagsSet(tags);
    }

    /**
     * Clears the content inside the textfield
     */
    public void clear(){
        textField.clear();
    }
    //endregion



}
