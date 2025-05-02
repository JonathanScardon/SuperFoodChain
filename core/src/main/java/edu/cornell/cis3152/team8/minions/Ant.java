package edu.cornell.cis3152.team8.minions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Minion;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Ant extends Minion {
    private static float MOVE_SPEED;
    private static int HEALTH;
    private static float ANIMATION_SPEED;
    private static float SIZE;
    private static SpriteSheet texture;

    /**
     * Constructs a Minion at the given position
     *
     * @param x      The x-coordinate of the object
     * @param y      The y-coordinate of the object
     * @param world
     * @param player
     */
    public Ant(float x, float y, World world, Player player) {
        super(x, y, world, player);
        moveSpeed = MOVE_SPEED;
        health = HEALTH;
        animationSpeed = ANIMATION_SPEED;
        size = SIZE;

        setSpriteSheet(texture);
    }

    /**
     * Sets Ant constants
     * @param constants attributes that describe ant minion characteristics like hp, movespeed, etc.
     */
    public static void setConstants(JsonValue constants) {
        MOVE_SPEED = constants.getFloat("moveSpeed", 1f);
        HEALTH = constants.getInt("health", 2);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.15f);
        SIZE = constants.getFloat("size", 0.3f) * units; // ensure size on-screen is multiplied by physics units
    }

    /**
     * Sets Ant assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("ant.animation", SpriteSheet.class);
    }

    @Override
    public void update(boolean moving) {
        super.update(moving);
        if (moving) {
            Vector2 pos = obstacle.getPosition().scl(units);
            Vector2 dir = player.getPlayerHead().getObstacle().getPosition().scl(units).sub(pos)
                .nor();
            obstacle.setLinearVelocity(dir.scl(moveSpeed));
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }
    }
}
