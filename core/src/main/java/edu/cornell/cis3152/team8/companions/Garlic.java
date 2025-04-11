package edu.cornell.cis3152.team8.companions;

import edu.cornell.cis3152.team8.*;

public class Garlic extends Companion {

    /**
     * Constructs a Garlic at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Garlic(float x, float y, int id) {
        super(x, y, id);
        setCompanionType(CompanionType.GARLIC);
        setCost(5);
        setCooldown(5);
    }


    @Override
    /**
     * A Garlic shoots a poison cloud in front of it
     */
    public void useAbility(GameState state) {
        GarlicProjectile projectile = ProjectilePools.garlicPool.obtain();
        int forwardDirection = state.getPlayer().getForwardDirection();

        projectile.setDestroyed(false);
        projectile.resetLife();
        // shoots in front
        projectile.setX(getX());
        projectile.setY(getY());
        float fireAngle = 0.0f;

        if (forwardDirection == InputController.CONTROL_MOVE_DOWN) {
            fireAngle = 90.0f;
        }
        else if (forwardDirection == InputController.CONTROL_MOVE_LEFT) {
            fireAngle = 180.0f;
        }
        else if (forwardDirection == InputController.CONTROL_MOVE_UP) {
            fireAngle = 270.0f;
        }


        projectile.setVX((float) Math.cos(Math.toRadians(fireAngle)));
        projectile.setVY((float) Math.sin(Math.toRadians(fireAngle)));

        // half as slow
        // twice as large?
//        projectiles.add(x, y, vx  / 2, vy / 2, size);
        state.getActiveProjectiles().add(projectile);

        coolDown(false, 0);
    };
}
