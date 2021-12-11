package model.Constants.GLaDOS.Devices;

/**
 * Interface containing all constants used in the Body class
 * @author Philipp Schulz
 */
public interface BodyConstants
{
    boolean INITIAL_STATE_IS_REACHABLE = false;                     // default value for the field isReachable
    boolean INITIAL_STATE_LAMP_STATE = false;                       // default value for the field lampState
    boolean INITIAL_STATE_EYE_STATE = false;                        // default value for the field eyeState
    boolean INITIAL_STATE_SERVO_STATE = false;                      // default value for the field servoState
    int INITIAL_STATE_SERVO_1_POSITION = 0;                         // default value for the field servo1Position
    int INITIAL_STATE_SERVO_2_POSITION = 0;                         // default value for the field servo2Position
    int INITIAL_STATE_SERVO_3_POSITION = 0;                         // default value for the field servo3Position
    int INITIAL_STATE_SERVO_4_POSITION = 0;                         // default value for the field servo4Position
    int INITIAL_STATE_SIMON_ID = -1;                                // default value for the field simonID

    int INDEX_SERVO_1 = 1;                                          // index of servo 1
    int INDEX_SERVO_2 = 2;                                          // index of servo 2
    int INDEX_SERVO_3 = 3;                                          // index of servo 3
    int INDEX_SERVO_4 = 4;                                          // index of servo 4

    int SERVO_DEFAULT_ANSWER_POSITION = -9999;                      // default return value for method getServoPosition()
}
