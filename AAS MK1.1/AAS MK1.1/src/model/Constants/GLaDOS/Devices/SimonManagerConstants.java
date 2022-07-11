package model.Constants.GLaDOS.Devices;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Interface containing all constants used in the SimonManager class
 * @author Philipp Schulz
 */
public interface SimonManagerConstants
{
    int INITIAL_STATE_OWN_ID = -1;                                  // default value for the field ownID
    boolean INITIAL_STATE_IS_BUSY = false;                          // default value for the field isBusy
    int OWN_SIMON_ID = 1;                                           // own SIMON ID
    Pin DATA_PIN = RaspiPin.GPIO_00;                                // piny of the data pin of the SIMON protocol
    Pin CLOCK_PIN = RaspiPin.GPIO_00;                               // pin of the clock pin of the SIMON protocol

}
