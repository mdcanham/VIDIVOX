package com.vidivox.Generators;

import com.sun.javafx.tk.Toolkit;
import com.vidivox.view.WarningDialogue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * A class that takes care of rendering the final video with audio merged in at the correct times.
 */
public class VideoController {

    File videoFile;
    Boolean mute;
    ObservableList<AudioDictation> audioDictations;

    public VideoController(File videoFile, Boolean mute, ObservableList<AudioDictation> audioDictations){
        this.videoFile = videoFile;
        this.mute = mute;
        this.audioDictations = audioDictations;
    }

    public void renderVideo(){

        try {
            //Determine the path of the video file
            String newVideoFilePath = videoFile.toURI().toURL().getPath();
            newVideoFilePath = newVideoFilePath.replace("%20", "\\ ");

            //Extract the audio from the original video
            String process = "ffmpeg -y -i " + newVideoFilePath + " -preset ultrafast -vn /tmp/tempAudio.mp3";
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", process);
            pb.start().waitFor();

            //Strip the audio from the video
            process = "ffmpeg -y -i " + newVideoFilePath + " -preset ultrafast -an /tmp/tempVideoNoSound.mp4";
            pb = new ProcessBuilder("/bin/sh", "-c", process);
            pb.start().waitFor();

            //Merge together the different audio inputs
            StringBuilder ffmpegInputs = new StringBuilder();
            int inputCount = 0;
            if(!mute){
                ffmpegInputs.append(" -i /tmp/tempAudio.mp3");
                inputCount++;
            }
            Iterator<AudioDictation> it = audioDictations.iterator();
            while(it.hasNext()){
                AudioDictation currentDictation = it.next();
                ffmpegInputs.append(" -itsoffset " + currentDictation.inTime / 1000 + " -i " + currentDictation.location.toURI().toURL().getPath().replace("%20", "\\ "));
                inputCount++;
            }

            //Merge the audio inputs into one single file
            process = "ffmpeg -y" + ffmpegInputs.toString() + " -preset ultrafast -filter_complex amix=inputs=" + inputCount + " -async 1 /tmp/tempFullOutput.mp3";
            pb = new ProcessBuilder("/bin/sh", "-c", process);
            pb.start().waitFor();

            //Finally merge together the audio and the video
            process = "ffmpeg -y -i /tmp/tempVideoNoSound.mp4 -i /tmp/tempFullOutput.mp3 -strict -2 -b:a 32k -preset ultrafast -filter_complex \"[1:0] apad\" -shortest " + newVideoFilePath;
            pb = new ProcessBuilder("/bin/sh", "-c", process);
            pb.start().waitFor();


        } catch (IOException e){
            WarningDialogue.genericError(e.getMessage());
        } catch (InterruptedException e) {
            WarningDialogue.genericError(e.getMessage());
        }
    }
}
