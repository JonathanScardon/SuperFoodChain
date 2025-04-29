package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.graphics.SpriteBatch;

/**
 * Projectile specific to the Strawberry companion
 *
 * The projectile should be shooting a set amount of projectiles in random
 * directions
 */
public class StrawberryProjectile extends Projectile {
    private static Texture texture; // Make texture static so it's shared

    public StrawberryProjectile(float x, float y, float vx, float vy, World world) {
        // Call the parent constructor (in Projectile)
        super(x, y, vx, vy, world);
        maxLife = 60;
        life = maxLife;
        if (texture == null) {
            texture = new Texture("images/Strawberry_Seed.png");
        }
        setAttack(1);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (obstacle.isActive()) {
            batch.draw(texture, obstacle.getX()* 64f - 16, obstacle.getY() * 64f - 16, 32, 32);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
