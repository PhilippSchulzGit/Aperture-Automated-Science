package control.GLaDOS.Devices;

import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.GLaDOS.Devices.BodyManagerConstants;
import model.GLaDOS.Devices.Body;

/**
 * Class for handling the interaction with the physical body of GLaDOS
 * Only called once by DeviceManager!
 * @author Philipp Schulz
 */
public class BodyManager implements BodyManagerConstants
{
    // objects of all required components of BodyManager
    private final ActionQueuePublic actionQueue;
    private final Body body;
    private final SimonManager simonManager;
    // local fields
    private int ownID;

    /**
     * Constructor of the BodyManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @param simonManager Reference to the simonManager instance
     */
    public BodyManager(ActionQueuePublic actionQueue, SimonManager simonManager)
    {
        // save references to the instances of the ActionQueue and SimonManager
        this.actionQueue = actionQueue;
        this.simonManager = simonManager;
        // initialize rest of components
        this.body = new Body();
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
     * Central method for handling any action that is targeted towards all components under BodyManager
     * only called by DeviceManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        //TODO: IMPLEMENT METHOD
    }
}
