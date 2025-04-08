package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;

public class Button {
    private float posX;
    private float posY;
    private Texture texture;

    private float width;

    private float height;

    private int exitCode;

    public Button(float x, float y, Texture texture, int exitCode){
        posX = x;
        posY = y;
        this.texture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
        this.exitCode = exitCode;
    }

    public boolean isHovering(){
        int x = Gdx.input.getX();
        int y = 720 - Gdx.input.getY();
        return x >= posX && x <= posX + width && y >= posY
            && y <= posY + height;
    }

    public void draw(SpriteBatch batch){
        if (isHovering()) {
            batch.setBlendMode(BlendMode.ADDITIVE);
        }
        batch.draw(texture,posX,posY);
        batch.setBlendMode(BlendMode.ALPHA_BLEND);
    }
    public int getExitCode(){
        return exitCode;
    }



}
