package control.GLaDOS;

import control.AUTO.Components.IdManager;
import control.GLaDOS.Devices.DeviceManager;
import control.GLaDOS.Manager.TerminalManager;
import control.GLaDOS.Network.NetworkManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.GLaDOS.GladosConstants;

/**
 * Class for handling anything of the section "facility management"
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class Glados implements GladosConstants, ActionHandlingConstants
{
    // objects of all required components of GLaDOS
    private final ActionQueuePublic actionQueue;
    private final DeviceManager deviceManager;
    private final NetworkManager networkManager;
    private final TerminalManager terminalManager;
    private final IdManager idManager;
    // local fields
    private int ownID;

    /**
     * Constructor of the GLaDOS class
     * @param actionQueue Instance of the public action queue
     * @param idManager Instance of the IdManager from Auto
     * @author Philipp Schulz
     */
    public Glados(ActionQueuePublic actionQueue, IdManager idManager)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.idManager = idManager;
        this.terminalManager = new TerminalManager(actionQueue, idManager);
        this.deviceManager = new DeviceManager(actionQueue, idManager);
        this.networkManager = new NetworkManager(actionQueue, idManager);
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
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
        System.out.println("Glados: "+id);
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
        this.networkManager.setOwnID(this.idManager.getComponentIDByName(this.networkManager.getClass().getSimpleName()));
        this.terminalManager.setOwnID(idManager.getComponentIDByName(this.terminalManager.getClass().getSimpleName()));
        this.deviceManager.setComponentIDs();
    }

    /**
     * Central method for handling any action that is targeted towards all components under GLaDOS
     * only called by ActionQueueManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // determine first digit of targetIndex for components with sub-components
        int targetIndex = Integer.parseInt(Integer.toString(action.getTargetIndex()).substring(INDEX_ONE, INDEX_THREE));
        // go through all components of GLaDOS
        if(action.getTargetIndex() == this.terminalManager.getOwnID())      // action is for TerminalManager
        {
            // let the TerminalManager handle the action
            this.terminalManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.networkManager.getOwnID())  // action is for NetworkManager
        {
            // let the NetworkManager handle the action
            this.networkManager.handleAction(action);
        }
        else if(targetIndex == Integer.parseInt(Integer.toString(this.deviceManager.getOwnID()).substring(INDEX_ONE, INDEX_THREE)))  // action is for DeviceManager
        {
            // let the DeviceManager handle the action
            this.deviceManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.ownID)                      // action is for this class
        {
            // handle the content of the received action
            if(action.getAction().equals(SHUTDOWN_ACTION))                  // if the action is for shutting down
            {
                // create action to shut down TerminalManager
                Action shutdownAction = new Action(this.terminalManager.getOwnID(), this.ownID, false, SHUTDOWN_ACTION, 0);
                // send action to TerminalManager
                this.actionQueue.addNewActionToActionQueue(shutdownAction);
                // adjust action for sending back
                action.setAction(SHUTDOWN_CONFIRMATION);
                action.setTargetIndex(action.getOriginIndex());
                action.setOriginIndex(this.getOwnID());
                // put action back into action queue
                this.actionQueue.addNewActionToActionQueue(action);
            }
            //TODO: IMPLEMENT REST OF METHOD
        }
    }
}
