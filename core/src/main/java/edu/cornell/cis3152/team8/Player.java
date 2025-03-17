package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.gdiac.graphics.*;

import java.util.LinkedList;

public class Player extends GameObject{

    /** All companions in the player's current chain */
    protected LinkedList<Companion> companions;

    /** The players coin count, used to purchase companions */
    protected int coins;

    /** Indicates whether the player is attacking */
    protected boolean attacking;

    /** Indicates whether the player has a shield */
    protected boolean shield;

    /** The direction the player is facing */
    protected int forwardDirection;

    private long ticks;

    public Player(int x, int y){
        super(x, y);
        this.companions = new LinkedList<>();
        this.coins = 0;
        this.attacking = false;
        this.shield = false;
        Companion head = new Strawberry(x,y);
        companions.add(head);
        radius = 1;
        ticks = 0;
    }

    public void update(int controlCode){

        ticks++;
        //float lastX = this.body[this.body.length-1].x;     // track the last X and Y
        //this.lastY = this.body[this.body.length-1].y;     // so we can put the new body there

        for (int i = companions.size()-1; i >= 1; i--) {
            Companion c = companions.get(i);
            c.follow(ticks);
            c.setVX(companions.get(i - 1).getPrevVelocity().x);
            c.setVY(companions.get(i - 1).getPrevVelocity().y);
            c.position.add(c.velocity);

            //System.out.println(i + " " +companions.get(i).position);

        }

        //companions.get(0).follow(ticks);
        companions.get(0).update(controlCode,ticks);

        //System.out.println(0 + " " +companions.get(0).position);
//        for (Companion c: companions){
//            c.update(controlCode);
//        }

        //update each companion in the list given the control code
    }

    public void draw(SpriteBatch batch){
        for (Companion c: companions){
            c.draw(batch);
        }

        //draw each companion (SpriteBatch, Affine2, SpriteSheet)
    }

    /**
     * Sets the player's attacking status
     * @param x the player's new attacking status
     */
    public void setAttacking(boolean x){
        this.attacking = x;
    }

    /**
     * Retrieves the player's attacking status
     * @return true if the player is attacking, false otherwise
     */
    public boolean isAttacking(){
        return this.attacking;
    }

    public int getHealth(){
        //TODO

        //is this necessary?
        return 0;
    }

    /**
     * Checks it the player is alive.
     * @return true when the player has at least one companion,
     * false otherwise
     */
    public boolean isAlive(){
        return !companions.isEmpty();
    }

    /**
     * Retrieves the current number of coins the player has
     * @return the number of coins the player has
     */
    public int getCoins(){
        return this.coins;
    }

    /**
     * Sets the player's coin count to a specified value
     * @param coins the new number of coins the player has
     */
    public void setCoins(int coins){
        this.coins = coins;
    }


    /**
     * Returns whether the player has a shield
     * @return whether the player has a shield
     */
    public boolean hasShield(){
        return this.shield;
    }

    /**
     * Sets whether the player has a shield
     * @param shield true if the player has a shield, false otherwise
     */
    public void setShield(boolean shield){
        this.shield = shield;
    }

    /**
     * Returning the direction the player is facing
     * @return the direction the player is facing
     */
    public int getForwardDirection(){
        return this.forwardDirection;
    }

    /**
     * Sets the direction the player is facing
     * @param forwardDirection the new number of coins the player has
     */
    public void setForwardDirection(int forwardDirection){
        this.forwardDirection = forwardDirection;
    }

    /**
     * Appends a companion to the player's chain
     * @param companion the companion to add
     */
    public void addCompanion(Companion companion){
        float x;
        float y;
        float px = companions.getLast().getX();
        float py = companions.getLast().getY();
        int dist = 55;
        companions.add(companion);

        if (forwardDirection == 1){
            x = px + dist;
            y = py;
        } else if (forwardDirection == 2) {
            x = px-dist;
            y = py;
        } else if (forwardDirection == 4) {
            x = px;
            y = py-dist;
        }else{
            x = px;
            y = py + dist;
        }
        companion.setX(x);
        companion.setY(y);

    }
    /**
     * Removes the companion from the player's chain
     * @param companion the companion to remove
     */
    public void deleteCompanion(Companion companion){
        int index = companions.indexOf(companion);
        //companion out of range
        if (index < 0 || index > companions.size()-1){
            return;
        }
        companions.remove(index);
    }

    /**
     * Returns GameObject type Player
     * */
    @Override
    public ObjectType getType() {
        return ObjectType.PLAYER;
    }
}
