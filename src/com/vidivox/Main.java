package com.vidivox;

import com.vidivox.controller.MainStageController;
import com.vidivox.view.WarningDialogue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 */
public class Main extends Application {

    public static Stage stage;
    public static MainStageController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainStage.fxml"));
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1000);
        MainStageController controller = (MainStageController) loader.getController();
        mainController = controller;

        stage = primaryStage;

        primaryStage.setTitle("VIDIVOX");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
