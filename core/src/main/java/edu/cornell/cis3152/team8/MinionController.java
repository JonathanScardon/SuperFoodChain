package edu.cornell.cis3152.team8;
//Heavily inspired by AILab AI Controller

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;


public class MinionController implements InputController {

    // Instance Attributes
    /** The minion identifier for this minion controller */
    private int id;
    /** The minion controlled by this AI */
    private Minion minion;
    /** The target companion. */
    private Companion target;
    /** The state of the game (needed by the AI) */
    private GameState session;
    /** The ship's current state in the FSM */
    private FSMState state;
    /** The minion's next action. */
    private int move; // A ControlCode
    /** The number of ticks since we started this controller */
    private long ticks;

    public MinionController(int id, GameState session){
        this.id = id;
        state = FSMState.SPAWN;
        move  = CONTROL_NO_ACTION;
        ticks = 0;
        target = null;
        this.session = session;
    }

    @Override
    public int getAction() {
        return 0;
    }

    /**Enumeration to encode the finite state machine.*/
    private static enum FSMState {
        /** The minion just spawned */
        SPAWN,
        /** The minion is chasing its target */
        CHASE
    }


}
