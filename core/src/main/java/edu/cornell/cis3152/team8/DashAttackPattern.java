package edu.cornell.cis3152.team8;

import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will start at the top or bottom of the screen and then travel across it until it is off
 * the screen again
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
    private SpriteSheet attackSprite;

    public DashAttackPattern(BossController controller, float x, float y, String dir,
        float warnDuration, SpriteSheet warnSprite, SpriteSheet attackSprite) {
        this.controller = controller;
        boss = controller.boss;
        boss.setSpriteSheet(attackSprite);
        this.startX = x;
        this.startY = y;
        this.warnDuration = warnDuration;
        this.attackSprite = attackSprite;

        switch (dir) {
            case "up":
                this.controlCode = CONTROL_MOVE_UP;
                this.warnPattern = new BossWarnPattern(startX, 720f / 2f);
                break;
            case "down":
                this.controlCode = CONTROL_MOVE_DOWN;
                this.warnPattern = new BossWarnPattern(startX, 720f / 2f);
                break;
            case "left":
                this.controlCode = CONTROL_MOVE_LEFT;
                this.warnPattern = new BossWarnPattern(1280f / 2f, startY);
                break;
            case "right":
                this.controlCode = CONTROL_MOVE_RIGHT;
                this.warnPattern = new BossWarnPattern(1280f / 2f, startY);
                break;
            default:
                throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        this.warnPattern.setSpriteSheet(warnSprite);

        this.state = AttackState.INACTIVE;
        boss.setState("inactive");
    }

    @Override
    public void start() {
        boss.setAttack("dash");
        state = AttackState.WARN;
        boss.setAttack("warn");
        controller.setAction(CONTROL_NO_ACTION);

        boss.setX(startX);
        boss.setY(startY);

        switch (controlCode) {
            case CONTROL_MOVE_UP:
                boss.setVX(0);
                boss.setVY(15f); // make the boss slide up a little bit
                boss.angle = 90f;
                break;
            case CONTROL_MOVE_DOWN:
                boss.setVX(0);
                boss.setVY(-15f); // make the boss slide down a little bit
                boss.angle = 270f;
                break;
            case CONTROL_MOVE_LEFT:
                boss.setVX(-15f); // make the boss slide left a little bit
                boss.setVY(0);
                boss.angle = 180f;
                break;
            case CONTROL_MOVE_RIGHT:
                boss.setVX(15f); // make the boss slide right a little bit
                boss.setVY(0);
                boss.angle = 0f;
                break;
        }

        warnTime = warnDuration;
        warnPattern.active = true;
        boss.curWarn = warnPattern;
    }

    public void attack() {
        state = AttackState.ATTACK;
        boss.setState("attack");
        controller.setAction(controlCode);

        warnPattern.active = false;
        boss.curWarn = null;
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case INACTIVE, ATTACK -> {
            }
            case WARN -> {
                if (warnTime > 0) {
                    warnTime -= delta;
                } else {
                    attack();
                }
                boss.setSpriteSheet(attackSprite);
            }
        }

    }

    @Override
    public boolean ended() {
        return switch (controlCode) {
            case CONTROL_MOVE_UP -> boss.getY() - boss.getRadius() * 2 > 720;
            case CONTROL_MOVE_DOWN -> boss.getY() + boss.getRadius() * 2 < 0;
            case CONTROL_MOVE_LEFT -> boss.getX() + boss.getRadius() * 2 < 0;
            case CONTROL_MOVE_RIGHT -> boss.getX() - boss.getRadius() * 2 > 1280;
            default -> true;
        };
    }
}
