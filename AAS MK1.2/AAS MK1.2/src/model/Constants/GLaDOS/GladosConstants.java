package model.Constants.GLaDOS;

/**
 * Interface containing all constants used in the GLaDOS class
 * @author Philipp Schulz
 */
public interface GladosConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    int SECOND_INDEX = 2;                                           // index to start when searching for component id
    int THIRD_INDEX = 3;                                            // index to stop when searching for component id

    boolean DIALOGUE_INACTIVE = false;                              // flag for finding out if a dialogue is currently active
    boolean DIALOGUE_ACTIVE = true;                                 // flag that indicates that a dialogue is currently active
    boolean RECEIVED_INPUT_START = false;                           // default value for field receivedInput

    int DIALOGUE_NUMBER_DEFAULT = -1;                               // Integer that shows which dialogue is active
    int[] STATE_MACHINES_DEFAULT = {0,0,0,0,0,0,0,0,0,0};           // Array that shows in which state the state machines are

    int DIALOGUE_STATE_MACHINE_0 = 0;                               // value for the field dialogueNumber for state machine 0
    int DIALOGUE_STATE_MACHINE_1 = 1;                               // value for the field dialogueNumber for state machine 1
    int DIALOGUE_STATE_MACHINE_2 = 2;                               // value for the field dialogueNumber for state machine 2

    int STATE_MACHINE_STATE_0 = 0;                                  // state 0 of any state machine
    int STATE_MACHINE_STATE_1 = 1;                                  // state 1 of any state machine
    int STATE_MACHINE_STATE_2 = 2;                                  // state 2 of any state machine
    int STATE_MACHINE_STATE_3 = 3;                                  // state 3 of any state machine
    int STATE_MACHINE_STATE_4 = 4;                                  // state 4 of any state machine
    int STATE_MACHINE_STATE_5 = 5;                                  // state 5 of any state machine
    int STATE_MACHINE_STATE_6 = 6;                                  // state 6 of any state machine
    int STATE_MACHINE_STATE_7 = 7;                                  // state 7 of any state machine
    int STATE_MACHINE_STATE_8 = 8;                                  // state 8 of any state machine
    int STATE_MACHINE_STATE_9 = 9;                                  // state 9 of any state machine

    int MINIMUM_ANSWER_LENGTH = 1;                                  // minimum String length of answer string to trigger action

    String RECOGNITION_START = "yes";                               // String for first answer in a dialogue with Glados
    String RECOGNITION_ABORTED = "okay";                            // String for answer when aborting dialogue with Glados

    String DOT = ".";                                               // String that contains the dot for text output format
    String Q_MARK = "?";                                            // String that contains the question mark for text output format
    String E_MARK = "!";                                            // String that contains the exclamation mark for text output format
    String VALID_SOUND_OUTPUT = "true";                             // String that indicates that the sound output is not busy

    String CONFIRMATION_YES = "yes";                                // String for completing a confirmation
    String CONFIRMATION_NO = "no";                                  // String for aborting a confirmation

    String SOUND_OUTPUT_NAME = "SoundManager";                      // name of the SoundManager class
    String TERMINAL_MANAGER_NAME = "TerminalManager";               // name of the TerminalManager class
    String VOICE_RECOGNITION_MANAGER_NAME = "VoiceRecognitionManager";  // name of the VoiceRecognitionManager class
    String ACTION_QUEUE_MANAGER_NAME = "ActionQueueManager";        // name of the ActionQueueManager class

    String RECOGNITION_START_1 = "hey";                             // String 1 for starting a dialogue with Auto
    String RECOGNITION_START_2 = "okay";                            // String 2 for starting a dialogue with Auto
    String RECOGNITION_GLADOS = "lettuce";                          // String for directing a dialogue towards Glados
    String RECOGNITION_ABORT = "abort dialogue";                    // String for aborting a currently active dialogue

    String GREETING_1 = "hi";                                       // String for activating greeting response
    String GREETING_2 = "hello";                                    // String for activating greeting response
    String GREETING_RESPONSE = "hello";                             // String for greeting response

    String INPUT_TURN_OFF = "turn off";                             // String for terminating this program
    String INPUT_SHUTDOWN = "shut down";                            // String for shutting down the machine on which this program runs
    String INPUT_REBOOT = "reboot";                                 // String for rebooting the machine on which this program runs
    String TURN_OFF_RESPONSE = "are you sure to terminate this program";// String for turn off response
    String SHUTDOWN_RESPONSE = "are you sure to shut down all systems"; // String for shutdown response
    String REBOOT_RESPONSE = "are you sure to reboot this system";  // String for reboot response
    String TURN_OFF_CONFIRM = "termination of program confirmed";   // String to signalize confirmation of turning off
    String TURN_OFF_ABORT = "termination of program aborted";       // String to signalize abortion of turning off
    String SHUTDOWN_CONFIRM = "shutdown confirmed";                 // String to signalize confirmation of shutdown
    String SHUTDOWN_ABORT = "shutdown aborted";                     // String to signalize abortion of shutdown
    String REBOOT_CONFIRM = "reboot confirmed";                     // String to signalize confirmation of reboot
    String REBOOT_ABORT = "reboot aborted";                         // String to signalize abortion of reboot
    String TURN_OFF_CONFIRMATION = "terminating this program";      // String for confirming turn off
    String SHUTDOWN_CONFIRMATION_TEXT = "shutting down all systems";// String for confirming shutdown
    String REBOOT_CONFIRMATION = "rebooting all systems";           // String for confirming reboot
}
