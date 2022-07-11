package control.Sound;

import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.Sound.SoundManagerConstants;
import model.Sound.Sound;

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
    private int comPortManagerID;

    /**
     * Constructor of the SoundManager class
     * @author Philipp Schulz
     */
    public SoundManager(ActionQueuePublic actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.sound = new Sound();
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.comPortManagerID = -1;
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
        //TODO: IMPLEMENT METHOD
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
