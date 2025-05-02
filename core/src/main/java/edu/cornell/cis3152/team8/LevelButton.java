package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;

public class LevelButton extends Button {

    /**
     * The number of this level
     */
    private final TextLayout number;

    /**
     * The lock texture
     */
    private final Texture lock;

    /**
     * Whether this level is locked
     */
    private boolean locked;

    public LevelButton(float x, float y, int exitCode, AssetDirectory assets) {
        super(x, y, assets.getEntry("plate", Texture.class),
            assets.getEntry("plateHover", Texture.class), exitCode);
        lock = assets.getEntry("lock", Texture.class);
        this.number = new TextLayout(exitCode + "", font);
        locked = true;
    }

    @Override
    public void draw(SpriteBatch batch, boolean allowHover) {
        batch.setBlendMode(BlendMode.ALPHA_BLEND);

        if (isHovering() && !locked) {
            batch.draw(hover, posX, posY, width, height); //draw hover state
            number.setColor(new Color(175f / 255, 73f / 255, 0, 1));
        } else {
            batch.draw(texture, posX, posY, width, height); //draw normally
        }

        if (locked) { //draw lock
            batch.draw(lock, posX + (width / 2f - lock.getWidth() / 2f),
                posY + (height / 2f - lock.getHeight() / 2f));
        } else { //draw number
            SpriteBatch.computeTransform(transform, 0,
                0, posX + (width / 2f),
                posY + (height / 2f), 0.0f, 1f, 1f);
            batch.drawText(number, transform);
        }
        number.setColor(fontColor);
    }

    /**
     * @return whether the level is unlocked
     */
    public boolean getUnlocked() {
        return !locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
