package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Mouse extends Boss {
    public Mouse(float x, float y) {
        super(x, y);
    }

    @Override
    public BossType getBossType() {
        return BossType.MOUSE;
    }

    /**
     * Draws this object to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        SpriteBatch.computeTransform(transform, origin.x, origin.y, position.x, position.y, -(-90 + angle), 4f, 4f);

        batch.setColor(Color.WHITE);
        batch.draw(animator, transform);
    }
}
