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
    private SpriteSheet attackSprite;

    public IdleAttackPattern(BossController controller, float x, float y, float warnDuration,
        float attackDuration, SpriteSheet warnSprite, SpriteSheet attackSprite) {
        this.controller = controller;
        boss = controller.boss;
        boss.setSpriteSheet(attackSprite);
        this.idleX = x;
        this.idleY = y;
        this.warnDuration = warnDuration;
        this.attackDuration = attackDuration;
        this.attackSprite = attackSprite;

        this.warnPattern = new BossWarnPattern(this.idleX, this.idleY);
        this.warnPattern.setSpriteSheet(warnSprite);

        this.state = AttackState.INACTIVE;
        boss.setState("inactive");
    }

    @Override
    public void start() {
        boss.setAttack("idle");
        state = AttackState.WARN;
        boss.setState("warn");
        controller.setAction(CONTROL_NO_ACTION);

        warnTime = warnDuration;
        attackTime = attackDuration;

        warnPattern.active = true;
        boss.curWarn = warnPattern;
    }

    public void attack() {
        state = AttackState.ATTACK;
        boss.setState("attack");
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
            case INACTIVE -> {
            }
            case WARN -> {
                if (warnTime > 0) {
                    warnTime -= delta;
                } else {
                    attack();
                }
                boss.setSpriteSheet(attackSprite);
            }
            case ATTACK -> {
                if (attackTime > 0) {
                    attackTime -= delta;
                }
            }
        }
    }

    @Override
    public boolean ended() {
        return state == AttackState.ATTACK && attackTime <= 0;
    }
}
