package control.GLaDOS.Devices;

import com.pi4j.io.gpio.Pin;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.GLaDOS.Devices.SimonManagerConstants;

/**
 * Class for handling everything of the SIMON protocol for data transmissions
 * Only called once by DeviceManager!
 * @author Philipp Schulz
 */
public class SimonManager implements SimonManagerConstants
{
    // objects of all required components of BodyManager
    private final ActionQueuePublic actionQueue;
    private final GpioManager gpioManager;
    // local fields
    private int ownID;
    private final int ownSimonID;
    private Pin dataPin;
    private Pin clockPin;
    private boolean isBusy;

    /**
     * Constructor of the SimonManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @param gpioManager Reference to the GpioManager instance
     * @author Philipp Schulz
     */
    public SimonManager(ActionQueuePublic actionQueue, GpioManager gpioManager)
    {
        // save reference to instance of the ActionQueue and GpioManager
        this.actionQueue = actionQueue;
        this.gpioManager = gpioManager;
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.ownSimonID = OWN_SIMON_ID;
        this.dataPin = DATA_PIN;
        this.clockPin = CLOCK_PIN;
        this.isBusy = INITIAL_STATE_IS_BUSY;
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
     * Central method for handling any action that is targeted towards all components under SimonManager
     * only called by DeviceManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for resetting the SIMON protocol and its pins
     * @author Philipp Schulz
     */
    public void resetSimon()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for setting up the SIMON protocol and its pins
     * @author Philipp Schulz
     */
    public void beginSimon()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for waiting a defined amount of time
     * @param milliseconds Time in milliseconds that should be waited for
     * @author Philipp Schulz
     */
    public void busyWaitMilliseconds(int milliseconds)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for sending a given binary String via GPIO pins
     * @param binaryString String that contains the binary data that should be sent
     * @author Philipp Schulz
     */
    private void sendDataString(String binaryString)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for determining the length of one individual signal via the GPIO pins
     * @return Signal length in milliseconds
     * @author Philipp Schulz
     */
    private int getSignalLength()
    {
        //TODO: IMPLEMENT METHOD
        return 0;
    }

    /**
     * Method for reading data from the GPIO pins and returning it as a binary String
     * @return String that contains binary data that was read from the GPIO pins
     * @author Philipp Schulz
     */
    private String readData()
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for sending data over the SIMON protocol
     * @param receiverNumber The SIMON ID of the receiver
     * @param command String that contains the data to be sent
     */
    public void sendSimonData(int receiverNumber, String command)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for reading data from the SIMON protocol
     * @return String that contains read data
     * @author Philipp Schulz
     */
    public String readSimonData()
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for determining if the SIMON protocol is currently busy
     * @return True if the protocol is busy, false if not
     * @author Philipp Schulz
     */
    public boolean isBusy()
    {
        //TODO: IMPLEMENT METHOD
        return false;
    }


}
