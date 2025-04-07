package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;

import java.util.LinkedList;

public class MouseController extends BossController {
    public MouseController(Boss boss, GameState gameState) {
        super(boss, gameState);

        // generate attack patterns
        int num_attacks = (int) Math.ceil(1280f / (boss.getRadius() * 2f));
        attackPatterns = new Array<>();
        for (int i = 0; i < num_attacks; i++) {
            attackPatterns.add(new DashAttackPattern(this, i * boss.getRadius() * 2 + boss.getRadius(), i % 2 == 1, 10, boss.warnSprites.get(0)));
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
                chooseAttacks();
                curAttack = attackPatterns.get(plannedAttacks.poll());
                curAttack.warn();
            }
        } else if (state == FSMState.ATTACK) {
            // if we finished the current attack do the next one in the queue
            if (curAttack.attackEnded()) {
                if (plannedAttacks.isEmpty()) {
                    idle(); // finished all attacks
                } else {
                    curAttack = attackPatterns.get(plannedAttacks.poll()); // get next attack in queue
                    curAttack.warn();
                }
            }
        }
    }

    private void chooseAttacks() {
        // choose set of attacks to do
        for (int i = 0; i < attackPatterns.size; i++) {
            plannedAttacks.add(i);
        }
    }

    @Override
    public int getAction() {
        ticks++;

        if (ticks % 10 == 0) {
            if (curAttack != null) {
                curAttack.update();
            }
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
        curAttack = null;

        boss.setX((float) 1280 / 2);
        boss.setY((float) 720 / 2);
        boss.setVX(0);
        boss.setVY(0);
    }
}
