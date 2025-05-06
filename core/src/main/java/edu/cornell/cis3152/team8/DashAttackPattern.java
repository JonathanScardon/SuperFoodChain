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
    private final float levelWidth, levelHeight;

    private float warnTime;
    private float origMoveSpeed;

    public DashAttackPattern(BossController controller, float x, float y, String dir,
        float warnDuration, float moveSpeed, float levelWidth, float levelHeight,
        SpriteSheet warnSprite) {
        super(controller);

        attackName = "dash";
        this.startX = x;
        this.startY = y;
        this.warnDuration = warnDuration;
        this.moveSpeed = moveSpeed;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        switch (dir) {
            case "up":
                this.controlCode = CONTROL_MOVE_UP;
                this.warnPattern = new RectWarnPattern(startX,
                    levelHeight / GameScene.PHYSICS_UNITS / 2f,
                    boss.getWidth() * GameScene.PHYSICS_UNITS, levelHeight);
                break;
            case "down":
                this.controlCode = CONTROL_MOVE_DOWN;
                this.warnPattern = new RectWarnPattern(startX,
                    levelHeight / GameScene.PHYSICS_UNITS / 2f,
                    boss.getWidth() * GameScene.PHYSICS_UNITS, levelHeight);
                break;
            case "left":
                this.controlCode = CONTROL_MOVE_LEFT;
                this.warnPattern = new RectWarnPattern(levelWidth / GameScene.PHYSICS_UNITS / 2f,
                    startY, levelWidth, boss.getHeight() * GameScene.PHYSICS_UNITS);
                break;
            case "right":
                this.controlCode = CONTROL_MOVE_RIGHT;
                this.warnPattern = new RectWarnPattern(levelWidth / GameScene.PHYSICS_UNITS / 2f,
                    startY, levelWidth, boss.getHeight() * GameScene.PHYSICS_UNITS);
                break;
            default:
                throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        this.warnPattern.setSpriteSheet(warnSprite);
        boss.warnPatterns.add(this.warnPattern);
    }

    @Override
    public void start() {
        boss.getObstacle().setAngle(0);
        boss.setState("dash");
        boss.getObstacle().setAngle(0);
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
            case CONTROL_MOVE_UP ->
                (boss.getObstacle().getY() - boss.getHeight()) * GameScene.PHYSICS_UNITS
                    > levelHeight;
            case CONTROL_MOVE_DOWN ->
                (boss.getObstacle().getY() + boss.getHeight()) * GameScene.PHYSICS_UNITS < 0;
            case CONTROL_MOVE_LEFT ->
                (boss.getObstacle().getX() + boss.getWidth()) * GameScene.PHYSICS_UNITS < 0;
            case CONTROL_MOVE_RIGHT ->
                (boss.getObstacle().getX() - boss.getWidth()) * GameScene.PHYSICS_UNITS
                    > levelWidth;
            default -> true;
        };
    }
}
