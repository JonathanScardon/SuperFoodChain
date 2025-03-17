package edu.cornell.cis3152.team8;

public class BossController implements InputController {

    private enum FSMState {
        /** Either the boss just spawned or it is waiting to attack again */
        IDLE,
        /** The boss is about to attack, and it is marking the tiles for the player to avoid */
        WARN,
        /** The boss is attacking */
        ATTACK,
    }

    private int id;
    /** The boss to be controlled */
    private Boss boss;
    /** The player */
    private Player player;
    /** The state of the game */
    private GameState session;
    /** The Boss's current state in the FSM */
    private FSMState state;
    /** The Boss's next action as a control code */
    private int action;
    /** The number of ticks since we started this controller */
    private long ticks;

    public BossController(int id, GameState session) {
        this.id = id;
        this.state = FSMState.IDLE;
        this.action  = CONTROL_NO_ACTION;
        this.ticks = 0;
        this.player = null;
        this.session = session;
    }

    @Override
    public int getAction() {
        return action;
    }
}
