package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;
public class Minion extends GameObject{

    private int health;
    private Texture texture;

    private int id;

    private float moveSpeed;

    /**
     * Constructs a Minion at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Minion(float x, float y, int id) {
        super(x, y);
        constants = new JsonValue("assets/constants.json");
        this.id = id;
        texture = new Texture("images/Minion.png");

        //setConstants(constants);
    }

    /**
     * Sets constants for this Coin
     *
     * @param constants The JsonValue of the object
     * */
    private void setConstants(JsonValue constants){
        this.constants = constants;
        radius = constants.getFloat("size");
        health = constants.getInt("health");
        //MOVE_SPEED = constants.getFloat("move speed");
        moveSpeed = 2;
    }

    @Override
    public ObjectType getType() {
        return ObjectType.MINION;
    }

    public int getHealth(){
        return health;
    }

    public void removeHealth(int shot){
        health -= shot;
    }

    public int getId(){
        return id;
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
    public void update(int controlCode){
        // If we are dead do nothing.
        if (isDestroyed()) {
            return;
        }

        // Determine how we are moving.
        boolean movingLeft  = controlCode ==  1;
        boolean movingRight = controlCode == 2;
        boolean movingUp    = controlCode == 4;
        boolean movingDown  = controlCode == 8;
        //System.out.println("" + movingLeft +movingRight+movingUp+movingDown);

        //System.out.println(controlCode == InputController.CONTROL_MOVE_LEFT);
        int s = 1;
        // Process movement command.
        if (movingLeft) {
            velocity.x = -s;
            velocity.y = 0;
        } else if (movingRight) {
            velocity.x = s;
            velocity.y = 0;
        } else if (movingUp) {
            velocity.y = -s;
            velocity.x = 0;
        } else if (movingDown) {
            velocity.y = s;
            velocity.x = 0;
        } else{
            velocity.x = 0;
            velocity.y = 0;
        }
        //System.out.println(velocity);
        position.add(velocity);
        //System.out.println(position);
    }

    /**
     * Draws this Minion to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch){
        batch.draw(texture,position.x,position.y,64,64);
    }
}
