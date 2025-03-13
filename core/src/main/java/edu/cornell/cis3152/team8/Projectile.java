package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class Projectile extends GameObject {
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

    public ObjectType getType() {
        return ObjectType.PROJECTILE;
    }

   
