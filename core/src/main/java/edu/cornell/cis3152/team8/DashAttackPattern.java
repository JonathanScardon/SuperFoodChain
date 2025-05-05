package edu.cornell.cis3152.team8;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will start at the top or bottom of the screen and then travel across it until it is off
 * the screen again
 */
public class DashAttackPattern extends BossAttackPattern {

    private final BossWarnPattern warnPattern;

    private final float startX, startY;
    private final int controlCode;
    private final float warnDuration;
    private final float moveSpeed;

    private float warnTime;
    private float origMoveSpeed;

    private static final float PHYSICS_UNITS = 64f;

    public DashAttackPattern(BossController controller, float x, float y, String dir,
                             float warnDuration, float moveSpeed, SpriteSheet warnSprite) {
        super(controller);

        attackName = "dash";
        this.startX = x;
        this.startY = y;
        this.warnDuration = warnDuration;
        this.moveSpeed = moveSpeed;

        switch (dir) {
            case "up":
                this.controlCode = CONTROL_MOVE_UP;
                this.warnPattern = new BossWarnPattern(startX, 720f / PHYSICS_UNITS / 2f, 100, 720);
                break;
            case "down":
                this.controlCode = CONTROL_MOVE_DOWN;
                this.warnPattern = new BossWarnPattern(startX, 720f / PHYSICS_UNITS / 2f, 100, 720);
                break;
            case "left":
                this.controlCode = CONTROL_MOVE_LEFT;
                this.warnPattern = new BossWarnPattern(1280f / PHYSICS_UNITS / 2f, startY, 1280, 100);
                break;
            case "right":
                this.controlCode = CONTROL_MOVE_RIGHT;
                this.warnPattern = new BossWarnPattern(1280f / PHYSICS_UNITS / 2f, startY, 1280, 100);
                break;
            default:
                throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        this.warnPattern.setSpriteSheet(warnSprite);
        boss.warnPatterns.add(this.warnPattern);
    }

    @Override
    public void start() {
        boss.setState("dash");
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);

        boss.getObstacle().setX(startX);
        boss.getObstacle().setY(startY);

        switch (controlCode) {
            case CONTROL_MOVE_UP:
                // make the boss slide up a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(0, 15f));
                boss.flipVertical = true;
                break;
            case CONTROL_MOVE_DOWN:
                // make the boss slide down a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(0, -15f));
                break;
            case CONTROL_MOVE_LEFT:
                // make the boss slide left a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(-15f, 0));
                break;
            case CONTROL_MOVE_RIGHT:
                // make the boss slide right a little bit
                boss.getObstacle().setLinearVelocity(new Vector2(15f, 0));
                boss.flipHorizontal = true;
                break;
        }

        if (controlCode == CONTROL_MOVE_UP || controlCode == CONTROL_MOVE_DOWN) {
            boss.setAnimation("dashVertical", 0f);
        } else if (controlCode == CONTROL_MOVE_LEFT || controlCode == CONTROL_MOVE_RIGHT) {
            boss.setAnimation("dashHorizontal", 0f);
        }

        warnTime = warnDuration;
        warnPattern.active = true;
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(controlCode);
        origMoveSpeed = boss.moveSpeed;
        boss.moveSpeed = moveSpeed;

        if (controlCode == CONTROL_MOVE_UP || controlCode == CONTROL_MOVE_DOWN) {
            boss.setAnimation("dashVertical", 0.1f);
        } else if (controlCode == CONTROL_MOVE_LEFT || controlCode == CONTROL_MOVE_RIGHT) {
            boss.setAnimation("dashHorizontal", 0.1f);
        }

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
            case ATTACK:
                if (isOutOfBounds()) {
                    state = AttackState.ENDED;
                    boss.moveSpeed = origMoveSpeed;
                    boss.flipVertical = false;
                    boss.flipHorizontal = false;
                }
                break;
        }
    }

    private boolean isOutOfBounds() {
        return switch (controlCode) {
            case CONTROL_MOVE_UP -> (boss.getObstacle().getY() - 4) * PHYSICS_UNITS > 720;
            case CONTROL_MOVE_DOWN -> (boss.getObstacle().getY() + 4) * PHYSICS_UNITS < 0;
            case CONTROL_MOVE_LEFT -> (boss.getObstacle().getX() + 4) * PHYSICS_UNITS < 0;
            case CONTROL_MOVE_RIGHT -> (boss.getObstacle().getX() - 4) * PHYSICS_UNITS > 1280;
            default -> true;
        };
    }
}
