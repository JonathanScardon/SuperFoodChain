package edu.cornell.cis3152.team8;

import java.util.LinkedList;

public class MouseController extends BossController {
    private class MouseAttackPattern implements AttackPattern {
        private final int startX, startY;
        private final int controlCode;
        private final int warnDuration;

        public MouseAttackPattern(int x, boolean top, int warnDuration) {
            startX = x;
            startY = top ? 720 + RADIUS: -RADIUS;
            controlCode = top ? CONTROL_MOVE_DOWN : CONTROL_MOVE_UP;
            this.warnDuration = warnDuration;
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
        }

        @Override
        public void attack() {
            state = FSMState.ATTACK;
            boss.attackCooldown(false);
            action = controlCode;
        }
    }

    final int RADIUS = 40 * 3 / 2; // TEMPORARY

    public MouseController(Boss boss, GameState gameState) {
        super(boss, gameState);

        // generate attack patterns
        int num_attacks = (int) Math.ceil(1280f / (RADIUS * 2f));
        attackPatterns = new AttackPattern[num_attacks];
        for (int i = 0; i < num_attacks; i++) {
            attackPatterns[i] = new MouseAttackPattern(i * RADIUS * 2 + RADIUS, i % 2 == 1, 10);
        }
        plannedAttacks = new LinkedList<Integer>();

        boss.attackCooldown(false);
        boss.angle = 90f;
        idle();
    }

    @Override
    public int getAction() {
        ticks++;

        if (ticks % 10 == 0) {
            // only update state every 10 frames
            if (state == FSMState.IDLE) {
                boss.attackCooldown(true); // decrease attack cooldown

                // try to attack all the time
                if (boss.canAttack()) {
                    // choose set of attacks to do
                    for (int i = 0; i < attackPatterns.length; i++) {
                        plannedAttacks.add(i);
                    }
                    state = FSMState.WARN;
                    attackPatterns[plannedAttacks.peek()].warn();
                }
            } else if (state == FSMState.WARN) {
                if (warnTime > 0) {
                    warnTime--;
                }
                boolean warningEnded = warnTime <= 0;
                if (warningEnded) {
                    attackPatterns[plannedAttacks.poll()].attack();
                }
            } else if (state == FSMState.ATTACK) {
                boolean attackEnded = boss.getY() + RADIUS < 0 || boss.getY() - RADIUS > 720;
                if (attackEnded) {
                    if (plannedAttacks.isEmpty()) {
                        // finished all attacks
                        idle();
                    } else {
                        state = FSMState.WARN;
                        attackPatterns[plannedAttacks.peek()].warn();
                    }
                }
            }
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
