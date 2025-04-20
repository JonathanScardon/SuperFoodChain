package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;

public abstract class BossController implements InputController {
    /**
     * The boss to be controlled
     */
    protected Boss boss;
    /**
     * The state of the game
     */
    protected GameState gameState;
    /**
     * The Boss's next action as a control code
     */
    protected int action;
    /**
     * The set of attack patterns that the boss cycles through
     */
    protected Array<BossAttackPattern> attackPatterns;
    /**
     * The index of the attack pattern that the boss is currently warning/executing
     */
    protected int curAttackIdx;

    public BossController(Boss boss, GameState gameState) {
        this.gameState = gameState;
        this.boss = boss;
        this.action = CONTROL_NO_ACTION;
        this.attackPatterns = new Array<>();
        this.curAttackIdx = 0;
    }

    /**
     * Add an attack pattern to the boss' list of possible attacks
     * @param attackPattern the attack pattern to add
     */
    public void addAttackPattern(BossAttackPattern attackPattern) {
        attackPatterns.add(attackPattern);
    }

    /**
     * Starts the current attack of the boss
     * If there are no attacks left, resets current attack index to 0
     */
    public void startAttack() {
        if (curAttackIdx >= this.attackPatterns.size) {
            curAttackIdx = 0;
        }
        this.attackPatterns.get(curAttackIdx).start();
    }

    @Override
    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void update(float delta) {
        // if we finished the current attack do the next one in the queue
        if (this.attackPatterns.get(curAttackIdx).isEnded()) {
            curAttackIdx++;
            this.startAttack();
        }
        this.attackPatterns.get(curAttackIdx).update(delta);
    }
}
