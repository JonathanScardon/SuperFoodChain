package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.graphics.SpriteBatch;

/**
 * Pineapple Projectile for the Pineapple companion.
 *
 * The pineapple projectile should be shot towards nearest enemy and explode upon impact,
 * damaging other enemies in nearby radius.
 */
public class PineappleProjectile extends Projectile {
    private static Texture texture; // art asset to be associated with the projectile

    public PineappleProjectile(float x, float y, float vx, float vy) {
        super(x,y,vx,vy);

        if (texture == null) {
            texture = new Texture("images/Projectile_Pineapple.png");
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
