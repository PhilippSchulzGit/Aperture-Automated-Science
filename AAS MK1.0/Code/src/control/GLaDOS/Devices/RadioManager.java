package control.GLaDOS.Devices;

import com.fazecast.jSerialComm.SerialPort;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.GLaDOS.Devices.RadioManagerConstants;
import model.GLaDOS.Devices.Radio;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for handling everything about the radio
 * Only called once by ComPortManager!
 * @author Philipp Schulz
 */
public class RadioManager implements RadioManagerConstants
{
    // objects of all required components of BodyManager
    private final ActionQueuePublic actionQueue;
    private final Radio radio;
    // local fields
    private int ownID;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * Constructor of the RadioManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @author Philipp Schulz
     */
    public RadioManager(ActionQueuePublic actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.radio = new Radio();
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
        System.out.println("RadioManager: "+id);
    }

    /**
     * Central method for handling any action that is targeted towards all components under RadioManager
     * only called by ComPortManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        System.out.println("This is RadioManager");
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for changing the SerialPort object of the COM port connection to the radio
     * @param comPort SerialPort object of the COM port connection to the radio
     * @author Philipp Schulz
     */
    public void changeRadioComPort(SerialPort comPort)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for connecting to the radio
     * @author Philipp Schulz
     */
    public void connect()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for homing the volume to a known state
     * @author Philipp Schulz
     */
    public void homeVolume()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for initializing the radio
     * @author Philipp Schulz
     */
    public void initializeRadio()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for reading the radio status
     */
    public void readRadioStatus()
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for changing the radio volume to a new value
     * @param newVolume new volume of the radio speaker in percent
     * @author Philipp Schulz
     */
    public void changeVolume(int newVolume)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for changing the radio state (turning it on/off)
     * @param newState new radio state (on=true / off=false)
     * @author Philipp Schulz
     */
    public void changeRadioState(boolean newState)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for resetting the radio
     * @author Philipp Schulz
     */
    public void resetRadio()
    {
        //TODO: IMPLEMENT METHOD
    }
}
