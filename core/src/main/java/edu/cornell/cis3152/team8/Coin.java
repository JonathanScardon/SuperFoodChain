package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public class Coin extends ObstacleSprite {

    /**
     * Visual
     */
    private static SpriteSheet texture;
    private static SpriteSheet plusOne;
    private float animationFrame;
    private static float animationSpeed;
    private static float deathAnimationSpeed;
    private float currAnimationSpeed;

    private static float size;
    private float alpha; //For disappearing

    /**
     * How long the coin should persist for
     */
    private static int life;
    private int currLife;

    /**
     * Collection and removal info
     */
    private boolean collected;
    boolean remove;

    /**
     * Physics units
     */
    private static final float units = 64f;

    /**
     * Constructs a Coin at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Coin(float x, float y, World world) {
        // taking in the minion position which is already in units --> change for initCoins
        super(new CapsuleObstacle(x, y, 0.8f, 0.8f), true);
        collected = false;
        remove = false;
        alpha = 1f;
        currLife = life;
        currAnimationSpeed = animationSpeed;

        obstacle = getObstacle();
        obstacle.setName("coin");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.KinematicBody);

        obstacle.setPhysicsUnits(units);

        obstacle.activatePhysics(world);
        obstacle.setUserData(this);
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.COIN_CATEGORY;
        filter.maskBits = CollisionController.PLAYER_CATEGORY;
        obstacle.setFilterData(filter);

        mesh.set(-size / 2.0f, -size / 2.0f, size, size);

        setSpriteSheet(texture);
    }

    /**
     * Sets constants for this Coin
     *
     * @param constants The JsonValue of the object
     */
    public static void setConstants(JsonValue constants) {
        life = constants.getInt("life");
        size = constants.getFloat("size") * units;
        animationSpeed = constants.getFloat("animationSpeed");
        deathAnimationSpeed = constants.getFloat("deathAnimationSpeed");
    }

    /**
     * Sets assets for this Coin
     *
     * @param assets The AssetDirectory of the object
     */
    public static void setAssets(AssetDirectory assets) {
        plusOne = assets.getEntry("plusOne.animation", SpriteSheet.class);
        texture = assets.getEntry("coin.animation", SpriteSheet.class);
    }

    /**
     * Returns the current life value of the coin
     *
     * @return life value
     */
    public int getLife() {
        return currLife;
    }

    /**
     * The coin is no longer needed once it is collected or disappeared.
     *
     * @return whether the coin should be removed from the world
     */
    public boolean shouldRemove() {
        return remove;
    }

    /**
     * Updates the state of this Coin. This method is only intended to update values that change
     * local state in well-defined ways, like position or a cooldown value. It does not handle
     * collisions (which are determined by the CollisionController). It is not intended to interact
     * with other objects in any way at all.
     *
     * @param delta Number of seconds since last animation frame
     */
    @Override
    public void update(float delta) {
        if (sprite != null) {
            animationFrame += currAnimationSpeed;
            if (!collected && animationFrame >= sprite.getSize()) {
                animationFrame -= sprite.getSize();
            }
        }
        currLife--;
        if (currLife < 0) {
            alpha -= delta;

        }
    }

    /**
     * Draws this Coin to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
            sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * units,
            obstacle.getPosition().y * units, 0.0f, size / units, size / units);
        if (!obstacle.isActive()) { // if destroyed...
            if (currLife <= 0) {
                batch.setColor(1, 1, 1, alpha);
            } else if (!collected) {
                animationFrame = 0;
                currAnimationSpeed = deathAnimationSpeed;

                collected = true;
                setSpriteSheet(plusOne);
            }
            if (animationFrame < sprite.getSize() && alpha > 0) { // and animation is not over
                sprite.setFrame((int) animationFrame);
                batch.draw(sprite, transform); //
            } else {
                remove = true;
            }
        } else { // if not destroyed, draw as normal
            sprite.setFrame((int) animationFrame);
            batch.draw(sprite, transform);
        }
        batch.setColor(Color.WHITE);
    }


}
