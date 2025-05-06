package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.GameScene;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;

/**
 * Projectile specific to the Garlic companion
 * <p>
 * The projectile should be shooting a projectile that then has a lingering poison cloud effect
 */
public class GarlicProjectile extends Projectile {

    private static int MAX_LIFE;
    private static int ATTACK;
    private static Texture texture; // Make texture static so it's shared

    public GarlicProjectile(float x, float y, World world) {
        // Call the parent constructor (in Projectile)
        super(x, y, world);
        setMaxLife(MAX_LIFE);
        life = getMaxLife();
        collisionDie = false;
        setAttack(ATTACK);
    }

    /**
     * Sets Garlic projectile constants
     *
     * @param constants json associated with garlic projectiles
     */
    public static void setConstants(JsonValue constants) {
        MAX_LIFE = constants.getInt("maxLife", 60);
        ATTACK = constants.getInt("attack", 1);
    }

    /**
     * Sets Garlic projectile assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("strawberryProjectile", Texture.class);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (obstacle.isActive()) {
            SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
                sprite.getRegionHeight() / 2.0f,
                obstacle.getX() * GameScene.PHYSICS_UNITS, obstacle.getY() * GameScene.PHYSICS_UNITS, 0, 0.2f,
                0.2f);
            batch.draw(texture, transform);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
