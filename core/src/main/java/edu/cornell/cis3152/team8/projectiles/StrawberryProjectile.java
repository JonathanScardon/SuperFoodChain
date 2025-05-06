package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.GameScene;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;

/**
 * Projectile specific to the Strawberry companion
 * <p>
 * The projectile should be shooting a set amount of projectiles in random directions
 */
public class StrawberryProjectile extends Projectile {
    private static int MAX_LIFE;
    private static int ATTACK;
    private static Texture texture; // Make texture static so it's shared

    private float angle;

    public StrawberryProjectile(float x, float y, float vx, float vy, World world) {
        // Call the parent constructor (in Projectile)
        super(x, y, vx, vy, world);
        maxLife = MAX_LIFE;
        life = maxLife;
        setAttack(ATTACK);
    }

    /**
     * Sets Strawberry projectile constants
     *
     * @param constants json associated with strawberry projectiles
     */
    public static void setConstants(JsonValue constants) {
        MAX_LIFE = constants.getInt("maxLife", 60);
        ATTACK = constants.getInt("attack", 1);
    }

    /**
     * Sets Strawberry projectile assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("strawberryProjectile", Texture.class);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (obstacle.isActive()) {
            SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
                sprite.getRegionHeight() / 2.0f,
                obstacle.getX() * GameScene.PHYSICS_UNITS - 16, obstacle.getY() * GameScene.PHYSICS_UNITS - 16, angle + 90, 0.2f,
                0.2f);
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
