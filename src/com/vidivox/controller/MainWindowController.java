package com.vidivox.controller;

import com.vidivox.Generators.FestivalSpeech;
import com.vidivox.Generators.VideoController;
import com.vidivox.view.WarningDialogue;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindowController {

    private File currentVideoLocation;

    @FXML
    private MediaView mainMediaViewer = new MediaView();
    private MediaPlayer mainMediaPlayer;

    @FXML
    private TextArea mainSpeechTextArea = new TextArea();

    @FXML
    private ToolBar videoOptionBar = new ToolBar();

    @FXML
    private ToolBar speechOptionBar = new ToolBar();

    @FXML
    private MenuBar mainMenuBar = new MenuBar();

    @FXML
    private Slider mainProgressSlider;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private Slider mainVolumeSlider;

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
                initaliseResizeListener();
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
    private void handleSpeechPreviewButton(){
        String textToSay = mainSpeechTextArea.getText();

        FestivalSpeech festival = new FestivalSpeech(textToSay);

        festival.speak();
    }

    private void playFadingAnimation(FadeTransition t){
        //Reduces code duplication by moving this repeated code here.
        t.setFromValue(1.0);
        t.setToValue(0.0);
        t.playFromStart();
    }

    @FXML
    private void handleSaveAudioButton(){
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save speech to mp3 file");
        fileChooser.setInitialFileName("Dialogue.mp3");
        File file = fileChooser.showSaveDialog(new Stage());
        FestivalSpeech textToSpeak = new FestivalSpeech(mainSpeechTextArea.getText());
        textToSpeak.exportToMP3(file);
    }

    @FXML
    private void handleAddToVideoButton(){

        //Check if there is a video currently loaded
        if(currentVideoLocation == null){
            new WarningDialogue("You must open a video from the file menu before you can add speech to it.");
            return;
        }

        //Select the location of the new video that will be created.
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save video with speech");
        fileChooser.setInitialFileName("My_New_Video.mp4");
        File newVideoFile = fileChooser.showSaveDialog(new Stage());

        //Create new audio file from text in the textbox and export it to mp3.
        //Save the location where this is saved as a file.
        File audioFile = new File("temp/tempAudioFile.mp3");

        FestivalSpeech text = new FestivalSpeech(mainSpeechTextArea.getText());
        text.exportToMP3(audioFile);

        //Create new video controller class with the current video
        VideoController vc = new VideoController(currentVideoLocation);

        //Call the mergeAudio() method
        vc.mergeAudio(audioFile, newVideoFile);

        new WarningDialogue("Great, you will now need to open the new file that you saved from the file menu.");


    }

    @FXML
    private void handleMouseMoved() {
        try {
            FadeTransition menuFT = new FadeTransition(Duration.millis(10000), mainMenuBar);
            FadeTransition videoFT = new FadeTransition(Duration.millis(10000), videoOptionBar);
            FadeTransition sliderFT = new FadeTransition(Duration.millis(10000), mainProgressSlider);
            if (mainMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                playFadingAnimation(menuFT);
                playFadingAnimation(videoFT);
                playFadingAnimation(sliderFT);
            } else {
                menuFT.stop();
                videoFT.stop();
                sliderFT.stop();
                mainProgressSlider.setOpacity(1.0);
                videoOptionBar.setOpacity(1.0);
                mainMenuBar.setOpacity(1.0);
        }
        }catch(NullPointerException e){
            //This means that no video is playing, so no MediaPlayer has been created yet.
            //Nothing has to be done here; this catch block is just to suppress error messages.
        }
    }

    @FXML
    private void handleAddSpeechButton() {
        if (speechOptionBar.isVisible()){
            return;
        }
        speechOptionBar.setVisible(true);
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        //Can't use the normal method here, its a fade in, not a fade out.
        speechFT.setFromValue(0.0);
        speechFT.setToValue(1.0);
        speechFT.playFromStart();
    }

    @FXML
    private void handleCloseButton() {
        if (!speechOptionBar.isVisible()){
            return;
        }
        FadeTransition speechFT = new FadeTransition(Duration.millis(100), speechOptionBar);
        playFadingAnimation(speechFT);
        speechFT.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                speechOptionBar.setVisible(false);
            }
        });
    }

    private void initalisePlayEnvironment(){

        mainMediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                mainProgressSlider.setValue(0);
                mainProgressSlider.setMin(0);
                mainProgressSlider.setMax(mainMediaPlayer.getTotalDuration().toMillis());

                //Add a timer to check the current position of the video
                TimerTask updateSliderPosition = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mainProgressSlider.setValue(mainMediaPlayer.getCurrentTime().toMillis());
                        } catch (NullPointerException e){
                            //This likely means that the window was closed while the video was open.
                            this.cancel();
                        }
                    }
                };
                final Timer durationTimer = new Timer();
                durationTimer.schedule(updateSliderPosition, 0, 100);

                //Listen for changes made to the progress slider by the user
                mainProgressSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

                        //Add a threshold that will stop the video skipping when the timer updates the slider position
                        if (Math.abs((double) oldValue - (double) newValue) > 150) {
                            mainMediaPlayer.seek(new Duration((Double) newValue));
                        }

                    }
                });

                //Set up volume slider
                mainVolumeSlider.setMax(1);
                mainVolumeSlider.setValue(1);
                mainMediaPlayer.setVolume(mainVolumeSlider.getValue());
                mainVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        mainMediaPlayer.setVolume((double)newValue);
                    }
                });
            }
        });
    }

    private void initaliseResizeListener(){
        //Sets MediaViewer to the size of the window on launch.
        mainMediaViewer.setFitWidth(mainWindow.getScene().getWidth());
        mainMediaViewer.setFitHeight(mainWindow.getScene().getHeight());
        //Listen for changes in the scene's width, and change the mediaview accordingly.
        mainWindow.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mainMediaViewer.setFitWidth(mainWindow.getScene().getWidth());
            }
        });
        //Listen for changes in the scene's height, and change the mediaview accordingly.
        mainWindow.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mainMediaViewer.setFitHeight(mainWindow.getScene().getHeight());
            }
        });
    }
}
