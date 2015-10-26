package com.vidivox.generator;

import java.io.File;

/**
 * A class that wraps audio files that are imported into the Vidivox system
 */
public class AudioDictation {
    public String name;
    public File location;
    public long inTime;

    public AudioDictation(File location){
        this(location, 0);
    }

    public AudioDictation(File location, long inTime){
        this.location = location;
        this.inTime = inTime;
        this.name = location.getName();
    }
}
