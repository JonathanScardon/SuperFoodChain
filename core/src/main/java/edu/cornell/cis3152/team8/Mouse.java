package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Mouse extends Boss {
    public Mouse(float x, float y) {
        super(x, y);
        radius = 40f * 3f / 2f;
    }

    @Override
    public BossType getBossType() {
        return BossType.MOUSE;
    }
}
