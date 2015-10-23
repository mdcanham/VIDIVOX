package com.vidivox.controller;

import com.vidivox.generators.FestivalSpeech;
import com.vidivox.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Text2SpeechDialogueController implements Initializable {

    private FestivalSpeech currentFestivalPreview = new FestivalSpeech("");

    @FXML
    private TextArea speechTextArea;

    @FXML
    private Button previewButton = new Button();

    @FXML
    private Button cancelPreviewButton = new Button();

    @FXML
    private Slider speedSelector = new Slider();

    @FXML
    private Label speedSelectorLabel = new Label();

    @FXML
    private Slider pitchSelector = new Slider();

    @FXML
    private Label pitchSelectorLabel = new Label();

    @FXML
    private Slider acrossUtteranceSelector = new Slider();

    @FXML
    private Label acrossUtteranceSelectorLabel = new Label();

    @FXML
    private ChoiceBox<String> presetSelector = new ChoiceBox<String>();

    @FXML
    private void handleSpeechPreviewButton(){
        if(currentFestivalPreview.isSpeaking()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Preview currently in progress.");
            alert.setContentText("Only one preview can be running at a time. Cancel the current preview to start again.");
            alert.showAndWait();
            return;
        }

        String textToSay = speechTextArea.getText();

        FestivalSpeech festival = new FestivalSpeech(textToSay);

        festival.speak();

        currentFestivalPreview = festival;
    }

    @FXML
    private void handleCancelPreviewButton(){
        currentFestivalPreview.stopSpeak();
    }

    @FXML
    private void handleSaveAudioToFileButton(){
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save speech to mp3 file");
        fileChooser.setInitialFileName("Dialogue.mp3");
        try {
            File file = fileChooser.showSaveDialog(new Stage());
            FestivalSpeech textToSpeak = new FestivalSpeech(speechTextArea.getText());
            textToSpeak.exportToMP3(file);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveAndAddToProjectButton(){


        //Select the location of the new audio file that will be created.
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save speech to mp3 file");
        fileChooser.setInitialFileName("Dialogue.mp3");

        File defaultLocation = new File("./audioFiles");
        defaultLocation.mkdirs();
        fileChooser.setInitialDirectory(defaultLocation);

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File newAudioFile = fileChooser.showSaveDialog(new Stage());

        FestivalSpeech text = new FestivalSpeech(speechTextArea.getText());
        text.exportToMP3(newAudioFile);

        Main.mainController.addAudioFile(newAudioFile);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initaliseSpeedSelector();
        initalisePitchSelector();
        initaliseAcrossUtteranceSelector();
        presetSelector.getItems().addAll("Male Happy Voice", "Male Normal Voice", "Male Sad Voice", "Female Happy Voice", "Female Normal Voice", "Female Sad Voice");
        presetSelector.getSelectionModel().select(1);
    }

    private void initaliseSpeedSelector(){
        speedSelector.minProperty().setValue(0.5);
        speedSelector.maxProperty().setValue(2);
        speedSelector.majorTickUnitProperty().setValue(0.5);
        speedSelector.setValue(1);

        speedSelectorLabel.setText("Speed (1.0x)");

        //Double click on the slider to reset it to 1
        speedSelector.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        speedSelector.setValue(1);
                    }
                }
            }
        });

        speedSelector.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String stringValue = String.format("Speed (%.1fx)", newValue);
                speedSelectorLabel.setText(stringValue);
            }
        });
    }

    private void initalisePitchSelector(){
        pitchSelector.minProperty().setValue(50);
        pitchSelector.maxProperty().setValue(300);
        pitchSelector.majorTickUnitProperty().setValue(50);
        pitchSelector.setValue(110);

        pitchSelectorLabel.setText("Pitch (110Hz)");

        pitchSelector.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        pitchSelector.setValue(110);
                    }
                }
            }
        });

        pitchSelector.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String stringValue = String.format("Pitch (%.0fHz)", newValue);
                pitchSelectorLabel.setText(stringValue);
            }
        });
    }

    private void initaliseAcrossUtteranceSelector(){
        acrossUtteranceSelector.minProperty().setValue(0);
        acrossUtteranceSelector.maxProperty().setValue(80);
        acrossUtteranceSelector.majorTickUnitProperty().setValue(20);
        acrossUtteranceSelector.setValue(20);

        acrossUtteranceSelectorLabel.setText("Across utterance range (20Hz)");

        acrossUtteranceSelector.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        acrossUtteranceSelector.setValue(20);
                    }
                }
            }
        });

        acrossUtteranceSelector.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String stringValue = String.format("Across utterance range (%.0fHz)", newValue);
                acrossUtteranceSelectorLabel.setText(stringValue);
            }
        });

    }

    private void initaliseFestivalSelectionBoxListeners(){

    }
}
