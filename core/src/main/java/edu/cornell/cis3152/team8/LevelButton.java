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

    public LevelButton(float x, float y, int exitCode, AssetDirectory assets) {
        super(x, y, new Texture("images/LevelSelectPlate.png"),
            new Texture("images/LevelSelectPlate.png"), exitCode);
        lock = new Texture("images/Lock.png");
        font = assets.getEntry("lpc", BitmapFont.class);
        font.setColor(Color.BROWN);
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
            batch.draw(lock, posX + texture.getWidth() / 2f - lock.getWidth() / 2f,
                posY + texture.getHeight() / 2f - lock.getHeight() / 2f);
        } else {

            Affine2 trans = new Affine2();
            trans.setToTrnScl(posX + (texture.getWidth() / 2f - number.getWidth() / 2f),
                posY + (texture.getHeight() / 2f) - number.getWidth(), 2f, 2f);
            batch.drawText(number, trans);

        }
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
