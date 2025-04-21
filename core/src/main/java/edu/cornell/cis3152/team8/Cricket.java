package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Cricket extends Minion {

    private enum State {WAIT, HOP}

    private State state;
    private float waitTimer, hopTimer;
    private static final float WAIT_DURATION = 2f, HOP_DURATION = 0.3f, HOP_SPEED = 5f;
    private Player player;
    private static final float units = 64f;

    public Cricket(float x, float y, int id, World world, Player player) {
        super(x, y, id, world, player);
        setTexture(new Texture("images/Cricket.png"));
        this.player = player;
        this.state = State.WAIT;
        this.waitTimer = WAIT_DURATION;
    }

    @Override
    public void update(boolean u) {
        if (u) {
            float delta = Gdx.graphics.getDeltaTime();
            Vector2 pos = obstacle.getPosition().scl(units);

            switch (state) {
                case WAIT -> {
                    obstacle.setLinearVelocity(new Vector2(0, 0));
                    waitTimer -= delta;
                    if (waitTimer <= 0) {
                        state = State.HOP;
                        hopTimer = HOP_DURATION;
                    }
                }
                case HOP -> {
                    Vector2 dir = player.getPlayerHead().getObstacle().getPosition().scl(units)
                        .sub(pos)
                        .nor();
                    obstacle.setLinearVelocity(dir.scl(HOP_SPEED));
                    hopTimer -= delta;
                    if (hopTimer <= 0) {
                        obstacle.setLinearVelocity(new Vector2(0, 0));
                        state = State.WAIT;
                        waitTimer = WAIT_DURATION;
                    }
                }
            }
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }
    }
}

