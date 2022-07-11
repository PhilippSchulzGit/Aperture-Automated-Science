package model.Constants;

/**
 * Interface to store all constants within the ActionQueueSystem
 * @author Philipp Schulz
 */
public interface ActionHandlingConstants
{
    String UPDATE_OWN_ID = "UPDATE_ID";                             // action for updating the component ID
    String WRONG_COMPONENT_ID = "";                                 // action for handling a wrong component ID
    String SHUTDOWN_ACTION = "SHUTDOWN";                            // value for shutdown request for the field action
    String REBOOT_ACTION = "REBOOT";                                // value for reboot request for the field action
    String TURN_OFF_ACTION = "TURN_OFF";                            // value for turn off request for the field action
    String SHUTDOWN_CONFIRMATION = "SHUTDOWN_COMPLETE";             // confirmation String that is sent by other components to signal completed shutdown
    String SPACE = " ";                                             // String that contains a space character for action handling
    String EMPTY_STRING = "";                                       // empty String

    String ACTION_HANDLE_ACTION = "HANDLE ";                        // wildcard action for implementing different functionalities
    String ACTION_TERMINAL_MANAGER_PRINT = "PRINT ";                // String that indicates printing to the terminal
    String ACTION_VOICE_RECOGNITION_START = "START_VOICE_RECOGNITION";; // String that indicates the start of the voice recognition module
    String ACTION_VOICE_RECOGNITION_STOP = "STOP_VOICE_RECOGNITION";// String that indicates stopping the voice recognition module
    String ACTION_VOICE_RECOGNITION_ABORT = "ABORT_VOICE_RECOGNITION";// String that indicates that the current dialogue of the voice recognition module should be stopped
    String ACTION_DEVICE_MANAGER_OS = "OS";                         // Sting that indicates that the current OS is requested
    String ACTION_PLAY_SOUND_GLADOS = "PLAY_SOUND ";                // String that indicates that sound should be played with Glados voice
    String ACTION_PLAY_SOUND_AUTO = "PLAY_SOUND_AUTO";              // String that indicates that sound should be played with Autos voice
    String ACTION_VOICE_RECOGNITION_INPUT = "VOICE_RECOGNITION";    // String that indicates that an input was received via voice recognition
    String ACTION_TERMINAL_INPUT = "TERMINAL_INPUT";                // String that indicates that an input was received via the terminal


    boolean WAIT_FOR_EXECUTION_FALSE = false;                       // value if an action does not require the waitForExecution field
    boolean WAIT_FOR_EXECUTION_TRUE = true;                         // value if an action requires the waitForExecution field

    int ERROR_COUNT_NEW = 0;                                        // value for shutdown request for the field errorCount
    int ERROR_COUNT_INCREMENT = 1;                                  // value by which the error count should be incremented

    int SUBSTRING_OFFSET_1 = 1;                                     // offset of 1 used in substrings

    int INDEX_ZERO = 0;                                             // index of position 0 in Strings
    int INDEX_ONE = 1;                                              // index of position 1 in Strings
    int INDEX_TWO = 2;                                              // index of position 2 in Strings
    int INDEX_THREE = 3;                                            // index of position 3 in Strings
    int INDEX_FOUR = 4;                                             // index of position 4 in Strings
    int INDEX_FIVE = 5;                                             // index of position 5 in Strings
}
