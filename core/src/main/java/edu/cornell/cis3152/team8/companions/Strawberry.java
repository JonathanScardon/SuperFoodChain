package edu.cornell.cis3152.team8.companions;

import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;

public class Strawberry extends Companion {

    /**
     * Constructs a Strawberry at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Strawberry(float x, float y) {
        super(x, y);
        setCompanionType(CompanionType.STRAWBERRY);
    }


    @Override
    /**
     * A Strawberry shoots 4 small and quick projectiles in a radius around it
     */
    public void useAbility(GameState state) {
        //ProjectilePool projectiles = state.getProjectiles();

        // Determines direction of projections
        float x = getX();
        float y = getY();
        for (float fireAngle = 0.0f; fireAngle < 360.0f; fireAngle += 90.0f) {
            float vx = (float) Math.cos(fireAngle * Math.PI / 180.0f);
            float vy = (float) Math.sin(fireAngle * Math.PI / 180.0f);

            // requires argument for size of projectile
            // quicker by x2
            //projectiles.add(x, y, vx  * 2, vy * 2, size);
        }

        coolDown(false);
    };
}
