package edu.cornell.cis3152.team8.minions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Minion;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Ant extends Minion {

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
        moveSpeed = 1f;
        health = 2;
        animationSpeed = 0.15f;
        size = 0.3f * units;

        setSpriteSheet(texture);
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
