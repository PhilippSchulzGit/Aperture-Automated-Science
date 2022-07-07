package control.GLaDOS.Devices;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.GLaDOS.Devices.GpioManagerConstants;

/**
 * Class for handling everything that has to do with the GPIO pins on the Raspberry Pi
 * Only called once by DeviceManager!
 * @author Philipp Schulz
 */
public class GpioManager implements GpioManagerConstants
{
    // objects of all required components of BodyManager
    private final ActionQueuePublic actionQueue;
    // local fields
    private int ownID;
    private int simonClockPinNumber;
    private int simonDataPinNumber;
    private boolean gpioEnabled;
    private GpioPinDigitalMultipurpose simonClockPin;
    private GpioPinDigitalMultipurpose simonDataPin;
    private GpioPinDigitalMultipurpose i2cSdaPin;
    private GpioPinDigitalMultipurpose i2cSclPin;
    private GpioController gpioController;

    /**
     * Constructor of the GpioManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @author Philipp Schulz
     */
    public GpioManager(ActionQueuePublic actionQueue, String operatingSystem)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        // check if this program runs on a Raspberry Pi (for GPIO control)
        if(operatingSystem.equals(OS_NAME_RASPBERRY))
        {
            // if this program runs on a Raspberry Pi
            this.gpioController = GpioFactory.getInstance();
            this.gpioEnabled = true;
        }
        else
        {
            // if this program does not run on a Raspberry Pi
            this.gpioController = null;
            this.gpioEnabled = false;
        }
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
        System.out.println("GpioManager: "+id);
    }

    /**
     * Central method for handling any action that is targeted towards all components under GpioManager
     * only called by DeviceManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for setting up the pins for the SIMON protocol
     * @param clockPin Pin of the simon clock pin
     * @param dataPin Pin of the simon data pin
     * @author Philipp Schulz
     */
    public void setUpSimonPins(int clockPin, int dataPin)
    {
        // save pins
        this.simonDataPinNumber = dataPin;
        this.simonClockPinNumber = clockPin;
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for setting a new pin state
     * @param pinNumber Pin for which the state should be changed
     * @param newState New state of the pin
     */
    public void setNewPinState(int pinNumber, boolean newState)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method for reading a pin state
     * @param pinNumber Pin for which the state should be read
     * @return True if the pin is high, false if the pin is low
     * @author Philipp Schulz
     */
    public boolean readPinState(int pinNumber)
    {
        //TODO: IMPLEMENT METHOD
        return false;
    }

    /**
     * Method for setting up the I2C pins of the Raspberry Pi GPIO
     * @author Philipp Schulz
     */
    public void setUpI2cPins()
    {
        //TODO: IMPLEMENT METHOD
    }


}
