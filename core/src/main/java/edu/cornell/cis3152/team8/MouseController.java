package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;

import java.util.LinkedList;

public class MouseController extends BossController {
    private class MouseAttackPattern implements AttackPattern {
        private final int id;
        private final int startX, startY;
        private final int controlCode;
        private final int warnDuration;

        public MouseAttackPattern(int id, int x, boolean top, int warnDuration) {
            this.id = id;
            this.startX = x;
            this.startY = top ? 720 + RADIUS : -RADIUS;
            this.controlCode = top ? CONTROL_MOVE_DOWN : CONTROL_MOVE_UP;
            this.warnDuration = warnDuration;

            WarnPattern wp = new WarnPattern(startX, 720f / 2f);
            wp.setSpriteSheet(boss.warnSprites.get(0));
            boss.warnPatterns.add(wp);
        }

        @Override
        public void warn() {
            state = FSMState.WARN;
            boss.setX(startX);
            boss.setY(startY);
            boss.setVX(0);
            if (controlCode == CONTROL_MOVE_UP) {
                boss.setVY(15f);
                boss.angle = 90f;
            } else if (controlCode == CONTROL_MOVE_DOWN) {
                boss.setVY(-15f);
                boss.angle = 270f;
            }
            action = CONTROL_NO_ACTION;
            warnTime = warnDuration;
            boss.warnPatterns.get(id).active = true;
        }

        @Override
        public void attack() {
            state = FSMState.ATTACK;
            boss.attackCooldown(false);
            action = controlCode;
            boss.warnPatterns.get(id).active = false;
        }
    }

    final int RADIUS = 40 * 3 / 2; // TEMPORARY

    public MouseController(Boss boss, GameState gameState) {
        super(boss, gameState);

        // generate attack patterns
        int num_attacks = (int) Math.ceil(1280f / (RADIUS * 2f));
        attackPatterns = new Array<>();
        for (int i = 0; i < num_attacks; i++) {
            attackPatterns.add(new MouseAttackPattern(i, i * RADIUS * 2 + RADIUS, i % 2 == 1, 10));
        }
        plannedAttacks = new LinkedList<>();

        boss.attackCooldown(false);
        boss.angle = 90f;
        idle();
    }

    private void updateState() {
        if (state == FSMState.IDLE) {
            boss.attackCooldown(true); // decrease attack cooldown

            // try to attack all the time
            if (boss.canAttack()) {
                // choose set of attacks to do
                for (int i = 0; i < attackPatterns.size; i++) {
                    plannedAttacks.add(i);
                }
                state = FSMState.WARN;
                attackPatterns.get(plannedAttacks.peek()).warn();
            }
        } else if (state == FSMState.WARN) {
            if (warnTime > 0) {
                warnTime--;
            }
            boolean warningEnded = warnTime <= 0;
            if (warningEnded) {
                attackPatterns.get(plannedAttacks.poll()).attack();
            }
        } else if (state == FSMState.ATTACK) {
            boolean attackEnded = boss.getY() + RADIUS < 0 || boss.getY() - RADIUS > 720;
            if (attackEnded) {
                if (plannedAttacks.isEmpty()) {
                    // finished all attacks
                    idle();
                } else {
                    state = FSMState.WARN;
                    attackPatterns.get(plannedAttacks.peek()).warn();
                }
            }
        }
    }

    @Override
    public int getAction() {
        ticks++;

        if (ticks % 10 == 0) {
            updateState();
        }

        return action;
    }

    /**
     * Start idling between sets of attacks
     */
    public void idle() {
        state = FSMState.IDLE;
        action = CONTROL_NO_ACTION;

        boss.setX((float) 1280 / 2);
        boss.setY((float) 720 / 2);
        boss.setVX(0);
        boss.setVY(0);
    }
}
