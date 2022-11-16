package model.ActionQueue;

import model.Constants.ActionQueue.ActionConstants;

/**
 * Container class used by the ActionQueueManager for performing new actions
 * @author Philipp Schulz
 */
public class Action implements ActionConstants
{
    // local fields
    private int targetIndex;
    private int originIndex;
    private int errorCount;
    private boolean waitForExecution;
    private String action;

    /**
     * Constructor of the action class
     * @param targetIndex ID of the target component
     * @param originIndex ID of the origin component
     * @param waitForExecution Boolean to decide if the component should wait for the execution
     * @param action String that contains the action that should be performed
     * @param errorCount Number of failed cycles of this event
     * @author Philipp Schulz
     */
    public Action(int targetIndex, int originIndex, boolean waitForExecution, String action, int errorCount)
    {
        // initialize local fields
        this.targetIndex = targetIndex;
        this.originIndex = originIndex;
        this.waitForExecution = waitForExecution;
        this.action = action;
        this.errorCount = errorCount;
    }

    /**
     * Method for getting the target component ID
     * @return ID of the target component
     * @author Philipp Schulz
     */
    public int getTargetIndex()
    {
        return targetIndex;
    }

    /**
     * Method for getting the origin component ID
     * @return ID of the origin component
     * @author Philipp Schulz
     */
    public int getOriginIndex() {
        return originIndex;
    }

    /**
     * Method for getting the information if execution should be waited for
     * @return True if the component should wait for the execution
     * @author Philipp Schulz
     */
    public boolean getWaitForExecution() {
        return waitForExecution;
    }

    /**
     * Method for getting the action that should be executed
     * @return String that contains the action that should be executed
     * @author Philipp Schulz
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Method for getting the number of failed cycles of this action
     * @return Number of failed action cycles
     * @author Philipp Schulz
     */
    public int getErrorCount()
    {
        return this.errorCount;
    }

    /**
     * Method for setting a new target component ID
     * @param targetIndex New ID of the target component
     * @author Philipp Schulz
     */
    public void setTargetIndex(int targetIndex)
    {
        this.targetIndex = targetIndex;
    }

    /**
     * Method for setting a new origin component ID
     * @param originIndex New ID of the origin component
     * @author Philipp Schulz
     */
    public void setOriginIndex(int originIndex)
    {
        this.originIndex = originIndex;
    }

    /**
     * Method for setting a new state for the field waitForExecution
     * @param waitForExecution New state for the field waitForExecution
     * @author Philipp Schulz
     */
    public void setWaitForExecution(boolean waitForExecution)
    {
        this.waitForExecution = waitForExecution;
    }

    /**
     * Method for setting a new action
     * @param action String that contains the new action
     * @author Philipp Schulz
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * Method for setting a new action failed cycle count
     * @param newCount New count of failed action cycles
     * @author Philipp Schulz
     */
    public void setErrorCount(int newCount)
    {
        this.errorCount = newCount;
    }

}
