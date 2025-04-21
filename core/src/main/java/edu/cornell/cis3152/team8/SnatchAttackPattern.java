package edu.cornell.cis3152.team8;

import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will target the head of the player
 */
public class SnatchAttackPattern implements BossAttackPattern {
    private final BossController controller;
    private final Boss boss;
    private final Player player;
    private final BossWarnPattern warnPattern;

    private final float warnDuration;
    private final float attackDuration;

    private float attackX, attackY;
    private float warnTime;
    private float attackTime;
    private AttackState state;

    public SnatchAttackPattern(BossController controller, float warnDuration, float attackDuration, SpriteSheet warnSprite, Player player) {
        this.controller = controller;
        this.boss = controller.boss;
        this.player = player;

        this.warnDuration = warnDuration;
        this.attackDuration = attackDuration;

        this.warnPattern = new BossWarnPattern(0, 0);
        this.warnPattern.setSpriteSheet(warnSprite);

        this.state = AttackState.INACTIVE;
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        controller.setAction(InputController.CONTROL_NO_ACTION);

        boss.setAnimation("snatch");

        attackX = player.getPlayerHead().getObstacle().getPosition().x;
        attackY = player.getPlayerHead().getObstacle().getPosition().y;
        warnPattern.setPosition(attackX, attackY);
        warnTime = warnDuration;
        attackTime = attackDuration;

        warnPattern.active = true;
        boss.curWarn = warnPattern;
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(InputController.CONTROL_NO_ACTION);

        boss.getObstacle().setX(this.attackX);
        boss.getObstacle().setY(this.attackY);
        attackTime = attackDuration;

        warnPattern.active = false;
        boss.curWarn = null;
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

    @Override
    public boolean isEnded() {
        return state == AttackState.ENDED;
    }
}
