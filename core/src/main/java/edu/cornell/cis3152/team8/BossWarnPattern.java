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
public class BossWarnPattern {
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
    private float x, y, w, h, angle;

    /**
     * The warning sprite
     */
    private SpriteSheet sprite;

    protected Affine2 transform;

    private Color lineColor = new Color(166f / 255f, 1f / 255f, 0f, 1f);

    /**
     * How fast we change frames
     */
    private static float animationSpeed = 0.05f;

    private static final float PHYSICS_UNITS = 64f;

    public BossWarnPattern(float x, float y, float w, float h) {
        this.active = false;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
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

        SpriteBatch.computeTransform(transform, 70.5f, sprite.getRegionHeight() / 2.0f, this.x * PHYSICS_UNITS, this.y * PHYSICS_UNITS, angle, 0.6f, 0.6f);
        sprite.setFrame((int) animeframe);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);
    }

    /**
     * Draws the border using the shape renderer
     */
    public void drawBorder(ShapeRenderer shape) {
        if (!active) {
            return;
        }
        drawRect(shape, x * PHYSICS_UNITS, y * PHYSICS_UNITS, w, h, angle, 5, lineColor);
    }

    public void drawRect(
        ShapeRenderer shape, float x, float y,
        float w, float h, float angleDegrees,
        float thickness, Color color
    ) {
        shape.setColor(color);

        float hw = w / 2f;
        float hh = h / 2f;

        float angleRad = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);

        // Axes
        Vector2 dx = new Vector2(cos, sin).scl(hw);
        Vector2 dy = new Vector2(-sin, cos).scl(hh);
        Vector2 extend = new Vector2(); // Reused for overlap

        // Corner positions
        Vector2 tr = new Vector2(x, y).add(dx).add(dy);
        Vector2 tl = new Vector2(x, y).sub(dx).add(dy);
        Vector2 bl = new Vector2(x, y).sub(dx).sub(dy);
        Vector2 br = new Vector2(x, y).add(dx).sub(dy);

        // Calculate outward directions for extension
        // Top edge: tl -> tr
        extend.set(tr).sub(tl).nor().scl(thickness / 2f);
        shape.rectLine(tl.x - extend.x, tl.y - extend.y, tr.x + extend.x, tr.y + extend.y, thickness);

        // Right edge: tr -> br
        extend.set(br).sub(tr).nor().scl(thickness / 2f);
        shape.rectLine(tr.x - extend.x, tr.y - extend.y, br.x + extend.x, br.y + extend.y, thickness);

        // Bottom edge: br -> bl
        extend.set(bl).sub(br).nor().scl(thickness / 2f);
        shape.rectLine(br.x - extend.x, br.y - extend.y, bl.x + extend.x, bl.y + extend.y, thickness);

        // Left edge: bl -> tl
        extend.set(tl).sub(bl).nor().scl(thickness / 2f);
        shape.rectLine(bl.x - extend.x, bl.y - extend.y, tl.x + extend.x, tl.y + extend.y, thickness);
    }
}
