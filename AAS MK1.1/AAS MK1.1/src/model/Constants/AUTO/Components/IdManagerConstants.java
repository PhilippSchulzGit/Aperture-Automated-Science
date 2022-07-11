package model.Constants.AUTO.Components;

/**
 * Interface containing all constants used in the IdManager class
 * @author Philipp Schulz
 */
public interface IdManagerConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    int DEFAULT_COMPONENT_ID = -1;                                  // default return value of getComponentIDByName()
    String DEFAULT_COMPONENT_NAME = "";                             // default return value of getComponentNameByOldID()

}
