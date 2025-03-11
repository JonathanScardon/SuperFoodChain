package edu.cornell.cis3152.team8;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;

public class Player extends GameObject{

    public Player(int x, int y){
        super(x, y);
    }

    public void update(float delta){
        //TODO
    }

    public void draw(SpriteBatch batch){
        //TODO
    }

    public void setAttacking(boolean x){
        //TODO
    }

    public boolean isAttacking(){
        //TODO
        return false;
    }

    public int getHealth(){
        //TODO
        return 0;
    }

    public boolean isAlive(){
        //TODO
        return true;
    }

    public int getCoins(){
        //TODO
        return 0;
    }

    public void setCoins(int coins){
        //TODO
    }

    public void addCompanion(Companion companion){
        //TODO
    }

    public void deleteCompanion(Companion companion){
        //TODO
    }


    /**
     * Returns GameObject type Player
     * */
    @Override
    public ObjectType getType() {
        return ObjectType.PLAYER;
    }
}
