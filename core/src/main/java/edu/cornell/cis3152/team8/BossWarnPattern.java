package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class BossWarnPattern extends GameObject {
    /**
     * Whether this pattern is currently displayed
     */
    public boolean active;

    /**
     * Current animation frame for this boss
     */
    private float animeframe;

    /**
     * How fast we change frames
     */
    private static float animationSpeed = 0.10f;

    public BossWarnPattern(float x, float y) {
        super(x, y);
        this.active = false;
    }

    @Override
    public ObjectType getType() {
        return ObjectType.WARNING;
    }

    @Override
    public void update(float delta) {
        if (!active) {
            return;
        }

        animeframe += animationSpeed;
    }

    /**
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    @Override
    public void draw(SpriteBatch batch) {
        if (!active) {
            return;
        }

        SpriteBatch.computeTransform(transform, origin.x, origin.y, position.x, position.y, 0, 1, 1);
        animator.setFrame((int) animeframe);
        batch.setColor(Color.WHITE);
        batch.draw(animator, transform);
    }
}
