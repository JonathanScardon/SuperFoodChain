package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;
public class Minion extends GameObject{


    /**
     * Constructs a Minion at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Minion(float x, float y) {
        super(x, y);
        setConstants(constants);
    }

    /**
     * Sets constants for this Coin
     *
     * @param constants The JsonValue of the object
     * */
    private void setConstants(JsonValue constants){
        this.constants = constants;
        radius = constants.getFloat("size");
    }

    @Override
    public ObjectType getType() {
        return ObjectType.MINION;
    }

    /**
     * Updates the state of this Minion.
     *
     * This method is only intended to update values that change local state
     * in well-defined ways, like position or a cooldown value. It does not
     * handle collisions (which are determined by the CollisionController). It
     * is not intended to interact with other objects in any way at all.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(float delta){
        //TODO
    }

    /**
     * Draws this Minion to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch){
        //TODO
    }
}
