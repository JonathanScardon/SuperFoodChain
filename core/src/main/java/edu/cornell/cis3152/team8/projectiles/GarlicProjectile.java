package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Projectile;

/**
 * Projectile specific to the Garlic companion
 *
 * The projectile should be shooting a projectile that then has a lingering poison cloud effect
 */
public class GarlicProjectile extends Projectile {
    public GarlicProjectile(float x, float y, float vx, float vy, World world) {
        // Call parent constructor for now
        super(x,y,vx,vy,world);
    }
}
