package edu.cornell.cis3152.team8;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * The boss will start at the top or bottom of the screen and then travel across it until it is off
 * the screen again
 */
public class SpinAttackPattern implements BossAttackPattern {

    private final BossController controller;
    private final Boss boss;
    private BossWarnPattern warnPattern;

    private final float startX, startY;
    private int controlCode;
    private final float warnDuration;

    private float warnTime;
    private AttackState state;
    private SpriteSheet warnSprite;
    private Player player;
    private SpriteSheet attackSprite;
    private boolean actionSet;
    private GameState gamestate;

    public SpinAttackPattern(BossController controller, float warnDuration, SpriteSheet warnSprite,
        Player player, SpriteSheet attackSprite, GameState gamestate) {
        this.controller = controller;
        this.player = player;
        boss = controller.boss;
        this.attackSprite = attackSprite;
        Vector2 bPos = controller.boss.getPosition();
        startX = bPos.x;
        startY = bPos.y;
        warnPattern = new BossWarnPattern(startX, startY);
        warnPattern.setSpriteSheet(warnSprite);
        actionSet = false;
        this.gamestate = gamestate;

        this.warnDuration = warnDuration;
    }

    @Override
    public void start() {
        if (!gamestate.getBosses().get(0).isDestroyed() && !gamestate.getBosses().get(1)
            .isDestroyed()) {
            boss.setAttack("spin");
            state = AttackState.WARN;
            boss.setState("warn");
            controller.setAction(CONTROL_NO_ACTION);
            setControlCode();
            warnTime = warnDuration;
            warnPattern.active = true;
            boss.curWarn = warnPattern;
        }
    }

    public void attack() {
        if (!gamestate.getBosses().get(0).isDestroyed() && !gamestate.getBosses().get(1)
            .isDestroyed()) {
            setControlCode();
            state = AttackState.ATTACK;
            controller.setAction(controlCode);
            warnPattern.active = false;
            boss.curWarn = null;
        }
    }

    @Override
    public void update(float delta) {
        if (!gamestate.getBosses().get(0).isDestroyed() && !gamestate.getBosses().get(1)
            .isDestroyed()) {
            switch (state) {
                case INACTIVE, ATTACK -> {
                }
                case WARN -> {
                    if (warnTime > 0) {
                        setControlCode();
                        switch (controlCode) {
                            case CONTROL_MOVE_UP -> {
                                warnPattern.setAngle(90f);
                            }
                            case CONTROL_MOVE_DOWN -> {
                                warnPattern.setAngle(270f);
                            }
                            case CONTROL_MOVE_LEFT -> {
                                warnPattern.setAngle(180f);
                            }
                            case CONTROL_MOVE_RIGHT -> {
                                warnPattern.setAngle(0f);
                                ;
                            }
                            case CONTROL_MOVE_LEFT_DOWN -> {
                                warnPattern.setAngle(225f);
                            }
                            case CONTROL_MOVE_LEFT_UP -> {
                                warnPattern.setAngle(135f);
                            }
                            case CONTROL_MOVE_RIGHT_UP -> {
                                warnPattern.setAngle(45f);
                            }
                            case CONTROL_MOVE_RIGHT_DOWN -> {
                                warnPattern.setAngle(315f);
                            }
                        }
                        warnTime -= delta;
                    } else {
                        attack();
                    }

                    boss.setSpriteSheet(attackSprite);
                }
            }
        }
    }

    @Override
    public boolean ended() {
        if (!gamestate.getBosses().get(0).isDestroyed() && !gamestate.getBosses().get(1)
            .isDestroyed()) {
            Vector2 pos = boss.getPosition();
            boolean wall = pos.x >= 1280 || pos.x <= 0 || pos.y >= 720
                || pos.y <= 0;
            if (wall) {
                if (pos.x >= 1280) {
                    boss.setX(1200);
                } else if (pos.x <= 0) {
                    boss.setX(80);
                } else if (pos.y >= 720) {
                    boss.setY(660);
                } else if (pos.y <= 0) {
                    boss.setY(80);
                }
            }
            return wall;
        }
        return true;
    }

    private void setControlCode() {
        Vector2 bPos = boss.getPosition();
        Vector2 pPos = player.getPlayerHead().getPosition();
        warnPattern.setX(bPos.x);
        warnPattern.setY(bPos.y);
        if (bPos.x < pPos.x) {
            if (bPos.y < pPos.y) {
                this.controlCode = CONTROL_MOVE_RIGHT_UP;
            } else if (bPos.y == pPos.y) {
                controlCode = CONTROL_MOVE_RIGHT;
            } else {
                controlCode = CONTROL_MOVE_RIGHT_DOWN;
            }
        } else if (bPos.x == pPos.x) {
            if (bPos.y < pPos.y) {
                controlCode = CONTROL_MOVE_UP;
            } else if (bPos.y == pPos.y) {
                controlCode = CONTROL_NO_ACTION;
            } else {
                controlCode = CONTROL_MOVE_DOWN;
            }
        } else {
            if (bPos.y < pPos.y) {
                controlCode = CONTROL_MOVE_LEFT_UP;
            } else if (bPos.y == pPos.y) {
                controlCode = CONTROL_MOVE_LEFT;
            } else {
                controlCode = CONTROL_MOVE_LEFT_DOWN;
            }
        }
    }
}
