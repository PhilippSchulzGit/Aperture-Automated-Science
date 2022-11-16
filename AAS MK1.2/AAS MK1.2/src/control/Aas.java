package control;

import control.AUTO.Auto;
import control.ActionQueue.ActionQueueManager;
import control.GLaDOS.Glados;
import control.Sound.SoundManager;
import control.VoiceRecognition.VoiceRecognitionManager;
import model.Constants.AASConstants;
import model.Constants.ActionHandlingConstants;

/**
 * Overall control point of all software components of AAS. Only called once by Main!
 * @author Philipp Schulz
 */
public class Aas implements AASConstants, ActionHandlingConstants
{
    // objects of all required components of AAS
    private ActionQueueManager actionQueueManager;
    private Auto auto;
    private Glados glados;
    private SoundManager soundManager;
    private VoiceRecognitionManager voiceRecognitionManager;

    /**
     * Method to start the overall program. Only called once by Main!
     * @author Philipp Schulz
     */
    public void startAAS()
    {
        // initialize components
        this.actionQueueManager = new ActionQueueManager();
        this.auto = new Auto(this.actionQueueManager.getPublicActionQueue());
        this.glados = new Glados(this.actionQueueManager.getPublicActionQueue(), this.auto.getIdManager());
        this.soundManager = new SoundManager(this.actionQueueManager.getPublicActionQueue(),
                this.auto.getIdManager().getComponentIDByName(COM_PORT_MANAGER_NAME),
                this.auto.getIdManager().getComponentIDByName(FILE_MANAGER_NAME));
        this.voiceRecognitionManager = new VoiceRecognitionManager(this.actionQueueManager.getPublicActionQueue(), this.auto.getIdManager());
        // save references to all above components in ActionQueueManager
        this.actionQueueManager.setObjectReferences(this.auto, this.glados, this.soundManager, this.voiceRecognitionManager);
        // give all components an ID
        this.actionQueueManager.setComponentIDs();
        // start ActionQueueSystem
        this.actionQueueManager.handleActions();
    }
}
