package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.graphics.SpriteBatch;

public abstract class Boss extends GameObject {
    /**
     * How far forward this boss can move in a single turn
     */
    private static float MOVE_SPEED = 3f;
    /**
     * The damping factor for deceleration
     */
    private static float SPEED_DAMP = 0.75f;
    /**
     * How long the boss must wait until it can attack again
     */
    private static float COOLDOWN = 60f;
    /**
     * An epsilon for float comparison
     */
    private static float EPSILON = 0.01f;

    /**
     * Current amount of time until next attack
     */
    private float attackCool;

    protected float angle; // angle of the sprite TEMPORARY

    public enum BossType {
        MOUSE,
        CHEF,
        CHOPSTICKS
    }

    protected float health;

    public Boss(float x, float y) {
        super(x, y);
    }

    // accessors
    @Override
    public ObjectType getType() {
        return ObjectType.BOSS;
    }

    public void update(int controlCode) {
        // Determine how we are moving.
        boolean movingLeft = (controlCode & InputController.CONTROL_MOVE_LEFT) != 0;
        boolean movingRight = (controlCode & InputController.CONTROL_MOVE_RIGHT) != 0;
        boolean movingUp = (controlCode & InputController.CONTROL_MOVE_UP) != 0;
        boolean movingDown = (controlCode & InputController.CONTROL_MOVE_DOWN) != 0;

        // Process movement command.
        if (movingLeft) {
            velocity.x = -MOVE_SPEED;
            velocity.y = 0;
            angle = 180f;
        } else if (movingRight) {
            velocity.x = MOVE_SPEED;
            velocity.y = 0;
            angle = 0f;
        } else if (movingUp) {
            velocity.y = MOVE_SPEED;
            velocity.x = 0;
            angle = 90f;
        } else if (movingDown) {
            velocity.y = -MOVE_SPEED;
            velocity.x = 0;
            angle = 270f;
        } else {
            // NOT MOVING, SO SLOW DOWN
            velocity.x *= SPEED_DAMP;
            velocity.y *= SPEED_DAMP;
            if (Math.abs(velocity.x) < EPSILON) {
                velocity.x = 0.0f;
            }
            if (Math.abs(velocity.y) < EPSILON) {
                velocity.y = 0.0f;
            }
        }

        position.add(velocity);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public abstract BossType getBossType();

    /**
     * Resets the cooldown of the boss attack
     *
     * If flag is true, the weapon will cool down by one animation frame.
     * Otherwise, it will reset to its maximum cooldown.
     *
     * @param flag whether to cooldown or reset
     */
    public void coolDown(boolean flag) {
        if (flag && attackCool > 0) {
            attackCool--;
        } else if (!flag) {
            attackCool = COOLDOWN;
        }
    }

    public boolean canAttack() {
        return attackCool <= 0;
    }
}
