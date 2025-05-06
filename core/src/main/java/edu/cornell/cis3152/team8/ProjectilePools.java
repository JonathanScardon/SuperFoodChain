package edu.cornell.cis3152.team8;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import edu.cornell.cis3152.team8.projectiles.*;
import edu.cornell.gdiac.physics2.BoxObstacle;
import edu.cornell.gdiac.physics2.Obstacle;

public class ProjectilePools {

    private static World world;

    // Initialize the pools with the world reference
    public static void initialize(World world) {
        ProjectilePools.world = world;
        strawberryPool.clear();
        garlicPool.clear();
        durianPool.clear();
        bossAreaPool.clear();
    }

    public static final Pool<StrawberryProjectile> strawberryPool = new Pool<StrawberryProjectile>() {
        @Override
        protected StrawberryProjectile newObject() {
            return new StrawberryProjectile(0, 0, 0, 0, world);
        }
    };

    public static final Pool<GarlicProjectile> garlicPool = new Pool<GarlicProjectile>() {
        @Override
        protected GarlicProjectile newObject() {
            Obstacle o = new BoxObstacle(0, 0, 1, 1);
            return new GarlicProjectile(o, world);
        }
    };

    public static final Pool<DurianProjectile> durianPool = new Pool<DurianProjectile>() {
        @Override
        protected DurianProjectile newObject() {
            return new DurianProjectile(0, 0, 0, 0, world);
        }
    };

    public static final Pool<BossAreaProjectile> bossAreaPool = new Pool<BossAreaProjectile>() {
        @Override
        protected BossAreaProjectile newObject() {
            Obstacle o = new BoxObstacle(0, 0, 1, 1);
            return new BossAreaProjectile(o, world);
        }
    };
}
