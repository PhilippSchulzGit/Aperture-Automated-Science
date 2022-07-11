package model.Constants;

/**
 * Interface to store all constants within the ActionQueueSystem
 * @author Philipp Schulz
 */
public interface ActionHandlingConstants
{
    String UPDATE_OWN_ID = "UPDATE ID";                             // action for updating the component ID
    String WRONG_COMPONENT_ID = "";                                 // action for handling a wrong component ID
    String SHUTDOWN_ACTION = "SHUTDOWN";                            // value for shutdown request for the field action
    String SHUTDOWN_CONFIRMATION = "SHUTDOWN COMPLETE";             // confirmation String that is sent by other components to signal completed shutdown
    String SPACE = " ";                                             // String that contains a space character for action handling
    String ACTION_TERMINAL_MANAGER_PRINT = "PRINT ";                // String that indicates printing to the terminal

    boolean WAIT_FOR_EXECUTION_FALSE = false;                       // value if an action does not require the waitForExecution field
    boolean WAIT_FOR_EXECUTION_TRUE = true;                         // value if an action requires the waitForExecution field

    int ERROR_COUNT_NEW = 0;                                        // value for shutdown request for the field errorCount

    int INDEX_ZERO = 0;                                             // index of position 0 in Strings
    int INDEX_ONE = 1;                                              // index of position 1 in Strings
    int INDEX_TWO = 2;                                              // index of position 2 in Strings
    int INDEX_THREE = 3;                                            // index of position 3 in Strings
    int INDEX_FOUR = 4;                                             // index of position 4 in Strings
    int INDEX_FIVE = 5;                                             // index of position 5 in Strings
}
