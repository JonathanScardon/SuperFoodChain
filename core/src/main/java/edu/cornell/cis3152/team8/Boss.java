package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public class Boss extends ObstacleSprite {
    // static constants
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

    // local properties
    /**
     * Current angle of the sprite
     */
    protected float angle;
    /**
     * The warn pattern that the boss is currently drawing
     */
    protected BossWarnPattern curWarn;
    /**
     * How far forward this boss can move in a single turn
     */
    protected float moveSpeed;
    /**
     * Whether the boss was damaged this frame
     */
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
        SPEED_DAMP = constants.getFloat("speedDamp", 0.75f);
        EPSILON = constants.getFloat("epsilon", 0.01f);
        animationSpeed = constants.getFloat("animationSpeed", 0.1f);
    }


    private static final float PHYSICS_UNITS = 64f;


    public Boss(float x, float y, int health, World world) {
        super(new CapsuleObstacle(x, y, 1.5f, 1.5f), true);
        this.health = health;
        angle = 90f;
        damage = false;
        moveSpeed = 0;

        obstacle = getObstacle();
        obstacle.setName("boss");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.DynamicBody);
        obstacle.setPhysicsUnits(PHYSICS_UNITS);

        obstacle.setBullet(true);
        obstacle.activatePhysics(world);
        obstacle.setUserData(this);
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.BOSS_CATEGORY;
        filter.maskBits = CollisionController.PLAYER_CATEGORY | CollisionController.PROJECTILE_CATEGORY;
        obstacle.setFilterData(filter);

        float size = 4 * PHYSICS_UNITS;
        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
    }


    public void update(float delta, int controlCode) {
        // Determine how we are moving.
        boolean movingLeft = (controlCode & InputController.CONTROL_MOVE_LEFT) != 0;
        boolean movingRight = (controlCode & InputController.CONTROL_MOVE_RIGHT) != 0;
        boolean movingUp = (controlCode & InputController.CONTROL_MOVE_UP) != 0;
        boolean movingDown = (controlCode & InputController.CONTROL_MOVE_DOWN) != 0;

        // Process movement command.
        Vector2 velocity = obstacle.getLinearVelocity();
        if (movingLeft) {
            velocity.x = -moveSpeed;
            velocity.y = 0;
            //angle = 180f;
        } else if (movingRight) {
            velocity.x = moveSpeed;
            velocity.y = 0;
            //angle = 0f;
        } else if (movingUp) {
            velocity.y = moveSpeed;
            velocity.x = 0;
            //angle = 90f;
        } else if (movingDown) {
            velocity.y = -moveSpeed;
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

        if ((movingDown || movingLeft || movingRight || movingUp) && sprite != null) {
            animeframe += animationSpeed;
            if (animeframe >= sprite.getSize()) {
                animeframe -= sprite.getSize();
            }
        }

        obstacle.setLinearVelocity(velocity);
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

    public void removeHealth(int dmg) { health -= dmg; }

    /**
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch, float delta) {
        SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f, sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * PHYSICS_UNITS, obstacle.getPosition().y * PHYSICS_UNITS, -90 + angle, 0.4f, 0.4f);

        sprite.setFrame((int) animeframe);
        if (damage) {
            batch.setColor(Color.RED);
        }
        batch.draw(sprite, transform);
        batch.setColor(Color.WHITE);

        if (curWarn != null) {
            curWarn.draw(batch, delta);
        }
        damage = false;

    }

    public void setDamage(boolean hit) {
        damage = hit;
    }

    public String getState() {
        return state;
    }

    public void setState(String s) {
        state = s;
    }

    public void setAnimationSpeed(float speed) {
        animationSpeed = speed;
    }
}
