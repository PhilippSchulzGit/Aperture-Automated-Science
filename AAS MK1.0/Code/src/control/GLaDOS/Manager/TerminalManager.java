package control.GLaDOS.Manager;

import control.AUTO.Components.IdManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.GLaDOS.Manager.TerminalManagerConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Class for handling the interaction of AAS with terminals
 * Only called once by GLaDOS!
 * @author Philipp Schulz
 */
public class TerminalManager implements TerminalManagerConstants, ActionHandlingConstants
{
    // objects of all required components of TerminalManager
    private final ActionQueuePublic actionQueue;
    private final IdManager idManager;
    // local fields
    private volatile boolean isActive;
    private int ownID;
    private Thread terminalInputThread;

    /**
     * Constructor of the TerminalManager class
     * @param actionQueue Reference to the ActionQueuePublic instance
     * @param idManager Instance of the IdManager
     * @author Philipp Schulz
     */
    public TerminalManager(ActionQueuePublic actionQueue, IdManager idManager)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.idManager = idManager;
        // initialize local fields
        this.isActive = INITIAL_STATE_IS_ACTIVE;
        this.ownID = INITIAL_STATE_OWN_ID;
        // start thread for receiving input from the terminal
        getInputFromTerminal();
        // print notice for help to terminal
        printToTerminal(HELP_NOTE);
    }

    /**
     * Method to get input from the terminal, only possible when isActive==true
     * @author Philipp Schulz
     */
    private void getInputFromTerminal()
    {
        // create new thread for handling terminal inputs
        terminalInputThread = new Thread(() ->
        {
            // create Reader for terminal inputs
            InputStreamReader terminalInputStream = new InputStreamReader(System.in);
            BufferedReader terminalReader = new BufferedReader(terminalInputStream);
            // loop until the flag is no longer set
            while (isActive)
            {
                try
                {
                    // check if the reader is ready
                    if(terminalReader.ready())
                    {
                        // get input from terminal
                        String terminalInput = terminalReader.readLine();
                        // check if any input is available from the terminal
                        if(terminalInput != null)
                        {
                            // check if the input is valid
                            if(terminalInput.length()>TERMINAL_INPUT_MINIMUM_LENGTH)
                            {
                                // handle the received input
                                handleTerminalInput(terminalInput);
                            }
                        }
                    }
                }
                catch(Exception ignored)
                {

                }
                // catch any errors that might occur
                try
                {
                    // sleep for the specified time
                    TimeUnit.MILLISECONDS.sleep(TERMINAL_INPUT_WAIT_TIME);
                }
                catch(Exception ignored)
                {

                }
            }
        });
        // start the thread for terminal inputs
        terminalInputThread.start();
    }

    /**
     * Method for handling an input that came from the terminal
     * @param input String that was received from the terminal
     * @author Philipp Schulz
     */
    private void handleTerminalInput(String input)
    {
        // go over possible input structures
        switch (input.toUpperCase(Locale.ROOT))
        {
            case TERMINAL_INPUT_HELP:               // if the user needs help with terminal inputs
                // print out list of available commands with descriptions
                printToTerminal(HELP_PRINT_START);
                printToTerminal(TERMINAL_INPUT_HELP+"               "+HELP_PRINT_BINDER+HELP_DESCRIPTION_HELP);
                printToTerminal(TERMINAL_INPUT_SHUTDOWN+"           "+HELP_PRINT_BINDER+HELP_DESCRIPTION_SHUTDOWN);
                printToTerminal(TERMINAL_INPUT_COMPONENT_LIST+HELP_PRINT_BINDER+HELP_DESCRIPTION_COMPONENT_LIST);
                //TODO: PRINT POSSIBLE COMMANDS & IDS TO TERMINAL
                break;

            case TERMINAL_INPUT_SHUTDOWN:           // if the user wants to end the program
                // create a new action for initiating shutdown
                Action shutdownAction = new Action(this.idManager.getComponentIDByName(NAME_ACTION_QUEUE_MANAGER),
                        this.ownID, SHUTDOWN_WAIT_FOR_EXECUTION, TERMINAL_INPUT_SHUTDOWN, SHUTDOWN_ERROR_COUNT);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(shutdownAction);
                // give confirmation to terminal
                printToTerminal(TERMINAL_INPUT_CONFIRMATION);
                break;

            case TERMINAL_INPUT_COMPONENT_LIST:     // if the user wants to read the component list file
                // create a new action for reading the component list file
                Action autoAction = new Action(this.idManager.getComponentIDByName(NAME_FILE_MANAGER),
                        this.ownID, SHUTDOWN_WAIT_FOR_EXECUTION, TERMINAL_INPUT_COMPONENT_LIST, SHUTDOWN_ERROR_COUNT);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(autoAction);
                // give confirmation to terminal
                printToTerminal(TERMINAL_INPUT_CONFIRMATION);
                break;
            default:                                // if the user gave an invalid command
                // give feedback to terminal
                printToTerminal(TERMINAL_INPUT_ERROR);
                break;
        }
        //TODO: IMPLEMENT METHOD
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
        System.out.println("TerminalManager: "+id);
    }

    /**
     * Central method for handling any action that is targeted towards all components under TerminalManager
     * only called by GLaDOS!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // go over all possible cases
        if(action.getAction().equals(TERMINAL_INPUT_SHUTDOWN))  // if the shutdown action was received
        {
            this.isActive = false;
            this.terminalInputThread.interrupt();
        }
        else if(action.getAction().contains(ACTION_PRINT))      // if something should be printed to the terminal
        {
            printToTerminal(action.getAction().substring(action.getAction().indexOf(SPACE)+INCREMENT));
        }
        //TODO: IMPLEMENT METHOD
    }

    /**
     * Method to handle the printing of a given text to the terminal
     * @param text String that should be printed to the terminal
     * @author Philipp Schulz
     */
    public void printToTerminal(String text)
    {
        // print text to terminal
        System.out.println(text);
    }
}
