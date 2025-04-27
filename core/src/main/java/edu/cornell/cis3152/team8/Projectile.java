package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public abstract class Projectile extends ObstacleSprite {
    /// CONSTANTS (defined by the JSON file)
    // Scale of the image
    private static float imageScale;
    // How fast the animation should be
    private static float animationSpeed;
    // How long the projectile should persist for
    protected static int maxLife;
    // Speed of the projectile
    protected float speed;
    // Damage of the projectile
    protected int attack;

    /// Attributes (per object)
    // Current animation frame of the projectile
    private float animeFrame;
    // How much "life" left for projectile to persist on screen
    protected int life;
    /**
     * Radius of the object (used for collisions)
     */
    protected float radius;
    private static final float units = 64f;

    // public static void setConstants (JsonValue constants) {
    // imageScale = constants.getFloat("imageScale");
    // animationSpeed = constants.getFloat("animationSpeed");
    // maxLife = constants.getInt("projectile-maxLife");
    // speed = constants.getInt("projectile-speed");
    // }

    /**
     * Creates a projectile with the given starting position.
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Projectile(float x, float y, float vx, float vy, World world) {
        // Parent constructor
        super(new CapsuleObstacle(x/units, y/units, 0.5f, 0.5f), true);

        // Attributes below are placeholder values since setConstants isn't implemented
        // yet
        radius = 1;
        speed = 750;
        attack = 1;
        maxLife = 150;
        imageScale = 1;
        animationSpeed = 4;

        // Update the velocities to be the associated x-velocity and y-velocity
        obstacle.setLinearVelocity(new Vector2(vx, vy));

        // Set initial animation frame to 0
        animeFrame = 0.0f;
        // Set current life to max allowable at initialization
        life = maxLife;


        obstacle = getObstacle();
        obstacle.setName("projectile");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.KinematicBody);

        obstacle.setPhysicsUnits(units);
        obstacle.setBullet(true);

        obstacle.activatePhysics(world);
        obstacle.setUserData(this);
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.PROJECTILE_CATEGORY;
        filter.maskBits = CollisionController.MINION_CATEGORY | CollisionController.BOSS_CATEGORY;
        obstacle.setFilterData(filter);

        float size = radius * units;
        mesh.set(-size/2.0f,-size/2.0f,size,size);
    }

    /**
     * Sets the sprite sheet for the game object.
     *
     * @param sheet The sprite sheet
     */
    @Override
    public void setSpriteSheet(SpriteSheet sheet) {
        super.setSpriteSheet(sheet);
        radius *= imageScale; // this will take care of updating the Projectile's radius
    }

    /**
     * Returns the current life value of the projectile
     *
     * @return life value
     */
    public int getLife() {
        return life;
    }

    /**
     * Sets the attack damage of the projectile
     */
    public void setAttack(int a) {
        attack = a;
    }

    /**
     * Returns the attack damage of the projectile
     *
     * @return attack damage
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Resets the projectile and returns to pool
     */
    public void reset() {
        life = maxLife;
        animeFrame = 0.0f;
        getObstacle().setActive(false);
        getObstacle().getBody().setActive(false);
        // Reset physics state without destroying
        getObstacle().getBody().setLinearVelocity(0, 0);
        getObstacle().getBody().setTransform(0, 0, 0);
        getObstacle().markRemoved(true);
    }

    /**
     * Updates the animation frame and velocity of the projectile.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(float delta) {
//        obstacle.setLinearVelocity(new Vector2(obstacle.getVX() * speed, obstacle.getVY() * speed));

        // Increase animation frame
        if (sprite != null) {
            // Increment animation frame
            animeFrame += animationSpeed;
            // If reaching end of animation frame, wrap around to beginning
            if (animeFrame >= sprite.getSize()) {
                animeFrame -= sprite.getSize();
            }
        }
        // Decrement projectile life to make progress towards it being on screen/not
        // destroyed
        life--;
    }

    /**
     * Draws this projectile to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        sprite.setFrame((int) animeFrame);
        // need to override because we pass in imageScale instead of 1.0f - need to put in size/radius instead?
        SpriteBatch.computeTransform(transform, sprite.getRegionWidth()/2.0f, sprite.getRegionHeight()/2.0f,
                obstacle.getX() * units, obstacle.getY() * units, 0.0f, radius, radius);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);
    }
}
