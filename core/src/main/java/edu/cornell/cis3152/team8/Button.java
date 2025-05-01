package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Affine2;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.SpriteSheet;
import edu.cornell.gdiac.graphics.TextLayout;

public class Button {

    /**
     * Position and size
     */
    protected float posX;
    protected float posY;
    protected float width;
    protected float height;

    /**
     * Visual
     */
    protected Texture texture;
    protected Texture hover;
    private final TextLayout text;
    private boolean flip = false;
    protected Affine2 transform = new Affine2();
    protected static BitmapFont font;
    protected static Color fontColor = new Color(89f / 255, 43f / 255, 34f / 255, 100f);

    /**
     * Pressing logic
     */
    private int exitCode;
    private boolean pressed = false;
    private float resetWait = 0.25f;

    public Button(float x, float y, Texture texture, Texture hover, int exitCode) {
        posX = x;
        posY = y;
        this.texture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
        this.exitCode = exitCode;
        text = new TextLayout("", font);
        this.hover = hover;
    }

    public Button(float x, float y, Texture texture, Texture hover, int exitCode, boolean flip) {
        posX = x;
        posY = y;
        this.texture = texture;
        width = -texture.getWidth();
        height = texture.getHeight();
        this.exitCode = exitCode;
        text = new TextLayout("", font);
        this.hover = hover;
        this.flip = flip;
    }

    public Button(float x, float y, Texture texture, Texture hover, int exitCode, float width,
        float height) {
        posX = x;
        posY = y;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.exitCode = exitCode;
        text = new TextLayout("", font);
        this.hover = hover;
    }

    public Button(float x, float y, Texture texture, Texture hover, int exitCode, float width,
        float height, boolean flip) {
        posX = x;
        posY = y;
        this.texture = texture;
        this.width = -width;
        this.height = height;
        this.exitCode = exitCode;
        text = new TextLayout("", font);
        this.hover = hover;
        this.flip = flip;
    }

    public Button(float x, float y, Texture texture, Texture hover, int exitCode, float width,
        float height,
        String name) {
        posX = x;
        posY = y;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.exitCode = exitCode;
        text = new TextLayout(name, font);
        this.hover = hover;
    }

    /**
     * Sets button font
     */
    public static void setAssets(AssetDirectory assets) {
        font = assets.getEntry("lpcBig", BitmapFont.class);
        font.setColor(fontColor);
    }

    /**
     * Allows time between button presses
     */
    public void update(float delta) {
        if (pressed) {
            resetWait -= delta;
        }
        if (resetWait <= 0) {
            resetWait = 0.25f;
            pressed = false;
        }
    }

    /**
     * Draws  button
     *
     * @param batch      the SpriteBatch
     * @param allowHover whether the button is able to be clicked
     */
    public void draw(SpriteBatch batch, boolean allowHover) {
        batch.setBlendMode(BlendMode.ALPHA_BLEND);
        if (isHovering() && allowHover) { //draw hovered button
            batch.draw(hover, posX, posY, width, height);
        } else { //draw normal button
            batch.draw(texture, posX, posY, width, height);
        }
        SpriteBatch.computeTransform(transform, 0,
            0, posX + (width / 2f),
            posY + (height / 2f), 0.0f, 1f, 1f);
        batch.drawText(text, transform);
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int code) {
        exitCode = code;
    }

    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }

    /**
     * @return true if the mouse is over this Button
     */
    protected boolean isHovering() {
        int x;
        if (posX > 1280 && posX < 2560) {
            x = Gdx.input.getX() + 1280;
        } else if (posX > 2560) {
            x = Gdx.input.getX() + 2560;
        } else {
            x = Gdx.input.getX();
        }

        int y = 720 - Gdx.input.getY();
        if (flip) {
            return x <= posX && x >= posX + width && y >= posY
                && y <= posY + height;
        } else {
            return x >= posX && x <= posX + width && y >= posY
                && y <= posY + height;
        }
    }

    /**
     * @return whether the button was pressed
     */
    public boolean isPressed() {
        if (isHovering() && Gdx.input.isTouched() && !pressed) {
            pressed = true;
            return true;
        } else {
            return false;
        }
    }
}
