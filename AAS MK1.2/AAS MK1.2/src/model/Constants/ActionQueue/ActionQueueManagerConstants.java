package model.Constants.ActionQueue;

/**
 * Interface containing all constants used in the ActionQueueManager class
 * @author Philipp Schulz
 */
public interface ActionQueueManagerConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    int ACTION_MANAGEMENT_MINIMUM_ID = 0;                           // minimum ID that is valid for actionManagement
    int ACTION_MANAGEMENT_WAIT_TIME = 10;                           // waiting time in [ms] until next check for actions
    int ACTION_MANAGEMENT_ERROR_COUNT_INCREMENT = 1;                // value by which the error counter should be incremented
    boolean INITIAL_STATE_MANAGE_ACTIONS = true;                    // default value for the field manageActions
    boolean INITIAL_STATE_AUTO_STOPPED = false;                     // default value for the field autoStopped
    boolean INITIAL_STATE_GLADOS_STOPPED = false;                   // default value for the field gladosStopped
    boolean INITIAL_STATE_SOUND_STOPPED = false;                    // default value for the field soundStopped
    boolean INITIAL_STATE_VOICE_RECOGNITION_STOPPED = false;        // default value for the field voiceRecognitionStopped
    String UPDATE_ID_ACTION = "UPDATE IDS";                         // action for updating the IDs of all components
    String CLASS_NAME_TERMINAL_MANAGER = "TerminalManager";         // name of the TerminalManager class
    String CLASS_NAME_SOUND_MANAGER = "SoundManager";               // name of the SoundManager class
    String BOOT_COMPLETE = "Initialization of AAS M K 1.2 complete."; // String that should be printed to the terminal after startup
    String SHUTDOWN_COMPLETE = "ActionQueueManager: All components are shut down. Good bye.";    // String that should be printed to the terminal before shutdown
    String DEVICE_MANAGER_NAME = "DeviceManager";                   // Name of the DeviceManager class
}
