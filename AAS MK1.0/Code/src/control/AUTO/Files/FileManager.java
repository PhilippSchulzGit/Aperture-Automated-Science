package control.AUTO.Files;

import model.AUTO.Components.Component;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.AUTO.Files.FileManagerConstants;
import model.Constants.ActionHandlingConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for handling anything that has to do with files
 * Only called once by AUTO!
 * @author Philipp Schulz
 */
public class FileManager implements FileManagerConstants, ActionHandlingConstants
{
    // objects of all required components of GLaDOS
    private final ActionQueuePublic actionQueue;

    // local fields
    private ArrayList<Component> componentList;
    private File componentListFile;
    private int ownID;

    /**
     * Constructor of the FileManager class
     * @param actionQueue Instance of the public action queue
     * @author Philipp Schulz
     */
    public FileManager(ActionQueuePublic actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.componentListFile = new File(COMPONENT_LIST_PATH);
        this.componentList = new ArrayList<>();
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
     * Central method for handling any action that is targeted towards all components under FileManager
     * only called by AUTO!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // go over all possible actions
        if(action.getAction().equals(ACTION_READ_COMPONENT_LIST))       // if the component list should be read
        {
            // read component list file
            readComponentList();
            // create new action for confirmation
            Action answerAction = new Action(action.getOriginIndex(),this.ownID, WAIT_FOR_EXECUTION_FALSE,
                    ACTION_TERMINAL_MANAGER_PRINT+COMPONENT_LIST_READ_CONFIRMATION, ERROR_COUNT_NEW);
            // put action into ActionQueue
            this.actionQueue.addNewActionToActionQueue(answerAction);
        }
        //TODO: IMPLEMENT METHOD
        System.out.println("hemlo am filemanager");

    }

    /**
     * Method for reading the content of the component list file
     * @author Philipp Schulz
     */
    public void readComponentList()
    {
        // catch errors that could occur during reading
        try
        {
            // initialize component list file
            componentListFile = new File(COMPONENT_LIST_PATH);
            // create scanner to read content of file
            Scanner componentListFileScanner = new Scanner(componentListFile);
            // loop over all lines of the file
            while(componentListFileScanner.hasNext())
            {
                // get the current line of the file
                String currentLine = componentListFileScanner.nextLine();
                // check if the line is valid
                if(currentLine.length()>COMPONENT_LIST_MINIMUM_LINE_LENGTH)
                {
                    // check if the line is not a comment
                    if(!currentLine.startsWith(COMPONENT_LIST_COMMENT))
                    {
                        // extract the current component name
                        String componentName = currentLine.substring(currentLine.indexOf(SPACE_STRING)+COMPONENT_LIST_NAME_OFFSET);
                        // extract the current component ID
                        int componentID = Integer.parseInt(currentLine.substring(COMPONENT_LIST_STRING_START,currentLine.indexOf(SPACE_STRING)));
                        // add the extracted name and ID to the list as a new component
                        this.componentList.add(new Component(componentID, componentName));
                    }
                }
            }
        }
        catch(Exception e)
        {
            // reinitialize the component list, resulting in an empty list
            this.componentList = new ArrayList<>();
        }
    }

    /**
     * Method for getting the content of the component list
     * @return ArrayList of type Component that contains the component list
     * @author Philipp Schulz
     */
    public ArrayList<Component> getComponentList()
    {
        return this.componentList;
    }
}
