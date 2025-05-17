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

        this.warnPattern = new RectWarnPattern(this.idleX, this.idleY, boss.getWidth()  * GameScene.PHYSICS_UNITS, boss.getHeight() * GameScene.PHYSICS_UNITS);
        this.warnPattern.setSpriteSheet(warnSprite);
        this.warnPattern.setAnimationSpeedWithDuration(warnDuration);
        this.boss.warnPatterns.add(this.warnPattern);
    }

    @Override
    public void start() {
        boss.setState("idle");
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);

        if (attackDuration > 30f) {
            // if the attack is too long, just sleep
            boss.setAnimation("sleep", 0.05f);
        } else {
            if (boss.getVariant() == 0){
                boss.setAnimation("idle", 0.1f);
            }else{
                boss.setAnimation("idle_1", 0.1f);
            }
        }
        boss.getObstacle().setAngle(0);

        warnTime = warnDuration;
        attackTime = attackDuration;

        warnPattern.setActive(true);
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(CONTROL_NO_ACTION);

        boss.getObstacle().setX(this.idleX);
        boss.getObstacle().setY(this.idleY);
        boss.flipHorizontal = flipHorizontal;
        attackTime = attackDuration;

        this.spawnMinions();

        warnPattern.setActive(false);
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
