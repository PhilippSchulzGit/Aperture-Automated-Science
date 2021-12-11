package control.ActionQueue;

import control.AUTO.Auto;
import control.GLaDOS.Glados;
import control.Sound.SoundManager;
import control.VoiceRecognition.VoiceRecognitionManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueue;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.ActionQueue.ActionQueueManagerConstants;

import java.util.concurrent.TimeUnit;

/**
 * Class for central management of all actions in the AAS program
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class ActionQueueManager implements ActionQueueManagerConstants, ActionHandlingConstants
{
    // objects of all required components of GLaDOS
    private final ActionQueue actionQueue;
    private final ActionQueuePublic actionQueuePublic;
    private Auto auto;
    private Glados glados;
    private SoundManager soundManager;
    private VoiceRecognitionManager voiceRecognitionManager;

    // local fields
    private int ownID;
    private boolean manageActions;
    private boolean autoStopped;
    private boolean gladosStopped;
    private boolean soundStopped;
    private boolean voiceRecognitionStopped;

    /**
     * Constructor of the ActionQueueManager class
     * @author Philipp Schulz
     */
    public ActionQueueManager()
    {
        // initialize rest of components
        this.actionQueue = new ActionQueue();
        this.actionQueuePublic = new ActionQueuePublic(this.actionQueue);
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.manageActions = INITIAL_STATE_MANAGE_ACTIONS;
        this.autoStopped = INITIAL_STATE_AUTO_STOPPED;
        this.gladosStopped = INITIAL_STATE_GLADOS_STOPPED;
        this.soundStopped = INITIAL_STATE_SOUND_STOPPED;
        this.voiceRecognitionStopped = INITIAL_STATE_VOICE_RECOGNITION_STOPPED;
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

    /**
     * Method to set IDs for all components under this class
     * @author Philipp Schulz
     */
    public void setComponentIDs()
    {
        // set own ID
        setOwnID(this.auto.getIdManager().getComponentIDByName(this.getClass().getSimpleName()));
        // set component IDs
        this.glados.setComponentIDs();
        this.auto.setComponentIDs();    // doesn't need the IdManager since it already has it
        this.soundManager.setOwnID(this.auto.getIdManager().getComponentIDByName(this.soundManager.getClass().getSimpleName()));
        this.voiceRecognitionManager.setOwnID(this.auto.getIdManager().getComponentIDByName(this.voiceRecognitionManager.getClass().getSimpleName()));
    }

    /**
     * Central method for continually managing actions from the ActionQueue, until the exit condition is met
     * author Philipp Schulz
     */
    public void handleActions()
    {
        // give signal to terminal that the program is ready
        Action bootAction = new Action(this.auto.getIdManager().getComponentIDByName(CLASS_NAME_TERMINAL_MANAGER),
                this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_TERMINAL_MANAGER_PRINT+BOOT_COMPLETE, ERROR_COUNT_NEW);
        // put action into ActionQueue
        this.actionQueue.addNewActionToActionQueue(bootAction);

        // loop until the flag manageActions is no longer set
        while(this.manageActions)
        {
            // check if the ActionQueue contains actions
            if(!this.actionQueue.getActionQueue().isEmpty())
            {
                // get new action from the ActionQueue
                Action action = this.actionQueue.getActionQueue().poll();
                // check if the action is not null
                if(action != null)
                {
                    // check if action is valid (via target component ID)
                    if(action.getTargetIndex()>=ACTION_MANAGEMENT_MINIMUM_ID)
                    {// determine first digit of targetIndex
                        int targetIndex = Integer.parseInt(Integer.toString(action.getTargetIndex()).substring(INDEX_ZERO,
                                INDEX_ONE));
                        // find out where the action needs to go to
                        if(action.getTargetIndex() == this.soundManager.getOwnID())
                        {
                            // if the SoundManager is still alive
                            if(!this.soundStopped)
                            {
                                // give the action to the SoundManager
                                this.soundManager.handleAction(action);
                            }
                        }
                        else if(action.getTargetIndex() == this.voiceRecognitionManager.getOwnID())
                        {
                            // if the VoiceRecognitionManager is still alive
                            if(!this.voiceRecognitionStopped)
                            {
                                // give the action to the VoiceRecognitionManager
                                this.voiceRecognitionManager.handleAction(action);
                            }
                        }
                        else if(targetIndex == Integer.parseInt(Integer.toString(this.glados.getOwnID()).substring(INDEX_ZERO,INDEX_ONE)))
                        {
                            // give the action to Glados
                            this.glados.handleAction(action);
                        }
                        else if(targetIndex == Integer.parseInt(Integer.toString(this.auto.getOwnID()).substring(INDEX_ZERO,
                                INDEX_ONE)))
                        {
                            // if Auto is still alive
                            if(!this.autoStopped)
                            {
                                // give the action to Auto
                                this.auto.handleAction(action);
                            }
                        }
                        else if(action.getTargetIndex() == this.ownID)
                        {
                            // give the action to this class
                            handleAction(action);
                        }
                    }
                    else
                    {
                        // adjust action contents
                        action.setAction(action.getAction() + WRONG_COMPONENT_ID + action.getTargetIndex());
                        action.setTargetIndex(action.getOriginIndex());
                        action.setOriginIndex(this.ownID);
                        action.setErrorCount(action.getErrorCount()+ACTION_MANAGEMENT_ERROR_COUNT_INCREMENT);
                        // send the action back to the origin
                        this.actionQueue.addNewActionToActionQueue(action);
                    }
                }
                // check if all components are shut down
                if(this.autoStopped && this.gladosStopped && this.voiceRecognitionStopped && this.soundStopped &&
                        this.actionQueue.getActionQueue().size() == 0)
                {
                    // only print allowed outside TerminalManager, since TerminalManager is shut down at this point
                    System.out.println("All components are shut down. Goodbye.");
                    // shut down the ActionQueueSystem loop, ending the entire program
                    this.manageActions = false;
                }
            }
            // catch any errors that might occur
            try
            {
                // sleep for the specified time
                TimeUnit.MILLISECONDS.sleep(ACTION_MANAGEMENT_WAIT_TIME);
            }
            catch(Exception ignored)
            {

            }
        }
    }

    /**
     * Method for handling any action that is targeted towards all components under ActionQueueManager
     * only called by this class itself (inside handleActions())!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // handle the content of the received action
        switch (action.getAction()) {
            case SHUTDOWN_CONFIRMATION:             // if the action is from components shutting down
                // determine sender of action
                if (action.getOriginIndex() == this.auto.getOwnID())
                {
                    // save completed shutdown for Auto
                    this.autoStopped = true;
                } else if (action.getOriginIndex() == this.glados.getOwnID())
                {
                    // save completed shutdown for Glados
                    this.gladosStopped = true;
                } else if (action.getOriginIndex() == this.voiceRecognitionManager.getOwnID())
                {
                    // save completed shutdown for VoiceRecognitionManager
                    this.voiceRecognitionStopped = true;
                } else if (action.getOriginIndex() == this.soundManager.getOwnID())
                {
                    // save completed shutdown for SoundManager
                    this.soundStopped = true;
                }
                break;

            case UPDATE_ID_ACTION:                  // if the component IDs need to be updated
                // update component IDs of all components
                this.auto.setComponentIDs();
                this.glados.setComponentIDs();
                this.soundManager.setOwnID(this.auto.getIdManager().getComponentIDByName(this.soundManager.getClass().getSimpleName()));
                this.voiceRecognitionManager.setOwnID(this.auto.getIdManager().getComponentIDByName(this.voiceRecognitionManager.getClass().getSimpleName()));
                break;

            case SHUTDOWN_ACTION:                   // if the program should be shut down
                // create and send action to Auto
                Action shutdownAction = new Action(this.auto.getOwnID(), this.ownID, WAIT_FOR_EXECUTION_FALSE,
                        SHUTDOWN_ACTION, ERROR_COUNT_NEW);
                this.actionQueue.addNewActionToActionQueue(shutdownAction);
                // create and send action to Glados
                shutdownAction = new Action(this.glados.getOwnID(), this.ownID, WAIT_FOR_EXECUTION_FALSE,
                        SHUTDOWN_ACTION, ERROR_COUNT_NEW);
                this.actionQueue.addNewActionToActionQueue(shutdownAction);
                // create and send action to voice recognition
                shutdownAction = new Action(this.voiceRecognitionManager.getOwnID(), this.ownID,
                        WAIT_FOR_EXECUTION_FALSE, SHUTDOWN_ACTION, ERROR_COUNT_NEW);
                this.actionQueue.addNewActionToActionQueue(shutdownAction);
                // create and send action to sound output
                shutdownAction = new Action(this.soundManager.getOwnID(), this.ownID, WAIT_FOR_EXECUTION_FALSE,
                        SHUTDOWN_ACTION, ERROR_COUNT_NEW);
                this.actionQueue.addNewActionToActionQueue(shutdownAction);
                break;
        }
    }

    /**
     * Method for getting the public action queue
     * @return Instance of ActionQueuePublic
     * @author Philipp Schulz
     */
    public ActionQueuePublic getPublicActionQueue()
    {
        return this.actionQueuePublic;
    }

    /**
     * Method for setting references to the instances of the other most important component of AAS
     * @param auto Instance of the Auto class
     * @param glados Instance of the Glados class
     * @param soundManager Instance of the SoundManager class
     * @param voiceRecognitionManager Instance of the VoiceRecognitionManager class
     * @author Philipp Schulz
     */
    public void setObjectReferences(Auto auto, Glados glados, SoundManager soundManager, VoiceRecognitionManager voiceRecognitionManager)
    {
        // save object references for action handling
        this.auto = auto;
        this.glados = glados;
        this.soundManager = soundManager;
        this.voiceRecognitionManager = voiceRecognitionManager;
    }
}
