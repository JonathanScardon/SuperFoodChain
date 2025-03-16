package edu.cornell.cis3152.team8.companions;

import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;

public class Durian extends Companion {

    /**
     * Constructs a Durian at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Durian(float x, float y) {
        super(x, y);
        setCompanionType(CompanionType.DURIAN);
        setCost(10);
        setCooldown(10);
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
