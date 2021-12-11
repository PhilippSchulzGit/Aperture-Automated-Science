package model.Constants.GLaDOS.Devices;

/**
 * Interface containing all constants used in the DeviceManager class
 * @author Philipp Schulz
 */
public interface DeviceManagerConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    String INITIAL_STATE_OPERATING_SYSTEM = "";                     // default value for the field operatingSystem
    String OS_NAME_DEFAULT = "None";                                // name used to indicate that the program runs on a Pi
    String DETERMINE_OS_COMMAND = "os.name";                        // command used to determine the OS
    String OS_NAME_WINDOWS = "Windows";                             // os name for Windows
    String OS_NAME_LINUX = "Linux";                                 // os name for Linux
}
