package control.AUTO;

import control.AUTO.Components.IdManager;
import control.AUTO.Files.FileManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.AUTO.AutoConstants;
import model.Constants.ActionHandlingConstants;

/**
 * Class for handling anything of the section "data management"
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class Auto implements AutoConstants, ActionHandlingConstants
{
    // objects of all required components of GLaDOS
    private final ActionQueuePublic actionQueue;
    private final FileManager fileManager;
    private final IdManager idManager;
    // local fields
    private int ownID;

    /**
     * Constructor of the AUTO class
     * @param actionQueue Instance of the public action queue
     * @author Philipp Schulz
     */
    public Auto(ActionQueuePublic actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.fileManager = new FileManager(actionQueue);
        this.idManager = new IdManager(actionQueue);
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;

        // save component list with FileManager and save it in IdManager
        this.fileManager.readComponentList();
        this.idManager.setComponentList(this.fileManager.getComponentList());
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
        setOwnID(this.idManager.getComponentIDByName(this.getClass().getSimpleName()));
        // set IDs of components
        this.fileManager.setOwnID(this.idManager.getComponentIDByName(this.fileManager.getClass().getSimpleName()));
        this.idManager.setOwnID(this.idManager.getComponentIDByName(this.idManager.getClass().getSimpleName()));
    }

    /**
     * Central method for handling any action that is targeted towards all components under AUTO
     * only called by ActionQueueManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // determine first digit of targetIndex for components with sub-components
        //int targetIndex = Integer.parseInt(Integer.toString(action.getTargetIndex()).substring(INDEX_ONE, INDEX_THREE));
        // go over all components of this class (if-statements because switches only work with constant expressions)
        if(action.getTargetIndex() == this.fileManager.getOwnID())
        {
            // handle the action in the FileManager
            this.fileManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.ownID)
        {
            // handle the action here in the Auto class

            // handle setting a new ID
            if(action.getAction().contains(UPDATE_OWN_ID))
            {
                // update the internal component ID
                this.ownID = Integer.parseInt(action.getAction().substring(action.getAction().indexOf(UPDATE_OWN_ID)));
            }

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
        }
        else
        {
            // handle undefined action
            action.setAction(action.getAction() + WRONG_COMPONENT_ID + action.getTargetIndex());
            action.setTargetIndex(action.getOriginIndex());
            action.setOriginIndex(this.ownID);
            action.setErrorCount(action.getErrorCount()+INCREMENT);
            this.actionQueue.addNewActionToActionQueue(action);
        }
    }

    /**
     * Method for getting access to the IdManager for id handling at startup
     * @return Instance of the IdManager class
     * @author Philipp Schulz
     */
    public IdManager getIdManager()
    {
        return this.idManager;
    }
}
