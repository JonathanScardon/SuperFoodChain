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

    private float warnTime;
    private float origMoveSpeed;

    private static final float PHYSICS_UNITS = 64f;

    public PreSpinAttackPattern(BossController controller, String dir, float warnDuration,
        float moveSpeed, SpriteSheet warnSprite) {
        super(controller);

        this.startX = 640;
        this.startY = 360;
        this.warnDuration = warnDuration;
        this.moveSpeed = moveSpeed;

        switch (dir) {
            case "left" -> {
                this.controlCode = CONTROL_MOVE_LEFT;
                this.warnPattern = new BossWarnPattern(1280f / PHYSICS_UNITS / 2f, startY);
            }
            case "right" -> {
                this.controlCode = CONTROL_MOVE_RIGHT;
                this.warnPattern = new BossWarnPattern(1280f / PHYSICS_UNITS / 2f, startY);
                boss.flipHorizontal = true;
            }
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        this.warnPattern.setSpriteSheet(warnSprite);
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        boss.setAnimation("dashHorizontal");
        warnTime = warnDuration;
        warnPattern.active = true;
        boss.curWarn = warnPattern;
    }

    public void attack() {
        state = AttackState.ATTACK;
        controller.setAction(controlCode);
        origMoveSpeed = boss.moveSpeed;
        boss.moveSpeed = moveSpeed;
        if (controlCode == InputController.CONTROL_MOVE_RIGHT
            && boss.getObstacle().getPosition().x * PHYSICS_UNITS >= startX
        ) {

            boss.getObstacle()
                .setPosition(new Vector2(startX / PHYSICS_UNITS, startY / PHYSICS_UNITS));
            controller.setAction(CONTROL_NO_ACTION);
            boss.setAnimation("spin");
            boss.flipHorizontal = false;
        } else if (controlCode == CONTROL_MOVE_LEFT
            && boss.getObstacle().getPosition().x * PHYSICS_UNITS <= startX) {

            boss.getObstacle()
                .setPosition(new Vector2(startX / PHYSICS_UNITS, startY / PHYSICS_UNITS));
            controller.setAction(CONTROL_NO_ACTION);
            boss.setAnimation("spin");
        }
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
                break;
        }
        attack();
    }
}

