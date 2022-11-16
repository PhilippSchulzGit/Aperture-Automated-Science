package control.GLaDOS.Devices;

import com.fazecast.jSerialComm.SerialPort;
import control.AUTO.Components.IdManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.GLaDOS.Devices.ComPortManagerConstants;

import java.util.ArrayList;

/**
 * Class for handling everything about COM ports
 * Only called once by DeviceManager!
 * @author Philipp Schulz
 */
public class ComPortManager implements ComPortManagerConstants
{
    // objects of all required components of BodyManager
    private final ActionQueuePublic actionQueue;
    private final RadioManager radioManager;
    // local fields
    private int ownID;
    private ArrayList<SerialPort> devicePortList;

    /**
     * Constructor of the ComPortManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @author Philipp Schulz
     */
    public ComPortManager(ActionQueuePublic actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.radioManager = new RadioManager(actionQueue);
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
    }

    /**
     * Method to set IDs for all components under this class
     * @param idManager Instance of the IdManager from Auto
     * @author Philipp Schulz
     */
    public void setComponentIDs(IdManager idManager)
    {
        // set own ID
        setOwnID(idManager.getComponentIDByName(this.getClass().getSimpleName()));
        // set IDs of components
        this.radioManager.setOwnID(idManager.getComponentIDByName(this.radioManager.getClass().getSimpleName()));
    }

    /**
     * Central method for handling any action that is targeted towards all components under ComPortManager
     * only called by DeviceManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        if(action.getTargetIndex() == this.radioManager.getOwnID())    // action is for RadioManager
        {
            // let the SimonManager handle the action
            this.radioManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.ownID)      // action is for BodyManager
        {
            // go over all possible actions
            //TODO: IMPLEMENT METHOD
        }
    }

    /**
     * Method for searching all available COM ports and storing them in an internal list
     * @author Philipp Schulz
     */
    public void searchPorts()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for getting the devicePortList, containing all available COM ports
     * @return ArrayList<SerialPort> of all available COM ports
     * @author Philipp Schulz
     */
    public ArrayList<SerialPort> getDevicePortList()
    {
        return this.devicePortList;
    }

    /**
     * Method for listening for events that come from any COM ports
     * @author Philipp Schulz
     */
    public void listenForEvents()
    {
        //TODO: IMPLEMENT METHOD
    }
}
