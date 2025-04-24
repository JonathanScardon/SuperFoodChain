package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import edu.cornell.gdiac.physics2.BoxObstacle;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

import static edu.cornell.cis3152.team8.InputController.CONTROL_MOVE_LEFT;
import static edu.cornell.cis3152.team8.InputController.CONTROL_NO_ACTION;

public abstract class Companion extends ObstacleSprite {

    public enum CompanionType {
        AVOCADO,
        GARLIC,
        STRAWBERRY,
        CORN,
        DURIAN,
        TANGERINE,
        BLUE_RASPBERRY,
        PINEAPPLE,
    }

    /**
     * How long the death sprite persists on screen in seconds
     */
    private float deathExpirationTimer = 3.0f;

    /**
     * The type of Companion
     */
    private CompanionType type;

    /**
     * Cost of companion
     */
    private int cost;

    /**
     * How long companion must wait until use ability again
     */
    private int cooldown;

    /**
     * The number of frames until use ability again
     */
    private float abilityCool;
    private boolean collected;
    private Vector2 prevVelocity;

    /**
     * The direction the companion is currently moving in
     */
    private int direction;

    private float prevX;

    private SpriteSheet deadCompanion;
    private float prevY;
    protected float animationSpeed;
    protected float animationFrame;
    protected Texture glow;
    protected boolean highlight;
    private boolean dead;
    private int id;
    private float size;

    private boolean remove;
    private boolean moving;

    private static float MOVE_SPEED = 150;

    private static float SPEED_BOOST = 0;

    private static final float units = 64f;

    public Companion(float x, float y, int id, World world) {
        super(new CapsuleObstacle(x / units, y / units, 0.8f, 0.8f), true);

        cost = 0;
        cooldown = 5;
        abilityCool = 0;
        collected = false;
        prevVelocity = new Vector2();
        direction = InputController.CONTROL_NO_ACTION;
        highlight = false;
        animationFrame = 1;
        sprite.setFrame((int) animationFrame);
        this.id = id;
        remove = false;
        moving = false;
        dead = false;

        Texture texture = new Texture("images/Companion_Death_Universal.png");
        deadCompanion = new SpriteSheet(texture, 1, 6);

        // change?
        obstacle = getObstacle();
        obstacle.setName("companion");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.DynamicBody);
        obstacle.setPhysicsUnits(units);

        obstacle.activatePhysics(world);
        obstacle.setUserData(this);

        // prevents physics?
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.COMPANION_CATEGORY;
        filter.maskBits = CollisionController.PLAYER_CATEGORY;
        obstacle.setFilterData(filter);

