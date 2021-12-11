package model.ActionQueue;

import model.Constants.ActionQueue.ActionQueueConstants;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Container class used by the ActionQueueManager for executing actions
 * @author Philipp Schulz
 */
public class ActionQueue implements ActionQueueConstants
{
    // local fields
    private final ArrayBlockingQueue<Action> actionQueue;

    /**
     * Constructor of the ActionQueue class
     * @author Philipp
     */
    public ActionQueue()
    {
        this.actionQueue = new ArrayBlockingQueue<>(MAXIMUM_QUEUE_SIZE);
    }

    /**
     * Method for adding a new action to the action queue
     * @param action Object of the Action class that should be performed
     */
    public void addNewActionToActionQueue(Action action)
    {
        this.actionQueue.add(action);
    }

    /**
     * Method for getting the next action from the action queue
     * @return Instance of the Action class containing all information of the next action
     * @author Philipp Schulz
     */
    public Action getNextActionFromQueue()
    {
        return actionQueue.poll();
    }

    /**
     * Method for getting the instance of the action queue
     * @return ArrayBlockingQueue of type Action
     * @author Philipp Schulz
     */
    public ArrayBlockingQueue<Action> getActionQueue()
    {
        return this.actionQueue;
    }
}
