package model.Sound;

import model.Constants.Sound.SoundConstants;
import model.Util.UtilityFunctions;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Class for sound output via Portal Radio
 * Only called once by SoundManager!
 * @author Philipp Schulz
 */
public class Sound implements SoundConstants
{
    // local fields
    private boolean playbackFinished;
    private boolean initialized;
    private volatile boolean isWindows;
    private volatile boolean alive;
    private final ArrayBlockingQueue<String> outputQueue;
    private final ArrayBlockingQueue<String> outputVoiceQueue;
    private Mixer mixer;
    private ArrayList<Clip> clips;
    private String autoSoundPath;
    private String gladosSoundPath;

    /**
     * constructor of the Sound class
     * @author Philipp Schulz
     */
    public Sound()
    {
        // initialize local fields
        this.playbackFinished = DISABLE_PLAYBACK;
        this.initialized = INITIALIZED_START;
        this.isWindows = IS_WINDOWS_START;
        this.alive = ALIVE_START;
        this.outputQueue = new ArrayBlockingQueue<>(QUEUE_LENGTH);
        this.outputVoiceQueue = new ArrayBlockingQueue<>(QUEUE_LENGTH);
        this.clips = new ArrayList<>();
        this.autoSoundPath = EMPTY_STRING;
        this.gladosSoundPath = EMPTY_STRING;
    }

    /**
     * Method for initializing sound system
     * @author Philipp Schulz
     */
    public void initialize()
    {
        // get information about all possible sound outputs
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        // check if program runs on a Raspberry Pi, does not need to be done on Windows
        if(this.gladosSoundPath.contains(PI_PATH))
        {
            // go over all entries of the mixerInfo array
            for (Mixer.Info info : mixerInfo)
            {
                // check if the correct output is found
                if (info.getDescription().equals(MIXER_PI_INFO))
                {
                    // assign mixer
                    this.mixer = AudioSystem.getMixer(info);
                    // break the loop
                    break;
                }
            }
        }
        // start the sound output thread
        this.playSound();
    }

    /**
     * Method for adding new text to the queue for sound output
     * @param source String that contains information about the voice to use
     * @param text String that contains the text/numbers that should be played via sound
     * @author Philipp Schulz
     */
    public void addTextToOutputQueue(String source, String text)
    {
        // add new text to output queue
        this.outputQueue.add(text);
        // handle source
        if(source.contains(GLADOS)) // voice for GLaDOS
        {
            // add voice to outputVoiceQueue
            this.outputVoiceQueue.add(GLADOS);
        }
        else if(source.contains(AUTO))  // voice for AUTO
        {
            // add voice to outputVoiceQueue
            this.outputVoiceQueue.add(AUTO);
        }
    }

    /**
     * Method for waiting a defined time interval
     * @param millis Waiting time in [ms]
     * @author Philipp Schulz
     */
    private void waitShortPeriod(int millis)
    {
        // handle exceptions
        try
        {
            // wait for specified time
            Thread.sleep(millis);
        }
        catch(Exception e)
        {
            // print out details to console
            e.printStackTrace();
        }
    }

    /**
     * Getter for the field playbackFinished
     * @return True if playback has finished
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
    private void setPlaybackFinished(boolean playbackFinished)
    {
        this.playbackFinished = playbackFinished;
    }

    /**
     * Method for clearing out the old word buffer
     * @author Philipp Schulz
     */
    private void refreshWordBuffer()
    {
        this.clips = new ArrayList<>();
    }

