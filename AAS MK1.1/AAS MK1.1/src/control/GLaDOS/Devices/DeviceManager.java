package control.GLaDOS.Devices;

import control.AUTO.Components.IdManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.GLaDOS.Devices.DeviceManagerConstants;

import java.util.Locale;

/**
 * Class for handling other devices connected to the AAS system
 * Only called once by GLaDOS!
 * @author Philipp Schulz
 */
public class DeviceManager implements DeviceManagerConstants, ActionHandlingConstants
{
    // objects of all required components of DeviceManager
    private final ActionQueuePublic actionQueue;
    private final IdManager idManager;
    private final SimonManager simonManager;
    private final BodyManager bodyManager;
    private final ComPortManager comPortManager;
    private final GpioManager gpioManager;
    // local fields
    private int ownID;
    private String operatingSystem;

    /**
     * Constructor of the DeviceManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @param idManager Instance of the IdManager
     * @author Philipp Schulz
     */
    public DeviceManager(ActionQueuePublic actionQueue, IdManager idManager)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // determine OS
        determineOS();
        // initialize rest of components
        this.idManager = idManager;
        this.gpioManager = new GpioManager(actionQueue, getOS());
        this.simonManager = new SimonManager(actionQueue,gpioManager);
        this.bodyManager = new BodyManager(actionQueue,simonManager);
        this.comPortManager = new ComPortManager(actionQueue);
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.operatingSystem = INITIAL_STATE_OPERATING_SYSTEM;
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
        this.gpioManager.setOwnID(this.idManager.getComponentIDByName(this.gpioManager.getClass().getSimpleName()));
        this.simonManager.setOwnID(this.idManager.getComponentIDByName(this.simonManager.getClass().getSimpleName()));
        this.bodyManager.setOwnID(this.idManager.getComponentIDByName(this.bodyManager.getClass().getSimpleName()));
        this.comPortManager.setComponentIDs(this.idManager);
    }

    /**
     * Central method for handling any action that is targeted towards all components under DeviceManager
     * only called by GLaDOS!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // determine first digit of targetIndex for components with sub-components
        int targetIndex = Integer.parseInt(Integer.toString(action.getTargetIndex()).substring(INDEX_THREE, INDEX_FIVE));
        // go through all components of GLaDOS
        if(action.getTargetIndex() == this.gpioManager.getOwnID())          // action is for GpioManager
        {
            // let the GpioManager handle the action
            this.gpioManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.simonManager.getOwnID())    // action is for SimonManager
        {
            // let the SimonManager handle the action
            this.simonManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.bodyManager.getOwnID())      // action is for BodyManager
        {
            // let the BodyManager handle the action
            this.bodyManager.handleAction(action);
        }
        else if(targetIndex == Integer.parseInt(Integer.toString(this.comPortManager.getOwnID()).substring(INDEX_THREE, INDEX_FIVE)))  // action is for ComPortManager
        {
            // let the ComPortManager handle the action
            this.comPortManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.ownID)                      // action is for this class
        {
            // go over all possible actions
            if(action.getAction().contains(ACTION_DEVICE_MANAGER_OS))   // if the os should be returned
            {
                // call method for determining OS
                this.determineOS();
                // create action for answer
                Action osAction = new Action(action.getOriginIndex(),this.ownID,WAIT_FOR_EXECUTION_FALSE,
                        action.getAction()+SPACE+this.operatingSystem,ERROR_COUNT_NEW);
                // add action to queue
                this.actionQueue.addNewActionToActionQueue(osAction);
            }
        }
    }

    /**
     * Method for determining the OS on which this program is running
     * @author Philipp Schulz
     */
    public void determineOS()
    {
        String operatingSystemAnswer = System.getProperty(DETERMINE_OS_COMMAND);
        if(operatingSystemAnswer.contains(OS_NAME_WINDOWS))
        {
            this.operatingSystem = OS_NAME_WINDOWS;
        }
        else if(operatingSystemAnswer.contains(OS_NAME_LINUX))  // TODO: TEST THIS PART
        {
            this.operatingSystem = OS_NAME_LINUX;
        }
        else
        {
            this.operatingSystem = OS_NAME_DEFAULT;
        }
    }

    /**
     * Method for getting the OS on which this program is running
     * @return String that includes the OS name
     * @author Philipp Schulz
     */
    public String getOS()
    {
        return this.operatingSystem;
    }
}
