package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.GameScene;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import edu.cornell.gdiac.physics2.BoxObstacle;
import edu.cornell.gdiac.physics2.Obstacle;

/**
 * Projectile specific to the Garlic companion
 * <p>
 * The projectile should be shooting a projectile that then has a lingering poison cloud effect
 */
public class GarlicProjectile extends Projectile {

    private static float MAX_LIFE;
    private static int ATTACK;
    private static SpriteSheet texture; // Make texture static so it's shared
    private float animationFrame = 0f;
    private float animationSpeed = 0.05f;

    public GarlicProjectile(Obstacle o, World world) {
        // Call the parent constructor (in Projectile)
        super(o, world);
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
        MAX_LIFE = constants.getFloat("maxLife", 1f);
        ATTACK = constants.getInt("attack", 1);
    }

    /**
     * Sets Garlic projectile assets
     */
    public static void setAssets(AssetDirectory assets) {

        texture = assets.getEntry("garlicProjectile.animation", SpriteSheet.class);

    }

    @Override
    public void draw(SpriteBatch batch) {
        if (obstacle.isActive()) {
            SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
                sprite.getRegionHeight() / 2.0f,
                obstacle.getX() * GameScene.PHYSICS_UNITS, obstacle.getY() * GameScene.PHYSICS_UNITS, 0, 0.2f,
                0.2f);
            animationFrame += animationSpeed;
            if (animationFrame >= texture.getSize()) {
                animationFrame = 0;
            }
            texture.setFrame((int) animationFrame);
            batch.draw(texture, transform);
        }
    }
}
