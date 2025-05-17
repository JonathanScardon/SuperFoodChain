package edu.cornell.cis3152.team8;

import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will target the head of the player
 */
public class SnatchAttackPattern extends BossAttackPattern {

    private final Player player;
    private final BossWarnPattern warnPattern;
    private final float warnDuration;
    private final float attackDuration;

    private float attackX, attackY;
    private float warnTime;
    private float attackTime;

    public SnatchAttackPattern(BossController controller, float warnDuration, float attackDuration,
        SpriteSheet warnSprite, Player player) {
        super(controller);
        this.player = player;

        attackName = "snatch";
        this.warnDuration = warnDuration;
        this.attackDuration = attackDuration;

        this.warnPattern = new RectWarnPattern(0, 0, boss.getWidth() * GameScene.PHYSICS_UNITS, boss.getHeight() * GameScene.PHYSICS_UNITS);
        this.warnPattern.setSpriteSheet(warnSprite);
        this.warnPattern.setAnimationSpeedWithDuration(warnDuration);
        boss.warnPatterns.add(this.warnPattern);

        this.state = AttackState.INACTIVE;
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        controller.setAction(InputController.CONTROL_NO_ACTION);

        boss.setAnimationSpeed(.2f);
        boss.setAnimation("snatch");
        boss.setState("snatch");

        attackX = player.getPlayerHead().getObstacle().getPosition().x;
        attackY = player.getPlayerHead().getObstacle().getPosition().y;
        warnPattern.setPosition(attackX, attackY);
        warnTime = warnDuration;
        attackTime = attackDuration;

        warnPattern.setActive(true);
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(InputController.CONTROL_NO_ACTION);

        boss.getObstacle().setX(this.attackX);
        boss.getObstacle().setY(this.attackY);
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
                    boss.setAnimationSpeed(.1f);
                    state = AttackState.ENDED;
                }
                break;
        }
    }
}
