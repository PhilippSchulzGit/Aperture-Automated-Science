package control.GLaDOS;

import control.AUTO.Components.IdManager;
import control.GLaDOS.Devices.DeviceManager;
import control.GLaDOS.Manager.TerminalManager;
import control.GLaDOS.Network.NetworkManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.GLaDOS.GladosConstants;

import java.util.Locale;

/**
 * Class for handling anything of the section "facility management"
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class Glados implements GladosConstants, ActionHandlingConstants
{
    // objects of all required components of GLaDOS
    private final ActionQueuePublic actionQueue;
    private final DeviceManager deviceManager;
    private final NetworkManager networkManager;
    private final TerminalManager terminalManager;
    private final IdManager idManager;
    // local fields
    private boolean dialogueActive;
    private boolean receivedInput;
    private int ownID;
    private int dialogueNumber;
    private int[] stateMachines;
    private String lastMessage;

    /**
     * Constructor of the GLaDOS class
     * @param actionQueue Instance of the public action queue
     * @param idManager Instance of the IdManager from Auto
     * @author Philipp Schulz
     */
    public Glados(ActionQueuePublic actionQueue, IdManager idManager)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.idManager = idManager;
        this.terminalManager = new TerminalManager(actionQueue, idManager);
        this.deviceManager = new DeviceManager(actionQueue, idManager);
        this.networkManager = new NetworkManager(actionQueue, idManager);
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.dialogueNumber = DIALOGUE_NUMBER_DEFAULT;
        this.stateMachines = STATE_MACHINES_DEFAULT;
        this.dialogueActive = DIALOGUE_INACTIVE;
        this.receivedInput = RECEIVED_INPUT_START;
        this.lastMessage = EMPTY_STRING;
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
     * Method to set IDs for all components under this class
     * @author Philipp Schulz
     */
    public void setComponentIDs()
    {
        // set own ID
        setOwnID(this.idManager.getComponentIDByName(this.getClass().getSimpleName()));
        // set IDs of components
        this.networkManager.setOwnID(this.idManager.getComponentIDByName(this.networkManager.getClass().getSimpleName()));
        this.terminalManager.setOwnID(idManager.getComponentIDByName(this.terminalManager.getClass().getSimpleName()));
        this.deviceManager.setComponentIDs();
    }

    /**
     * Central method for handling any action that is targeted towards all components under GLaDOS
     * only called by ActionQueueManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // determine first digit of targetIndex for components with sub-components
        int targetIndex = Integer.parseInt(Integer.toString(action.getTargetIndex()).substring(INDEX_ONE, INDEX_THREE));
        // go through all components of GLaDOS
        if(action.getTargetIndex() == this.terminalManager.getOwnID())      // action is for TerminalManager
        {
            // let the TerminalManager handle the action
            this.terminalManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.networkManager.getOwnID())  // action is for NetworkManager
        {
            // let the NetworkManager handle the action
            this.networkManager.handleAction(action);
        }
        else if(targetIndex == Integer.parseInt(Integer.toString(this.deviceManager.getOwnID()).substring(INDEX_ONE, INDEX_THREE)))  // action is for DeviceManager
        {
            // let the DeviceManager handle the action
            this.deviceManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.ownID)                      // action is for this class
        {
            // handle the content of the received action
            if(action.getAction().equals(SHUTDOWN_ACTION))                  // if the action is for shutting down
            {
                // create action to shut down TerminalManager
                Action shutdownAction = new Action(this.terminalManager.getOwnID(), this.ownID, false, SHUTDOWN_ACTION, 0);
                // send action to TerminalManager
                this.actionQueue.addNewActionToActionQueue(shutdownAction);
                // adjust action for sending back
                action.setAction(SHUTDOWN_CONFIRMATION);
                action.setTargetIndex(action.getOriginIndex());
                action.setOriginIndex(this.getOwnID());
                // put action back into action queue
                this.actionQueue.addNewActionToActionQueue(action);
            }
            // if the action is for an input from the voice recognition or terminal
            else if(action.getAction().contains(ACTION_VOICE_RECOGNITION_INPUT) || action.getAction().contains(ACTION_TERMINAL_INPUT))
            {
                // pass action on to sound output handler method
                handleInputDuringSoundOutput(action.getAction());
            }
            else if(action.getAction().contains(ACTION_SOUND_OUTPUT_BUSY))  // if the action is for giving the sound output state
            {
                // pass action on to sound output handler method
                handleInputDuringSoundOutput(action.getAction());
            }
        }
        else
        {
            // handle undefined action
            action.setAction(action.getAction() + WRONG_COMPONENT_ID + action.getTargetIndex());
            action.setTargetIndex(action.getOriginIndex());
            action.setOriginIndex(this.ownID);
            action.setErrorCount(action.getErrorCount()+ERROR_COUNT_INCREMENT);
            this.actionQueue.addNewActionToActionQueue(action);
        }
    }

    /**
     * Method to handle voice recognition during sound output
     * @param completeAction String that was contained in the last action
     * @author Philipp Schulz
     */
    private void handleInputDuringSoundOutput(String completeAction)
    {
        // check if an input was already received from the voice recognition
        if(this.receivedInput)
        {
            // check if the action contains the state of the sound output
            if(completeAction.contains(ACTION_SOUND_OUTPUT_BUSY))
            {
                // if the sound output is not currently busy
                if(completeAction.contains(VALID_SOUND_OUTPUT))
                {
                    // print input from voice recognition to terminal, create a new action for that
                    Action terminalPrintAction = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                            this.idManager.getComponentIDByName(VOICE_RECOGNITION_MANAGER_NAME),
                            WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+this.lastMessage,ERROR_COUNT_NEW);
                    // put actions into action queue
                    this.actionQueue.addNewActionToActionQueue(terminalPrintAction);
                    // handle the previous voice recognition input
                    handleDialogueInput(this.lastMessage);
                }
            }
            // reset fields for next input
            this.receivedInput = false;
            this.lastMessage = EMPTY_STRING;
        }
        else    // if the input from the voice recognition is given first
        {
            // extract message from action
            String actionContent = completeAction.trim().substring(completeAction.indexOf(SPACE) + SUBSTRING_OFFSET_1);
            // check if the current action is an input from the voice recognition
            if(completeAction.contains(ACTION_VOICE_RECOGNITION_INPUT))
            {
                // set fields to save current action for next cycle
                this.receivedInput = !RECEIVED_INPUT_START;
                this.lastMessage = actionContent;
                // create new action to request current sound output state
                Action soundOutputStateAction = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                        this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_SOUND_OUTPUT_BUSY,ERROR_COUNT_NEW);
                // put actions into action queue
                this.actionQueue.addNewActionToActionQueue(soundOutputStateAction);

            }
            if(completeAction.contains(ACTION_TERMINAL_INPUT))  // if the input came from the terminal
            {
                // pass the input to the state machines, terminal input does not depend on sound output
                handleDialogueInput(actionContent);
            }
        }
    }

    /**
     * Method for handling any input that was received via voice recognition or terminal
     * @param input String that contains the input from the user
     * @author Philipp Schulz
     */
    public void handleDialogueInput(String input)
    {
        // check if new dialogue is active
        if(!this.dialogueActive && input.contains(RECOGNITION_GLADOS))
        {
            // set flag for active dialogue
            this.dialogueActive = DIALOGUE_ACTIVE;
            // set dialogue number
            this.dialogueNumber = DIALOGUE_STATE_MACHINE_0;
        }

        // state machine 0 is implemented here: dialogue activation / abortion + dialogue decision
        // if a dialogue is currently active
        if(this.dialogueActive)
        {
            // check if the dialogue should be aborted
            if(input.equals(RECOGNITION_ABORT))
            {
                // reset the dialogue number
                this.dialogueNumber = DIALOGUE_NUMBER_DEFAULT;
                // reset the dialogue flag
                this.dialogueActive = DIALOGUE_INACTIVE;
                // reset the state machines
                this.stateMachines = STATE_MACHINES_DEFAULT;

                // create new action for playing the corresponding sound of Glados
                Action abortAnswerGladosAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                        this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_GLADOS+ RECOGNITION_ABORTED,ERROR_COUNT_NEW);
                // create new action for terminal output of Glados
                Action abortAnswerGladosText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                        this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+RECOGNITION_ABORTED,
                        ERROR_COUNT_NEW);
                // add actions to ActionQueue
                this.actionQueue.addNewActionToActionQueue(abortAnswerGladosAudio);
                this.actionQueue.addNewActionToActionQueue(abortAnswerGladosText);
            }
            // match input to state machine
            switch(dialogueNumber)
            {
                case DIALOGUE_STATE_MACHINE_0:  // state machine 0
                    // match input to state machine
                    if(input.contains(GREETING_1) || input.contains(GREETING_2)) // state machine 1
                    {
                        // set dialogue number
                        this.dialogueNumber = DIALOGUE_STATE_MACHINE_1;
                        // go through the state machine
                        stateMachine1(stateMachines[DIALOGUE_STATE_MACHINE_1], input);
                    }
                    else if(input.contains(RECOGNITION_START_1) || input.contains(RECOGNITION_START_2)) // first response for dialogues
                    {
                        // create new action for playing the corresponding sound of Glados
                        Action initialAnswerGladosAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                                this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_GLADOS+ RECOGNITION_START,ERROR_COUNT_NEW);
                        // create new action for terminal output of Glados
                        Action initialAnswerGladosText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                                this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+ RECOGNITION_START,
                                ERROR_COUNT_NEW);
                        // add actions to ActionQueue
                        this.actionQueue.addNewActionToActionQueue(initialAnswerGladosAudio);
                        this.actionQueue.addNewActionToActionQueue(initialAnswerGladosText);
                    }
                    else if(input.toLowerCase(Locale.ROOT).contains(INPUT_TURN_OFF) ||
                            input.toLowerCase(Locale.ROOT).contains(INPUT_SHUTDOWN) ||
                            input.toLowerCase(Locale.ROOT).contains(INPUT_REBOOT))       // state machine 2
                    {
                        // set dialogue number
                        this.dialogueNumber = DIALOGUE_STATE_MACHINE_2;
                        // go through the state machine
                        stateMachines[DIALOGUE_STATE_MACHINE_2] = stateMachine2(stateMachines[DIALOGUE_STATE_MACHINE_2], input);
                    }
                    //####################################################################################################
                    //------------------------ADD START CONDITIONS OF NEW STATE MACHINES HERE-----------------------------
                    //####################################################################################################
                    break;

                case DIALOGUE_STATE_MACHINE_1:     // state machine 1
                    // call state machine
                    stateMachine1(stateMachines[DIALOGUE_STATE_MACHINE_1], input);
                    // state machine 1 does not return a state, not saved in stateMachines Array
                    break;

                case DIALOGUE_STATE_MACHINE_2:     // state machine 2
                    // call state machine
                    stateMachines[DIALOGUE_STATE_MACHINE_2] = stateMachine2(stateMachines[DIALOGUE_STATE_MACHINE_2], input);
                    // if a shutdown, reboot or turning off was achieved
                    if(STATE_MACHINE_STATE_3<stateMachines[DIALOGUE_STATE_MACHINE_2] && stateMachines[DIALOGUE_STATE_MACHINE_2]<STATE_MACHINE_STATE_7)
                    {
                        stateMachines[DIALOGUE_STATE_MACHINE_2] = stateMachine2(stateMachines[DIALOGUE_STATE_MACHINE_2], input);
                    }
                    break;

                default:
                    // none other state machines are active
            }
        }
    }

    /**
     * Method for implementing state machine 1
     * @param state current state of the state machine
     * @param input String that contains the input that was given by the user
     * @author Philipp Schulz
     */
    private void stateMachine1(int state, String input)
    {
        // state machine for greeting

        // go over all possible states
        // default state
        if (state == STATE_MACHINE_STATE_0)             // check if correct greetings were used
        {
            if (input.contains(GREETING_1) || input.contains(GREETING_2))
            {
                // create new action for playing the corresponding sound of Glados
                Action answerGladosAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_PLAY_SOUND_GLADOS + GREETING_RESPONSE,
                        ERROR_COUNT_NEW);
                // create new action for terminal output of Glados
                Action answerGladosText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_TERMINAL_MANAGER_PRINT+GREETING_RESPONSE+DOT,
                        ERROR_COUNT_NEW);
                // create new action to avoid deadlock of VoiceRecognitionManager
                Action notifyVoiceRecognitionManager = new Action(this.idManager.getComponentIDByName(VOICE_RECOGNITION_MANAGER_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_VOICE_RECOGNITION_ABORT,
                        ERROR_COUNT_NEW);
                // add actions to ActionQueue
                this.actionQueue.addNewActionToActionQueue(answerGladosAudio);
                this.actionQueue.addNewActionToActionQueue(answerGladosText);
                this.actionQueue.addNewActionToActionQueue(notifyVoiceRecognitionManager);

                // reset dialogue flags to avoid being stuck in a dialogue

                // reset the dialogue number
                this.dialogueNumber = DIALOGUE_NUMBER_DEFAULT;
                // reset the dialogue flag
                this.dialogueActive = DIALOGUE_INACTIVE;
                // reset the state machines
                this.stateMachines = STATE_MACHINES_DEFAULT;
            }
        }
        // no return statement due to insufficient complexity of state machine
    }

    /**
     * Method for implementing state machine 2
     * @param state current state of the state machine
     * @param input String that contains the input that was given by the user
     * @return Updated state of the state machine
     * @author Philipp Schulz
     */
    private int stateMachine2(int state, String input)
    {
        // state machine for processing turn off, shutdown and reboot
        int newState = state;
        // Strings for answers to avoid duplicate code in cases
        String answerStringSound = EMPTY_STRING;
        String answerStringText = EMPTY_STRING;
        // go over all possible states
        switch(state)
        {
            case STATE_MACHINE_STATE_0: // initial state
                if(input.toLowerCase(Locale.ROOT).contains(INPUT_TURN_OFF))   // if the program should be turned off
                {
                    newState = STATE_MACHINE_STATE_1;               // save the new state for next cycle
                    answerStringSound = TURN_OFF_RESPONSE;          // save answer String
                    answerStringText = TURN_OFF_RESPONSE+Q_MARK;    // save answer String
                }
                else if(input.toLowerCase(Locale.ROOT).contains(INPUT_SHUTDOWN)) // if the device should be shut down
                {
                    newState = STATE_MACHINE_STATE_2;               // save the new state for next cycle
                    answerStringSound = SHUTDOWN_RESPONSE;          // save answer String
                    answerStringText = SHUTDOWN_RESPONSE+Q_MARK;    // save answer String
                }
                else if(input.toLowerCase(Locale.ROOT).contains(INPUT_REBOOT)) // if the device should be rebooted
                {
                    newState = STATE_MACHINE_STATE_3;               // save the new state for next cycle
                    answerStringSound = REBOOT_RESPONSE;            // save answer String
                    answerStringText = REBOOT_RESPONSE+Q_MARK;      // save answer String
                }
                break;

            case STATE_MACHINE_STATE_1: // state for awaiting confirmation of turn off
                if(input.toLowerCase(Locale.ROOT).contains(CONFIRMATION_YES))       // if the confirmation was successful
                {
                    newState = STATE_MACHINE_STATE_4;               // save the new state for next cycle
                    answerStringSound = TURN_OFF_CONFIRM;           // save answer String
                    answerStringText = TURN_OFF_CONFIRM+DOT;        // save answer String
                }
                else if(input.toLowerCase(Locale.ROOT).contains(CONFIRMATION_NO))   // if the confirmation was aborted
                {
                    newState = STATE_MACHINE_STATE_0;               // save the new state for next cycle
                    answerStringSound = TURN_OFF_ABORT;             // save answer String
                    answerStringText = TURN_OFF_ABORT+DOT;          // save answer String
                    // reset the dialogue number
                    this.dialogueNumber = DIALOGUE_NUMBER_DEFAULT;
                    // reset the dialogue flag
                    this.dialogueActive = DIALOGUE_INACTIVE;
                    // reset the state machines
                    this.stateMachines = STATE_MACHINES_DEFAULT;
                    // create new action to avoid deadlock of VoiceRecognitionManager
                    Action notifyVoiceRecognitionManager = new Action(this.idManager.getComponentIDByName(VOICE_RECOGNITION_MANAGER_NAME),
                            this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_VOICE_RECOGNITION_ABORT,
                            ERROR_COUNT_NEW);
                    // add action into queue
                    this.actionQueue.addNewActionToActionQueue(notifyVoiceRecognitionManager);
                }
                break;

            case STATE_MACHINE_STATE_2: // state for awaiting confirmation of shutdown
                if(input.toLowerCase(Locale.ROOT).contains(CONFIRMATION_YES))       // if the confirmation was successful
                {
                    newState = STATE_MACHINE_STATE_5;               // save the new state for next cycle
                    answerStringSound = SHUTDOWN_CONFIRM;           // save answer String
                    answerStringText = SHUTDOWN_CONFIRM+DOT;        // save answer String
                }
                else if(input.toLowerCase(Locale.ROOT).contains(CONFIRMATION_NO))   // if the confirmation was aborted
                {
                    newState = STATE_MACHINE_STATE_0;               // save the new state for next cycle
                    answerStringSound = SHUTDOWN_ABORT;             // save answer String
                    answerStringText = SHUTDOWN_ABORT+DOT;          // save answer String
                    // reset the dialogue number
                    this.dialogueNumber = DIALOGUE_NUMBER_DEFAULT;
                    // reset the dialogue flag
                    this.dialogueActive = DIALOGUE_INACTIVE;
                    // reset the state machines
                    this.stateMachines = STATE_MACHINES_DEFAULT;
                    // create new action to avoid deadlock of VoiceRecognitionManager
                    Action notifyVoiceRecognitionManager = new Action(this.idManager.getComponentIDByName(VOICE_RECOGNITION_MANAGER_NAME),
                            this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_VOICE_RECOGNITION_ABORT,
                            ERROR_COUNT_NEW);
                    // add action into queue
                    this.actionQueue.addNewActionToActionQueue(notifyVoiceRecognitionManager);
                }
                break;

            case STATE_MACHINE_STATE_3: // state for awaiting confirmation of reboot
                if(input.toLowerCase(Locale.ROOT).contains(CONFIRMATION_YES))       // if the confirmation was successful
                {
                    newState = STATE_MACHINE_STATE_6;               // save the new state for next cycle
                    answerStringSound = REBOOT_CONFIRM;             // save answer String
                    answerStringText = REBOOT_CONFIRM+DOT;          // save answer String
                }
                else if(input.toLowerCase(Locale.ROOT).contains(CONFIRMATION_NO))   // if the confirmation was aborted
                {
                    newState = STATE_MACHINE_STATE_0;               // save the new state for next cycle
                    answerStringSound = REBOOT_ABORT;               // save answer String
                    answerStringText = REBOOT_ABORT+DOT;            // save answer String
                    // reset the dialogue number
                    this.dialogueNumber = DIALOGUE_NUMBER_DEFAULT;
                    // reset the dialogue flag
                    this.dialogueActive = DIALOGUE_INACTIVE;
                    // reset the state machines
                    this.stateMachines = STATE_MACHINES_DEFAULT;
                    // create new action to avoid deadlock of VoiceRecognitionManager
                    Action notifyVoiceRecognitionManager = new Action(this.idManager.getComponentIDByName(VOICE_RECOGNITION_MANAGER_NAME),
                            this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_VOICE_RECOGNITION_ABORT,
                            ERROR_COUNT_NEW);
                    // add action into queue
                    this.actionQueue.addNewActionToActionQueue(notifyVoiceRecognitionManager);
                }
                break;

            case STATE_MACHINE_STATE_4: // state for confirmation of turn off
                newState = STATE_MACHINE_STATE_0;                   // save the new state for next cycle
                answerStringSound = TURN_OFF_CONFIRMATION;          // save answer String
                answerStringText = TURN_OFF_CONFIRMATION+DOT;       // save answer String
                // create a new action for playing sounds for initiating turning off
                Action turnOffSoundAction = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_PLAY_SOUND_GLADOS+TURN_OFF_CONFIRMATION, ERROR_COUNT_NEW);
                // put action into ActionQueue
                this.actionQueue.addNewActionToActionQueue(turnOffSoundAction);
                // create a new action for initiating turning off
                Action turnOffAction = new Action(this.idManager.getComponentIDByName(ACTION_QUEUE_MANAGER_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, TURN_OFF_ACTION, ERROR_COUNT_NEW);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(turnOffAction);
                break;

            case STATE_MACHINE_STATE_5: // state for confirmation of shutdown
                newState = STATE_MACHINE_STATE_0;                   // save the new state for next cycle
                answerStringSound = SHUTDOWN_CONFIRMATION_TEXT;     // save answer String
                answerStringText = SHUTDOWN_CONFIRMATION_TEXT+DOT;  // save answer String
                // create a new action for initiating shutdown
                Action shutdownAction = new Action(this.idManager.getComponentIDByName(ACTION_QUEUE_MANAGER_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, SHUTDOWN_ACTION, ERROR_COUNT_NEW);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(shutdownAction);
                break;

            case STATE_MACHINE_STATE_6: // state for confirmation of reboot
                newState = STATE_MACHINE_STATE_0;                   // save the new state for next cycle
                answerStringSound = REBOOT_CONFIRMATION;            // save answer String
                answerStringText = REBOOT_CONFIRMATION+DOT;         // save answer String
                // create a new action for initiating reboot
                Action rebootAction = new Action(this.idManager.getComponentIDByName(ACTION_QUEUE_MANAGER_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, REBOOT_ACTION, ERROR_COUNT_NEW);
                // add action to the ActionQueue
                actionQueue.addNewActionToActionQueue(rebootAction);
                break;
        }
        // check if any action should be put into the queue
        if(answerStringSound.length() > MINIMUM_ANSWER_LENGTH) // only one needs to be checked as both are always assigned
        {
            // create new action for playing the corresponding sound of Glados
            Action answerGladosAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                    this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_PLAY_SOUND_GLADOS + answerStringSound,
                    ERROR_COUNT_NEW);
            // create new action for terminal output of Glados
            Action answerGladosText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                    this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_TERMINAL_MANAGER_PRINT+answerStringText,
                    ERROR_COUNT_NEW);
            // add actions to ActionQueue
            this.actionQueue.addNewActionToActionQueue(answerGladosAudio);
            this.actionQueue.addNewActionToActionQueue(answerGladosText);
        }
        return newState;
    }

    // TODO: ADD MORE STATE MACHINES ACCORDING TO DIALOGUES
}
