package edu.cornell.cis3152.team8;

public class MouseController extends BossController {
    final int RADIUS = 40 * 3 / 2; // TEMPORARY

    public MouseController(Boss boss, GameState gameState) {
        super(boss, gameState);
        attack();
    }

    @Override
    public int getAction() {
        ticks++;

        if (ticks % 10 == 0) {
            // only update state every 10 frames
            if (state == FSMState.ATTACK) {
                if (boss.getY() + RADIUS < 0) {
                    boss.setX(boss.getX() + RADIUS * 2);
                    action = CONTROL_MOVE_UP;
                } else if (boss.getY() - RADIUS > 720) {
                    boss.setX(boss.getX() + RADIUS * 2);
                    action = CONTROL_MOVE_DOWN;
                }

                if (boss.getX() - RADIUS > 1280) {
                    // off the screen on the right, we're done attacking
                    state = FSMState.IDLE;
                    boss.setX((float) 1280 / 2);
                    boss.setY((float) 720 / 2);
                    action = CONTROL_NO_ACTION;
                }
            } else if (state == FSMState.IDLE) {
                boss.coolDown(true); // decrease attack cooldown

                // try to attack all the time
                if (boss.canAttack()) {
                    attack();
                }
            }
        }

        return action;
    }

    @Override
    public void attack() {
        boss.setX(RADIUS);
        boss.setY(-RADIUS);
        boss.coolDown(false);
        state = FSMState.ATTACK;
        action = CONTROL_MOVE_UP;
    }
}
