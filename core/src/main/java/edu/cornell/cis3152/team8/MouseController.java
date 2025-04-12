package edu.cornell.cis3152.team8;

public class MouseController extends BossController {
    public MouseController(Boss boss, GameState gameState) {
        super(boss, gameState);

        boss.radius = 40f * 3 / 2; // TODO: Move this to constants
    }
}
