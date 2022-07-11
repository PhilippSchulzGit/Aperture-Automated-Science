package model.VoiceRecognition;

import model.Constants.VoiceRecognition.VoiceRecognitionConstants;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;


/**
 * Class for voice recognition via CMUSphinx
 * Only called once by VoiceRecognitionManager!
 * @author Philipp Schulz
 */
public class VoiceRecognitionCMU implements VoiceRecognitionConstants
{
    // local fields
    private boolean isRunning;
    private Configuration configuration;
    private LiveSpeechRecognizer recognizer;

    /**
     * Constructor of the VoiceRecognitionCMU class
     * @author Philipp Schulz
     */
    public VoiceRecognitionCMU()
    {
        //TODO: IMPLEMENT METHOD

        // initialize local fields
        this.isRunning = false;
        this.configuration = new Configuration();
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
        this.isRunning = isRunning;
    }

    /**
     * Method for using the CMU Sphinx library to recognize a single result
     * @return String of the next voice recognition result
     * @author Philipp Schulz
     */
    public String recognize()
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }
}
