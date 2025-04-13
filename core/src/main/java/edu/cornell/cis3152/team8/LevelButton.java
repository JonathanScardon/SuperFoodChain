package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;

public class LevelButton extends Button{

    private Texture number;
    private Texture lock;
    private boolean locked;
    public LevelButton(float x, float y, Texture number, int exitCode) {
        super(x, y, new Texture("images/LevelSelectPlate.png"), exitCode);
        lock = new Texture("images/Lock.png");
        this.number = number;

        locked = true;

    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isHovering() && !locked) {
            batch.setBlendMode(BlendMode.ADDITIVE);
        }
        batch.draw(texture,posX,posY,width,height);
        batch.setBlendMode(BlendMode.ALPHA_BLEND);
        batch.draw(number,posX,posY);
        if (locked){
            batch.draw(lock,posX,posY);
        }
    }

    public boolean getLocked(){
        return locked;
    }
    public void setLocked(boolean locked){
        this.locked = locked;
    }
}
