package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;

import java.util.Queue;

public abstract class BossController implements InputController {
    public enum FSMState {
        /**
         * Either the boss just spawned or it is waiting to attack again
         */
        IDLE,
        /**
         * The boss is about to attack, and it is marking the tiles for the player to avoid
         */
        WARN,
        /**
         * The boss is attacking
         */
        ATTACK,
    }

    /**
     * The boss to be controlled
     */
    protected Boss boss;
    /**
     * The state of the game
     */
    protected GameState gameState;
    /**
     * The Boss's current state in the FSM
     */
    protected FSMState state;
    /**
     * The Boss's next action as a control code
     */
    protected int action;
    /**
     * The number of ticks since we started this controller
     */
    protected long ticks;
    /**
     * The set of attack patterns that the boss can choose from
     */
    protected Array<BossAttackPattern> attackPatterns;
    /**
     * A queue of attacks the boss plans to use before idling again
     */
    protected Queue<Integer> plannedAttacks;
    /**
     * The attack pattern that the boss is currently warning/executing
     */
    protected BossAttackPattern curAttack;

    public BossController(Boss boss, GameState gameState) {
        this.state = FSMState.IDLE;
        this.action = CONTROL_NO_ACTION;
        this.gameState = gameState;
        this.boss = boss;
    }

    @Override
    public int getAction() {
        return action;
    }

    public void setState(FSMState state) {
        this.state = state;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
