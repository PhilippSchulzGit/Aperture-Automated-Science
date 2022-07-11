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
    int COMPONENT_LIST_COMMENT_END_INDEX = 2;                       // index at which the comment symbol should end in the component lsit
    String COMPONENT_LIST_PATH = "resources/componentList.txt";     // path to the component list file
    String COMPONENT_LIST_COMMENT = "//";                           // String containing the comment symbol from the component list
    String SPACE_STRING = " ";                                      // String that contains one space
    String ACTION_READ_COMPONENT_LIST = "READ COMPONENT LIST";      // String to signalize that the terminal user wants to read the component list file

    String COMPONENT_LIST_READ_CONFIRMATION = "component list read.";// Answer to give after reading component list
}
