package edu.cornell.cis3152.team8;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will start at the top or bottom of the screen and then travel across it until it is off
 * the screen again
 */
public class SpinAttackPattern extends BossAttackPattern {

    private BossWarnPattern warnPattern;
    private final float startX, startY;
    private float attackX, attackY;
    private int controlCode;
    private final float warnDuration;

    private float warnTime;
    private SpriteSheet warnSprite;
    private Player player;
    private SpriteSheet attackSprite;
    private boolean actionSet;
    private GameState gamestate;
    private static final float PHYSICS_UNITS = 64f;
    private float origMoveSpeed;
    private final float moveSpeed;

    private BossAttackPattern preSpin;

    public SpinAttackPattern(BossController controller, float warnDuration, float moveSpeed,
        SpriteSheet warnSprite,
        Player player, GameState gamestate) {
        super(controller);
        attackName = "spin";
        this.player = player;
        this.moveSpeed = moveSpeed;
        startX = 640 / PHYSICS_UNITS;
        startY = 360 / PHYSICS_UNITS;
        warnPattern = new BossWarnPattern(0, 0);
        warnPattern.setSpriteSheet(warnSprite);
        this.warnSprite = warnSprite;
        actionSet = false;
        this.gamestate = gamestate;

        this.warnDuration = warnDuration;
    }

    @Override
    public void start() {
        if (twoBosses()) {
            state = AttackState.WARN;
            boss.setState("warn");
            if (boss.equals(gamestate.getBosses().get(0))) {
                preSpin = new PreSpinAttackPattern(controller, "right", 5f, 100,
                    warnSprite);
            } else {
                preSpin = new PreSpinAttackPattern(controller, "left", 5f, 100,
                    warnSprite);
            }

            controller.setAction(CONTROL_NO_ACTION);
            Vector2 b1Pos = gamestate.getBosses().get(0).getObstacle().getPosition();
            Vector2 b2Pos = gamestate.getBosses().get(1).getObstacle().getPosition();
//            if (!b1Pos.equals(b2Pos)) {
//                if (boss.equals(gamestate.getBosses().get(0))) {
//                    boss.getObstacle()
//                        .setLinearVelocity(new Vector2(50f, 0f));
//                    //controller.setAction(CONTROL_MOVE_RIGHT);
//                    //controller.
//                } else {
//
//                    boss.getObstacle()
//                        .setLinearVelocity(new Vector2(-50f, 0f));
//                    //controller.setAction(CONTROL_MOVE_LEFT);
//                }
//
//                if (gamestate.getBosses().get(0).getObstacle().getPosition().x
//                    >= gamestate.getBosses().get(1).getObstacle().getPosition().x) {
//                    gamestate.getBosses().get(0).getObstacle()
//                        .setPosition(new Vector2(startX, startY));
//                    gamestate.getBosses().get(1).getObstacle()
//                        .setPosition(new Vector2(startX, startY));
//                }

            preSpin.start();
        }

        setControlCode();
        warnTime = warnDuration;
        warnPattern.active = true;
        boss.curWarn = warnPattern;
        setControlCode();


    }

    public void attack() {
        if (twoBosses()) {
            boss.setAnimation("spin");
            state = AttackState.ATTACK;
            controller.setAction(controlCode);
            origMoveSpeed = boss.moveSpeed;
            boss.moveSpeed = moveSpeed;
            warnPattern.active = false;
            boss.curWarn = null;
            this.spawnMinions();
        }
    }

