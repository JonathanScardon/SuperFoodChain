package edu.cornell.cis3152.team8;

public abstract class BossController implements InputController {

    protected enum FSMState {
        /** Either the boss just spawned or it is waiting to attack again */
        IDLE,
        /** The boss is about to attack, and it is marking the tiles for the player to avoid */
        WARN,
        /** The boss is attacking */
        ATTACK,
    }

    /** The boss to be controlled */
    protected Boss boss;
    /** The state of the game */
    protected GameState gameState;
    /** The Boss's current state in the FSM */
    protected FSMState state;
    /** The Boss's next action as a control code */
    protected int action;
    /** The number of ticks since we started this controller */
    protected long ticks;

    public BossController(Boss boss, GameState gameState) {
        this.state = FSMState.IDLE;
        this.action  = CONTROL_NO_ACTION;
        this.gameState = gameState;
        this.boss = boss;
    }

    @Override
    public int getAction() {
        return action;
    }

    public abstract void attack();
}
