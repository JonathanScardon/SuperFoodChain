package edu.cornell.cis3152.team8;

import edu.cornell.cis3152.team8.projectiles.BossAreaProjectile;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class AreaAttackPattern extends BossAttackPattern {
    private final GameState gameState; // TODO: this is probably bad design
    private final BossWarnPattern warnPattern;
    private final float targetX, targetY;
    private final float attackRadius;
    private final float warnDuration;
    private final float attackDuration;

    private float warnTime;
    private float attackTime;

    public AreaAttackPattern(BossController controller, float x, float y, float r, float warnDuration, float attackDuration, SpriteSheet warnSprite, GameState gameState) {
        super(controller);

        this.gameState = gameState;

        attackName = "area";
        this.targetX = x;
        this.targetY = y;
        this.attackRadius = r;
        this.warnDuration = warnDuration;
        this.attackDuration = attackDuration;

        this.warnPattern = new CircleWarnPattern(x, y, r * GameScene.PHYSICS_UNITS);
        this.warnPattern.setSpriteSheet(warnSprite);
        this.boss.warnPatterns.add(this.warnPattern);
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        boss.setAnimation("area", 0.05f, true);
        warnTime = warnDuration;
        warnPattern.active = true;
    }

    public void attack() {
        state = AttackState.ATTACK;
        attackTime = attackDuration;
        this.spawnMinions();
        warnPattern.active = false;

        BossAreaProjectile projectile = ProjectilePools.bossAreaPool.obtain();
        projectile.setFixture(attackRadius);
        projectile.getObstacle().getBody().setActive(true);
        projectile.getObstacle().setVX(0);
        projectile.getObstacle().setVY(0);

        projectile.getObstacle().setPosition(targetX, targetY);
        projectile.setSpriteSheet(boss.getAnimation("areaAttack"));
        projectile.setMaxLife((int) attackDuration);
        projectile.setLife((int) attackDuration);

        gameState.getActiveProjectiles().add(projectile);
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
                break;
            case ATTACK:
                if (attackTime > 0) {
                    attackTime -= delta;
                } else {
                    state = AttackState.ENDED;
                }
                break;
        }
    }
}
