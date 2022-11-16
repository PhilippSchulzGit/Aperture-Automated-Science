package control.AUTO.Files;

import model.AUTO.Components.Component;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.AUTO.Files.FileManagerConstants;
import model.Constants.ActionHandlingConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
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
    private int soundManagerID;
    private String homePath;

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
        this.soundManagerID = INITIAL_STATE_OWN_ID;
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
        else if(action.getAction().split(SPACE)[INDEX_ZERO].equals(ACTION_DEVICE_MANAGER_OS))      // if the os of the system is returned
        {
            // get from action if the program runs on Windows or not
            String os = action.getAction().substring(action.getAction().indexOf(ACTION_DEVICE_MANAGER_OS)
                    +ACTION_DEVICE_MANAGER_OS.length()).toLowerCase(Locale.ROOT).trim();
            // check if the program runs on Windows
            if(os.contains(OS_WINDOWS))
            {
                // get absolute home path to program directory on Windows
                this.homePath = new File(System.getProperty(USER_DIR)).getAbsolutePath() + SLASH;
            }
            else    // if the program runs on Linux (aka on a Raspberry Pi)
            {
                // get absolute home path to program directory on Raspberry Pi
                this.homePath = PI_DIR;
            }
            // create new action to notify SoundManager
            Action notifyAction = new Action(soundManagerID,this.ownID,WAIT_FOR_EXECUTION_FALSE,NOTIFY_COMPONENT,ERROR_COUNT_NEW);
            // put actions into action queue
            this.actionQueue.addNewActionToActionQueue(notifyAction);
        }
        else if(action.getAction().contains(ACTION_GET_PATH_TO))        // if the path to a folder or file is requested
        {
            // check what file or folder is requested
            if(action.getAction().contains(AUTO_COMPONENT_NAME))        // if the folder for the sound files of AUTO is requested
            {
                // create new actions to deliver the path to sound files for AUTO
                Action autoPathAction = new Action(soundManagerID,this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_GET_PATH_TO+SPACE+
                        SOUND_FILE_PATH_AUTO,ERROR_COUNT_NEW);
                // put action into action queue
                this.actionQueue.addNewActionToActionQueue(autoPathAction);
            }
            else if(action.getAction().contains(GLADOS_COMPONENT_NAME)) // if the folder for the sound files of GLaDOS is requested
            {
                // create new actions to deliver the path to sound files for AUTO
                Action gladosPathAction = new Action(soundManagerID,this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_GET_PATH_TO+SPACE+
                        SOUND_FILE_PATH_GLADOS,ERROR_COUNT_NEW);
                // put action into action queue
                this.actionQueue.addNewActionToActionQueue(gladosPathAction);
            }
        }
        //TODO: IMPLEMENT METHOD

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
                        // save ID of SoundManager
                        if(componentName.equals(SOUND_MANAGER_COMPONENT_NAME))
                        {
                            this.soundManagerID = componentID;
                        }
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
