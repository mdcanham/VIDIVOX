package com.vidivox;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class MainWindowController {

    @FXML
    private MediaView mainMediaViewer = new MediaView();

    @FXML
    private TextArea mainSpeechTextArea = new TextArea();

    @FXML
    private ToolBar videoOptionBar = new ToolBar();

    @FXML
    private ToolBar speechOptionBar = new ToolBar();

    @FXML
    private MenuBar mainMenuBar = new MenuBar();

    @FXML
    private void handleOpenVideoButton(){
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                mainMediaViewer.setMediaPlayer(new MediaPlayer(new Media(
                        file.toURI().toString())
                ));
            } catch(MediaException e) {
                if( e.getType() == MediaException.Type.MEDIA_UNSUPPORTED ){
                    System.out.println("Sorry, we didn't recognise that file type. Currently VIDIVOX supports MP4 files.");
                }
            }
        }
    }

    @FXML
    private void handlePlayPauseButton(){
        try {
            if(mainMediaViewer.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING){
                mainMediaViewer.getMediaPlayer().pause();
            } else {
                mainMediaViewer.getMediaPlayer().play();
            }
        } catch (NullPointerException e){
            //This is where we tell the user that they need to load in video content before they can play the video
            System.out.println("You need to open a video file before you can play anything...");
        }
    }

    @FXML
    private void handleSpeechPreviewButton(){
        String textToSay = mainSpeechTextArea.getText();

        FestivalSpeech festival = new FestivalSpeech(textToSay);

        festival.speak();
    }

    @FXML
    private void handleMouseMoved(){
        FadeTransition menuFT = new FadeTransition(Duration.millis(10000), mainMenuBar);
        menuFT.setFromValue(1.0);
        menuFT.setToValue(0.0);
        menuFT.playFromStart();
        FadeTransition videoFT = new FadeTransition(Duration.millis(10000), videoOptionBar);
        videoFT.setFromValue(1.0);
        videoFT.setToValue(0.0);
        videoFT.playFromStart();
        FadeTransition speechFT = new FadeTransition(Duration.millis(10000), speechOptionBar);
        speechFT.setFromValue(1.0);
        speechFT.setToValue(0.0);
        speechFT.playFromStart();
    }

    @FXML
    private void handleAddSpeechButton() {
        if (speechOptionBar.isVisible()){
            return;
        }
        speechOptionBar.setVisible(true);
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        speechFT.setFromValue(0.0);
        speechFT.setToValue(1.0);
        speechFT.playFromStart();
    }

    @FXML
    private void handleReturnButton() {
        if (!speechOptionBar.isVisible()){
            return;
        }
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        speechFT.setFromValue(1.0);
        speechFT.setToValue(0.0);
        speechFT.playFromStart();
        speechFT.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                speechOptionBar.setVisible(false);
            }
        });
    }
}
