package edu.cornell.cis3152.team8;

public class Mouse extends Boss {

    private String state;

    public Mouse(float x, float y, float health) {
        super(x, y, health);
    }


    public BossType getBossType() {
        return BossType.MOUSE;
    }

    public String getState() {
        return state;
    }

    public void setState(String s) {
        state = s;
    }
}
