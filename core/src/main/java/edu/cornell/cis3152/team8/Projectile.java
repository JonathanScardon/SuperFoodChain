package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.gdiac.physics2.BoxObstacle;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.Obstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public abstract class Projectile extends ObstacleSprite {

    /// CONSTANTS (defined by the JSON file)
    // Scale of the image
    private static float imageScale;
    // How fast the animation should be
    private static float animationSpeed;
    // How long the projectile should persist for
    private float maxLife;
    // Speed of the projectile
    protected float speed;
    // Damage of the projectile
    protected int attack;

    /// Attributes (per object)
    // Current animation frame of the projectile
    protected float animeFrame;
    // How much "life" left for projectile to persist on screen
    protected float life;
    // If the projectile dies on collision
    protected boolean collisionDie;
    /**
     * Radius of the object (used for collisions)
     */
    protected float radius;

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
        super(new CapsuleObstacle(x / GameScene.PHYSICS_UNITS, y / GameScene.PHYSICS_UNITS, 0.5f, 0.5f), true);

        // Attributes below are placeholder values since setConstants isn't implemented
        // yet
        radius = 1;
        speed = 750;
        attack = 2;
        maxLife = 2.5f;
        imageScale = 1;
        animationSpeed = 4;
        collisionDie = true;

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

        obstacle.setPhysicsUnits(GameScene.PHYSICS_UNITS);
        obstacle.setBullet(true);

        obstacle.activatePhysics(world);
        obstacle.setUserData(this);
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.PROJECTILE_CATEGORY;
        filter.maskBits = CollisionController.MINION_CATEGORY | CollisionController.BOSS_CATEGORY;
        obstacle.setFilterData(filter);

        float size = radius * GameScene.PHYSICS_UNITS;
        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
    }

    /**
     * Creates a box projectile with the given starting position.
     */
    public Projectile(Obstacle o, World world) {
        // Parent constructor
        super(o, true);

        // Attributes below are placeholder values since setConstants isn't implemented
        // yet
        radius = 1;
        speed = 750;
        attack = 2;
        maxLife = 2.5f;
        imageScale = 1;
        animationSpeed = 4;
        collisionDie = false;

        // Update the velocities to be the associated x-velocity and y-velocity
        obstacle.setLinearVelocity(new Vector2(0, 0));

        // Set initial animation frame to 0
        animeFrame = 0.0f;
        // Set current life to max allowable at initialization
        life = maxLife;

        obstacle = getObstacle();
        obstacle.setName("projectile");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.KinematicBody);

        obstacle.setPhysicsUnits(GameScene.PHYSICS_UNITS);
        obstacle.setBullet(false);

        obstacle.activatePhysics(world);
        obstacle.setUserData(this);
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.PROJECTILE_CATEGORY;
        filter.maskBits = CollisionController.MINION_CATEGORY | CollisionController.BOSS_CATEGORY;
        obstacle.setFilterData(filter);

        float size = radius * GameScene.PHYSICS_UNITS;
        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
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
    public float getLife() {
        return life;
    }

    /**
     * Sets the life of the projectile
     *
     * @param life the life to set it to
     */
    public void setLife(float life) {
        this.life = life;
    }

    /**
     * Returns true if the projectile dies upon collision
     *
     * @return if the projectile dies upon collision
     */
    public boolean getCollisionDie() {
        return collisionDie;
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
     * Sets the max life of the projectile
     */
    public void setMaxLife(float max) {
        maxLife = max;
    }

    /**
     * Returns the max life of the projectile
     *
     * @return attack damage
     */
    public float getMaxLife() {
        return maxLife;
    }

    /**
     * Resets the projectile and returns to pool
     */
    public void reset() {
        life = maxLife;
        animeFrame = 0.0f;
        // Reset physics state without destroying
        getObstacle().getBody().setLinearVelocity(0, 0);
        getObstacle().getBody().setTransform(0, 0, 0);
        getObstacle().getBody().setActive(false);
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
            animeFrame += animationSpeed * delta;
            // If reaching end of animation frame, wrap around to beginning
            if (animeFrame >= sprite.getSize()) {
                animeFrame -= sprite.getSize();
            }
        }
        // Decrement projectile life to make progress towards it being on screen/not
        // destroyed
        life -= delta;
    }

    /**
     * Draws this projectile to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        sprite.setFrame((int) animeFrame);
        // need to override because we pass in imageScale instead of 1.0f - need to put in size/radius instead?
        SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
            sprite.getRegionHeight() / 2.0f,
            obstacle.getX() * GameScene.PHYSICS_UNITS, obstacle.getY() * GameScene.PHYSICS_UNITS, 0.0f, radius, radius);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);
    }
}
