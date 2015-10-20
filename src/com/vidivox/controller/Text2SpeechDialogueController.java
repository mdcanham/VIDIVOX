package com.vidivox.controller;

import com.vidivox.Generators.FestivalSpeech;
import com.vidivox.Generators.VideoController;
import com.vidivox.Main;
import com.vidivox.view.WarningDialogue;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.lang.reflect.Array;

public class Text2SpeechDialogueController {

    private FestivalSpeech currentFestivalPreview = new FestivalSpeech("");

    @FXML
    private TextArea speechTextArea;

    @FXML
    private Button previewButton = new Button();

    @FXML
    private Button cancelPreviewButton = new Button();

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

}