    /**
     * Method for creating a file based on the currently active clip
     * @param filePath String that contains the path to the file
     * @param fileName String that contains the name of the file
     * @return File pointing towards the required file of the current clip
     * @author Philipp Schulz
     */
    private AudioInputStream createAudioInputStream(String filePath, String fileName)
    {
        // initialize file name
        String fileNamePath = filePath+fileName+FILE_FORMAT;
        try
        {
            // create AudioInputStream from fileNamePath
            return AudioSystem.getAudioInputStream(new File(fileNamePath).toURI().toURL());
        }
        catch(Exception e)
        {
            // return null due to error
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method for playing sounds of a given message.
     * Starts a new thread that will run until field alive is reset
     * @author Philipp Schulz
     */
    public void playSound()
    {
        // create new thread for handling sound output
        Thread thread = new Thread(() -> {
            // enter temporary loop that waits for initialization to complete
            while(!initialized)
            {
                // sleep for specified time
                waitShortPeriod(LOOP_WAIT_TIME);
            }
            // enter main loop
            while(alive || !outputQueue.isEmpty())
            {
                // check if queue has elements
                if(!outputQueue.isEmpty())
                {
                    // set flag for other program parts
                    setPlaybackFinished(ENABLE_PLAYBACK);
                    // get current sentence to output
                    String sentence = outputQueue.poll();
                    // get current output voice
                    String outputVoice = outputVoiceQueue.poll();
                    // get correct path to sound files
                    String path = EMPTY_STRING;
                    assert outputVoice != null;
                    if(outputVoice.contains(AUTO))  // if voice is for AUTO
                    {
                        // set file path to AUTO
                        path = autoSoundPath;
                    }
                    else if(outputVoice.contains(GLADOS))   // if voice is for GLaDOS
                    {
                        // set file path to GLaDOS
                        path = gladosSoundPath;
                    }
                    // refresh word buffer and split sentence to single words
                    refreshWordBuffer();
                    ArrayList<String> words = UtilityFunctions.extractSingleWordsFromText(sentence.toLowerCase(Locale.ROOT));
                    // loop over all words and prepare all clips
                    for (String word : words)
                    {
                        // if this program runs on Windows
                        if(isWindows)
                        {
                            try
                            {
                                // create new clip for current word
                                Clip clip = AudioSystem.getClip();
                                // open current clip
                                clip.open(createAudioInputStream(path, word));
                                // add current clip to ArrayList
                                clips.add(clip);
                            }
                            catch (Exception e)  // print exception if the clip fails
                            {
                                e.printStackTrace();
                            }
                        }
                        else    // if this program runs on Linux
                        {
                            try
                            {
                                // create new clip for current word
                                Clip clip = (Clip) mixer.getLine(new DataLine.Info(Clip.class, null));
                                // open current clip
                                clip.open(createAudioInputStream(path, word));
                                // add current clip to ArrayList
                                clips.add(clip);
                            }
                            catch (Exception e)  // print exception if the clip fails
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    // loop over all clips
                    for(Clip clip : clips)
                    {
                        // start current clip
                        clip.start();
                        // wait until clip is finished
                        do
                        {
                            waitShortPeriod(WAITING_TIME);
                        }while(clip.isActive());
                        // close current clip
                        clip.stop();
                    }
                    // clear word buffer
                    refreshWordBuffer();
                    // wait for sentence spacer
                    waitShortPeriod(SENTENCE_SPACER);
                }
                // sleep for specified time
                waitShortPeriod(LOOP_WAIT_TIME);
                // reset flag for other program parts
                setPlaybackFinished(DISABLE_PLAYBACK);
            }
        });
        // start the tread
        thread.start();
    }

    /**
     * Setter for path to sound files for AUTO
     * @param autoSoundPath String that contains the complete path to the sound files for AUTO
     * @author Philipp Schulz
     */
    public void setAutoSoundPath(String autoSoundPath)
    {
        this.autoSoundPath = autoSoundPath;
    }

    /**
     * Setter for path to sound files for GLaDOS
     * @param gladosSoundPath String that contains the complete path to the sound files for GLaDOS
     * @author Philipp Schulz
     */
    public void setGladosSoundPath(String gladosSoundPath)
    {
        this.gladosSoundPath = gladosSoundPath;
        this.initialized = !INITIALIZED_START;
    }

    /**
     * Setter for field alive to stop sound playback
     * @param alive new state for field alive
     * @author Philipp Schulz
     */
    public void setAlive(boolean alive)
    {
        this.alive = alive;
    }

    /**
     * Setter for field isWindows to handle correct clip generation
     * @param isWindows new state for field isWindows
     * @author Philipp Schulz
     */
    public void setIsWindows(boolean isWindows)
    {
        this.isWindows = isWindows;
    }
}
