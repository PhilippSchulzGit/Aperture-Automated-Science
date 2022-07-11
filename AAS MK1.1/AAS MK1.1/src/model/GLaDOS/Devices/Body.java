package model.GLaDOS.Devices;

import model.Constants.GLaDOS.Devices.BodyConstants;

/**
 *  Class representing the functionality of the physical body of GLaDOS
 *  This class only stores states, the actual logic is in the BodyManager
 *  @author Philipp Schulz
 */
public class Body implements BodyConstants
{
    //local fields
    private int simonID;
    private boolean isReachable;
    private boolean lampState;
    private boolean eyeState;
    private boolean servoState;
    private double servo1Position;
    private double servo2Position;
    private double servo3Position;
    private double servo4Position;

    /**
     * Constructor of the Body class
     * @author Philipp Schulz
     */
    public Body()
    {
        // initialize local fields
        this.isReachable = INITIAL_STATE_IS_REACHABLE;
        this.lampState = INITIAL_STATE_LAMP_STATE;
        this.eyeState = INITIAL_STATE_EYE_STATE;
        this.servoState = INITIAL_STATE_SERVO_STATE;
        this.servo1Position = INITIAL_STATE_SERVO_1_POSITION;
        this.servo2Position = INITIAL_STATE_SERVO_2_POSITION;
        this.servo3Position = INITIAL_STATE_SERVO_3_POSITION;
        this.servo4Position = INITIAL_STATE_SERVO_4_POSITION;
        this.simonID = INITIAL_STATE_SIMON_ID;
    }

    /**
     * Method to set a new state whether the physical body is reachable
     * @author Philipp Schulz
     */
    public void setAvailability(boolean newState)
    {
        this.isReachable = newState;
    }

    /**
     * Method for setting a new state for the lamps on GLaDOS
     * @param newState New state of the lamp (true == on)
     * @author Philipp Schulz
     */
    public void setLampState(boolean newState)
    {
        this.lampState = newState;
    }

    /**
     * Method for setting a new state for the eye
     * @param newState New state of the eye (true == on)
     * @author Philipp Schulz
     */
    public void setEyeState(boolean newState)
    {
        this.eyeState = newState;
    }

    /**
     * Method for setting a new state for the servos
     * @param newState New state of the servos
     * @author Philipp Schulz
     */
    public void setServoState(boolean newState)
    {
        this.servoState = newState;
    }

    /**
     * Method to set a new position of a specified servo
     * @param servoNumber Index of the servo that should change position
     * @param newPosition New position of the specified servo
     * @author Philipp Schulz
     */
    public void setServoPosition(int servoNumber, double newPosition)
    {
        switch(servoNumber)
        {
            case INDEX_SERVO_1:
                this.servo1Position = newPosition;
                break;
            case INDEX_SERVO_2:
                this.servo2Position = newPosition;
                break;
            case INDEX_SERVO_3:
                this.servo3Position = newPosition;
                break;
            case INDEX_SERVO_4:
                this.servo4Position = newPosition;
                break;
        }
    }

    /**
     * Method to set a new simon ID
     * @param simonID ID of the body in the SIMON protocol
     * @author Philipp Schulz
     */
    public void setSimonID(int simonID)
    {
        this.simonID = simonID;
    }

    /**
     * Method for getting the connection status to the physical body
     * @return True if the physical body is reachable
     * @author Philipp Schulz
     */
    public boolean getAvailability()
    {
        return this.isReachable;
    }

    /**
     * Method for getting the current lamp state
     * @return True if the lamp is currently on
     * @author Philipp Schulz
     */
    public boolean getLampState()
    {
        return this.lampState;
    }

    /**
     * Method for getting the current eye state
     * @return True if the eye is currently on
     * @author Philipp Schulz
     */
    public boolean getEyeState()
    {
        return this.eyeState;
    }

    /**
     * Method for getting the current servo state
     * @return True if the servos are powered
     * @author Philipp Schulz
     */
    public boolean getServoState()
    {
        return this.servoState;
    }

    /**
     * Method for getting the current position of a specified servo
     * @param servoNumber Index of the servo which position should be found
     * @return Current position of the specified servo
     */
    public double getServoPosition(int servoNumber)
    {
        // assign default return value
        double servoPosition = SERVO_DEFAULT_ANSWER_POSITION;
        // switch for different servos
        switch(servoNumber)
        {
            case INDEX_SERVO_1:     // servo 1
                servoPosition = this.servo1Position;
                break;
            case INDEX_SERVO_2:     // servo 2
                servoPosition = this.servo2Position;
                break;
            case INDEX_SERVO_3:     // servo 3
                servoPosition = this.servo3Position;
                break;
            case INDEX_SERVO_4:     // servo 4
                servoPosition = this.servo4Position;
                break;
        }
        return servoPosition;
    }

    /**
     * Method for getting the ID of the body in the SIMON protocol
     * @return ID of the body in the SIMON protocol
     * @author Philipp Schulz
     */
    public int getSimonID()
    {
        return this.simonID;
    }
}
