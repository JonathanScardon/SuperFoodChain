package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public abstract class Boss extends ObstacleSprite {
    /**
     * How far forward this boss can move in a single turn
     */
    private static float MOVE_SPEED = 10f;
    /**
     * The damping factor for deceleration
     */
    private static float SPEED_DAMP = 0.75f;
    /**
     * How long the boss must wait until it can attack again
     */
    private static float IDLE_DURATION = 5f;
    /**
     * An epsilon for float comparison
     */
    private static float EPSILON = 0.01f;

    /**
     * Current amount of time until next set of attacks
     */
    private float idleTime;
    protected float angle; // angle of the sprite TEMPORARY

    /**
     * Current animation frame for this ship
     */
    private float animeframe;
    /**
     * How fast we change frames
     */
    private static float animationSpeed = 0.10f;

    /**
     * The sprite sheets that correspond to each warning pattern
     */
    protected Array<SpriteSheet> warnSprites;
    /**
     * The warning patterns that correspond to the attack patterns
     */
    protected Array<BossController.WarnPattern> warnPatterns;

    public enum BossType {
        MOUSE,
        CHEF,
        CHOPSTICKS
    }

    protected float health;

    private static final float units = 64f;


    public Boss(float x, float y, World world) {
        super(new CapsuleObstacle(x/units, y/units, 1.5f, 1.5f), true);
        warnPatterns = new Array<>();
        warnSprites = new Array<>();
        health = 10;

        obstacle = getObstacle();
        obstacle.setName("boss");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.DynamicBody);
        obstacle.setPhysicsUnits(units);

        obstacle.setBullet(true);
        obstacle.activatePhysics(world);
        obstacle.setUserData(this);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.BOSS_CATEGORY;
        filter.maskBits = CollisionController.PLAYER_CATEGORY | CollisionController.PROJECTILE_CATEGORY;
        obstacle.setFilterData(filter);

        float size = 4 * units;
        mesh.set(-size/2.0f,-size/2.0f,size,size);
    }

    // accessors
    public void update(int controlCode) {
        // Determine how we are moving.
        boolean movingLeft = (controlCode & InputController.CONTROL_MOVE_LEFT) != 0;
        boolean movingRight = (controlCode & InputController.CONTROL_MOVE_RIGHT) != 0;
        boolean movingUp = (controlCode & InputController.CONTROL_MOVE_UP) != 0;
        boolean movingDown = (controlCode & InputController.CONTROL_MOVE_DOWN) != 0;

        // Process movement command.
        Vector2 velocity = obstacle.getLinearVelocity();
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

        if ((movingDown || movingLeft || movingRight || movingUp) && sprite != null) {
            animeframe += animationSpeed;
            if (animeframe >= sprite.getSize()) {
                animeframe -= sprite.getSize();
            }
        }

        obstacle.setLinearVelocity(velocity);
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
     * <p>
     * If flag is true, the weapon will cool down by one animation frame.
     * Otherwise, it will reset to its maximum cooldown.
     *
     * @param flag whether to cooldown or reset
     */
    public void attackCooldown(boolean flag) {
        if (flag && idleTime > 0) {
            idleTime--;
        } else if (!flag) {
            idleTime = IDLE_DURATION;
        }
    }

    public boolean canAttack() {
        return idleTime <= 0;
    }

    /**
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        SpriteBatch.computeTransform(transform, sprite.getRegionWidth()/2.0f, sprite.getRegionHeight()/2.0f, obstacle.getPosition().x * units, obstacle.getPosition().y * units, -(-90 + angle), 4f, 4f);

        sprite.setFrame((int) animeframe);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);

        for (BossController.WarnPattern wp : warnPatterns) {
            if (wp.active) {
                wp.draw(batch);
            }
        }
    }
}
