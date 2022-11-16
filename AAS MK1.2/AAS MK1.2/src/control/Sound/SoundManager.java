package control.Sound;

import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.Sound.SoundManagerConstants;
import model.Sound.Sound;

import java.util.Locale;

/**
 * Class for realization of sound output for all other components
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class SoundManager implements SoundManagerConstants, ActionHandlingConstants
{
    // objects of all required components of VoiceRecognitionManager
    private final ActionQueuePublic actionQueue;
    private final Sound sound;
    // local fields
    private int ownID;
    private final int comPortManagerID;
    private final int fileManagerID;

    /**
     * Constructor of the SoundManager class
     * @author Philipp Schulz
     */
    public SoundManager(ActionQueuePublic actionQueue, int comPortManagerID, int fileManagerID)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.sound = new Sound();
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.comPortManagerID = comPortManagerID;
        this.fileManagerID = fileManagerID;
    }

    /**
     * Central method for handling any action that is targeted towards all components under SoundManager
     * only called by ActionQueueManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // handle the content of the received action
        if(action.getAction().equals(SHUTDOWN_ACTION))    // if the action is for shutting down
        {
            // adjust action for sending back
            action.setAction(SHUTDOWN_CONFIRMATION);
            action.setTargetIndex(action.getOriginIndex());
            action.setOriginIndex(this.getOwnID());
            // put action back into action queue
            this.actionQueue.addNewActionToActionQueue(action);
        }
        else if(action.getAction().equals(SHUTDOWN_SOUND_PLAYBACK)) // if specifically the sound output should be shut down
        {
            // reset field in Sound class
            this.sound.setAlive(KILL_SOUND_OUTPUT);
        }
        else if(action.getAction().contains(ACTION_PLAY_SOUND_GLADOS))  // if the action is for playing a sound from glados
        {
            // pass string to sound object and indicate sound from glados
            sound.addTextToOutputQueue(ACTION_PLAY_SOUND_GLADOS,
                    action.getAction().substring(action.getAction().indexOf(ACTION_PLAY_SOUND_GLADOS)+
                            ACTION_PLAY_SOUND_GLADOS.length()).replace(COMMA,EMPTY_STRING)); // replace comma with empty String
        }
        else if(action.getAction().contains(ACTION_PLAY_SOUND_AUTO))    // if the action is for playing a sound from auto
        {
            // pass string to sound object and indicate sound from auto
            sound.addTextToOutputQueue(ACTION_PLAY_SOUND_AUTO,
                    action.getAction().substring(action.getAction().indexOf(ACTION_PLAY_SOUND_AUTO)+
                            ACTION_PLAY_SOUND_AUTO.length()).replace(COMMA,EMPTY_STRING)); // replace comma with empty String
        }
        else if(action.getAction().contains(ACTION_GET_PATH_TO))        // if the action is for requesting a path
        {
            // check which path was returned, last path will call initialization of this class
            if(action.getAction().toUpperCase(Locale.ROOT).contains(AUTO_COMPONENT_NAME))
            {
                // set path for AUTO in Sound class
                this.sound.setAutoSoundPath(action.getAction().substring(ACTION_GET_PATH_TO.length()+INDEX_ONE));
            }
            else if(action.getAction().toUpperCase(Locale.ROOT).contains(GLADOS_COMPONENT_NAME))
            {
                // set path for GLaDOS in Sound class
                this.sound.setGladosSoundPath(action.getAction().substring(ACTION_GET_PATH_TO.length()+INDEX_ONE));
                // check if current os is Windows based on home path
                if(!action.getAction().substring(ACTION_GET_PATH_TO.length()).contains(LINUX_HOME_PATH))
                {
                    // set flag in Sound class
                    this.sound.setIsWindows(WINDOWS_OS_PRESENT);
                }
                // start proper initialization of Sound class
                this.sound.initialize();
            }
        }
        else if(action.getAction().contains(NOTIFY_COMPONENT))          // if the action notifies this class
        {
            // create new actions to request sound file paths for different voices
            Action autoPathAction = new Action(fileManagerID,this.ownID,WAIT_FOR_EXECUTION_FALSE,
                    ACTION_GET_PATH_TO+SPACE+AUTO_COMPONENT_NAME,ERROR_COUNT_NEW);
            Action gladosPathAction = new Action(fileManagerID,this.ownID,WAIT_FOR_EXECUTION_FALSE,
                    ACTION_GET_PATH_TO+SPACE+GLADOS_COMPONENT_NAME,ERROR_COUNT_NEW);
            // put actions into action queue
            this.actionQueue.addNewActionToActionQueue(autoPathAction);
            this.actionQueue.addNewActionToActionQueue(gladosPathAction);
        }
        else if(action.getAction().contains(ACTION_SOUND_OUTPUT_BUSY))  // if the action is for requesting the sound output state
        {
            // create new action to give back the current state of the sound output
            Action soundOutputStateAction = new Action(action.getOriginIndex(),this.ownID,WAIT_FOR_EXECUTION_FALSE,
                    ACTION_SOUND_OUTPUT_BUSY+SPACE+this.sound.getPlaybackFinished(),ERROR_COUNT_NEW);
            // put actions into action queue
            this.actionQueue.addNewActionToActionQueue(soundOutputStateAction);
        }
    }

    /**
     * Method to get the component ID of this class
     * @return Component ID of this class
     * @author Philipp Schulz
     */
    public int getOwnID()
    {
        return this.ownID;
    }

    /**
     * Method to set a new component ID of this class
     * @param id New component ID of this class
     * @author Philipp Schulz
     */
    public void setOwnID(int id)
    {
        this.ownID = id;
    }
}
