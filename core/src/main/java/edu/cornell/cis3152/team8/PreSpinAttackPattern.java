package edu.cornell.cis3152.team8;

import static edu.cornell.cis3152.team8.InputController.CONTROL_MOVE_DOWN;
import static edu.cornell.cis3152.team8.InputController.CONTROL_MOVE_LEFT;
import static edu.cornell.cis3152.team8.InputController.CONTROL_MOVE_RIGHT;
import static edu.cornell.cis3152.team8.InputController.CONTROL_MOVE_UP;
import static edu.cornell.cis3152.team8.InputController.CONTROL_NO_ACTION;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.cis3152.team8.BossAttackPattern.AttackState;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class PreSpinAttackPattern extends BossAttackPattern {

    private final BossWarnPattern warnPattern;
    private final float startX, startY;
    private final int controlCode;
    private final float warnDuration;
    private final float moveSpeed;
    private final float levelWidth, levelHeight;

    private float warnTime;
    private float origMoveSpeed;
    private String dir;

    public PreSpinAttackPattern(BossController controller, String dir, float warnDuration,
        float levelWidth, float levelHeight,
        float moveSpeed, SpriteSheet warnSprite) {
        super(controller);

        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
        this.startX = levelWidth / 2f;
        this.startY = levelHeight / 2f;
        this.warnDuration = warnDuration;
        this.moveSpeed = moveSpeed;
        this.dir = dir;

        switch (dir) {
            case "left" -> {
                this.controlCode = CONTROL_MOVE_LEFT;
                this.warnPattern = new RectWarnPattern(levelWidth / GameScene.PHYSICS_UNITS / 2f,
                    startY, 300, 100);
            }
            case "right" -> {
                this.controlCode = CONTROL_MOVE_RIGHT;
                this.warnPattern = new RectWarnPattern(levelWidth / GameScene.PHYSICS_UNITS / 2f,
                    startY, 300, 100);
                boss.flipHorizontal = true;
            }
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        this.warnPattern.setSpriteSheet(warnSprite);
        this.boss.warnPatterns.add(warnPattern);
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        boss.setAnimation("dashHorizontal");
        warnTime = warnDuration;
        warnPattern.active = true;
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(controlCode);
        origMoveSpeed = boss.moveSpeed;
        boss.moveSpeed = moveSpeed;
        if (controlCode == InputController.CONTROL_MOVE_RIGHT
            && boss.getObstacle().getPosition().x * GameScene.PHYSICS_UNITS >= startX
        ) {

            boss.getObstacle()
                .setPosition(new Vector2(startX / GameScene.PHYSICS_UNITS,
                    startY / GameScene.PHYSICS_UNITS));
            controller.setAction(CONTROL_NO_ACTION);
            boss.getObstacle().setAngle(90);
            boss.setAnimation("spin");
            boss.flipHorizontal = false;
            warnPattern.active = false;
        } else if (controlCode == CONTROL_MOVE_LEFT
            && boss.getObstacle().getPosition().x * GameScene.PHYSICS_UNITS <= startX) {

            boss.getObstacle()
                .setPosition(new Vector2(startX / GameScene.PHYSICS_UNITS,
                    startY / GameScene.PHYSICS_UNITS));
            controller.setAction(CONTROL_NO_ACTION);
            boss.getObstacle().setAngle(90);
            boss.setAnimation("spin");
            warnPattern.active = false;
        }
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case WARN:
                if (warnTime > 0) {
                    if (dir.equals("left")) {
//                        warnPattern.setPosition(controller.boss.getObstacle().getX()
//                                - controller.boss.getSpriteSheet().getRegionWidth()
//                                / GameScene.PHYSICS_UNITS / 3f,
//                            controller.boss.getObstacle().getY());
                    } else {
//                        warnPattern.setPosition(controller.boss.getObstacle().getX()
//                                + controller.boss.getSpriteSheet().getRegionWidth()
//                                / GameScene.PHYSICS_UNITS / 3f,
//                            controller.boss.getObstacle().getY());
                    }

                    warnTime -= delta;
                } else {
                    warnPattern.active = false;
                    attack();
                }
            case ATTACK:
                break;
        }
        attack();
    }
}

