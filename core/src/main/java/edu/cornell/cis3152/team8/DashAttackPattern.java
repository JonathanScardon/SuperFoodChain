package edu.cornell.cis3152.team8;

import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will start at the top or bottom of the screen
 * and then travel across it until it is off the screen again
 */
public class DashAttackPattern implements BossAttackPattern {
    private final BossController controller;
    private final Boss boss;
    private final BossWarnPattern warnPattern;

    private final float startX, startY;
    private final int controlCode;
    private final float warnDuration;

    private float warnTime;
    private AttackState state;

    public DashAttackPattern(BossController controller, float x, boolean top, float warnDuration, SpriteSheet warnSprite) {
        this.controller = controller;
        this.boss = controller.boss;

        this.startX = x;
        this.startY = top ? 720f + boss.getRadius() : -boss.getRadius();
        this.controlCode = top ? CONTROL_MOVE_DOWN : CONTROL_MOVE_UP;
        this.warnDuration = warnDuration;

        this.warnPattern = new BossWarnPattern(startX, 720f / 2f);
        this.warnPattern.setSpriteSheet(warnSprite);
    }

    @Override
    public void warn() {
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);

        boss.setX(startX);
        boss.setY(startY);
        boss.setVX(0);
        if (controlCode == CONTROL_MOVE_UP) {
            boss.setVY(15f); // make the boss slide up a little bit
            boss.angle = 90f;
        } else if (controlCode == CONTROL_MOVE_DOWN) {
            boss.setVY(-15f); // make the boss slide down a little bit
            boss.angle = 270f;
        }
        warnTime = warnDuration;
        warnPattern.active = true;
        boss.curWarn = warnPattern;
    }

    @Override
    public void attack() {
        state = AttackState.WARN;
        controller.setAction(controlCode);

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
            case ATTACK:
                break;
        }

    }

    private boolean warnEnded() {
        return warnTime <= 0;
    }

    @Override
    public boolean attackEnded() {
        if (controlCode == CONTROL_MOVE_UP) {
            return boss.getY() - boss.getRadius() > 720;
        } else if (controlCode == CONTROL_MOVE_DOWN) {
            return boss.getY() + boss.getRadius() < 0;
        }
        return true; // it should never reach here
    }
}
