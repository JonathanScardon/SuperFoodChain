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
    protected Texture hover;


    protected float width;

    protected float height;

    private TextLayout text;

    private int exitCode;

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
        int x = Gdx.input.getX();
        int y = 720 - Gdx.input.getY();
        return x >= posX && x <= posX + width && y >= posY
            && y <= posY + height;
    }

    public void draw(SpriteBatch batch) {
        batch.setBlendMode(BlendMode.ALPHA_BLEND);
        if (isHovering()) {
            batch.draw(hover, posX, posY, width, height);
        } else {
            batch.draw(texture, posX, posY, width, height);
        }

        text.getFont().setColor(Color.BROWN);
        batch.drawText(text, posX + (width / 2f),
            posY + height / 2f);
        text.getFont().setColor(Color.WHITE);
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }

}
