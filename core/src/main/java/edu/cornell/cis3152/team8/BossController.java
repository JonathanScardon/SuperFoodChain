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

    /** The boss to be controlled */
    private Boss boss;
    /** The state of the game */
    private GameState gameState;
    /** The Boss's current state in the FSM */
    private FSMState state;
    /** The Boss's next action as a control code */
    private int action;

    public BossController(int id, GameState gameState) {
        this.state = FSMState.IDLE;
        this.action  = CONTROL_NO_ACTION;
        this.gameState = gameState;
    }

    @Override
    public int getAction() {
        return action;
    }
}
