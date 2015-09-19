package com.vidivox;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainWindowController {

    @FXML
    private MediaView mainMediaViewer = new MediaView();

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

}
