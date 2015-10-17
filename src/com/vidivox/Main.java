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

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainStage.fxml"));
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        MainStageController controller = (MainStageController) loader.getController();

        bindMediaViewerSize(controller, scene);

        primaryStage.setTitle("VIDIVOX");
        primaryStage.show();
    }

    private void bindMediaViewerSize(MainStageController controller, Scene scene){
        controller.mainMediaViewer.fitWidthProperty().bind(scene.widthProperty().subtract(300));

        controller.mainMediaViewer.fitHeightProperty().bind(scene.heightProperty().subtract(100));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
