package control.AUTO;

import control.AUTO.Components.IdManager;
import control.AUTO.Files.FileManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.AUTO.AutoConstants;
import model.Constants.ActionHandlingConstants;

import java.util.Locale;

/**
 * Class for handling anything of the section "data management"
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class Auto implements AutoConstants, ActionHandlingConstants
{
    // objects of all required components of Auto
    private final ActionQueuePublic actionQueue;
    private final FileManager fileManager;
    private final IdManager idManager;
    // local fields
    private int ownID;
    private int dialogueNumber;
    private int[] stateMachines;
    private boolean dialogueActive;

    /**
     * Constructor of the AUTO class
     * @param actionQueue Instance of the public action queue
     * @author Philipp Schulz
     */
    public Auto(ActionQueuePublic actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.fileManager = new FileManager(actionQueue);
        this.idManager = new IdManager(actionQueue);
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.dialogueNumber = DIALOGUE_NUMBER_DEFAULT;
        this.stateMachines = STATE_MACHINES_DEFAULT;
        this.dialogueActive = DIALOGUE_INACTIVE;

        // save component list with FileManager and save it in IdManager
        this.fileManager.readComponentList();
        this.idManager.setComponentList(this.fileManager.getComponentList());
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
        this.fileManager.setOwnID(this.idManager.getComponentIDByName(this.fileManager.getClass().getSimpleName()));
        this.idManager.setOwnID(this.idManager.getComponentIDByName(this.idManager.getClass().getSimpleName()));
    }

    /**
     * Central method for handling any action that is targeted towards all components under AUTO
     * only called by ActionQueueManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // determine first digit of targetIndex for components with sub-components
        //int targetIndex = Integer.parseInt(Integer.toString(action.getTargetIndex()).substring(INDEX_ONE, INDEX_THREE));
        // go over all components of this class (if-statements because switches only work with constant expressions)
        if(action.getTargetIndex() == this.fileManager.getOwnID())
        {
            // handle the action in the FileManager
            this.fileManager.handleAction(action);
        }
        else if(action.getTargetIndex() == this.ownID)  // if the action is for this class
        {
            // handle the content of the received action
            if(action.getAction().contains(UPDATE_OWN_ID))      // if the component ID should be updated
            {
                // update the internal component ID
                this.ownID = Integer.parseInt(action.getAction().substring(action.getAction().indexOf(UPDATE_OWN_ID)));
            }
            else if(action.getAction().equals(SHUTDOWN_ACTION))    // if the action is for shutting down
            {
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
                // handle input
                handleDialogueInput(action.getAction().trim().substring(action.getAction().indexOf(SPACE)+SUBSTRING_OFFSET_1));
            }
            //TODO: IMPLEMENT REST OF METHOD
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
     * Method for getting access to the IdManager for id handling at startup
     * @return Instance of the IdManager class
     * @author Philipp Schulz
     */
    public IdManager getIdManager()
    {
        return this.idManager;
    }

    /**
     * Method for handling any input that was received via voice recognition or terminal
     * @param input String that contains the input from the user
     * @author Philipp Schulz
     */
    public void handleDialogueInput(String input)
    {
        // check if new dialogue is active
        if(!this.dialogueActive && input.contains(RECOGNITION_AUTO))
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

                // create new action for playing the corresponding sound of Auto
                Action abortAnswerAutoAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                        this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_AUTO+ RECOGNITION_ABORTED,ERROR_COUNT_NEW);
                // create new action for terminal output of Auto
                Action abortAnswerAutoText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                        this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+RECOGNITION_ABORTED,
                        ERROR_COUNT_NEW);
                // add actions to ActionQueue
                this.actionQueue.addNewActionToActionQueue(abortAnswerAutoAudio);
                this.actionQueue.addNewActionToActionQueue(abortAnswerAutoText);

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
                        // create new action for playing the corresponding sound of Auto
                        Action initialAnswerAutoAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                                this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_AUTO+ RECOGNITION_START,ERROR_COUNT_NEW);
                        // create new action for terminal output of Auto
                        Action initialAnswerAutoText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                                this.ownID,WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+ RECOGNITION_START,
                                ERROR_COUNT_NEW);
                        // add actions to ActionQueue
                        this.actionQueue.addNewActionToActionQueue(initialAnswerAutoAudio);
                        this.actionQueue.addNewActionToActionQueue(initialAnswerAutoText);
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
                // create new action for playing the corresponding sound of Auto
                Action answerAutoAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_PLAY_SOUND_AUTO + GREETING_RESPONSE,
                        ERROR_COUNT_NEW);
                // create new action for terminal output of Auto
                Action answerAutoText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_TERMINAL_MANAGER_PRINT+GREETING_RESPONSE+DOT,
                        ERROR_COUNT_NEW);
                // create new action to avoid deadlock of VoiceRecognitionManager
                Action notifyVoiceRecognitionManager = new Action(this.idManager.getComponentIDByName(VOICE_RECOGNITION_MANAGER_NAME),
                        this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_VOICE_RECOGNITION_ABORT,
                        ERROR_COUNT_NEW);
                // add actions to ActionQueue
                this.actionQueue.addNewActionToActionQueue(answerAutoAudio);
                this.actionQueue.addNewActionToActionQueue(answerAutoText);
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
            // create new action for playing the corresponding sound of Auto
            Action answerAutoAudio = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),
                    this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_PLAY_SOUND_AUTO + answerStringSound,
                    ERROR_COUNT_NEW);
            // create new action for terminal output of Auto
            Action answerAutoText = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),
                    this.ownID, WAIT_FOR_EXECUTION_FALSE, ACTION_TERMINAL_MANAGER_PRINT+answerStringText,
                    ERROR_COUNT_NEW);
            // add actions to ActionQueue
            this.actionQueue.addNewActionToActionQueue(answerAutoAudio);
            this.actionQueue.addNewActionToActionQueue(answerAutoText);
        }
        return newState;
    }

    // TODO: ADD MORE STATE MACHINES ACCORDING TO DIALOGUES
}
