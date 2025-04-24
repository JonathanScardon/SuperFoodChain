package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;

public class Button {

    protected float posX;
    protected float posY;
    protected Texture texture;

    protected float width;

    protected float height;

    private TextLayout text;

    private int exitCode;

    public Button(float x, float y, Texture texture, int exitCode) {
        posX = x;
        posY = y;
        this.texture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
        this.exitCode = exitCode;
        text = new TextLayout("", new BitmapFont());
    }

    public Button(float x, float y, Texture texture, int exitCode, float width, float height) {
        posX = x;
        posY = y;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.exitCode = exitCode;
        text = new TextLayout("", new BitmapFont());
    }

    public Button(float x, float y, Texture texture, int exitCode, float width, float height,
        String name, BitmapFont font) {
        posX = x;
        posY = y;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.exitCode = exitCode;
        text = new TextLayout(name, font);
    }


    public boolean isHovering() {
        int x = Gdx.input.getX();
        int y = 720 - Gdx.input.getY();
        return x >= posX && x <= posX + width && y >= posY
            && y <= posY + height;
    }

    public void draw(SpriteBatch batch) {
        if (isHovering()) {
            batch.setBlendMode(BlendMode.ADDITIVE);
        }
        batch.draw(texture, posX, posY, width, height);
        text.getFont().setColor(Color.BROWN);
        batch.drawText(text, posX + (texture.getWidth() / 2f - (text.getWidth() / 2f)),
            posY + texture.getHeight() / 2f);
        text.getFont().setColor(Color.WHITE);
        batch.setBlendMode(BlendMode.ALPHA_BLEND);
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }

}
