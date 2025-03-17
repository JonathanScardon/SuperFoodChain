/**
 * Heavily inspired by the Optimization lab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;

public abstract class GameObject {
    public enum ObjectType {
        PLAYER,
        COMPANION,
        MINION,
        COIN,
        BOSS,
        PROJECTILE,
    }

    // Attributes for all game objects
    /**
     * Object position (centered on the texture middle)
     */
    protected Vector2 position;
    /**
     * Object velocity vector
     */
    protected Vector2 velocity;
    /**
     * Reference to texture origin
     */
    protected Vector2 origin;
    /**
     * Radius of the object (used for collisions)
     */
    protected float radius;
    /**
     * Whether the object should be removed at next time step.
     */
    protected boolean destroyed;
    /**
     * CURRENT image for this object. May change over time.
     */
    protected SpriteSheet animator;
    /**
     * Affine transform to draw the sprite sheet
     */
    protected Affine2 transform;
    /**
     * The constants defining this game object
     */
    protected JsonValue constants;

    // ACCESSORS

    /**
     * Sets the sprite sheet for this game object
     * <p>
     * This value can change over time.
     *
     * @param sheet The sprite sheet
     */
    public void setSpriteSheet(SpriteSheet sheet) {
        animator = sheet;
        radius = animator.getRegionHeight() / 2.0f;
        origin = new Vector2(animator.getRegionWidth() / 2.0f, animator.getRegionHeight() / 2.0f);
    }

    /**
     * Returns the sprite sheet for this game object
     * <p>
     * This value can change over time.
     *
     * @return the sprite sheet for this game object
     */
    public SpriteSheet getSpriteSheet() {
        return animator;
    }

    /**
     * Returns the position of this object (e.g. location of the center pixel)
     * <p>
     * The value returned is a reference to the position vector, which may be
     * modified freely.
     *
     * @return the position of this object
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the x-coordinate of the object position (center).
     *
     * @return the x-coordinate of the object position
     */
    public float getX() {
        return position.x;
    }

    /**
     * Sets the x-coordinate of the object position (center).
     *
     * @param value the x-coordinate of the object position
     */
    public void setX(float value) {
        position.x = value;
    }

    /**
     * Returns the y-coordinate of the object position (center).
     *
     * @return the y-coordinate of the object position
     */
    public float getY() {
        return position.y;
    }

    /**
     * Sets the y-coordinate of the object position (center).
     *
     * @param value the y-coordinate of the object position
     */
    public void setY(float value) {
        position.y = value;
    }

    /**
     * Returns the velocity of this object in pixels per animation frame.
     * <p>
     * The value returned is a reference to the velocity vector, which may be
     * modified freely.
     *
     * @return the velocity of this object
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Returns the x-coordinate of the object velocity.
     *
     * @return the x-coordinate of the object velocity.
     */
    public float getVX() {
        return velocity.x;
    }

    /**
     * Sets the x-coordinate of the object velocity.
     *
     * @param value the x-coordinate of the object velocity.
     */
    public void setVX(float value) {
        velocity.x = value;
    }

    /**
     * Gets the y-coordinate of the object velocity.
     */
    public float getVY() {
        return velocity.y;
    }

    /**
     * Sets the y-coordinate of the object velocity.
     *
     * @param value the y-coordinate of the object velocity.
     */
    public void setVY(float value) {
        velocity.y = value;
    }

    /**
     * Returns true if this object is destroyed.
     * <p>
     * Objects are not removed immediately when destroyed. They are garbage
     * collected at the end of the frame. This tells us whether the object
     * should be garbage collected at the frame end.
     *
     * @return true if this object is destroyed
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Sets whether this object is destroyed.
     * <p>
     * Objects are not removed immediately when destroyed. They are garbage
     * collected at the end of the frame. This tells us whether the object
     * should be garbage collected at the frame end.
     *
     * @param value whether this object is destroyed
     */
    public void setDestroyed(boolean value) {
        destroyed = value;
    }

    /**
     * Returns the radius of this object.
     * <p>
     * All of our objects are circles, to make collision detection easy.
     *
     * @return the radius of this object.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Returns the type of this object.
     * <p>
     * We use this instead of runtime-typing for performance reasons.
     *
     * @return the type of this object.
     */
    public abstract ObjectType getType();

    /**
     * Constructs a trivial game object at the given position
     * <p>
     * The created object has no size. That should be set by the subclasses.
     * Any parameters other than position should be derived from the JSON
     * value of constants associated with that object.
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public GameObject(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0.0f, 0.0f);
        radius = 0.0f;
        destroyed = false;
        transform = new Affine2();
    }

    /**
     * Updates the state of this object.
     * <p>
     * This method is only intended to update values that change local state
     * in well-defined ways, like position or a cooldown value. It does not
     * handle collisions. It is not intended to interact with other objects in any way at all.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(float delta) {
        position.add(velocity);
    }

    /**
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        SpriteBatch.computeTransform(transform, origin.x, origin.y, position.x, position.y, 0, 1.0f, 1.0f);

        batch.setColor(Color.WHITE);
        batch.draw(animator, transform);
    }

}
