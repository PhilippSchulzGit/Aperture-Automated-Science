package model.Sound;

import model.Constants.Sound.SoundConstants;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Mixer;
import java.io.File;
import java.util.ArrayList;

/**
 * Class for sound output via Portal Radio
 * Only called once by SoundManager!
 * @author Philipp Schulz
 */
public class Sound implements SoundConstants
{
    // local fields
    private boolean playbackFinished;
    private ArrayList<String> singleWords;
    private Mixer mixer;
    private Clip clip;
    private AudioInputStream audioIn;

    /**
     * constructor of the Sound class
     * @author Philipp Schulz
     */
    public Sound()
    {
        // initialize local fields
        this.playbackFinished = true;
        this.singleWords = new ArrayList<>();
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for waiting until the currently played sounds has finished
     * @author Philipp Schulz
     */
    public void waitForSoundFinished()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for waiting a defined time interval
     * @author Philipp Schulz
     */
    public void waitShortPeriod()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for waiting until the currently played clip has finished
     * @author Philipp Schulz
     */
    public void waitForClipFinished()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for getting the current state of playbackFinished
     * @return True if the playback of all scheduled sounds finished
     * @author Philipp Schulz
     */
    public boolean getPlaybackFinished()
    {
        return this.playbackFinished;
    }

    /**
     * Method for setting the current state of playbackFinished
     * @param playbackFinished New state for the playbackFinished field
     * @author Philipp Schulz
     */
    public void setPlaybackFinished(boolean playbackFinished)
    {
        this.playbackFinished = playbackFinished;
    }

    /**
     * Method for clearing out the old word buffer
     * @author Philipp Schulz
     */
    public void refreshWordBuffer()
    {
        this.singleWords = new ArrayList<>();
    }

    /**
     * Method for creating a file based on the currently active clip
     * @return File pointing towards the required file of the current clip
     * @author Philipp Schulz
     */
    public File createClipFile()
    {
        //TODO: IMPLEMENT METHOD
        return new File("");
    }

    /**
     * Method for playing sounds of a given message
     * @author Philipp Schulz
     */
    public void playSound(String message)
    {
        //TODO: IMPLEMENT METHOD
    }
}
