package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Durian extends Companion {

    Texture texture;
    /**
     * Constructs a Durian at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Durian(float x, float y) {
        super(x, y);
        setCompanionType(CompanionType.DURIAN);
        setCost(0);
        setCooldown(10);
        radius = 1;
        texture = new Texture("images/Durian.png");
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture,position.x,position.y,64,64);
    }
    @Override
    /**
     * A Durian creates a shield for the player
     */
    public void useAbility(GameState state) {
        state.getPlayer().setShield(true);

        coolDown(false);
    };
}
