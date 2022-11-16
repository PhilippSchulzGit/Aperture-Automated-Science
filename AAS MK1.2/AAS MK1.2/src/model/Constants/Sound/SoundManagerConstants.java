package model.Constants.Sound;

/**
 * Interface containing all constants used in the SoundManager class
 * @author Philipp Schulz
 */
public interface SoundManagerConstants
{
    boolean KILL_SOUND_OUTPUT = false;                              // value to stop the sound output thread
    boolean WINDOWS_OS_PRESENT = true;                              // value to indicate that current OS is Windows
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    int SOUND_FILE_PATH_INDEX = 1;                                  // index at which the path to the sound files is in the action
    String WINDOWS_OS = "windows";                                  // String that contains the name of Windows OS
    String LINUX_HOME_PATH = "/home/pi/";                           // String that contains part of the home path on Linux
    String COMMA = ",";                                             // String that contains a single comma character
}
