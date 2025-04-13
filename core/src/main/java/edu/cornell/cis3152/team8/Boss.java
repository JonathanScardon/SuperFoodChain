package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;

public abstract class Boss extends GameObject {
    /**
     * How far forward this boss can move in a single turn
     */
    private static float MOVE_SPEED;
    /**
     * The damping factor for deceleration
     */
    private static float SPEED_DAMP;
    /**
     * An epsilon for float comparison
     */
    private static float EPSILON;
    /**
     * Current animation frame for this boss
     */
    private float animeframe;
    /**
     * How fast we change frames
     */
    private static float animationSpeed;
    /**
     * Current angle of the sprite
     */
    protected float angle;
    /**
     * The warn pattern that the boss is currently drawing
     */
    protected BossWarnPattern curWarn;

    private boolean damage;

    public enum BossType {
        MOUSE,
        CHEF,
        CHOPSTICKS
    }

    protected float health;

    private String state;

    /**
     * Defines the constants for this class.
     *
     * @param constants The JSON value with constants
     */
    public static void setConstants(JsonValue constants) {
        MOVE_SPEED = constants.getFloat("moveSpeed", 10);
        SPEED_DAMP = constants.getFloat("speedDamp", 0.75f);
        EPSILON = constants.getFloat("epsilon", 0.01f);
        animationSpeed = constants.getFloat("animationSpeed", 0.1f);
    }

    public Boss(float x, float y) {
        super(x, y);
        //warnSprites = new Array<>();
        health = 30;// TODO: move this to constants?
        angle = 90f;
        damage = false;
    }

    // accessors
    @Override
    public ObjectType getType() {
        return ObjectType.BOSS;
    }

    public void update(float delta, int controlCode) {
        // Determine how we are moving.
        boolean movingLeft = (controlCode & InputController.CONTROL_MOVE_LEFT) != 0;
        boolean movingRight = (controlCode & InputController.CONTROL_MOVE_RIGHT) != 0;
        boolean movingUp = (controlCode & InputController.CONTROL_MOVE_UP) != 0;
        boolean movingDown = (controlCode & InputController.CONTROL_MOVE_DOWN) != 0;

        // Process movement command.
        if (movingLeft) {
            velocity.x = -MOVE_SPEED;
            velocity.y = 0;
            //angle = 180f;
        } else if (movingRight) {
            velocity.x = MOVE_SPEED;
            velocity.y = 0;
            //angle = 0f;
        } else if (movingUp) {
            velocity.y = MOVE_SPEED;
            velocity.x = 0;
            //angle = 90f;
        } else if (movingDown) {
            velocity.y = -MOVE_SPEED;
            velocity.x = 0;
            //angle = 270f;
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

        if ((movingDown || movingLeft || movingRight || movingUp) && animator != null) {
            animeframe += animationSpeed;
            if (animeframe >= animator.getSize()) {
                animeframe -= animator.getSize();
            }
        }

        position.add(velocity);

        if (curWarn != null) {
            curWarn.update(delta);
        }
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    /**
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    @Override
    public void draw(SpriteBatch batch, float delta) {
        SpriteBatch.computeTransform(transform, origin.x, origin.y, position.x, position.y, -90 + angle, 0.4f, 0.4f);

        animator.setFrame((int) animeframe);
        if (damage){
            batch.setColor(Color.RED);
        }
        //System.out.println(damage);
        batch.draw(animator, transform);
        batch.setColor(Color.WHITE);
        if (curWarn != null) {
            curWarn.draw(batch, delta);
        }
        damage = false;

    }

    public void setDamage(boolean hit) {
        damage = hit;
    }

    public String getState(){
        return state;
    }

    public void setState(String s){
        state = s;
    }
    public void setAnimationSpeed(float speed){
        animationSpeed = speed;
    }
}
