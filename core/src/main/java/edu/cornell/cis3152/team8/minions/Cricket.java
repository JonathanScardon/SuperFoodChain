package edu.cornell.cis3152.team8.minions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Minion;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Cricket extends Minion {

    private static SpriteSheet texture;

    private enum State {WAIT, HOP}

    private State state;
    private float waitTimer, hopTimer;
    private static final float WAIT_DURATION = 2f, HOP_DURATION = 0.3f, HOP_SPEED = 5f;

    public Cricket(float x, float y, World world, Player player) {
        super(x, y, world, player);
        this.player = player;
        this.state = State.WAIT;
        this.waitTimer = WAIT_DURATION;
        health = 2;
        animationSpeed = 0.15f;
        size = 0.3f * units;

        setSpriteSheet(texture);
    }

    /**
     * Sets Cricket assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("cricket.animation", SpriteSheet.class);
    }

    @Override
    public void update(boolean moving) {
        super.update(moving);
        if (moving) {
            float delta = Gdx.graphics.getDeltaTime();
            Vector2 pos = obstacle.getPosition().scl(units);

            switch (state) {
                case WAIT -> {
                    obstacle.setLinearVelocity(new Vector2(0, 0));
                    waitTimer -= delta;
                    animationFrame = 0;
                    if (waitTimer <= 0) {
                        state = State.HOP;
                        hopTimer = HOP_DURATION;
                    }
                }
                case HOP -> {
                    animationFrame = 1;
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

