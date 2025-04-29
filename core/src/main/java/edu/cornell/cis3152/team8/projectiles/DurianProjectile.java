package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class DurianProjectile extends Projectile {
    private static Texture texture;

    public DurianProjectile(float x, float y, float vx, float vy, World world) {
        super(x, y, vx, vy, world);
        maxLife = 15;
        life = maxLife;
        if (texture == null) {
            // TODO: Change Durian projectile texture to Durian spike when asset available
            texture = new Texture("images/Projectile_Pineapple.png");
        }
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
