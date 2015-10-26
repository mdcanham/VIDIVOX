package com.vidivox.taskThreads;

import com.vidivox.generator.AudioDictation;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.util.Iterator;

/**
 * Render out the final video on a seperate thread
 */
public class RenderVideoTask extends Service<Void> {

    File inputVideoFile;
    File outputVideoFile;
    Boolean mute;
    ObservableList<AudioDictation> audioDictations;

    public RenderVideoTask(File inputVideoFile, File outputVideoFile, Boolean mute, ObservableList<AudioDictation> audioDictations){
        this.inputVideoFile = inputVideoFile;
        this.outputVideoFile = outputVideoFile;
        this.mute = mute;
        this.audioDictations = audioDictations;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //Determine the path of the video file
                String newVideoFilePath = inputVideoFile.toURI().toURL().getPath();
                newVideoFilePath = newVideoFilePath.replace("%20", "\\ ");

                updateMessage("Extracting audio from original video");
                updateProgress(0, 100);

                //Extract the audio from the original video
                String process = "ffmpeg -y -i " + newVideoFilePath + " -vn /tmp/originalAudio.mp3";
                ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", process);
                pb.start().waitFor();

                updateMessage("Removing audio from original video");
                updateProgress(20, 100);

                //Strip the audio from the video
                process = "ffmpeg -y -i " + newVideoFilePath + " -an -vcodec copy /tmp/tempVideoNoSound.mp4";
                pb = new ProcessBuilder("/bin/sh", "-c", process);
                pb.start().waitFor();

                updateMessage("Merging audio sources");
                updateProgress(40, 100);

                //Merge together the different audio inputs
                StringBuilder ffmpegInputs = new StringBuilder();
                int inputCount = 0;
                if (!mute) {
                    ffmpegInputs.append(" -i /tmp/originalAudio.mp3");
                    inputCount++;
                }
                Iterator<AudioDictation> it = audioDictations.iterator();
                while (it.hasNext()) {
                    AudioDictation currentDictation = it.next();
                    ffmpegInputs.append(" -itsoffset " + currentDictation.inTime / 1000 + " -i " + currentDictation.location.toURI().toURL().getPath().replace("%20", "\\ "));
                    inputCount++;
                }

                //Merge the audio inputs into one single file
                process = "ffmpeg -y" + ffmpegInputs.toString() + " -filter_complex amix=inputs=" + inputCount + " -ac 2 -ab 128k -async 1 /tmp/tempFullOutput.mp3";
                pb = new ProcessBuilder("/bin/sh", "-c", process);
                pb.start().waitFor();

                updateMessage("Merging final video and final audio");
                updateProgress(80, 100);

                //Finally merge together the audio and the video
                process = "ffmpeg -y -i /tmp/tempVideoNoSound.mp4 -i /tmp/tempFullOutput.mp3 -strict experimental -acodec aac -b:a 32k -vcodec copy -filter_complex \"[1:0]apad\" -shortest " + outputVideoFile.toURI().toURL().getPath().replace("%20", "\\ ");
                pb = new ProcessBuilder("/bin/sh", "-c", process);
                pb.start().waitFor();

                updateProgress(100, 100);
                succeeded();

                return null;
            }
        };
    }
}
