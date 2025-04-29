package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.graphics.SpriteBatch;

/**
 * Pineapple Projectile for the Pineapple companion.
 *
 * The pineapple projectile should be shot towards nearest enemy and explode upon impact,
 * damaging other enemies in nearby radius.
 */
public class PineappleProjectile extends Projectile {
    private static Texture texture; // art asset to be associated with the projectile

    public PineappleProjectile(float x, float y, float vx, float vy, World world) {
        super(x,y,vx,vy,world);

        if (texture == null) {
            texture = new Texture("images/Projectile_Pineapple.png");
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (obstacle.isActive()) {
            // TODO: change for physics units
            batch.draw(texture, obstacle.getPosition().x, obstacle.getPosition().y, 32, 32);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
