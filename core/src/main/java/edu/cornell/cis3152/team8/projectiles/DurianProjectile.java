package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class DurianProjectile extends Projectile {

    private static Texture texture;

    //TODO: Change asset in json to durian spike
    public DurianProjectile(float x, float y, float vx, float vy, World world) {
        super(x, y, vx, vy, world);
        maxLife = 15;
        life = maxLife;
    }

    /**
     * Sets Durian Projectile assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("durianProjectile", Texture.class);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (obstacle.isActive()) {
            batch.draw(texture, obstacle.getX() * 64f - 16, obstacle.getY() * 64f - 16, 32, 32);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
