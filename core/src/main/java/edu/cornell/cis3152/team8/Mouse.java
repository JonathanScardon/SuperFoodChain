package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Mouse extends Boss {
    public Mouse(float x, float y, World world) {
        super(x, y, world);
    }

    @Override
    public BossType getBossType() {
        return BossType.MOUSE;
    }
}
