package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Pool;

public class ProjectilePools {
    public static final Pool<StrawberryProjectile> strawberryPool = new Pool<StrawberryProjectile>() {
        @Override
        protected StrawberryProjectile newObject() {
            return new StrawberryProjectile(0, 0, 0, 0);
        }
    };

    public static final Pool<GarlicProjectile> garlicPool = new Pool<GarlicProjectile>() {
        @Override
        protected GarlicProjectile newObject() {
            return new GarlicProjectile(0, 0, 0, 0);
        }
    };

    public static final Pool<PineappleProjectile> pineapplePool = new Pool<PineappleProjectile>() {
        @Override
        protected PineappleProjectile newObject() {
            return new PineappleProjectile(0, 0, 0, 0);
        }
    };
}
