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
    private boolean receivedInput;
    private int ownID;
    private String lastMessage;
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
        this.receivedInput = RECEIVED_INPUT_START;
        this.lastMessage = EMPTY_STRING;
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
                                handleInputDuringSoundOutput(terminalInput);
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
        printToTerminal(NAME_TERMINAL_MANAGER+TERMINAL_PRINT_PREFIX+input);
        // go over possible input structures
        switch (input.toUpperCase(Locale.ROOT))
        {
            case TERMINAL_INPUT_HELP:               // if the user needs help with terminal inputs
                // print out list of available commands with descriptions
                printToTerminal(HELP_PRINT_START);
                printToTerminal(TERMINAL_INPUT_HELP+"               "+HELP_PRINT_BINDER+HELP_DESCRIPTION_HELP);
                printToTerminal(TERMINAL_INPUT_SHUTDOWN+"           "+HELP_PRINT_BINDER+HELP_DESCRIPTION_SHUTDOWN);
                printToTerminal(TERMINAL_INPUT_COMPONENT_LIST+HELP_PRINT_BINDER+HELP_DESCRIPTION_COMPONENT_LIST);
                printToTerminal(TERMINAL_INPUT_RECOGNITION_START+"           "+HELP_PRINT_BINDER+HELP_VOICE_RECOGNITION_START);
                printToTerminal(TERMINAL_INPUT_RECOGNITION_STOP+"            "+HELP_PRINT_BINDER+HELP_VOICE_RECOGNITION_STOP);
                printToTerminal(TERMINAL_INPUT_VOICE_RECOGNITION+"                 "+HELP_PRINT_BINDER+HELP_DESCRIPTION_DIALOGUE);
                printToTerminal(TERMINAL_INPUT_PLAY_SOUND+"                "+HELP_PRINT_BINDER+HELP_PLAY_SOUND);
                break;

            case TERMINAL_INPUT_SHUTDOWN:           // if the user wants to end the program
                // create a new action for initiating shutdown
                Action shutdownAction = new Action(this.idManager.getComponentIDByName(NAME_ACTION_QUEUE_MANAGER),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, SHUTDOWN_ACTION, ERROR_COUNT_NEW);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(shutdownAction);
                // give confirmation to terminal
                printToTerminal(TERMINAL_INPUT_CONFIRMATION);
                break;

            case TERMINAL_INPUT_COMPONENT_LIST:     // if the user wants to read the component list file
                // create a new action for reading the component list file
                Action autoAction = new Action(this.idManager.getComponentIDByName(NAME_FILE_MANAGER),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, TERMINAL_INPUT_COMPONENT_LIST, ERROR_COUNT_NEW);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(autoAction);
                // give confirmation to terminal
                printToTerminal(TERMINAL_INPUT_CONFIRMATION);
                break;
            case TERMINAL_INPUT_RECOGNITION_START:
                // create a new action for starting the voice recognition
                Action VRStartAction = new Action(this.idManager.getComponentIDByName(NAME_VOICE_RECOGNITION_MANAGER),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_VOICE_RECOGNITION_START, ERROR_COUNT_NEW);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(VRStartAction);
                // give confirmation to terminal
                printToTerminal(TERMINAL_INPUT_CONFIRMATION);
                break;
            case TERMINAL_INPUT_RECOGNITION_STOP:
                // create a new action for stopping the voice recognition
                Action VRStopAction = new Action(this.idManager.getComponentIDByName(NAME_VOICE_RECOGNITION_MANAGER),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_VOICE_RECOGNITION_STOP, ERROR_COUNT_NEW);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(VRStopAction);
                // give confirmation to terminal
                printToTerminal(TERMINAL_INPUT_CONFIRMATION);
                break;

            default:
                // get command from input
                String firstCommand = input.toUpperCase(Locale.ROOT).substring(SUBSTRING_START, input.indexOf(SPACE));
                // if a voice recognition command was given
                if(firstCommand.equals(TERMINAL_INPUT_VOICE_RECOGNITION))
                {
                    // get user command
                    String command = input.substring(input.indexOf(SPACE));
                    // create a new action for reading the component list file
                    Action dialogueAction = new Action(this.idManager.getComponentIDByName(NAME_VOICE_RECOGNITION_MANAGER),
                            this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_HANDLE_ACTION+SPACE+command, ERROR_COUNT_NEW);
                    // add action to the ActionQueue
                    actionQueue.addNewActionToActionQueue(dialogueAction);
                    // give confirmation to terminal
                    printToTerminal(TERMINAL_INPUT_CONFIRMATION);
                }
                else if(firstCommand.equals(TERMINAL_INPUT_PLAY_SOUND)) // if a sentence should be played as sound
                {
                    // split input into voice and sentence
                    String voice = input.split(SPACE)[COMMAND_VOICE_INDEX];
                    String sentence = input.substring(input.indexOf(voice)+voice.length()+INCREMENT).toLowerCase(Locale.ROOT);
                    // handle different voices
                    switch(voice.toUpperCase(Locale.ROOT))
                    {
                        case GLADOS_COMPONENT_NAME:     // voice of GLaDOS
                            // create new action for playing the corresponding sound of Glados
                            Action audioGladosAction = new Action(this.idManager.getComponentIDByName(NAME_SOUND_OUTPUT_MANAGER),
                                    this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_GLADOS+sentence,ERROR_COUNT_NEW);
                            // add action to ActionQueue
                            this.actionQueue.addNewActionToActionQueue(audioGladosAction);
                            break;
                        case AUTO_COMPONENT_NAME:       // voice of AUTO
                            // create new action for playing the corresponding sound of Glados
                            Action audioAutoAction = new Action(this.idManager.getComponentIDByName(NAME_SOUND_OUTPUT_MANAGER),
                                    this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_AUTO+ sentence,ERROR_COUNT_NEW);
                            // add action to ActionQueue
                            this.actionQueue.addNewActionToActionQueue(audioAutoAction);
                            break;
                        default:                        // unsupported voice
                            printToTerminal(PLAY_SOUND_WRONG_VOICE);
                    }
                }
                else         // if the user gave an invalid command
                {
                    // give feedback to terminal
                    printToTerminal(TERMINAL_INPUT_ERROR);
                }
                break;
        }
    }


    /**
     * Method to handle voice recognition during sound output
     * @param terminalInput String that was received from the terminal
     * @author Philipp Schulz
     */
    private void handleInputDuringSoundOutput(String terminalInput)
    {
        // check if an input was already received from the voice recognition
        if(this.receivedInput)
        {
            // check if the action contains the state of the sound output
            if(terminalInput.contains(ACTION_SOUND_OUTPUT_BUSY))
            {
                // if the sound output is not currently busy
                if(terminalInput.contains(VALID_SOUND_OUTPUT))
                {
                    // handle the previous voice recognition input
                    handleTerminalInput(this.lastMessage);
                }
                else    // if the sound output is currently busy
                {
                    printToTerminal(TERMINAL_INPUT_REJECT);
                }
            }
            // reset fields for next input
            this.receivedInput = false;
            this.lastMessage = EMPTY_STRING;
        }
        else    // if the input from the voice recognition is given first
        {
            // check if the current action is an input for the voice recognition dialogue
            if(terminalInput.split(SPACE)[ZERO_INDEX].toLowerCase(Locale.ROOT).equals(TERMINAL_INPUT_VOICE_RECOGNITION.toLowerCase(Locale.ROOT)))
            {
                // set fields to save current action for next cycle
                this.receivedInput = !RECEIVED_INPUT_START;
                this.lastMessage = terminalInput;
                // create new action to request current sound output state
                Action soundOutputStateAction = new Action(this.idManager.getComponentIDByName(NAME_SOUND_OUTPUT_MANAGER),
                        this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_SOUND_OUTPUT_BUSY,ERROR_COUNT_NEW);
                // put actions into action queue
                this.actionQueue.addNewActionToActionQueue(soundOutputStateAction);

            }
            else    // handle commands without regard to sound output
            {
                // pass the input to the state machines, terminal input does not depend on sound output
                handleTerminalInput(terminalInput);
            }
        }
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
        else if(action.getAction().startsWith(ACTION_PRINT))      // if something should be printed to the terminal
        {
            printToTerminal(this.idManager.getComponentNameByID(action.getOriginIndex())+TERMINAL_PRINT_PREFIX
                    +action.getAction().substring(action.getAction().indexOf(SPACE)+INCREMENT));
        }
        else if(action.getAction().contains(ACTION_SOUND_OUTPUT_BUSY))  // if the action is for giving the sound output state
        {
            // pass action on to sound output handler method
            handleInputDuringSoundOutput(action.getAction());
        }
    }

    /**
     * Method to handle the printing of a given text to the terminal
     * @param text String that should be printed to the terminal
     * @author Philipp Schulz
     */
    public void printToTerminal(String text)
    {
        // adjust content of text for glados and auto
        String newText = text.replace(GLADOS_WRONG_1,GLADOS_RIGHT).replace(GLADOS_WRONG_2,GLADOS_RIGHT).replace(GLADOS_WRONG_3,GLADOS_RIGHT);
        newText = newText.replace(AUTO_WRONG_1,AUTO_RIGHT).replace(AUTO_WRONG_2,AUTO_RIGHT);
        // print text to terminal
        System.out.println(newText);
    }
}
