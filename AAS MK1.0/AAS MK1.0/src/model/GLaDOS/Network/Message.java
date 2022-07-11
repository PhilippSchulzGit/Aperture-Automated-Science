package model.GLaDOS.Network;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Container class used by the NetworkManager for message transmission via network
 * @author Philipp Schulz
 */
public class Message
{
    // local fields
    private ArrayList<String> messages;
    private InetAddress receiverIpAddress;
    private InetAddress senderIpAddress;

    /**
     * Constructor of the Message class
     * @author Philipp Schulz
     */
    public Message(ArrayList<String> messages, InetAddress receiverIpAddress, InetAddress senderIpAddress)
    {
        // initialize local fields
        this.messages = messages;
        this.receiverIpAddress = receiverIpAddress;
        this.senderIpAddress = senderIpAddress;
    }

    /**
     * Method to get all messages contained in this class
     * @return ArrayList that contains all messages of type String
     * @author Philipp Schulz
     */
    public ArrayList<String> getMessages()
    {
        return messages;
    }

    /**
     * Method to get the receiver IP address
     * @return InetAddress of the receiver
     * @author Philipp Schulz
     */
    public InetAddress getReceiverIpAddress()
    {
        return receiverIpAddress;
    }

    /**
     * Method to get the sender IP address
     * @return InetAddress of the sender
     * @author Philipp Schulz
     */
    public InetAddress getSenderIpAddress()
    {
        return senderIpAddress;
    }

    /**
     * Method to set new messages
     * @param messages ArrayList containing all messages of type String
     * @author Philipp Schulz
     */
    public void setMessages(ArrayList<String> messages)
    {
        this.messages = messages;
    }

    /**
     * Method to set new receiver IP address
     * @param receiverIpAddress InetAddress of the receiver
     * @author Philipp Schulz
     */
    public void setReceiverIpAddress(InetAddress receiverIpAddress)
    {
        this.receiverIpAddress = receiverIpAddress;
    }

    /**
     * Method to set new sender IP Address
     * @param senderIpAddress InetAddress of the sender
     * @author Philipp Schulz
     */
    public void setSenderIpAddress(InetAddress senderIpAddress)
    {
        this.senderIpAddress = senderIpAddress;
    }
}
