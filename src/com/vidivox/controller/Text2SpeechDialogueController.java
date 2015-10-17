package com.vidivox.controller;

import com.vidivox.Generators.FestivalSpeech;
import com.vidivox.Generators.VideoController;
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

/**
 * Created by matthewcanham on 17/10/15.
 */
public class Text2SpeechDialogueController {

    @FXML
    private TextArea speechTextArea;

    @FXML
    private void handleSpeechPreviewButton(){
        String textToSay = speechTextArea.getText();

        FestivalSpeech festival = new FestivalSpeech(textToSay);

        festival.speak();
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

//    @FXML
//    private void handleAddToProjectButton(){
//
//        //Check if there is a video currently loaded
//        if(currentVideoLocation == null){
//            new WarningDialogue("You must open a video from the file menu before you can add speech to it.");
//            return;
//        }
//
//        //Select the location of the new video that will be created.
//        final FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Save video with speech");
//        fileChooser.setInitialFileName("My_New_Video.mp4");
//        File newVideoFile = fileChooser.showSaveDialog(new Stage());
//
//        //Create new audio file from text in the textbox and export it to mp3.
//        //Save the location where this is saved as a file.
//        File audioFile = new File("temp/tempAudioFile.mp3");
//
//        FestivalSpeech text = new FestivalSpeech(speechTextArea.getText());
//        text.exportToMP3(audioFile);
//
//        //Create new video controller class with the current video
//        VideoController vc = new VideoController(currentVideoLocation);
//
//        //Call the mergeAudio() method
//        vc.mergeAudio(audioFile, newVideoFile);
//
//        new WarningDialogue("Great, you will now need to open the new file that you saved from the file menu.");
//
//
//    }

}
