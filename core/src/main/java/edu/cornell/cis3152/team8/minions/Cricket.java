package edu.cornell.cis3152.team8.minions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Minion;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Cricket extends Minion {

    private static SpriteSheet texture;

    private enum State {WAIT, HOP}

    private State state;
    private float waitTimer, hopTimer;
    private static float WAIT_DURATION;
    private static float HOP_DURATION;
    private static float HOP_SPEED;
    private static int HEALTH;
    private static float ANIMATION_SPEED;
    private static float SIZE;

    public Cricket(float x, float y, World world, Player player) {
        super(x, y, world, player);
        this.player = player;
        this.state = State.WAIT;
        this.waitTimer = WAIT_DURATION;
        health = HEALTH;
        animationSpeed = ANIMATION_SPEED;
        size = SIZE * units;

        setSpriteSheet(texture);
    }

    /**
     * Sets Cricket constants
     * @param constants attributes that describe cricket characteristics like hp, movespeed, etc.
     */
    public static void setConstants(JsonValue constants) {
        WAIT_DURATION = constants.getFloat("waitDuration", 2f);
        HOP_DURATION = constants.getFloat("hopeDuration", 0.3f);
        HOP_SPEED = constants.getFloat("hopSpeed", 5f);
        HEALTH = constants.getInt("health", 2);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.15f);
        SIZE = constants.getFloat("size", 0.3f);
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

