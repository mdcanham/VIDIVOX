package com.vidivox.controller;

import com.vidivox.generator.AudioDictation;
import com.vidivox.Main;
import com.vidivox.generator.FestivalSpeech;
import com.vidivox.taskThreads.RenderVideoTask;
import com.vidivox.view.WarningDialogue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class MainStageController implements Initializable {

    public static File currentVideoLocation;
    public static File originalVideoLocation;

    public static ObservableList<AudioDictation> audioItems = FXCollections.observableArrayList();

    @FXML
    public MediaView mainMediaViewer = new MediaView();
    private MediaPlayer mainMediaPlayer;

    @FXML
    private Button playPauseButton = new Button();

    @FXML
    private Button stopButton = new Button();

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
    private ListView<AudioDictation> audioList = new ListView<>();

    @FXML
    private CheckBox removeOriginalAudioCheckbox;

    @FXML
    private TextField inTimeTextField = new TextField();

    @FXML
    private Button applyChangesButton = new Button();

    @FXML
    private Label leftActivityInfo = new Label();

    @FXML
    private Label rightActivityInfo = new Label();

    @FXML
    private ProgressBar leftProgressBar = new ProgressBar();

    @FXML
    private Pane mediaContainer = new Pane();

    @FXML
    private Button currentTimeButton = new Button();

    @FXML
    private Button removeSelectedAudioButton = new Button();

    @FXML
    private Button exportAsMP4Button = new Button();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initaliseAudioList();
        initaliseButtons();
        mainMediaViewer.fitWidthProperty().bind(mediaContainer.widthProperty());
        mainMediaViewer.fitHeightProperty().bind(mediaContainer.heightProperty());

        inTimeTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                audioList.getSelectionModel().selectedItemProperty().getValue().inTime = Long.parseLong(newValue);
            }
        });
    }

    private void initaliseAudioList(){
        audioList.setCellFactory(new Callback<ListView<AudioDictation>, ListCell<AudioDictation>>() {
            @Override
            public ListCell<AudioDictation> call(ListView<AudioDictation> param) {
                ListCell<AudioDictation> audio = new ListCell<AudioDictation>() {

                    @Override
                    protected void updateItem(AudioDictation item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.name);
                        } else {
                            setText("");
                        }
                    }
                };
                return audio;
            }
        });

        audioList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AudioDictation>() {
            @Override
            public void changed(ObservableValue<? extends AudioDictation> observable, AudioDictation oldValue, AudioDictation newValue) {
                if(newValue != null) {
                    inTimeTextField.setText(String.valueOf(newValue.inTime));
                }
            }
        });
    }

    private void initaliseButtons(){
        applyChangesButton.setDisable(true);
        playPauseButton.setDisable(true);
        playPauseButton.getStyleClass().add("play");
        stopButton.setDisable(true);
        mainProgressSlider.setDisable(true);
        mainVolumeSlider.setDisable(true);
        currentTimeButton.setDisable(true);
        removeOriginalAudioCheckbox.setDisable(true);
        removeSelectedAudioButton.setDisable(true);
    }

    @FXML
    private void handleQuitButton(){
        Main.stage.close();
    }

    @FXML
    private void removeSelectedAudioButtonHandler(){
        audioItems.remove(audioList.getSelectionModel().getSelectedItem());
        audioList.setItems(audioItems);
    }

    @FXML
    private void handleAboutVidivoxButton(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vidivox/view/AboutVidivox.fxml"));

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

    @FXML
    private void handleExportAsMP4Button(){
        if(currentVideoLocation != null){

            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save video as MP4");
            fileChooser.setInitialFileName("MyVidivoxVideo.mp4");
            try {
                File outputFileLocation = fileChooser.showSaveDialog(new Stage());

                final File videoTempLocation = new File("/tmp/temporaryRender.mp4");

                RenderVideoTask renderVideoTask = new RenderVideoTask(originalVideoLocation, outputFileLocation, removeOriginalAudioCheckbox.isSelected(), audioItems);

                renderVideoTask.setOnScheduled(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        leftActivityInfo.setText("Exporting video as MP4");
                        leftProgressBar.setVisible(true);
                    }
                });

                renderVideoTask.progressProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        leftProgressBar.setProgress((double) newValue);
                    }
                });

                renderVideoTask.messageProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        rightActivityInfo.setText(newValue);
                    }
                });

                renderVideoTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        leftActivityInfo.setText("Video Exported as MP4.");
                        rightActivityInfo.setText("");
                        leftProgressBar.setVisible(false);
                        applyChangesButton.setDisable(false);


                    }
                });

                renderVideoTask.restart();

            } catch (NullPointerException e){
                e.printStackTrace();
            }
        } else {
            new WarningDialogue("You need to open a video before you can export it as MP4.");
        }
    }

    @FXML
    private void handleApplyChangesButton(){
        final File videoTempLocation = new File("/tmp/temporaryRender.mp4");
        RenderVideoTask renderVideoTask = new RenderVideoTask(originalVideoLocation, videoTempLocation, removeOriginalAudioCheckbox.isSelected(), audioItems);

        renderVideoTask.setOnScheduled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                leftActivityInfo.setText("Applying changes to video");
                leftProgressBar.setVisible(true);
                applyChangesButton.setDisable(true);
            }
        });

        renderVideoTask.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                leftProgressBar.setProgress((double) newValue);
            }
        });

        renderVideoTask.messageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                rightActivityInfo.setText(newValue);
            }
        });

        renderVideoTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                openNewVideo(videoTempLocation);
                mainMediaPlayer.play();

                leftActivityInfo.setText("Changes applied");
                rightActivityInfo.setText("");
                leftProgressBar.setVisible(false);
                applyChangesButton.setDisable(false);


            }
        });

        renderVideoTask.restart();

    }

    @FXML
    private void handleOpenVideoButton(){
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Video Files", "*.mp4");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        originalVideoLocation = originalVideoLocation == null ? file : originalVideoLocation;
        openNewVideo(file);

        //Remove the "Import a media track..." background
        mediaContainer.getStyleClass().removeAll("init");
    }

    private void openNewVideo(File file) {
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

    public void addAudioFile(File file){
        if(file != null){
            AudioDictation audio = new AudioDictation(file);
            audioItems.add(audio);
            removeSelectedAudioButton.setDisable(false);
            audioList.setItems(audioItems);
            audioList.getSelectionModel().select(audio);
        }
    }

    @FXML
    private void handleImportVideoFromFileButton(){
        initaliseAudioList();
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        addAudioFile(file);
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("No video open");
            alert.setContentText("You need to have opened a video before you can play it. Please go to the file menu and click on the open video option.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleStopButton(){
        try {
            mainMediaPlayer.stop();
        } catch (NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("No video open");
            alert.setContentText("You need to have opened a video before you can stop it. Please go to the file menu and click on the open video option.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleTextToSpeechButton(){
        initaliseAudioList();
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

    @FXML
    private void handleSetAsCurrentTimeButton(){
        long currentTime = Math.round(mainMediaPlayer.getCurrentTime().toMillis());
        inTimeTextField.setText(String.valueOf(currentTime));
    }

    private void initalisePlayEnvironment(){

        mainMediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                mainProgressSlider.setValue(0);
                mainProgressSlider.setMin(0);
                mainProgressSlider.setMax(mainMediaPlayer.getTotalDuration().toMillis());

                playPauseButton.setDisable(false);
                stopButton.setDisable(false);
                mainVolumeSlider.setDisable(false);
                mainProgressSlider.setDisable(false);
                currentTimeButton.setDisable(false);
                applyChangesButton.setDisable(false);
                removeOriginalAudioCheckbox.setDisable(false);

                playPauseButton.getStyleClass().removeAll("pause");
                playPauseButton.getStyleClass().add("play");

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

                mainMediaPlayer.setOnPlaying(new Runnable() {
                    @Override
                    public void run() {
                        playPauseButton.getStyleClass().removeAll("play");
                        playPauseButton.getStyleClass().add("pause");
                    }
                });

                mainMediaPlayer.setOnPaused(new Runnable() {
                    @Override
                    public void run() {
                        playPauseButton.getStyleClass().removeAll("pause");
                        playPauseButton.getStyleClass().add("play");
                    }
                });

                mainMediaPlayer.setOnStopped(new Runnable() {
                    @Override
                    public void run() {
                        playPauseButton.getStyleClass().removeAll("pause");
                        playPauseButton.getStyleClass().add("play");
                    }
                });

                mainMediaPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        mainMediaPlayer.stop();
                        playPauseButton.getStyleClass().removeAll("pause");
                        playPauseButton.getStyleClass().add("play");
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
