package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;

/**
 * Draw a square with a blinking warning icon
 */
public abstract class BossWarnPattern {
    /**
     * Whether this pattern is currently displayed
     */
    private boolean active;

    /**
     * Current animation frame for this warn pattern
     */
    private float animeframe;

    /**
     * Coordinates and size of the warn pattern
     */
    protected float x, y, angle;

    /**
     * The warning sprite
     */
    private SpriteSheet sprite;

    protected Affine2 transform;

    protected static final Color LINE_COLOR = new Color(166f / 255f, 1f / 255f, 0f, 1f);

    /**
     * The number of full rotations we want the warn pattern to display during the warn time
     */
    private static final int CYCLES = 3;

    private int curCycle = 0;

    /**
     * How fast we change frames
     */
    private float animationSpeed = 1f;

    public BossWarnPattern(float x, float y) {
        this.active = false;
        this.x = x;
        this.y = y;
        this.angle = 0f;
        this.transform = new Affine2();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setAngle(float a) {
        angle = a % 360;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            animeframe = 0;
            curCycle = 0;
        }
    }

    public boolean isActive() {return active;}

    public void setSpriteSheet(SpriteSheet spriteSheet) {
        sprite = spriteSheet;
    }

    public void setAnimationSpeed(float speed) {animationSpeed = speed;}

    /**
     * Sets the animation speed based on completing CYCLES cycles in duration seconds
     * @param duration the warning duration
     */
    public void setAnimationSpeedWithDuration(float duration) {
        animationSpeed = (sprite.getSize() * CYCLES) / duration;

        // if the animation speed is too high, don't animate at all
        if (animationSpeed >= 10 || duration == 0) {
            animationSpeed = 0;
        }
    }

    public void update(float delta) {
        if (!this.isActive()) {
            return;
        }

        animeframe += animationSpeed * delta;
        if (animeframe >= sprite.getSize()) {
            animeframe -= sprite.getSize();
            curCycle++;

            if (curCycle >= CYCLES) {
                animeframe = sprite.getSize() - 1e-3f; // stop on the last frame
                active = false;
            }
        }
    }

    /**
     * Draws the warning symbol to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void drawIcon(SpriteBatch batch) {
        if (!this.isActive()) {
            return;
        }

        SpriteBatch.computeTransform(transform, 70.5f, sprite.getRegionHeight() / 2.0f, this.x * GameScene.PHYSICS_UNITS, this.y * GameScene.PHYSICS_UNITS, 0, 0.6f, 0.6f);
        sprite.setFrame((int) animeframe);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);
    }

    /**
     * Draws the border using the shape renderer
     */
    public abstract void drawBorder(ShapeRenderer shape);
}
