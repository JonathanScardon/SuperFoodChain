package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class DurianProjectile extends Projectile {
    private static int MAX_LIFE;
    private static int ATTACK;
    private static Texture texture;
    private float angle;

    //TODO: Change asset in json to durian spike
    public DurianProjectile(float x, float y, float vx, float vy, World world) {
        super(x, y, vx, vy, world);
        maxLife = MAX_LIFE;
        life = maxLife;
        setAttack(ATTACK);
    }

    /**
     * Sets Durian projectile constants
     *
     * @param constants json associated with durian projectile constants
     */
    public static void setConstants(JsonValue constants) {
        MAX_LIFE = constants.getInt("maxLife");
        ATTACK = constants.getInt("attack");
    }

    /**
     * Sets Durian projectile assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("durianProjectile", Texture.class);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (obstacle.isActive()) {
            SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
                sprite.getRegionHeight() / 2.0f,
                obstacle.getX() * units, obstacle.getY() * units, angle + 90, 0.15f,
                0.15f);
            batch.draw(texture, transform);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void setAngle(float a) {
        angle = a;
    }
}
