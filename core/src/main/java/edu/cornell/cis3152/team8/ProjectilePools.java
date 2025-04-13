package edu.cornell.cis3152.team8;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

public class ProjectilePools {

    private static World world;

    // Initialize the pools with the world reference
    public static void initialize(World world) {
        ProjectilePools.world = world;
    }

    public static final Pool<StrawberryProjectile> strawberryPool = new Pool<StrawberryProjectile>() {
        @Override
        protected StrawberryProjectile newObject() {
            return new StrawberryProjectile(0,0, 0, 0, world);
        }
    };

    public static final Pool<GarlicProjectile> garlicPool = new Pool<GarlicProjectile>() {
        @Override
        protected GarlicProjectile newObject() {
            return new GarlicProjectile(0,0, 0, 0, world);
        }
    };
}
