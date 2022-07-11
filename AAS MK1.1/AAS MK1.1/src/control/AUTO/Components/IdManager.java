package control.AUTO.Components;

import model.AUTO.Components.Component;
import model.ActionQueue.ActionQueuePublic;
import model.Constants.AUTO.Components.IdManagerConstants;

import java.util.ArrayList;


/**
 * Class for handling anything that has to do with component ids
 * Only called once by Auto!
 * @author Philipp Schulz
 */
public class IdManager implements IdManagerConstants
{
    // objects of all required components of IdManager
    private final ActionQueuePublic actionQueue;

    // local fields
    private ArrayList<Component> componentList;
    private ArrayList<Component> componentListOld;
    private int ownID;

    /**
     * Constructor of the IdManager class
     * @param actionQueue Instance of the public action queue
     * @author Philipp Schulz
     */
    public IdManager(ActionQueuePublic actionQueue)
    {
        // save reference to instance of the ActionQueue
        this.actionQueue = actionQueue;
        // initialize local fields
        this.ownID = INITIAL_STATE_OWN_ID;
        this.componentList = new ArrayList<>();
        this.componentListOld = new ArrayList<>();
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
     * Method for getting the component id for a given component name
     * @param componentName String that contains the (class) name of the component
     * @return ID of the component, -1 if not found in component list
     * @author Philipp Schulz
     */
    public int getComponentIDByName(String componentName)
    {
        // initialize return value
        int componentID = DEFAULT_COMPONENT_ID;
        // loop over component list
        for (Component component : componentList) {
            // check if the current component name matches
            if (component.getComponentName().equals(componentName)) {
                // save current component ID
                componentID = component.getComponentID();
                // break the loop to save time
                break;
            }
        }
        // return component ID
        return componentID;
    }

    /**
     * Method for getting the component name for a given old component ID
     * @param oldID Old component ID of a specific component
     * @return Component name, "" if not found in old component list
     * @author Philipp Schulz
     */
    public String getComponentNameByOldID(int oldID)
    {
        // initialize return value
        String componentName = DEFAULT_COMPONENT_NAME;
        // loop over old component list
        for(Component component : componentListOld)
        {
            // check if the current component ID matches
            if(component.getComponentID() == oldID)
            {
                // save current component name
                componentName = component.getComponentName();
                // break the loop to save time
                break;
            }
        }
        // return component name
        return componentName;
    }

    /**
     * Method for getting the component name for a given component ID
     * @param ID Component ID of a specific component
     * @return Component name, "" if not found in old component list
     * @author Philipp Schulz
     */
    public String getComponentNameByID(int ID)
    {
        // initialize return value
        String componentName = DEFAULT_COMPONENT_NAME;
        // loop over old component list
        for(Component component : componentList)
        {
            // check if the current component ID matches
            if(component.getComponentID() == ID)
            {
                // save current component name
                componentName = component.getComponentName();
                // break the loop to save time
                break;
            }
        }
        // return component name
        return componentName;
    }

    /**
     * Method to set a new component list, e.g. when the component list was updated
     * @param componentList ArrayList of Type Component containing the contents of the component list file
     * @author Philipp Schulz
     */
    public void setComponentList(ArrayList<Component> componentList)
    {
        // save current component list as old list
        this.componentListOld = this.componentList;
        // save new component list as current list
        this.componentList = componentList;
    }
}
