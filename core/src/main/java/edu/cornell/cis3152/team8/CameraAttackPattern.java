package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraAttackPattern extends BossAttackPattern {
    private final OrthographicCamera camera;
    private final float warnDuration;
    private final Vector3 targetPosition;

    private float warnTime;
    private final Vector3 originalPosition;
    private final Vector3 temp;

    protected CameraAttackPattern(BossController controller, float x, float y, float warnDuration, OrthographicCamera camera) {
        super(controller);

        attackName = "camera";
        this.warnDuration = warnDuration;
        this.camera = camera;
        this.targetPosition = new Vector3(x, y, camera.position.z);
        this.originalPosition = new Vector3();
        this.temp = new Vector3();
    }

    @Override
    public void start() {
        this.state = AttackState.WARN;
        this.warnTime = 0f;

        originalPosition.set(camera.position);
        boss.setAnimation("camera");
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case WARN:
                warnTime += delta;
                float t = Math.min(warnTime / warnDuration, 1f);
                float easedT = (t < 0.5f) ? 2f * t * t : -1f + (4f - 2f * t) * t;

                temp.set(originalPosition).lerp(targetPosition, easedT);
                camera.position.set(temp);
                camera.update();

                if (t >= 1f) {
                    state = AttackState.ATTACK;
                }
                break;
            case ATTACK:
                this.spawnMinions();
                state = AttackState.ENDED;
                break;
        }
    }
}
