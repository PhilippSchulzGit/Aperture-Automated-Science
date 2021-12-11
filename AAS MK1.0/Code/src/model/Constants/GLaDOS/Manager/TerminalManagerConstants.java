package model.Constants.GLaDOS.Manager;

/**
 * Interface containing all constants used in the TerminalManager class
 * @author Philipp Schulz
 */
public interface TerminalManagerConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    boolean INITIAL_STATE_IS_ACTIVE = true;                         // default value for the field isActive
    int INCREMENT = 1;                                              // amount of increments for String manipulation
    int TERMINAL_INPUT_MINIMUM_LENGTH = 1;                          // minimum length for a valid terminal input
    int TERMINAL_INPUT_WAIT_TIME = 100;                             // time in [ms] to wait between terminal input handling cycles
    String TERMINAL_INPUT_HELP = "HELP";                            // String to signalize that the terminal user needs help
    String TERMINAL_INPUT_SHUTDOWN = "SHUTDOWN";                    // String to signalize that the terminal user wants the program to end
    String NAME_ACTION_QUEUE_MANAGER = "ActionQueueManager";        // name of the class ActionQueueManager
    String NAME_FILE_MANAGER = "FileManager";                       // name of the class FileManager
    String TERMINAL_INPUT_COMPONENT_LIST = "READ COMPONENT LIST";   // String to signalize that the terminal user wants to read the component list file
    int SHUTDOWN_ERROR_COUNT = 0;                                   // value of field errorCount for shutdown terminal input
    boolean SHUTDOWN_WAIT_FOR_EXECUTION = false;                    // value of field waitForExecution for shutdown terminal input
    String TERMINAL_INPUT_CONFIRMATION = "Request confirmed";       // standard print to give the user feedback that the command has been accepted
    String TERMINAL_INPUT_ERROR = "Command is not supported.";      // print to signalize user that the previous input was invalid
    String ACTION_PRINT = "PRINT ";                                 // action command for printing to console
    String HELP_NOTE = "Type in 'HELP' to see available commands."; // text to print to terminal to signalize help

    String HELP_PRINT_BINDER = " - ";                               // String for formatting purposes
    String HELP_PRINT_START = "possible commands:";                 // first String to print for help
    String HELP_DESCRIPTION_HELP = "lists all possible commands";   // description of the HELP command
    String HELP_DESCRIPTION_SHUTDOWN = "shuts down the program";    // description of the SHUTDOWN command
    String HELP_DESCRIPTION_COMPONENT_LIST = "reads the component list file";   // description of the READ COMPONENT LIST command
}
