package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class BlueRaspberry extends Companion {

    Texture texture;
    float dx = 0.0f;
    float dy = 0.0f;

    boolean usedBoost;

    /**
     * Constructor for
     *
     * @param x x-position for the Blue Raspberry companion
     * @param y y-position for the Blue Raspberry companion
     */
    public BlueRaspberry(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.BLUE_RASPBERRY);
        setCost(2);
        // need to think about how CD will work with support characters
        setCooldown(7);
//        radius = 1;
        texture = new Texture("images/BlueRaspberry.png");
        SpriteSheet blueRaspberry = new SpriteSheet(texture, 1, 8);
        setSpriteSheet(blueRaspberry);
        animationSpeed = 0.25f;
//        size = 0.4f;

        usedBoost = false;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }


    @Override
    public boolean canUse() {
        return !usedBoost;
    }

    /**
     * The blue raspberry increases the speed of the player. usedBoost is set to true, preventing
     * the speed increase from being used more than once
     */
    @Override
    public void useAbility(GameState state) {
        usedBoost = true;
        //increase the player speed
        Companion.increaseBoost(25f);
        Player.calculateDelay();
    }
}
