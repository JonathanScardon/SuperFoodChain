package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;

/**
 * Draw a square with a blinking warning icon
 */
public abstract class BossWarnPattern {
    /**
     * Whether this pattern is currently displayed
     */
    public boolean active;

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

    protected static final Color lineColor = new Color(166f / 255f, 1f / 255f, 0f, 1f);

    /**
     * How fast we change frames
     */
    private static float animationSpeed = 0.02f;

    protected static final float PHYSICS_UNITS = 64f;

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

    public void setSpriteSheet(SpriteSheet spriteSheet) {
        sprite = spriteSheet;
    }

    public void update(float delta) {
        if (!active) {
            return;
        }

        animeframe += animationSpeed;
        if (animeframe >= sprite.getSize()) {
            animeframe -= sprite.getSize();
        }
    }

    /**
     * Draws the warning symbol to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void drawIcon(SpriteBatch batch) {
        if (!active) {
            return;
        }

        SpriteBatch.computeTransform(transform, 70.5f, sprite.getRegionHeight() / 2.0f, this.x * PHYSICS_UNITS, this.y * PHYSICS_UNITS, 0, 0.6f, 0.6f);
        sprite.setFrame((int) animeframe);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);
    }

    /**
     * Draws the border using the shape renderer
     */
    public abstract void drawBorder(ShapeRenderer shape);
}
