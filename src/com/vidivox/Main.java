package com.vidivox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Timer;

/**
 *
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("VIDIVOX");
        primaryStage.setScene(new Scene(root));
        primaryStage.getScene().fillProperty().setValue(Color.BLACK);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
