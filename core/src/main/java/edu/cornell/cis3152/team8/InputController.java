package edu.cornell.cis3152.team8;

public interface InputController {
    // Constants for the control codes
    /** Do not do anything */
    int CONTROL_NO_ACTION  = 0x00;
    /** Move to the left */
    int CONTROL_MOVE_LEFT  = 0x01;
    /** Move to the right */
    int CONTROL_MOVE_RIGHT = 0x02;
    /** Move up */
    int CONTROL_MOVE_UP    = 0x04;
    /** Move down */
    int CONTROL_MOVE_DOWN  = 0x08;
    /** Use ability */
    int CONTROL_USE_ABILITY = 0x10;

    int getAction();
}
