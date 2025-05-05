package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * Triggers multiple boss attacks at once
 */
public class MultiAttackPattern extends BossAttackPattern {
    private final Array<BossAttackPattern> attackPatterns;
    private final float warnDuration;
    private final BossWarnPattern warnPattern;

    private float warnTime;

    public MultiAttackPattern(BossController controller, float warnDuration, Array<BossAttackPattern> attackPatterns, SpriteSheet warnSprite) {
        super(controller);

        attackName = "multi";

        this.attackPatterns = attackPatterns;
        this.warnDuration = warnDuration;

        this.warnPattern = new RectWarnPattern(boss.getObstacle().getPosition().x, boss.getObstacle().getPosition().y, 0, 0);
        this.warnPattern.setSpriteSheet(warnSprite);
        this.boss.warnPatterns.add(warnPattern);
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);
        boss.setAnimation("multi");

        warnTime = warnDuration;

        for (BossAttackPattern attackPattern : attackPatterns) {
            attackPattern.start();
        }

        warnPattern.active = true;
    }

    public void attack() {
        state = AttackState.ATTACK;

        this.spawnMinions();

        warnPattern.active = false;
    }

    @Override
    public void update(float delta) {
        for (BossAttackPattern attackPattern : attackPatterns) {
            attackPattern.update(delta);
        }

        switch (state) {
            case WARN:
                if (warnTime > 0) {
                    warnTime -= delta;
                } else {
                    attack();
                }
                break;
            case ATTACK:
                boolean allEnded = true;
                for (BossAttackPattern attackPattern : attackPatterns) {
                    if (!attackPattern.isEnded()) {
                        allEnded = false;
                        break;
                    }
                }

                if (allEnded) {
                    this.state = AttackState.ENDED;
                }
                break;
        }
    }
}
