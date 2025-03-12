package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class Projectile {
    // Factor to multiply with velocity for collisions
    private static float SHOT_SPEED;
    // number of frames before Projectile is deactivated
    private static int MAX_AGE;
    // Color to tint the Projectile
    private Color tint;
    // Projectile position
    private Vector2 position;
    // Projectile velocity
    private Vector2 velocity;
    // Number of animation frames left to live
    private int life;
    // Marks whether the Projectile is dead but not deallocated
    private boolean dirty;
    // ID of the companion that created the Projectile
    private int companion;

    public static void setConstants (JsonValue constants) {

    }
    public Projectile() {
        tint = new Color();
        position = new Vector2();
        velocity = new Vector2();

        // set default/undefined values for now
        life = -1;
        companion = -1;
        dirty = false;
    }

    public void set(int companion, float x, float y, float vx, float vy, Color color) {
        // TODO
        return;
    }

    // Return the position x-value
    public float getX() {
        return position.x;
    }

    // Set the position x-value
    public void setX(float x) {
        position.x = x;
    }

    // Return the position y-value
    public float getY() {
        return position.y;
    }

    // Set the position y-value
    public void setY(float y) {
        position.y = y;
    }

    // Return the position of the projectile
    public Vector2 getPosition() {
        return position;
    }

    // Return the velocity x-value
    public float getVX() {
        return velocity.x;
    }

    // Set the velocity x-value
    public void setVX(float vx) {
        velocity.x = vx;
    }

    // Return the velocity y-value
    public float getVY() {
        return velocity.y;
    }

    // Set the velocity y-value
    public void setVY(float vy) {
        velocity.y = vy;
    }

    // Return the velocity of the projectile
    public Vector2 getVelocity() {
        return velocity;
    }

    // Return the tint/color
    public Color getColor() {
        return tint;
    }

    // Return the companion associated with the projectile
    public int getCompanion() {
        return companion;
    }

    // Return whether or not the projectile is alive and persisting
    public boolean isAlive() {
        return life > 0;
    }

    // Set projectile's life to 0 to indicate dead and set dirty to true to indicate unallocated
    public void destroy() {
        life = 0;
        dirty = true;
    }

    // Decrement life value as projectile persists
    public void age() {
        life --;
    }

    // Return whether or not a projectile is dead but not unallocated yet
    public boolean isDirty() {
        return dirty;
    }
}
