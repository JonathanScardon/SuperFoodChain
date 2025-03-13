package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;

public abstract class Projectile extends GameObject {
    /// CONSTANTS (defined by the JSON file)
    // Scale of the image
    private static float imageScale;
    // How fast the animation should be
    private static float animationSpeed;
    // x component of the projectile velocity
    private static float vx;
    // y component of the projectile velocity
    private static float vy;
    // How long the projectile should persist for
    private static int maxLife;

    /// Attributes (per object)
    // Current animation frame of the projectile
    private float animeFrame;
    // How much "life" left for projectile to persist on screen
    private int life;

    public ObjectType getType() {
        return ObjectType.PROJECTILE;
    }

    public static void setConstants (JsonValue constants) {
        imageScale = constants.getFloat("imageScale");
        animationSpeed = constants.getFloat("animationSpeed");
        vx = constants.getFloat("projectile-velocity", 0);
        vy = constants.getFloat("projectile-velocity", 1);
        maxLife = constants.getInt("projectile-maxLife");
    }

    /**
     * Creates a projectile with the given starting position.
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Projectile(float x, float y) {
        // Parent constructor
        super(x, y);

        // Update the velocities to be the associated x-velocity and y-velocity
        velocity.x = vx;
        velocity.y = vy;

        // Set initial animation frame to 0
        animeFrame = 0.0f;
        // Set current life to max allowable at initialization
        life = maxLife;
    }

    /**
     * Sets the sprite sheet for the game object.
     *
     * @param sheet The sprite sheet
     */
    @Override
    public void setSpriteSheet(SpriteSheet sheet) {
        super.setSpriteSheet( sheet );
        radius *= imageScale; // this will take care of updating the Projectile's radius
    }

    /**
     * Updates the animation frame and velocity of the projectile.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(float delta) {
        // call the super update function since we still want to update position values in terms of projectile velocity
        super.update(delta);

        // Increase animation frame
        if (animator != null) {
            // Increment animation frame
            animeFrame += animationSpeed;
            // If reaching end of animation frame, wrap around to beginning
            if (animeFrame >= animator.getSize()) {
                animeFrame -= animator.getSize();
            }
        }
        // Decrement projectile life to make progress towards it being on screen/not destroyed
        life--;
    }

    /**
     * Draws this projectile to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        animator.setFrame((int)animeFrame);
        // need to override because we pass in imageScale instead of 1.0f in computeTransform
        SpriteBatch.computeTransform(transform, origin.x, origin.y,
            position.x, position.y, 0.0f, imageScale, imageScale);
        batch.setColor( Color.WHITE );
        batch.draw( animator, transform );
    }
}


