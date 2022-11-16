package model.VoiceRecognition;

import edu.cmu.sphinx.api.SpeechResult;
import model.Constants.VoiceRecognition.VoiceRecognitionCMUConstants;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import java.util.concurrent.TimeUnit;

/**
 * Class for voice recognition via CMUSphinx
 * Only called once by VoiceRecognitionManager!
 * @author Philipp Schulz
 */
public class VoiceRecognitionCMU implements VoiceRecognitionCMUConstants
{
    // local fields
    private boolean isRunning;
    private String os;
    private LiveSpeechRecognizer recognizer;

    /**
     * Constructor of the VoiceRecognitionCMU class
     * @author Philipp Schulz
     */
    public VoiceRecognitionCMU()
    {
        // initialize local fields
        this.isRunning = INITIAL_STATE_IS_RUNNING;
        this.recognizer = null;
        this.os = INITIAL_STATE_OS;
    }

    /**
     * Method for creating the recognizer, done separately from the constructor
     * @param OS String that contains information about the OS "pi", "windows", ...
     * @return True if the recognizer was created
     * @author Philipp Schulz
     */
    public boolean createRecognizer(String OS)
    {
        // save os information
        this.os = OS;
        // disable logging from voice recognition
        System.setProperty(LOGGER_PROPERTY1, LOGGER_PROPERTY2);
        // set up the Sphinx4 configuration
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(SPHINX4_ACOUSTIC_MODEL_PATH);
        configuration.setDictionaryPath(SPHINX4_DICTIONARY_PATH);
        configuration.setLanguageModelPath(SPHINX4_LANGUAGE_MODEL_PATH);
        // set the name of the grammar file
        configuration.setGrammarName(SPHINX4_GRAMMAR_NAME);
        // indicate that the grammar should be used
        configuration.setUseGrammar(SPHINX4_USE_GRAMMAR);

        // handle different parts due to OS
        if (this.os.equals(OS_LINUX))    // if the program runs on Linux (aka on a Pi)
        {
            // set grammar path for Pi
            configuration.setGrammarPath(SPHINX4_GRAMMAR_PATH_PI);
        } else if (this.os.equals(OS_WINDOWS))    // if the program runs on Windows
        {
            // set grammar path for Windows
            configuration.setGrammarPath(FILE + System.getProperty(SPHINX4_USER_DIR) + SPHINX4_GRAMMAR_PATH_WINDOWS);
        }
        // create recognizer
        try
        {
            this.recognizer = new LiveSpeechRecognizer(configuration);
            // start the recognition
            setIsRunning(RECOGNITION_START);
            return RECOGNIZER_CREATION_SUCCESS;
        }
        catch(Exception e)
        {
            // errors should not occur, but if they do, print them to the terminal
            e.printStackTrace();
            return RECOGNIZER_CREATION_FAILED;
        }
    }

    /**
     * Method for getting the current state of the isRunning field
     * @return Current value of isRunning (true=is running)
     * @author Philipp Schulz
     */
    public boolean getIsRunning()
    {
        return this.isRunning;
    }

    /**
     * Method for setting a new value of the isRunning field
     * @param isRunning New value for the isRunning field
     * @author Philipp Schulz
     */
    public void setIsRunning(boolean isRunning)
    {
        // handle new state of isRunning for recognizer
        if(isRunning && !this.isRunning)
        {
            // catch errors in case recognizer already runs
            try
            {
                // start the recognition
                recognizer.startRecognition(CLEAR_RECOGNIZER);
            }
            catch(Exception ignored)
            {
                // exception can be ignored
            }
        }
        else if(!isRunning && this.isRunning)
        {
            // stop the recognition
            boolean flag = RECOGNITION_START;
            // loop until recognition is no longer busy
            while(flag)
            {
                // catch any errors
                try
                {
                    // stop the recognition module and set the flag
                    recognizer.stopRecognition();
                    flag = RECOGNITION_STOP;
                }
                catch(NullPointerException e)
                {
                    flag = RECOGNITION_STOP;
                }
                catch(Exception ignored)
                {

                }
            }
        }
        // save new state of isRunning
        this.isRunning = isRunning;
    }

    /**
     * Method for using the CMU Sphinx library to recognize a single result
     * @return String of the next voice recognition result
     * @author Philipp Schulz
     */
    public String recognize()
    {
        // only recognize when it should do so
        if(isRunning)
        {
            // get the result from the recognizer
            try
            {
                // get the recognition result
                SpeechResult result = recognizer.getResult();
                // return the best final result without filler
                return result.getResult().getBestFinalResultNoFiller();
            }
            catch(IllegalStateException e)
            {
                // in case the recognizer is not ready for recognition
                try
                {
                    TimeUnit.MILLISECONDS.sleep(RECOGNIZE_TIMEOUT);
                }
                catch(Exception ignored)
                {

                }
                return EMPTY_RESULT;
            }
        }
        else
        {
            // return an empty String
            return EMPTY_RESULT;
        }
    }
}
