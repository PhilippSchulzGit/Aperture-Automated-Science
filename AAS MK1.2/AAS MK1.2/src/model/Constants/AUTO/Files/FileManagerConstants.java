package model.Constants.AUTO.Files;

/**
 * Interface containing all constants used in the FileManagerConstants class
 * @author Philipp Schulz
 */
public interface FileManagerConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    int COMPONENT_LIST_STRING_START = 0;                            // index on which a substring should start
    int COMPONENT_LIST_MINIMUM_LINE_LENGTH = 2;                     // minimum length of one component list line to be valid
    int COMPONENT_LIST_NAME_OFFSET = 1;                             // offset required to parse the component list correctly
    String COMPONENT_LIST_PATH = "resources/componentList.txt";     // path to the component list file
    String COMPONENT_LIST_COMMENT = "//";                           // String containing the comment symbol from the component list
    String SPACE_STRING = " ";                                      // String that contains one space
    String ACTION_READ_COMPONENT_LIST = "READ COMPONENT LIST";      // String to signalize that the terminal user wants to read the component list file

    String SOUND_MANAGER_COMPONENT_NAME = "SoundManager";           // Name of the SoundManager class
    String SOUND_FILE_PATH_AUTO = "resources/audio/AUTO/";          // Path from home path to sound files for voice of AUTO
    String SOUND_FILE_PATH_GLADOS = "resources/audio/GLaDOS/";      // Path from home path to sound files for voice of GLaDOS
    String USER_DIR = "user.dir";                                   // command to find out user directory on Windows
    String PI_DIR = "/home/pi/";                                    // home path for files on Raspberry Pi
    String SLASH = "/";                                             // String that contains a single forward slash for paths
    String OS_WINDOWS = "windows";                                  // String that contains the name of the Windows operating system
    String COMPONENT_LIST_READ_CONFIRMATION = "component list read.";// Answer to give after reading component list
}
