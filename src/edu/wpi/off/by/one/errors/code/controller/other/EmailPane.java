package edu.wpi.off.by.one.errors.code.controller.other;

import edu.wpi.off.by.one.errors.code.controller.ControllerSingleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Created by jules on 12/16/2015.
 */
public class EmailPane extends BorderPane {


    public EmailPane(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/off/by/one/errors/code/view/other/EmailPane.fxml"));

        loader.setRoot(this);
        loader.setController(this);
        try{
            loader.load();
        }catch(IOException excpt){
            throw new RuntimeException(excpt);
        }
    }

    //region Methods for listeners
    @FXML private void onCancelButtonClick(){
        ControllerSingleton.getInstance().getMainPane().showEmailPane(false);
    }
    //endregion
}
