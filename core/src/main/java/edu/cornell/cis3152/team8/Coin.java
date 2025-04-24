package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public class Coin extends ObstacleSprite {

    /**
     * Current animation frame for this coin
     */
    private float animationFrame;
    /**
     * How fast we change frames
     */
    private static float animationSpeed;
    private float size;

    private SpriteSheet plusOne;
    private boolean collected;
    boolean remove;
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

        Texture texture = new Texture("images/coin.png");
        SpriteSheet coin = new SpriteSheet(texture, 1, 22);
        setSpriteSheet(coin);
        collected = false;
        plusOne = new SpriteSheet(new Texture("images/+1.png"), 1, 6);
        animationSpeed = 0.5f;
        remove = false;
        //setConstants(constants);

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

        size = 0.3f * units;
        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
    }

    /**
     * Sets constants for this Coin
     *
     * @param constants The JsonValue of the object
     */
    private void setConstants(JsonValue constants) {
//        this.constants = constants;
        animationSpeed = constants.getFloat("animation speed");
    }


    @Override
    /**
     * Updates the state of this Coin.
     *
     * This method is only intended to update values that change local state
     * in well-defined ways, like position or a cooldown value. It does not
     * handle collisions (which are determined by the CollisionController). It
     * is not intended to interact with other objects in any way at all.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(float delta) {
        if (sprite != null) {
            animationFrame += animationSpeed;
            if (!collected && animationFrame >= sprite.getSize()) {
                animationFrame -= sprite.getSize();
            }
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
            if (!collected) {
                animationFrame = 0;
                animationSpeed = 0.15f;
                collected = true;

                setSpriteSheet(plusOne);
            }
            if (animationFrame < sprite.getSize()) { // and animation is not over
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

    public boolean shouldRemove() {
        return remove;
    }
}
