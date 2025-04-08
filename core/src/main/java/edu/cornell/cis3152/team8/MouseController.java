package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;

import java.util.LinkedList;

public class MouseController extends BossController {
    public MouseController(Boss boss, GameState gameState) {
        super(boss, gameState);

        // generate attack patterns
        boss.radius = 40f * 3 / 2; // TODO: Move this to constants

        int num_attacks = (int) Math.ceil(1280f / (boss.getRadius() * 2f));
        attackPatterns = new Array<>();
        attackPatterns.add(new IdleAttackPattern(this, 1280f / 2, 720f / 2, 2, 2, boss.warnSprites.get(0)));
        for (int i = 0; i < num_attacks; i++) {
            attackPatterns.add(new DashAttackPattern(this, i * boss.getRadius() * 2 + boss.getRadius(), i % 2 == 1, 2, boss.warnSprites.get(1)));
        }

        plannedAttacks = new LinkedList<>();
        chooseAttacks();

        // immediately start doing the first attack
        curAttack = attackPatterns.get(plannedAttacks.poll());
        curAttack.warn();
    }

    /**
     * The boss chooses which attacks to do
     */
    private void chooseAttacks() {
        // the mouse just does all of its attacks in order
        for (int i = 0; i < attackPatterns.size; i++) {
            plannedAttacks.add(i);
        }
    }

    @Override
    public int getAction() {
        return action;
    }

    @Override
    public void update(float delta) {
        ticks++;

        if (curAttack != null) {
            curAttack.update(delta);
        }

        if (ticks % 10 == 0) {
            // if we finished the current attack do the next one in the queue
            if (curAttack != null && curAttack.attackEnded()) {
                if (plannedAttacks.isEmpty()) {
                    chooseAttacks();
                }
                curAttack = attackPatterns.get(plannedAttacks.poll());
                curAttack.warn();
            }
        }
    }
}
