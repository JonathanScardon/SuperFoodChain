package edu.cornell.cis3152.team8;

import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will idle in the center of the screen
 */
public class IdleAttackPattern extends BossAttackPattern {

    private final BossWarnPattern warnPattern;
    private final float idleX, idleY;
    private final float warnDuration;
    private final float attackDuration;
    private final boolean flipHorizontal;

    private float warnTime;
    private float attackTime;

    public IdleAttackPattern(BossController controller, float x, float y, float warnDuration,
            float attackDuration, boolean flipHorizontal, SpriteSheet warnSprite) {
        super(controller);

        attackName = "idle";
        this.idleX = x;
        this.idleY = y;
        this.warnDuration = warnDuration;
        this.attackDuration = attackDuration;
        this.flipHorizontal = flipHorizontal;

        this.warnPattern = new RectWarnPattern(this.idleX, this.idleY, 100, 100);
        this.warnPattern.setSpriteSheet(warnSprite);
        this.boss.warnPatterns.add(this.warnPattern);
    }

    @Override
    public void start() {
        boss.setState("idle");
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);

        boss.setAnimation("idle");
        boss.getObstacle().setAngle(0);

        warnTime = warnDuration;
        attackTime = attackDuration;

        warnPattern.active = true;
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(CONTROL_NO_ACTION);

        boss.getObstacle().setX(this.idleX);
        boss.getObstacle().setY(this.idleY);
        boss.flipHorizontal = flipHorizontal;
        attackTime = attackDuration;

        this.spawnMinions();

        warnPattern.active = false;
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case WARN:
                if (warnTime > 0) {
                    warnTime -= delta;
                } else {
                    attack();
                }
                break;
            case ATTACK:
                if (attackTime > 0) {
                    attackTime -= delta;
                } else {
                    state = AttackState.ENDED;
                }
                break;
        }
    }
}
