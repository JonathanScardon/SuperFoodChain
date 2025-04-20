package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class BlueRaspberry extends Companion {
    Texture texture;
    float dx = 0.0f;
    float dy = 0.0f;

    /**
     * Constructor for
     *
     * @param x x-position for the Blue Raspberry companion
     * @param y y-position for the Blue Raspberry companion
     */
    public BlueRaspberry(float x, float y, int id) {
        super(x, y, id);
        setCompanionType(CompanionType.BLUE_RASPBERRY);
        setCost(2);
        // need to think about how CD will work with support characters
        setCooldown(7);
        radius = 1;
        texture = new Texture("images/Companion_Raspberry_Static.PNG");
        SpriteSheet blueRaspberry = new SpriteSheet(texture, 1, 7);
        setSpriteSheet(blueRaspberry);
        animationSpeed = 0.25f;
        size = 0.4f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void useAbility(GameState state) {

    }
}
