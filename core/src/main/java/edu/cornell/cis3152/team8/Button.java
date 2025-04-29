package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;

public class Button {

    protected float posX;
    protected float posY;
    protected Texture texture;
    protected Texture hover;


    protected float width;

    protected float height;

    private TextLayout text;

    private int exitCode;

    private boolean flip = false;
    private Affine2 transform = new Affine2();
    private boolean pressed = false;

    private float resetWait = 1;


    public Button(float x, float y, Texture texture, Texture hover, int exitCode) {
        posX = x;
        posY = y;
        this.texture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
        this.exitCode = exitCode;
        text = new TextLayout("", new BitmapFont());
        this.hover = hover;
    }

    public Button(float x, float y, Texture texture, Texture hover, int exitCode, boolean flip) {
        posX = x;
        posY = y;
        this.texture = texture;
        width = -texture.getWidth();
        height = texture.getHeight();
        this.exitCode = exitCode;
        text = new TextLayout("", new BitmapFont());
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
        text = new TextLayout("", new BitmapFont());
        this.hover = hover;
    }

    public Button(float x, float y, Texture texture, Texture hover, int exitCode, float width,
        float height,
        String name, BitmapFont font) {
        posX = x;
        posY = y;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.exitCode = exitCode;
        text = new TextLayout(name, font);
        this.hover = hover;
    }


    public boolean isHovering() {
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

    public void update(float delta) {
        if (pressed) {
            resetWait -= delta;
        }
        if (resetWait <= 0) {
            resetWait = 1f;
            pressed = false;
        }
    }

    public void draw(SpriteBatch batch, boolean allowHover) {
        batch.setBlendMode(BlendMode.ALPHA_BLEND);
        if (isHovering() && allowHover) {
            batch.draw(hover, posX, posY, width, height);
        } else {
            batch.draw(texture, posX, posY, width, height);
        }

        text.getFont().setColor(Color.BROWN);
        batch.drawText(text, posX + (width / 2f),
            posY + height / 2f);
        text.getFont().setColor(Color.WHITE);
    }

    public void draw(SpriteBatch batch, boolean allowHover, float x, float y) {
        batch.setBlendMode(BlendMode.ALPHA_BLEND);
        if (isHovering() && allowHover) {
            batch.draw(hover, x, y, width, height);
        } else {
            batch.draw(texture, x, y, width, height);
        }

        text.getFont().setColor(Color.BROWN);
        batch.drawText(text, posX + (width / 2f),
            posY + height / 2f);
        text.getFont().setColor(Color.WHITE);
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


    public boolean isPressed() {
        if (isHovering() && Gdx.input.isTouched() && !pressed) {
            pressed = true;
            return true;
        } else {
            return false;
        }
    }
}
