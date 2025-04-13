package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Durian extends Companion {


    /**
     * Constructs a Durian at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Durian(float x, float y, World world) {
        super(x, y, world);
        //System.out.println(animator);
        //System.out.println(origin);
        setCompanionType(CompanionType.DURIAN);
        //temp cost (was 10)
        setCost(1);
        setCooldown(10);
        setTexture(new Texture("images/Durian.png"));
    }

    @Override
    /**
     * A Durian creates a shield for the player
     */
    public void useAbility(GameState state) {
        state.getPlayer().setShield(true);
        coolDown(false, 0);
    }
}
