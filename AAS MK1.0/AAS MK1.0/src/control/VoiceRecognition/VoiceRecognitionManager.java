package control.VoiceRecognition;

import control.AUTO.Components.IdManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.VoiceRecognition.VoiceRecognitionManagerConstants;
import model.VoiceRecognition.VoiceRecognitionCMU;

/**
 * Class for realization of voice recognition for overall program
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class VoiceRecognitionManager implements VoiceRecognitionManagerConstants, ActionHandlingConstants
{
    // objects of all required components of VoiceRecognitionManager
    private final ActionQueuePublic actionQueue;
    private final VoiceRecognitionCMU voiceRecognitionCMU;
    private final IdManager idManager;
    // local fields
    private int ownID;
    private int receiverID;

    /**
     * Constructor of the VoiceRecognitionManager class
     * @param actionQueue Instance of the public action queue
     * @param idManager Instance of the IdManager from Auto
     * @author Philipp Schulz
     */
    public VoiceRecognitionManager(ActionQueuePublic actionQueue, IdManager idManager)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.idManager = idManager;
        this.voiceRecognitionCMU = new VoiceRecognitionCMU();
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.receiverID = -1;
    }

    /**
     * Central method for handling any action that is targeted towards all components under VoiceRecognitionManager
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
     * Method for starting and maintaining the voice recognition
     * @author Philipp Schulz
     */
    public void runRecognition()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for determining the receiver ID of the next voice recognition result
     * @author Philipp Schulz
     */
    public void determineReceiverID()
    {
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
        System.out.println("VoiceRecognitionManager: "+id);
    }
}
