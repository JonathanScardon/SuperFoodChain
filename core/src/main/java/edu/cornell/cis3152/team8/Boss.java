package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

import java.util.HashMap;
import java.util.Map;

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
    private float animationSpeed;
    private static float deathAnimationSpeed;

    // local properties
    /**
     * Map of animation name to the sprite sheet associated with it
     */
    private final Map<String, SpriteSheet> animationMap;
    /**
     * Scale of the sprite
     */
    protected Vector2 spriteScale;
    /**
     * Whether the sprite is flipped in each direction
     */
    protected boolean flipVertical;
    protected boolean flipHorizontal;
    /**
     * List of warn patterns used by this boss
     */
    protected Array<BossWarnPattern> warnPatterns;
    /**
     * How far forward this boss can move in a single turn
     */
    protected float moveSpeed;
    /**
     * Whether the boss was damaged this frame
     */
    private boolean damage;
    private boolean dead;

    protected float health;
    private final float startHealth;

    private String state;
    private final String name;
    private boolean remove;

    /**
     * Defines the constants for this class.
     *
     * @param constants The JSON value with constants
     */
    public static void setConstants(JsonValue constants) {
        SPEED_DAMP = constants.getFloat("speedDamp", 0.75f);
        EPSILON = constants.getFloat("epsilon", 0.01f);
        deathAnimationSpeed = constants.getFloat("deathAnimationSpeed", 0.1f);
    }

    public Boss(float x, float y, float width, float height, int health, String name, World world) {
        super(new CapsuleObstacle(x, y, width, height), true);

        this.health = health;
        startHealth = health;
        this.name = name;
        obstacle.setAngle(0f);
        flipHorizontal = false;
        flipVertical = false;
        damage = false;
        moveSpeed = 0;
        animationMap = new HashMap<>();
        warnPatterns = new Array<>();
        dead = false;
        remove = false;
        animationSpeed = 0.1f;

        spriteScale = new Vector2(0.4f, 0.4f);

        obstacle = getObstacle();
        obstacle.setName("boss");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.DynamicBody);
        obstacle.setPhysicsUnits(GameScene.PHYSICS_UNITS);

        obstacle.setBullet(true);
        obstacle.activatePhysics(world);
        obstacle.setUserData(this);
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.BOSS_CATEGORY;
        filter.maskBits =
            CollisionController.PLAYER_CATEGORY | CollisionController.PROJECTILE_CATEGORY;
        obstacle.setFilterData(filter);

        float size = 4 * GameScene.PHYSICS_UNITS;
        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
    }


    public void update(float delta, int controlCode) {
        if (getObstacle().isActive()) {
            // Determine how we are moving.
            boolean movingLeft = controlCode == InputController.CONTROL_MOVE_LEFT;
            boolean movingRight = controlCode == InputController.CONTROL_MOVE_RIGHT;
            boolean movingUp = controlCode == InputController.CONTROL_MOVE_UP;
            boolean movingDown = controlCode == InputController.CONTROL_MOVE_DOWN;
            boolean movingLeftUp = controlCode == InputController.CONTROL_MOVE_LEFT_UP;
            boolean movingLeftDown = controlCode == InputController.CONTROL_MOVE_LEFT_DOWN;
            boolean movingRightUp = controlCode == InputController.CONTROL_MOVE_RIGHT_UP;
            boolean movingRightDown = controlCode == InputController.CONTROL_MOVE_RIGHT_DOWN;

            // Process movement command.
            Vector2 velocity = obstacle.getLinearVelocity();
            if (movingLeft) {
                velocity.x = -moveSpeed * delta;
                velocity.y = 0;
            } else if (movingRight) {
                velocity.x = moveSpeed * delta;
                velocity.y = 0;
            } else if (movingUp) {
                velocity.y = moveSpeed * delta;
                velocity.x = 0;
            } else if (movingDown) {
                velocity.y = -moveSpeed * delta;
                velocity.x = 0;
            } else if (movingLeftUp) {
                velocity.x = -moveSpeed * delta;
                velocity.y = moveSpeed * delta;
            } else if (movingLeftDown) {
                velocity.x = -moveSpeed * delta;
                velocity.y = -moveSpeed * delta;
            } else if (movingRightUp) {
                velocity.x = moveSpeed * delta;
                velocity.y = moveSpeed * delta;
            } else if (movingRightDown) {
                velocity.x = moveSpeed * delta;
                velocity.y = -moveSpeed * delta;
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
            obstacle.setLinearVelocity(velocity);

            for (BossWarnPattern warn : warnPatterns) {
                if (warn.active) {
                    warn.update(delta);
                }
            }
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }

        if (sprite != null) {
            animeframe += animationSpeed;
            if (animeframe >= sprite.getSize() && getObstacle().isActive()) {
                animeframe -= sprite.getSize();
            }
        }


    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void removeHealth(int dmg) {
        health -= dmg;
    }

    /**
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch, float delta) {
        float scaleX = spriteScale.x * (flipHorizontal ? -1 : 1);
        float scaleY = spriteScale.y * (flipVertical ? -1 : 1);
        SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
            sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * GameScene.PHYSICS_UNITS,
            obstacle.getPosition().y * GameScene.PHYSICS_UNITS, obstacle.getAngle(), scaleX,
            scaleY);

        if (!obstacle.isActive()) { // if destroyed...
            if (!dead) {
                animeframe = 0;
                dead = true;
                setAnimation("death", deathAnimationSpeed);
            }
            if (animeframe < sprite.getSize()) { // and animation is not over
                sprite.setFrame((int) animeframe);
                batch.draw(sprite, transform);// draw dead boss
            } else {
                remove = true;
            }
            batch.setColor(Color.WHITE);
        } else { //else draw as normal
            sprite.setFrame((int) animeframe);
            if (damage) {
                batch.setColor(Color.RED);
            }
            if (!getObstacle().isActive()) {
                batch.setColor(Color.BLACK);
            }
            batch.draw(sprite, transform);
            batch.setColor(Color.WHITE);

            damage = false;
        }
    }

    public void drawWarningIcons(SpriteBatch batch) {
        for (BossWarnPattern warn : warnPatterns) {
            warn.drawIcon(batch);
        }
    }

    public void drawWarningBorders(ShapeRenderer shape) {
        for (BossWarnPattern warn : warnPatterns) {
            warn.drawBorder(shape);
        }
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

    /**
     * Add a sprite sheet to the animation map
     *
     * @param name  the name of the animation
     * @param sheet the sprite sheet for the animation
     */
    public void addAnimation(String name, SpriteSheet sheet) {
        animationMap.put(name, sheet);
    }

    /**
     * Set the current sprite sheet to the animation which corresponds to the name If it cannot be
     * found, it just sets it to the default sprite sheet
     *
     * @param name           the name of the animation we want to use
     * @param animationSpeed the speed of the animation
     */
    public void setAnimation(String name, float animationSpeed) {
        animeframe = 0;
        if (!animationMap.containsKey(name)) {
            // sprite sheet not found, using default
            name = "default";
        }
        if (!animationMap.containsKey("default")) {
            throw new RuntimeException("Boss does not have a default animation");
        }
        this.animationSpeed = animationSpeed;
        this.setSpriteSheet(animationMap.get(name));
    }

    public void setAnimation(String name) {
        setAnimation(name, animationSpeed);
    }

    public void setAnimationSpeed(float speed) {
        animationSpeed = speed;
    }

    public float getStartHealth() {
        return startHealth;
    }

    public String getName() {
        return name;
    }

    public boolean shouldRemove() {
        return remove;
    }

    public void setAnimationFrame(float frame) {
        animeframe = frame;
    }
}
