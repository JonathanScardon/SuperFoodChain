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
    private final float levelWidth, levelHeight;

    private float warnTime;
    private SpriteSheet warnSprite;
    private Player player;
    private SpriteSheet attackSprite;
    private boolean actionSet;
    private GameState gamestate;
    private float origMoveSpeed;
    private final float moveSpeed;

    private BossAttackPattern preSpin;

    public SpinAttackPattern(BossController controller, float warnDuration, float moveSpeed,
        float levelWidth, float levelHeight,
        SpriteSheet warnSprite,
        Player player, GameState gamestate) {
        super(controller);
        attackName = "spin";
        this.player = player;
        this.moveSpeed = moveSpeed;
        startX = levelWidth / GameScene.PHYSICS_UNITS / 2f;
        startY = levelHeight / GameScene.PHYSICS_UNITS / 2f;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        warnPattern = new SpinWarnPattern(0, 0, Math.max(boss.getWidth() * GameScene.PHYSICS_UNITS,
            boss.getHeight() * GameScene.PHYSICS_UNITS));
        warnPattern.setSpriteSheet(warnSprite);
        this.boss.warnPatterns.add(warnPattern);

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
                preSpin = new PreSpinAttackPattern(controller, "right", 5f, levelWidth, levelHeight,
                    moveSpeed / 2f,
                    warnSprite);
            } else {
                preSpin = new PreSpinAttackPattern(controller, "left", 5f, levelWidth, levelHeight,
                    moveSpeed / 2f, warnSprite);
            }

            controller.setAction(CONTROL_NO_ACTION);
            preSpin.start();
            warnPattern.active = true;
        } else {
            warnPattern.active = false;
        }

        setControlCode();
        warnTime = warnDuration;
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
            this.spawnMinions();
        } else {
            warnPattern.active = false;
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
                            warnPattern.active = false;
                        } else {
                            warnPattern.active = true;
                            boss.setState("spinning");
                            boss.setAnimation("spin", .5f);
                            boss.getObstacle().setAngle(boss.getObstacle().getAngle() + 4.5f);
                            //boss.setAnimationFrame(0);
                        }
                        if (warnTime > warnDuration
                            * .1f) { // Stop picking new location after half of the warning time
                            setControlCode();
                        }
                        warnPattern.setPosition(controller.boss.getObstacle().getX(),
                            controller.boss.getObstacle().getY());
                        warnTime -= delta;
                    } else {
                        attack();
                    }
                }
                case ATTACK -> {
                    boss.getObstacle().setAngle(boss.getObstacle().getAngle() + 9);
                    boss.setAnimationSpeed(0.15f);
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
                if (pos.x >= levelWidth / GameScene.PHYSICS_UNITS) {
                    scootX = -15f;
                    scootY = 0;
                } else if (pos.x <= 0) {
                    scootX = 15f;
                    scootY = 0;
                } else if (pos.y >= levelHeight / GameScene.PHYSICS_UNITS) {
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
        float gap = 120 / GameScene.PHYSICS_UNITS;
        if (pPos.x >= bPos.x - gap
            && pPos.x <= bPos.x + gap) {
            if (bPos.y < pPos.y) {
                controlCode = CONTROL_MOVE_UP;
                warnPattern.setAngle(90f);
            } else if (bPos.y == pPos.y) {
                controlCode = CONTROL_NO_ACTION;
                warnPattern.setAngle(0f);
            } else {
                controlCode = CONTROL_MOVE_DOWN;
                warnPattern.setAngle(-90f);
            }
        } else if (bPos.x < pPos.x) {
            if (pPos.y >= bPos.y - gap
                && pPos.y <= bPos.y + gap) {
                controlCode = CONTROL_MOVE_RIGHT;
                warnPattern.setAngle(0f);
            } else if (bPos.y < pPos.y) {
                this.controlCode = CONTROL_MOVE_RIGHT_UP;
                warnPattern.setAngle(45f);
            } else {
                controlCode = CONTROL_MOVE_RIGHT_DOWN;
                warnPattern.setAngle(-45f);
            }

        } else {
            if (pPos.y >= bPos.y - gap
                && pPos.y <= bPos.y + gap) {
                controlCode = CONTROL_MOVE_LEFT;
                warnPattern.setAngle(180f);
            } else if (bPos.y < pPos.y) {
                controlCode = CONTROL_MOVE_LEFT_UP;
                warnPattern.setAngle(135f);
            } else {
                controlCode = CONTROL_MOVE_LEFT_DOWN;
                warnPattern.setAngle(-135f);
            }
        }
    }

    private boolean atWall() {
        Vector2 pos = boss.getObstacle().getPosition();
        return pos.x >= levelWidth / GameScene.PHYSICS_UNITS || pos.x <= 0
            || pos.y >= levelHeight / GameScene.PHYSICS_UNITS
            || pos.y <= 0;
    }

    private boolean twoBosses() {
        return gamestate.getBosses().get(0).getObstacle().isActive() && gamestate.getBosses().get(1)
            .getObstacle().isActive();
    }
}