    @Override
    public void update(float delta) {
        if (twoBosses()) {
            switch (state) {
                case INACTIVE -> {
                }
                case WARN -> {
                    if (warnTime > 0) {
                        if (!gamestate.getBosses().get(0).getObstacle().getPosition()
                            .equals(gamestate.getBosses().get(1).getObstacle().getPosition())) {
                            preSpin.update(delta);
                        } else {
                            boss.setState("spinning");
                            boss.setAnimation("spin");
                            boss.setAnimationFrame(0);
                        }
                        if (warnTime > warnDuration
                            / 2f) { // Stop picking new location after half of the warning time
                            setControlCode();
                        }
                        warnPattern.getObstacle().setX(controller.boss.getObstacle().getX());
                        warnPattern.getObstacle().setY(controller.boss.getObstacle().getY());
                        warnTime -= delta;
                    } else {
                        attack();
                    }
                }
                case ATTACK -> {
                    boss.angle += 6;
                    boss.setAnimationSpeed(.15f);
                    if (atWall()) {
                        state = AttackState.ENDED;
                        boss.moveSpeed = origMoveSpeed;
                    }
                }
            }
        }
    }

    @Override
    public boolean isEnded() {
        if (twoBosses()) {
            Vector2 pos = boss.getObstacle().getPosition();
            boolean wall = atWall();
            float scootX;
            float scootY;
            if (wall) {
                if (pos.x >= 1280 / PHYSICS_UNITS) {
                    scootX = -15f;
                    scootY = 0;
                } else if (pos.x <= 0) {
                    scootX = 15f;
                    scootY = 0;
                } else if (pos.y >= 720 / PHYSICS_UNITS) {
                    scootX = 0;
                    scootY = -15f;
                } else {
                    scootX = 0;
                    scootY = 15f;
                }
                controller.boss.getObstacle().setLinearVelocity(new Vector2(scootX, scootY));
            }
            return wall;
        }
        return true;
    }

    private void setControlCode() {
        Vector2 bPos = boss.getObstacle().getPosition();
        Vector2 pPos = player.getPlayerHead().getObstacle().getPosition();
        float gap = 120 / PHYSICS_UNITS;
        if (pPos.x >= bPos.x - gap
            && pPos.x <= bPos.x + gap) {
            if (bPos.y < pPos.y) {
                controlCode = CONTROL_MOVE_UP;
                warnPattern.getObstacle().setAngle(90f);
            } else if (bPos.y == pPos.y) {
                controlCode = CONTROL_NO_ACTION;
                warnPattern.getObstacle().setAngle(0f);
            } else {
                controlCode = CONTROL_MOVE_DOWN;
                warnPattern.getObstacle().setAngle(-90f);
            }
        } else if (bPos.x < pPos.x) {
            if (pPos.y >= bPos.y - gap
                && pPos.y <= bPos.y + gap) {
                controlCode = CONTROL_MOVE_RIGHT;
                warnPattern.getObstacle().setAngle(0f);
            } else if (bPos.y < pPos.y) {
                this.controlCode = CONTROL_MOVE_RIGHT_UP;
                warnPattern.getObstacle().setAngle(45f);
            } else {
                controlCode = CONTROL_MOVE_RIGHT_DOWN;
                warnPattern.getObstacle().setAngle(-45f);
            }

        } else {
            if (pPos.y >= bPos.y - gap
                && pPos.y <= bPos.y + gap) {
                controlCode = CONTROL_MOVE_LEFT;
                warnPattern.getObstacle().setAngle(180f);
            } else if (bPos.y < pPos.y) {
                controlCode = CONTROL_MOVE_LEFT_UP;
                warnPattern.getObstacle().setAngle(135f);
            } else {
                controlCode = CONTROL_MOVE_LEFT_DOWN;
                warnPattern.getObstacle().setAngle(-135f);
            }
        }
    }

    private boolean atWall() {
        Vector2 pos = boss.getObstacle().getPosition();
        return pos.x >= 1280 / PHYSICS_UNITS || pos.x <= 0 || pos.y >= 720 / PHYSICS_UNITS
            || pos.y <= 0;
    }

    private boolean twoBosses() {
        return gamestate.getBosses().get(0).getObstacle().isActive() && gamestate.getBosses().get(1)
            .getObstacle().isActive();
    }
}
