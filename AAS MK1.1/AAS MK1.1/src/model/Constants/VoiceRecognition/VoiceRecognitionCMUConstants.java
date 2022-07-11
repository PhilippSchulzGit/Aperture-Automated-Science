package model.Constants.VoiceRecognition;

/**
 * Interface containing all constants used in the VoiceRecognitionCMU class
 * @author Philipp Schulz
 */
public interface VoiceRecognitionCMUConstants
{
    boolean CLEAR_RECOGNIZER = true;                                // value to give the recognizer when starting it
    boolean SPHINX4_USE_GRAMMAR = true;                             // default value for using the grammar file during recognition

    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    int RECOGNIZE_TIMEOUT = 1000;                                   // timeout for recognition [ms]

    boolean INITIAL_STATE_IS_RUNNING = false;                       // default value for the field isRunning
    boolean RECOGNIZER_CREATION_SUCCESS = true;                     // return value if recognizer was created
    boolean RECOGNIZER_CREATION_FAILED = false;                     // return value if recognizer was not created
    boolean RECOGNITION_START = true;                               // state of the field isRunning when the recognition should run
    boolean RECOGNITION_STOP = false;                               // state of the field isRunning when the recognition should stop


    String INITIAL_STATE_OS = "";                                   // default value for the field os

    String SPHINX4_USER_DIR = "user.dir";                           // user direction for library
    String SPHINX4_ACOUSTIC_MODEL_PATH = "resource:/resources/CMUSphinx/edu/cmu/sphinx/models/en-us/en-us"; // default path for Sphinx4 acoustic model
    String SPHINX4_DICTIONARY_PATH = "resource:/resources/CMUSphinx/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict"; // default path for Sphinx4 dictionary
    String SPHINX4_LANGUAGE_MODEL_PATH = "resource:/resources/CMUSphinx/edu/cmu/sphinx/models/en-us/en-us.lm.bin";  // default path for Sphinx4 language model
    String SPHINX4_GRAMMAR_NAME = "CMUSphinxGrammar";               // default name of the grammar file
    String SPHINX4_GRAMMAR_PATH_PI = "file:///home/pi/AAS/resources/"; // default path of the grammar file on a Rasp    berry Pi
    String SPHINX4_GRAMMAR_PATH_WINDOWS = "/resources/CMUSphinx/";// default path of the grammar file on Windows

    String FILE = "file:///";                                       // String to help with defining a file
    String EMPTY_RESULT = "";                                       // empty result in case the voice recognition should not run
    String OS_LINUX = "Linux";                                      // String to indicate that the program runs on Linux
    String OS_WINDOWS = "Windows";                                  // String to indicate that the program runs on Windows

    String LOGGER_PROPERTY1 = "java.util.logging.config.file";      // logging field
    String LOGGER_PROPERTY2 = "ignoreAllSphinx4LoggingOutput";      // logging state
}
