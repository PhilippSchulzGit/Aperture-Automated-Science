package model.Constants.VoiceRecognition;

/**
 * Interface containing all constants used in the VoiceRecognitionManager class
 * @author Philipp Schulz
 */
public interface VoiceRecognitionManagerConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    int INITIAL_STATE_RECEIVER_ID = -1;                             // default value for the field receiverID
    boolean INITIAL_STATE_VOICE_RECOGNITION_SET_UP = false;         // default value for the field voiceRecognitionSetUp
    boolean INITIAL_STATE_IS_ALIVE = true;                          // default value for the field isAlive
    boolean INITIAL_STATE_DIALOGUE_GLADOS = false;                  // default value for the field dialogueGlados
    boolean INITIAL_STATE_DIALOGUE_AUTO = false;                    // default value for the field dialogueAuto

    int WAIT_TIME = 100;                                            // time in [ms] to wait before next loop when setting up voice recognition

    boolean VOICE_RECOGNITION_START = true;                         // new value for flag to start the voice recognition
    boolean VOICE_RECOGNITION_STOP = false;                         // new value for flag to stop the voice recognition
    boolean KILL_VOICE_RECOGNITION = false;                         // value for the field isAlive when a complete shutdown is requested

    String ERROR_RECOGNIZER_CREATION = "could not create recognizer."; // String to indicate that the recognizer creation failed
    String DEVICE_MANAGER_NAME = "DeviceManager";                   // name of the component DeviceManager
    String TERMINAL_MANAGER_NAME = "TerminalManager";               // name of the component TerminalManager
    String SOUND_OUTPUT_NAME = "SoundManager";                      // name of the component SoundManager
    String GLADOS_NAME = "Glados";                                  // name of the component Glados
    String AUTO_NAME = "Auto";                                      // name of the component Auto

    String RECOGNIZER_CREATION_SUCCESS = "Voice Recognition ready.";// print to the terminal to indicate that the voice recognition module is ready
    String RECOGNITION_START = "Voice Recognition started.";        // print to the terminal to indicate that the voice recognition is started
    String RECOGNITION_STOP = "Voice Recognition stopped.";         // print to the terminal to indicate that the voice recognition is stopped

    boolean DIALOGUE_ACTIVE = true;                                 // state of the field dialogueActive when a dialogue is active
    boolean DIALOGUE_INACTIVE = false;                              // state of the field dialogueActive when a dialogue is not active

    String RECOGNITION_GLADOS = "lettuce";                          // String for directing a dialogue towards Glados
    String RECOGNITION_GLADOS_2 = "glados";                         // String for directing a dialogue towards Glados
    String RECOGNITION_AUTO = "auto";                               // String for directing a dialogue towards Auto
    String RECOGNITION_ABORT = "abort dialogue";                    // String for aborting a currently active dialogue

    String RECOGNITION_UNKNOWN = "<unk>";                           // String to decide if the recognition could recognize something
}
