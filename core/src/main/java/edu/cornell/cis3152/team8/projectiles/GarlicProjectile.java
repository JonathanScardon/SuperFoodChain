package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.assets.AssetDirectory;

/**
 * Projectile specific to the Garlic companion
 * <p>
 * The projectile should be shooting a projectile that then has a lingering poison cloud effect
 */
public class GarlicProjectile extends Projectile {

    private static Texture texture;

    public GarlicProjectile(float x, float y, float vx, float vy, World world) {
        // Call parent constructor for now
        super(x, y, vx, vy, world);
    }

    /**
     * Sets Garlic Projectile assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("garlicProjectile", Texture.class);
    }
}
