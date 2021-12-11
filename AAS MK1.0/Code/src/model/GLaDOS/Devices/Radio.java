package model.GLaDOS.Devices;

import com.fazecast.jSerialComm.SerialPort;
import model.Constants.GLaDOS.Devices.RadioConstants;

/**
 *  Class representing the functionality of the radio
 *  This class only stores states, the actual logic is in the ComPortManager
 *  @author Philipp Schulz
 */
public class Radio implements RadioConstants
{
    // local fields
    private SerialPort comPort;
    private boolean isConnected;
    private boolean isEnabled;
    private int volume;

    /**
     * Constructor of the Radio class
     * @author Philipp Schulz
     */
    public Radio()
    {
        // initialize local fields
        this.isConnected = INITIAL_STATE_IS_CONNECTED;
        this.isEnabled = INITIAL_STATE_IS_ENABLED;
        this.volume = INITIAL_STATE_VOLUME;
    }

    /**
     * Method for getting the current SerialPort of the radio
     * @return SerialPort object of the COM port connection to the radio
     * @author Philipp Schulz
     */
    public SerialPort getComPort()
    {
        return comPort;
    }

    /**
     * Method for getting the current connection state of the radio
     * @return True if this program is connected to the radio
     * @author Philipp Schulz
     */
    public boolean getIsConnected()
    {
        return isConnected;
    }

    /**
     * Method for getting the current enabled state of the radio
     * @return True if the radio is currently turned on
     * @author Philipp Schulz
     */
    public boolean getIsEnabled()
    {
        return isEnabled;
    }

    /**
     * Method for getting the current volume of the speaker inside the radio
     * @return Integer in percent of max volume of the radio speaker
     * @author Philipp Schulz
     */
    public int getVolume()
    {
        return volume;
    }

    /**
     * Method for setting a new SerialPort of the radio
     * @param comPort SerialPort object of the COM port connection to the radio
     * @author Philipp Schulz
     */
    public void setComPort(SerialPort comPort)
    {
        this.comPort = comPort;
    }

    /**
     * Method for setting a new state for the connection status of the radio
     * @param connected New boolean state of the radio connection status
     * @author Philipp Schulz
     */
    public void setIsConnected(boolean connected)
    {
        isConnected = connected;
    }

    /**
     * Method for setting a new state for the enabled status of the radio
     * @param enabled New boolean state of the radio enabled status
     * @author Philipp Schulz
     */
    public void setIsEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }

    /**
     * Method for setting a new volume for the radio speaker
     * @param volume Integer representing the radio volume in percent
     * @author Philipp Schulz
     */
    public void setVolume(int volume)
    {
        this.volume = volume;
    }
}
