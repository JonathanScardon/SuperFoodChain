package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.graphics.SpriteSheet;

import static edu.cornell.cis3152.team8.InputController.*;

/**
 * Triggers multiple boss attacks at once
 */
public class MultiAttackPattern extends BossAttackPattern {
    private final Array<BossAttackPattern> attackPatterns;
    private final Array<Float> attackDelayDurations;
    private final Array<Float> attackDelayTimes;
    private final float warnDuration;
    private final BossWarnPattern warnPattern;

    private float warnTime;

    public MultiAttackPattern(BossController controller, float warnDuration, Array<BossAttackPattern> attackPatterns, Array<Float> attackDelayDurations, SpriteSheet warnSprite) {
        super(controller);

        attackName = "multi";

        this.attackPatterns = attackPatterns;
        this.attackDelayDurations = attackDelayDurations;
        this.attackDelayTimes = new Array<>(attackDelayDurations.size);
        for (int i = 0; i < attackDelayDurations.size; i++) {
            this.attackDelayTimes.add(0f);
        }
        this.warnDuration = warnDuration;

        this.warnPattern = new RectWarnPattern(boss.getObstacle().getPosition().x, boss.getObstacle().getPosition().y, 0, 0);
        this.warnPattern.setSpriteSheet(warnSprite);
        this.warnPattern.setAnimationSpeedWithDuration(warnDuration);
        this.boss.warnPatterns.add(warnPattern);
    }

    @Override
    public void start() {
        state = AttackState.WARN;
        controller.setAction(CONTROL_NO_ACTION);
        boss.setAnimation("multi", 0.05f, true);

        warnTime = warnDuration;

        warnPattern.setActive(true);
    }

    public void attack() {
        state = AttackState.ATTACK;

        this.spawnMinions();

        for (int i = 0; i < attackDelayDurations.size; i++) {
            attackDelayTimes.set(i, attackDelayDurations.get(i));
        }

        warnPattern.setActive(false);
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
                for (int i = 0; i < attackDelayDurations.size; i++) {
                    if (attackDelayTimes.get(i) > 0) {
                        attackDelayTimes.set(i, attackDelayTimes.get(i) - delta);
                    } else if (attackDelayTimes.get(i) != -1f){
                        attackDelayTimes.set(i, -1f); // mark as started
                        attackPatterns.get(i).start();
                    }
                }

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
