package model.ActionQueue;

import model.Constants.ActionQueue.ActionQueuePublicConstants;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Container class used by any classes for adding new actions to the ActionQueueSystem
 * @author Philipp Schulz
 */
public class ActionQueuePublic implements ActionQueuePublicConstants
{

    // local fields
    private final ArrayBlockingQueue<Action> actionQueue;

    /**
     * Constructor of the ActionQueue class
     * @author Philipp Schulz
     */
    public ActionQueuePublic(ActionQueue actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue.getActionQueue();
    }

    /**
     * Method for adding a new action to the action queue
     * @param action Object of the Action class that should be performed
     * @author Philipp Schulz
     */
    public void addNewActionToActionQueue(Action action)
    {
        this.actionQueue.add(action);
    }
}
