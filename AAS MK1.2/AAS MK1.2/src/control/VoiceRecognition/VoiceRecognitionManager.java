package control.VoiceRecognition;

import control.AUTO.Components.IdManager;
import model.ActionQueue.Action;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.ActionHandlingConstants;
import model.Constants.VoiceRecognition.VoiceRecognitionManagerConstants;
import model.VoiceRecognition.VoiceRecognitionCMU;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Class for realization of voice recognition for overall program
 * Only called once by AAS!
 * @author Philipp Schulz
 */
public class VoiceRecognitionManager implements VoiceRecognitionManagerConstants, ActionHandlingConstants
{
    // objects of all required components of VoiceRecognitionManager
    private final ActionQueuePublic actionQueue;
    private final VoiceRecognitionCMU voiceRecognitionCMU;
    private final IdManager idManager;
    // local fields
    private boolean voiceRecognitionSetUp;
    private boolean isAlive;
    private boolean dialogueGlados;
    private boolean dialogueAuto;
    private int ownID;
    private int receiverID;

    /**
     * Constructor of the VoiceRecognitionManager class
     * @param actionQueue Instance of the public action queue
     * @param idManager Instance of the IdManager from Auto
     * @author Philipp Schulz
     */
    public VoiceRecognitionManager(ActionQueuePublic actionQueue, IdManager idManager)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize rest of components
        this.idManager = idManager;
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.voiceRecognitionSetUp = INITIAL_STATE_VOICE_RECOGNITION_SET_UP;
        this.isAlive = INITIAL_STATE_IS_ALIVE;
        this.dialogueGlados = INITIAL_STATE_DIALOGUE_GLADOS;
        this.dialogueAuto = INITIAL_STATE_DIALOGUE_AUTO;
        this.receiverID = INITIAL_STATE_RECEIVER_ID;
        // start thread to instantiate the VoiceRecognitionCMU object
        this.voiceRecognitionCMU = new VoiceRecognitionCMU();
    }

    /**
     * Central method for handling any action that is targeted towards all components under VoiceRecognitionManager
     * only called by ActionQueueManager!
     * @param action Action that should be performed by a component
     * @author Philipp Schulz
     */
    public void handleAction(Action action)
    {
        // handle the content of the received action
        if(action.getAction().equals(SHUTDOWN_ACTION))    // if the action is for shutting down
        {
            // set flag for thread
            this.isAlive = KILL_VOICE_RECOGNITION;
            // adjust action for sending back
            action.setAction(SHUTDOWN_CONFIRMATION);
            action.setTargetIndex(action.getOriginIndex());
            action.setOriginIndex(this.getOwnID());
            // put action back into action queue
            this.actionQueue.addNewActionToActionQueue(action);
        }
        else if(action.getAction().equals(ACTION_VOICE_RECOGNITION_START))   // if the voice recognition should be started
        {
            // start up the recognition thread
            runRecognitionCMU();
            // create action for print to terminal
            Action terminalAction = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),this.ownID,
                    WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+RECOGNITION_START,ERROR_COUNT_NEW);
            // put action into ActionQueue
            this.actionQueue.addNewActionToActionQueue(terminalAction);
        }
        else if(action.getAction().equals(ACTION_VOICE_RECOGNITION_STOP))    // if the voice recognition should be stopped
        {
            // set new state for the voice recognition module, stopping it shortly after by itself
            this.isAlive = KILL_VOICE_RECOGNITION;
        }
        else if(action.getAction().contains(ACTION_DEVICE_MANAGER_OS))       // if the current OS was determined
        {
            try
            {
                // try to create the recognizer
                boolean answer = this.voiceRecognitionCMU.createRecognizer(action.getAction().substring(action.getAction().indexOf(SPACE)+SPACE.length()));
                // set the flag, only when successful
                if(answer)
                {
                    // set flag
                    this.voiceRecognitionSetUp = true;
                    // create action for sound output
                    Action soundStartAction = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),this.ownID,
                            WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_AUTO+RECOGNIZER_CREATION_SUCCESS.toLowerCase(Locale.ROOT),
                            ERROR_COUNT_NEW);
                    // put action into ActionQueue
                    this.actionQueue.addNewActionToActionQueue(soundStartAction);
                    // create action for print to terminal
                    Action terminalAction = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),this.ownID,
                            WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+RECOGNIZER_CREATION_SUCCESS,ERROR_COUNT_NEW);
                    // put action into ActionQueue
                    this.actionQueue.addNewActionToActionQueue(terminalAction);
                }
            }
            catch(Exception e)
            {
                // create new action to report the failed creation of the recognizer
                Action recognizerFailedAction = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),this.ownID,
                        WAIT_FOR_EXECUTION_FALSE, ACTION_TERMINAL_MANAGER_PRINT+ERROR_RECOGNIZER_CREATION,ERROR_COUNT_NEW);
                // put action into queue
                this.actionQueue.addNewActionToActionQueue(recognizerFailedAction);
            }
        }
        else if(action.getAction().contains(ACTION_VOICE_RECOGNITION_ABORT))    // if the current dialogue should be aborted
        {
            // reset flags to indicate dialogue and receiverID
            this.receiverID = INITIAL_STATE_RECEIVER_ID;
            this.dialogueGlados = DIALOGUE_INACTIVE;
            this.dialogueAuto = DIALOGUE_INACTIVE;
        }
        else if(action.getAction().contains(ACTION_HANDLE_ACTION) &&
                (action.getOriginIndex() == this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME))) // if a dialogue input was given via terminal
        {
            // handle the input as if it came from the voice recognition
            handleVoiceRecognitionResultSphinx4(action.getAction().substring(action.getAction().indexOf(SPACE)));
        }
    }

    /**
     * Method for starting and maintaining the voice recognition
     * @author Philipp Schulz
     */
    public void runRecognitionCMU()
    {
        // create new Thread for independent voice recognition & action handling
        Thread voiceRecognitionManagerThread = new Thread(() ->
        {
            boolean osRequested = false;
            // if this thread is called the first time
            if(!voiceRecognitionSetUp && isAlive)
            {
                // loop until voice recognition is set up (depends on os type)
                while(!voiceRecognitionSetUp && isAlive)
                {
                    // check if the OS was already requested
                    if(!osRequested)
                    {
                        // create a new action for the DeviceManager
                        Action getOSAction = new Action(idManager.getComponentIDByName(DEVICE_MANAGER_NAME),ownID,WAIT_FOR_EXECUTION_FALSE,
                                ACTION_DEVICE_MANAGER_OS, ERROR_COUNT_NEW);
                        // put action into ActionQueue
                        actionQueue.addNewActionToActionQueue(getOSAction);
                        // set flag to avoid spamming actions into queue
                        osRequested = true;
                    }
                    else
                    {
                        // catch any errors that might occur
                        try
                        {
                            // sleep for the specified time
                            TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
                        }
                        catch(Exception ignored)
                        {

                        }
                    }
                }
            }
            // start up the voice recognition
            this.voiceRecognitionCMU.setIsRunning(VOICE_RECOGNITION_START);
            // create action for sound output
            Action soundStartAction = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),this.ownID,
                    WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_AUTO+RECOGNITION_START.toLowerCase(Locale.ROOT),ERROR_COUNT_NEW);
            // put action into ActionQueue
            this.actionQueue.addNewActionToActionQueue(soundStartAction);
            // create action for print to terminal
            Action terminalStartAction = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),this.ownID,
                    WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+RECOGNITION_START,ERROR_COUNT_NEW);
            // put action into ActionQueue
            this.actionQueue.addNewActionToActionQueue(terminalStartAction);
            // loop until either the voice recognition is turned off or the program is shutting down
            while(voiceRecognitionCMU.getIsRunning() && isAlive)
            {
                // get the current result from voice recognition
                String recognitionResult = voiceRecognitionCMU.recognize();
                // check if recognized input is valid
                if(!recognitionResult.equals(EMPTY_STRING) && !recognitionResult.equals(RECOGNITION_UNKNOWN))
                {
                    // determine target component and handle content of result
                    handleVoiceRecognitionResultSphinx4(recognitionResult);
                }
                // wait for a short time
                try
                {
                    TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
                }
                catch(Exception ignored)
                {

                }
            }
            // stop the voice recognition, waits until newest result is gathered
            this.voiceRecognitionCMU.setIsRunning(VOICE_RECOGNITION_STOP);
            // create action for sound output
            Action soundStopAction = new Action(this.idManager.getComponentIDByName(SOUND_OUTPUT_NAME),this.ownID,
                    WAIT_FOR_EXECUTION_FALSE,ACTION_PLAY_SOUND_AUTO+RECOGNITION_STOP.toLowerCase(Locale.ROOT),ERROR_COUNT_NEW);
            // put action into ActionQueue
            this.actionQueue.addNewActionToActionQueue(soundStopAction);
            // create action for print to terminal
            Action terminalStopAction = new Action(this.idManager.getComponentIDByName(TERMINAL_MANAGER_NAME),this.ownID,
                    WAIT_FOR_EXECUTION_FALSE,ACTION_TERMINAL_MANAGER_PRINT+RECOGNITION_STOP,ERROR_COUNT_NEW);
            // put action into ActionQueue
            this.actionQueue.addNewActionToActionQueue(terminalStopAction);
        });
        // start the Thread
        voiceRecognitionManagerThread.start();
    }

    /**
     * Method for handling the content of a single voice recognition result from the Sphinx4 library
     * @author Philipp Schulz
     */
    public void handleVoiceRecognitionResultSphinx4(String result)
    {
        // remove leading spaces
        result = result.trim().replace(RECOGNITION_GLADOS_2,RECOGNITION_GLADOS);
        // check if the result actually contains something
        if(!result.equals(EMPTY_STRING) && !result.equals(RECOGNITION_UNKNOWN))
        {
            // check if the results are for Glados or Auto, starting the dialogue
            if(result.contains(RECOGNITION_GLADOS) && !this.dialogueGlados && !this.dialogueAuto) // if the dialogue is for Glados
            {
                // set the component ID for Glados
                this.receiverID = this.idManager.getComponentIDByName(GLADOS_NAME);

                // create new action for handling the input in Glados
                Action inputGlados = new Action(this.receiverID, this.ownID, WAIT_FOR_EXECUTION_FALSE,
                        ACTION_VOICE_RECOGNITION_INPUT+SPACE+result,ERROR_COUNT_NEW);
                // add action to ActionQueue
                this.actionQueue.addNewActionToActionQueue(inputGlados);

                // set flag to indicate dialogue with Glados
                this.dialogueGlados = DIALOGUE_ACTIVE;
            }
            else if(result.contains(RECOGNITION_AUTO) && !this.dialogueAuto && !this.dialogueGlados) // if the dialogue is for Auto
            {
                // set the component ID for Glados
                this.receiverID = this.idManager.getComponentIDByName(AUTO_NAME);

                // create new action for handling the input in Auto
                Action inputAuto = new Action(this.receiverID, this.ownID, WAIT_FOR_EXECUTION_FALSE,
                        ACTION_VOICE_RECOGNITION_INPUT+SPACE+result,ERROR_COUNT_NEW);
                // add action to ActionQueue
                this.actionQueue.addNewActionToActionQueue(inputAuto);

                // set flag to indicate dialogue with Auto
                this.dialogueAuto = DIALOGUE_ACTIVE;
            }
            else if(this.dialogueAuto || this.dialogueGlados) // check if any dialogue is active
            {
                // create new action to send recognized input to dialogue partner
                Action inputAll = new Action(this.receiverID, this.ownID, WAIT_FOR_EXECUTION_FALSE,
                        ACTION_VOICE_RECOGNITION_INPUT+SPACE+result,ERROR_COUNT_NEW);
                // put action into ActionQueue
                this.actionQueue.addNewActionToActionQueue(inputAll);
                // handle case of aborting dialogue
                if(result.contains(RECOGNITION_ABORT))
                {
                    // reset flags to indicate dialogue and receiverID
                    this.receiverID = INITIAL_STATE_RECEIVER_ID;
                    this.dialogueGlados = DIALOGUE_INACTIVE;
                    this.dialogueAuto = DIALOGUE_INACTIVE;
                }
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
}
