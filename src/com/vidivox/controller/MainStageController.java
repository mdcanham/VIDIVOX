package com.vidivox.controller;

import com.vidivox.view.WarningDialogue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class MainStageController {

    private File currentVideoLocation;

    @FXML
    public MediaView mainMediaViewer = new MediaView();
    private MediaPlayer mainMediaPlayer;

    @FXML
    private ToolBar videoOptionBar = new ToolBar();

    @FXML
    private MenuBar mainMenuBar = new MenuBar();

    @FXML
    private Slider mainProgressSlider;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private Slider mainVolumeSlider;

    @FXML
    private Label mainTimeLabel;

    @FXML
    private void handleOpenVideoButton(){
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        openNewVideo(file);
    }

    private void openNewVideo(File file){
        if (file != null) {
            try {

                //Get rid of the current video that is playing if there is one
                if(mainMediaPlayer != null){
                    mainMediaPlayer.dispose();
                }

                currentVideoLocation = file;
                mainMediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
                mainMediaViewer.setMediaPlayer(mainMediaPlayer);
                initalisePlayEnvironment();

            } catch(MediaException e) {
                if( e.getType() == MediaException.Type.MEDIA_UNSUPPORTED ){
                    new WarningDialogue("Sorry, we didn't recognise that file type. Currently VIDIVOX supports MP4 files.");
                }
            }
        }
    }

    @FXML
    private void handlePlayPauseButton(){
        try {
            if(mainMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                mainMediaPlayer.pause();
            } else {
                mainMediaPlayer.play();
            }
        } catch (NullPointerException e){
            new WarningDialogue("You need to open a video file before you can play anything");
        }
    }

    @FXML
    private void handleTextToSpeechButton(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vidivox/view/Text2SpeechDialogue.fxml"));

            BorderPane pane = loader.load();
            Scene scene = new Scene(pane);

            final Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    private void initalisePlayEnvironment(){

        mainMediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                mainProgressSlider.setValue(0);
                mainProgressSlider.setMin(0);
                mainProgressSlider.setMax(mainMediaPlayer.getTotalDuration().toMillis());

                //Add a listener to check the value of the slider and update the media element accordingly
                mainProgressSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        if (mainProgressSlider.isValueChanging()) {
                            mainMediaPlayer.seek(new Duration((Double) newValue));
                        } else if (Math.abs((Double) oldValue - (Double) newValue) > 200) {
                            mainMediaPlayer.seek(new Duration((Double) newValue));
                        }

                    }
                });

                //Add a listener to check the current time of the video and update the slider position accordingly
                mainMediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                        mainProgressSlider.setValue(newValue.toMillis());
                        Double seconds = mainMediaPlayer.getCurrentTime().toSeconds();
                        int hours = (int) Math.floor(seconds / 3600);
                        seconds -= hours * 3600;
                        int minutes = (int) Math.floor(seconds / 60);
                        seconds -= minutes * 60;

                        int secondsInt = (int) Math.floor(seconds);

                        mainTimeLabel.setText(String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", secondsInt));
                    }
                });

                //Set up volume slider
                mainVolumeSlider.setMax(1);
                mainVolumeSlider.setValue(1);
                mainMediaPlayer.setVolume(mainVolumeSlider.getValue());
                mainVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        mainMediaPlayer.setVolume((double) newValue);
                    }
                });
            }
        });
    }
}
