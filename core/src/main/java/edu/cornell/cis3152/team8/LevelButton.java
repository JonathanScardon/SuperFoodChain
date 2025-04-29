package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Affine2;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;

public class LevelButton extends Button {

    private TextLayout number;
    private Texture lock;
    private boolean locked;
    private BitmapFont font;
    private Color fontColor;

    public LevelButton(float x, float y, int exitCode, AssetDirectory assets) {
        super(x, y, new Texture("images/LevelSelectPlate.png"),
            new Texture("images/LevelSelectPlate.png"), exitCode);
        lock = new Texture("images/Lock.png");
        font = assets.getEntry("lpcBig", BitmapFont.class);
        fontColor = new Color(89f / 255, 43f / 255, 34f / 255, 100f);
        this.number = new TextLayout(exitCode + "", font);
        locked = true;
    }

    @Override
    public void draw(SpriteBatch batch, boolean allowHover) {
        if (isHovering() && !locked) {
            batch.setBlendMode(BlendMode.ADDITIVE);
        }
        batch.draw(texture, posX, posY, width, height);
        batch.setBlendMode(BlendMode.ALPHA_BLEND);

        if (locked) {
            batch.draw(lock, posX + (width / 2f - lock.getWidth() / 2f),
                posY + (height / 2f - lock.getHeight() / 2f));
        } else {
            font.setColor(fontColor);
            batch.drawText(number, posX + (width / 2f),
                posY + (height / 2f));
        }
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
