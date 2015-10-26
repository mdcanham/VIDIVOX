package com.vidivox.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A class to handle basic warning dialogue that will be used when the user tries to do something that they are not meant to do.
 */
public class WarningDialogue{

    public WarningDialogue(String warningText) {
        //Set up a generic alert
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("That can't be done");
        alert.setContentText(warningText);
        alert.showAndWait();
    }

    /**
     * Set up a generic error warning message to be displayed in the case of a generic error.
     * @param exceptionMessage
     */
    public static void genericError(String exceptionMessage){
        String message = "Whoops, something has gone wrong.\n";
        message += exceptionMessage;
        new WarningDialogue(message);
    }
}
