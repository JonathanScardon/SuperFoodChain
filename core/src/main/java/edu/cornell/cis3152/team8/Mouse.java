package edu.cornell.cis3152.team8;

import com.badlogic.gdx.physics.box2d.World;

private String state;

public class Mouse extends Boss {
    public Mouse(float x, float y, World world) {
        super(x, y, world);
    }

//    @Override
//    public BossType getBossType() {
//        return BossType.MOUSE;
//    }

    public String getState(){
        return state;
    }

    public void setState(String s){
        state = s;
    }
}
