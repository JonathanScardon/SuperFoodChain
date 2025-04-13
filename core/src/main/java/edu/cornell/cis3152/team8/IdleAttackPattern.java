package edu.cornell.cis3152.team8;

import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will idle in the center of the screen
 */
public class IdleAttackPattern implements BossAttackPattern {
    private final BossController controller;
    private final Boss boss;
    private final BossWarnPattern warnPattern;

    private final float idleX, idleY;
    private final float warnDuration;
    private final float attackDuration;

    private float warnTime;
    private float attackTime;
    private AttackState state;

    public IdleAttackPattern(BossController controller, float x, float y, float warnDuration, float attackDuration, SpriteSheet warnSprite) {
        this.controller = controller;
        this.boss = controller.boss;

        this.idleX = x;
        this.idleY = y;
        this.warnDuration = warnDuration;
        this.attackDuration = attackDuration;

        this.warnPattern = new BossWarnPattern(this.idleX, this.idleY);
        this.warnPattern.setSpriteSheet(warnSprite);
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);

        warnTime = warnDuration;
        attackTime = attackDuration;

        warnPattern.active = true;
        boss.curWarn = warnPattern;
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(CONTROL_NO_ACTION);

        boss.setX(this.idleX);
        boss.setY(this.idleY);
        attackTime = attackDuration;
        boss.angle = 90f; // make the boss face upwards

        warnPattern.active = false;
        boss.curWarn = null;
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case WARN:
                if (warnTime > 0) {
                    warnTime -= delta;
                }
                if (warnEnded()) {
                    attack();
                }
                break;
            case ATTACK:
                if (attackTime > 0) {
                    attackTime -= delta;
                }
                break;
        }
    }

    private boolean warnEnded() {
        return warnTime <= 0;
    }

    @Override
    public boolean ended() {
        return attackTime <= 0;
    }
}
