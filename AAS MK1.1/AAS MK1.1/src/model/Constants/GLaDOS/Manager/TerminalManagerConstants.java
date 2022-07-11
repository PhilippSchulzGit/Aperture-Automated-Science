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
    int SUBSTRING_START = 0;                                        // start index of substring for terminal input usage
    String TERMINAL_INPUT_HELP = "HELP";                            // String to signalize that the terminal user needs help
    String TERMINAL_INPUT_SHUTDOWN = "SHUTDOWN";                    // String to signalize that the terminal user wants the program to end
    String NAME_ACTION_QUEUE_MANAGER = "ActionQueueManager";        // name of the class ActionQueueManager
    String NAME_FILE_MANAGER = "FileManager";                       // name of the class FileManager
    String NAME_VOICE_RECOGNITION_MANAGER = "VoiceRecognitionManager";  // name of the class VoiceRecognitionManager
    String NAME_TERMINAL_MANAGER = "TerminalManager";               // name of the class TerminalManager
    String TERMINAL_INPUT_COMPONENT_LIST = "READ COMPONENT LIST";   // String to signalize that the terminal user wants to read the component list file
    String TERMINAL_INPUT_VOICE_RECOGNITION = "VR";                 // String to signalize that the terminal user wants to use voice recognition commands
    String TERMINAL_INPUT_RECOGNITION_START = "VR START";           // String to signalize that the terminal user wants the voice recognition to start
    String TERMINAL_INPUT_RECOGNITION_STOP = "VR STOP";             // String to signalize that the terminal user wants the voice recognition to stop
    String TERMINAL_INPUT_CONFIRMATION = "Request confirmed";       // standard print to give the user feedback that the command has been accepted
    String TERMINAL_INPUT_ERROR = "Command is not supported.";      // print to signalize user that the previous input was invalid
    String ACTION_PRINT = "PRINT ";                                 // action command for printing to console
    String HELP_NOTE = "Type in 'HELP' to see available commands."; // text to print to terminal to signalize help

    String TERMINAL_PRINT_PREFIX = ": ";                            // prefix to format terminal prints with origin and text

    String GLADOS_RIGHT = "GLaDOS";                                 // correct string to print to terminal for glados
    String GLADOS_WRONG_1 = "lettuce";                              // wrong string 1 to print to terminal for glados
    String GLADOS_WRONG_2 = "glados";                               // wrong string 2 to print to terminal for glados
    String GLADOS_WRONG_3 = "Glados";                               // wrong string 3 to print to terminal for glados
    String AUTO_RIGHT = "AUTO";                                     // correct String to print to terminal for auto
    String AUTO_WRONG_1 = "Auto";                                   // wrong string 1 to print to terminal for auto
    String AUTO_WRONG_2 = "auto";                                   // wrong string 2 to print to terminal for auto


    String HELP_PRINT_BINDER = " - ";                               // String for formatting purposes
    String HELP_PRINT_START = "possible commands:";                 // first String to print for help
    String HELP_DESCRIPTION_HELP = "lists all possible commands";   // description of the HELP command
    String HELP_DESCRIPTION_SHUTDOWN = "shuts down the program";    // description of the SHUTDOWN command
    String HELP_DESCRIPTION_COMPONENT_LIST = "reads the component list file";   // description of the READ COMPONENT LIST command
    String HELP_DESCRIPTION_DIALOGUE = "same options as voice recognition. Append command in the following convention: 'VR command'"; // description of the voice recognition commands
    String HELP_VOICE_RECOGNITION_START = "starts the voice recognition";   // description of the VR START command
    String HELP_VOICE_RECOGNITION_STOP = "stops the voice recognition"; // description of the VR STOP command
}
