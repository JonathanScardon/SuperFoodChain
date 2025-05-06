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

public class Spider extends Minion {

    private static SpriteSheet texture;
    private Vector2 direction;
    private static float ALIVE_DURATION; // alive_duration of 5 seconds
    private float aliveTimer;
    private static float MOVE_SPEED;
    private static int HEALTH;
    private static float ANIMATION_SPEED;
    private static float SIZE;

    public Spider(float x, float y, World world, Player player) {
        super(x, y, world, player);
        //setSpriteSheet(new SpriteSheet(new Texture("images/Spider.png"), 1, 3));
        this.aliveTimer = ALIVE_DURATION;
        moveSpeed = MOVE_SPEED;
        health = HEALTH;
        animationSpeed = ANIMATION_SPEED;
        size = SIZE * units;
        Vector2 spawnPos = new Vector2(x, y);
        Vector2 playerPos = new Vector2(player.getPlayerHead().getObstacle().getPosition()).scl(
            units);
        this.direction = new Vector2(playerPos.x - spawnPos.x,playerPos.y - spawnPos.y ).nor();
        setSpriteSheet(texture);
    }

    /**
     * Sets Spider constants
     * @param constants attributes that describe spider characteristics like hp, movespeed, etc.
     */
    public static void setConstants(JsonValue constants) {
        ALIVE_DURATION = constants.getFloat("aliveDuration", 5f);
        MOVE_SPEED = constants.getFloat("moveSpeed", 7);
        HEALTH = constants.getInt("health", 1);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.15f);
        SIZE = constants.getFloat("size", 0.3f);
    }

    /**
     * Sets Strawberry assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("spider.animation", SpriteSheet.class);
    }

    @Override
    public void update(boolean moving) {
        super.update(moving);
        if (moving) {
            float delta = Gdx.graphics.getDeltaTime();
            aliveTimer -= delta;
            if (aliveTimer < 0) {
                // should reduce the Spider's health to 0 --> clean-up in CollisionController's postUpdate function
                removeHealth(getHealth());
            }
            obstacle.setLinearVelocity(new Vector2(direction).scl(moveSpeed));
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }
    }
}
