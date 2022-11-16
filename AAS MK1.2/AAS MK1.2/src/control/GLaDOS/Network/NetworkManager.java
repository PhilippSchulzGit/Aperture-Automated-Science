package control.GLaDOS.Network;

import control.AUTO.Components.IdManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.GLaDOS.Network.NetworkManagerConstants;
import model.GLaDOS.Network.Message;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Class for handling network traffic between AAS and other devices
 * Only called once by GLaDOS!
 * @author Philipp Schulz
 */
public class NetworkManager implements NetworkManagerConstants
{
    // objects of all required components of NetworkManager
    private final ActionQueuePublic actionQueue;
    private final IdManager idManager;
    // local fields
    private int ownID;
    private InetAddress ownIpAddress;
    /**
     * Constructor of the TerminalManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @param idManager Instance of the IdManager
     * @author Philipp Schulz
     */
    public NetworkManager(ActionQueuePublic actionQueue, IdManager idManager)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.idManager = idManager;
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
     * Central method for handling any action that is targeted towards all components under NetworkManager
     * only called by GLaDOS!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method to find out if a device with the given address is reachable
     * @param address InetAddress of the target device
     * @return True if device is reachable
     * @author Philipp Schulz
     */
    public boolean isDeviceReachable(InetAddress address)
    {
        //TODO: IMPLEMENT METHOD
        return false;
    }

    /**
     * Method for receiving a message from a device with the given address
     * @param address InetAddress of the target device
     * @return Message object containing all information of the received message
     * @author Philipp Schulz
     */
    public Message receiveFromDevice(InetAddress address)
    {
        //TODO: IMPLEMENT METHOD
        return null;
    }

    /**
     * Method for sending an array of String-based messages to the given receiver address
     * @param address InetAddress of the target device
     * @param message ArrayList of type String containing all messages to send
     * @return True if all messages were sent successfully
     */
    public boolean sendToDevice(InetAddress address, ArrayList<String> message)
    {
        //TODO: IMPLEMENT METHOD
        return false;
    }
}
