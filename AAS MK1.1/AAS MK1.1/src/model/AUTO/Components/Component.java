package model.AUTO.Components;

/**
 * Container class used by the IdManager and FileManager for component identification
 * @author Philipp Schulz
 */
public class Component
{
    // local fields
    private int componentID;
    private String componentName;

    /**
     * Constructor of the Component class
     * @param componentID ID of the component
     * @param componentName Name of the component
     * @author Philipp Schulz
     */
    public Component(int componentID, String componentName)
    {
        // initialize local fields
        this.componentID = componentID;
        this.componentName = componentName;
    }

    /**
     * Method for getting the component id saved in this instance
     * @return Component ID in form of an Integer
     * @author Philipp Schulz
     */
    public int getComponentID()
    {
        return this.componentID;
    }

    /**
     * Method for getting the component name saved in this instance
     * @return Component name in form of a String
     * @author Philipp Schulz
     */
    public String getComponentName()
    {
        return this.componentName;
    }

    /**
     * Method for setting a new component id
     * @param componentID Component ID in form of an Integer
     * @author Philipp Schulz
     */
    public void setComponentID(int componentID)
    {
        this.componentID = componentID;
    }

    /**
     * Method for setting a new component name
     * @param componentName Component name in form of a String
     * @author Philipp Schulz
     */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }
}
