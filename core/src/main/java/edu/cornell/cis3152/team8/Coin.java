package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;

public class Coin extends GameObject{

    /**
     * Constructs a Coin at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Coin(float x, float y) {
        super(x,y);
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

    /**
     * Returns GameObject type Coin
     * */
    @Override
    public ObjectType getType() {
        return ObjectType.COIN;
    }

    /**
     * Updates the state of this Coin.
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
     * Draws this Coin to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch){
        //TODO
    }

}
