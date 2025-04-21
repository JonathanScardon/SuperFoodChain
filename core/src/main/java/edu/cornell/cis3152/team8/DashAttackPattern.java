package edu.cornell.cis3152.team8;

import com.badlogic.gdx.math.Vector2;
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
    private final float moveSpeed;

    private float warnTime;
    private AttackState state;

    private static final float PHYSICS_UNITS = 64f;

    public DashAttackPattern(BossController controller, float x, float y, String dir, float warnDuration, float moveSpeed, SpriteSheet warnSprite) {
        this.controller = controller;
        this.boss = controller.boss;

        this.startX = x;
        this.startY = y;
        this.warnDuration = warnDuration;
        this.moveSpeed = moveSpeed;

        switch (dir) {
            case "up":
                this.controlCode = CONTROL_MOVE_UP;
                this.warnPattern = new BossWarnPattern(startX, 720f / PHYSICS_UNITS / 2f);
                break;
            case "down":
                this.controlCode = CONTROL_MOVE_DOWN;
                this.warnPattern = new BossWarnPattern(startX, 720f / PHYSICS_UNITS / 2f);
                break;
            case "left":
                this.controlCode = CONTROL_MOVE_LEFT;
                this.warnPattern = new BossWarnPattern(1280f / PHYSICS_UNITS / 2f, startY);
                break;
            case "right":
                this.controlCode = CONTROL_MOVE_RIGHT;
                this.warnPattern = new BossWarnPattern(1280f / PHYSICS_UNITS / 2f, startY);
                break;
            default:
                throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        this.warnPattern.setSpriteSheet(warnSprite);

        this.state = AttackState.INACTIVE;
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);

        boss.getObstacle().setX(startX);
        boss.getObstacle().setY(startY);

        switch (controlCode) {
            case CONTROL_MOVE_UP:
                // make the boss slide up a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(0, 15f));
                boss.angle = 90f;
                break;
            case CONTROL_MOVE_DOWN:
                // make the boss slide down a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(0, -15f));
                boss.angle = 270f;
                break;
            case CONTROL_MOVE_LEFT:
                // make the boss slide left a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(-15f, 0));
                boss.angle = 180f;
                break;
            case CONTROL_MOVE_RIGHT:
                // make the boss slide right a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(15f, 0));
                boss.angle = 0f;
                break;
        }

        warnTime = warnDuration;
        warnPattern.active = true;
        boss.curWarn = warnPattern;
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(controlCode);
        boss.setMoveSpeed(moveSpeed);

        warnPattern.active = false;
        boss.curWarn = null;
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case INACTIVE:
                break;
            case WARN:
                if (warnTime > 0) {
                    warnTime -= delta;
                } else {
                    attack();
                }
            case ATTACK:
                break;
        }

    }

    @Override
    public boolean isEnded() {
        return switch (controlCode) {
            case CONTROL_MOVE_UP -> (boss.getObstacle().getY() - 4) * PHYSICS_UNITS > 720;
            case CONTROL_MOVE_DOWN -> (boss.getObstacle().getY() + 4) * PHYSICS_UNITS < 0;
            case CONTROL_MOVE_LEFT -> (boss.getObstacle().getX() + 4) * PHYSICS_UNITS < 0;
            case CONTROL_MOVE_RIGHT -> (boss.getObstacle().getX() - 4) * PHYSICS_UNITS > 1280;
            default -> true;
        };
    }
}
