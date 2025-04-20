package edu.cornell.cis3152.team8;

import com.badlogic.gdx.physics.box2d.World;

public class Mouse extends Boss {
    private String state;
    public Mouse(float x, float y, World world) {
        super(x, y, world);
    }

    public String getState(){
        return state;
    }

    public void setState(String s){
        state = s;
    }
}
