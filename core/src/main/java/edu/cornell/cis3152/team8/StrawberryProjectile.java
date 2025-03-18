package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.graphics.SpriteBatch;

/**
 * Projectile specific to the Strawberry companion
 *
 * The projectile should be shooting a set amount of projectiles in random
 * directions
 */
public class StrawberryProjectile extends Projectile {
    private static Texture texture; // Make texture static so it's shared

    public StrawberryProjectile(float x, float y, float vx, float vy) {
        // Call the parent constructor (in Projectile)
        super(x, y, vx, vy);
        if (texture == null) {
            texture = new Texture("images/Strawberry_Seed.png");
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isDestroyed()) {
            batch.draw(texture, position.x, position.y, 32, 32);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
