package model.Constants.Sound;

/**
 * Interface containing all constants used in the Sound class
 * @author Philipp Schulz
 */
public interface SoundConstants
{
    boolean INITIALIZED_START = false;          // default value for field initialized
    boolean IS_WINDOWS_START = false;           // default value for field isWindows
    boolean ALIVE_START = true;                 // default value for field alive
    boolean ENABLE_PLAYBACK = false;            // value to indicate that sound will be played
    boolean DISABLE_PLAYBACK = true;            // value to indicate that sound playback is finished
    int WAITING_TIME = 5;                       // waiting time in [ms]
    int LOOP_WAIT_TIME = 50;                    // waiting time in output thread in [ms]
    int SENTENCE_SPACER = 200;                  // time in [ms] to wait between sentences
    int QUEUE_LENGTH = 100;                     // Maximum number of elements in sound output queue
    String GLADOS = "GLADOS";                   // String to indicate voice origin for glados
    String AUTO = "AUTO";                       // String to indicate voice origin for auto
    String EMPTY_STRING = "";                   // Empty String
    String FILE_FORMAT = ".wav";                // format of the sound files
    String PI_PATH = "/home/pi/";               // Segment in the file path to decide which OS is used
    String MIXER_PI_INFO = "Direct Audio Device: USB Audio Device, USB Audio, USB Audio";    // description of sound output on Raspberry Pi
    }