        // TODO: was this different?
        size = 0.4f * units;
        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
    }

    // accessors

    /**
     * Get type of Companion
     */
    public CompanionType getCompanionType() {
        return type;
    }

    ;

    /**
     * Set type of Companion
     */
    public void setCompanionType(CompanionType type) {
        this.type = type;
    }

    /**
     * Get cost of Companion
     */
    public int getCost() {
        return cost;
    }

    ;

    /**
     * Set cost of Companion
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Get cooldown of ability
     */
    public int getCooldown() {
        return cooldown;
    }

    ;

    /**
     * Set cooldown of ability
     */
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Get active cooldown of ability
     */
    public float getActiveCooldown() {
        return abilityCool;
    }

    /**
     * Set active cooldown of ability
     */
    public void setActiveCooldown(float cd) {
        abilityCool = cd;
    }

    /**
     * Returns whether companion can use ability
     */
    public boolean canUse() {
        return abilityCool <= 0;
    }

    /**
     * Companion uses ability
     */
    public void useAbility(GameState state) {

        // individual abilities depending on type --> overrided by different types
    }

    ;

    /**
     * Resets the cool down of the companion ability
     * <p>
     * If flag is true, the weapon will cool down by one animation frame. Otherwise it will reset to
     * its maximum cooldown.
     *
     * @param flag whether to cooldown or reset
     */
    public void coolDown(boolean flag, float delta) {
        if (flag && abilityCool > 0) {
            abilityCool -= delta;
        } else if (!flag) {
            abilityCool = cooldown;
        }
    }

    public void follow(long delta) {
        if (delta % 120 == 0) {
            prevVelocity = obstacle.getLinearVelocity();
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(Boolean b) {
        collected = b;
    }

    public Vector2 getPrevVelocity() {
        return prevVelocity;
    }

    /**
     * Updates the movement of a companion in the chain
     *
     * @param controlCode new direction of the companion
     */
    public void update(float delta, int controlCode) {
        if (!obstacle.isActive()) {
            if (sprite != null && dead) {
                animationFrame += animationSpeed;
            }
            return;
        }
        highlight = false;

        // Determine how we are moving.
        boolean movingLeft = controlCode == 1;
        boolean movingRight = controlCode == 2;
        boolean movingUp = controlCode == 4;
        boolean movingDown = controlCode == 8;

        // Process movement command.

        float speed = (MOVE_SPEED + SPEED_BOOST);
        Vector2 velocity = obstacle.getLinearVelocity();
        if (movingLeft) {
            this.direction = InputController.CONTROL_MOVE_LEFT;
            velocity.x = -speed * delta;
            velocity.y = 0;
            moving = true;
        } else if (movingRight) {
            this.direction = InputController.CONTROL_MOVE_RIGHT;
            velocity.x = speed * delta;
            velocity.y = 0;
            moving = true;
        } else if (movingUp) {
            this.direction = InputController.CONTROL_MOVE_UP;
            velocity.y = speed * delta;
            velocity.x = 0;
            moving = true;
        } else if (movingDown) {
            this.direction = InputController.CONTROL_MOVE_DOWN;
            velocity.y = -speed * delta;
            velocity.x = 0;
            moving = true;
        } else {
            this.direction = CONTROL_NO_ACTION;
            velocity.x = 0;
            velocity.y = 0;
            moving = false;
        }
        obstacle.setLinearVelocity(velocity);
    }

    public void draw(SpriteBatch batch) {
        if (!obstacle.isActive()) { // if destroyed...
            if (!dead) {
                animationFrame = 0;
                animationSpeed = 0.1f;
                dead = true;
                setSpriteSheet(deadCompanion);
            }
            if (animationFrame < sprite.getSize()) { // and animation is not over
                sprite.setFrame((int) animationFrame);
                batch.draw(sprite, transform); // draw dead Minion
            } else {
                remove = true;
            }
        } else {
            if (collected) {
                if (moving) {
                    sprite.setFrame((int) animationFrame);
                }
            } else {
                if (highlight) {
                    sprite.setFrame(0);
                } else {
                    sprite.setFrame(1);
                }
            }
            batch.setColor(Color.WHITE);
        }

        SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
            sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * units,
            obstacle.getPosition().y * units + 16f, 0.0f, size / units, size / units);

        batch.draw(sprite, transform);
        batch.setColor(Color.WHITE);
        setGlow(false);
    }

    /**
     * Returns player's speed (including boost)
     */
    public static float getSpeed(){
        return MOVE_SPEED + SPEED_BOOST;
    }

    /**
     * @param boost increase to SPEED_BOOST
     */
    public static void increaseBoost(float boost){
        SPEED_BOOST += boost;
    }

    /**
     * @param boost decrease to SPEED_BOOST
     */
    public static void decreaseBoost(float boost){
        SPEED_BOOST -= boost;
    }


    /**
     * Resets the speed boost to 0
     */
    public static void resetBoost(){
        SPEED_BOOST = 0;
    }

    /**
     * @return control code of companion's current movement
     */
    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public float getPrevX() {
        return this.prevX;
    }

    public float getPrevY() {
        return this.prevY;
    }

    public void setGlow(Boolean g) {
        highlight = g;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean shouldRemove() {
        return remove;
    }

    public SpriteSheet getAnimator() {
        return sprite;
    }

    public void decreaseDeathExpirationTimer(float delta) {
        deathExpirationTimer -= delta;
    }

    public boolean getTrash() {
        return deathExpirationTimer < 0.0;
    }

}
