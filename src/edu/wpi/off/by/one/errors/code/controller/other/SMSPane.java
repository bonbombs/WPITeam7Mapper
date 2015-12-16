package edu.wpi.off.by.one.errors.code.controller.other;

import edu.wpi.off.by.one.errors.code.controller.ControllerSingleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Created by jules on 12/16/2015.
 */
public class SMSPane extends BorderPane {

    public SMSPane(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/other/SMSPane.fxml"));

        loader.setRoot(this);
        loader.setController(this);
        try{
            loader.load();
        }catch(IOException excpt){
            throw new RuntimeException(excpt);
        }
    }

    //region Method for EventListener

    /**
     * Hides the parent container
     */
    @FXML private void onCancelButtonClick(){
        ControllerSingleton.getInstance().getMainPane().showSMSPane(false);
    }
    //endregion
}
