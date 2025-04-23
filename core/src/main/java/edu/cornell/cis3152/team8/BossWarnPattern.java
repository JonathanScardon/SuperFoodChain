package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.physics2.BoxObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public class BossWarnPattern extends ObstacleSprite {
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
    private static float animationSpeed = 0.05f;

    private static final float PHYSICS_UNITS = 64f;

    public BossWarnPattern(float x, float y) {
        super(new BoxObstacle(x, y, 1f, 1f), false);
        this.active = false;
        obstacle.setAngle(0);
    }

    public void setPosition(float x, float y) {
        obstacle.setPosition(x, y);
    }

    @Override
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
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch, float delta) {
        if (!active) {
            return;
        }

//        System.out.println(sprite.getRegionWidth() + " " + sprite.getRegionHeight());
//        System.out.println(obstacle.getPosition().x + " " + obstacle.getPosition().y);

        SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f, sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * PHYSICS_UNITS, obstacle.getPosition().y * PHYSICS_UNITS, obstacle.getAngle(), 1, 1);
        sprite.setFrame((int) animeframe);
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);
    }
}
