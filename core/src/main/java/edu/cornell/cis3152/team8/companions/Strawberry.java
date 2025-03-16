package edu.cornell.cis3152.team8.companions;

import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;

import java.util.Random;

public class Strawberry extends Companion {

    /**
     * Constructs a Strawberry at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Strawberry(float x, float y) {
        super(x, y);
        setCompanionType(CompanionType.STRAWBERRY);
        setCost(3);
        setCooldown(3);
    }


    @Override
    /**
     * A Strawberry shoots 5 small and quick projectiles in a radius around it
     */
    public void useAbility(GameState state) {
        StrawberryProjectilePool projectiles = state.getProjectiles();

        // Determines direction of projections - 5 random directions
        float x = getX();
        float y = getY();
        float vx = 0.0f;
        float vy = 0.0f;
        float fireAngle = 0.0f;

        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            fireAngle = (float) rand.nextInt(360);
            vx = (float) Math.cos(Math.toRadians(fireAngle));
            vy = (float) Math.sin(Math.toRadians(fireAngle));

            // requires argument for size of projectile
            // quicker by x2
            projectiles.add(x, y, vx  * 2, vy * 2, size);
        }

        coolDown(false);
    };
}
