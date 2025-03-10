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

    public float getX() {
        // TODO
        return 0.0f;
    }

    public void setX(float x) {
        // TODO
        return;
    }

    public float getY() {
        // TODO
        return 0.0f;
    }

    public void setY(float y) {
        // TODO
        return;
    }

    public Vector2 getPosition() {
        // TODO
        return null;
    }

    public float getVX() {

    }

    public void setVX(float vx) {

    }

    public float getVY() {

    }

    public void setVY(float vy) {

    }

    public Vector2 getVelocity() {

    }

    public Color getColor() {

    }

    public int getCompanion() {

    }

    public boolean isAlive() {

    }

    public void destroy() {

    }

    public void age() {

    }

    public boolean isDirty() {

    }
}
