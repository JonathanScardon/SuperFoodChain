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
    private final int warnDuration;

    private int warnTime;

    public DashAttackPattern(BossController controller, float x, boolean top, int warnDuration, SpriteSheet warnSprite) {
        this.controller = controller;
        this.boss = controller.boss;

        this.startX = x;
        this.startY = top ? 720f + boss.getRadius() : -boss.getRadius();
        this.controlCode = top ? CONTROL_MOVE_DOWN : CONTROL_MOVE_UP;
        this.warnDuration = warnDuration;

        this.warnPattern = new BossWarnPattern(startX, 720f / 2f);
        this.warnPattern.setSpriteSheet(warnSprite);
    }

    public void warn() {
        controller.setState(BossController.FSMState.WARN);
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

    public void attack() {
        controller.setState(BossController.FSMState.ATTACK);
        controller.setAction(controlCode);

        warnPattern.active = false;
        boss.curWarn = null;
    }

    public void update() {
        switch (controller.state) {
            case WARN:
                if (warnTime > 0) {
                    warnTime--;
                }
                if (warnEnded()) {
                    attack();
                }
                break;
            case ATTACK:
                break;
            default:
                break;
        }
    }

    private boolean warnEnded() {
        return warnTime <= 0;
    }

    public boolean attackEnded() {
        return boss.getY() + boss.getRadius() < 0 || boss.getY() - boss.getRadius() > 720;
    }
}
