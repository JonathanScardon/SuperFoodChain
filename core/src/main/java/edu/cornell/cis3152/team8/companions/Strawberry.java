package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.ProjectilePools;
import edu.cornell.cis3152.team8.StrawberryProjectile;

import edu.cornell.gdiac.graphics.SpriteBatch;
import java.util.Random;

public class Strawberry extends Companion {

    /**
     * Constructs a Strawberry at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    Texture texture;
    public Strawberry(float x, float y) {
        super(x, y);
        setCompanionType(CompanionType.STRAWBERRY);
        setCost(3);
        setCooldown(3);
        radius = 1;
        texture = new Texture("images/Strawberry.png");
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture,position.x,position.y,64,64);
    }


    @Override
    /**
     * A Strawberry shoots 5 small and quick projectiles in a radius around it
     */
    public void useAbility(GameState state) {
        // Determines direction of projections - 5 random directions
        float fireAngle = 0.0f;

        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            StrawberryProjectile projectile = ProjectilePools.strawberryPool.obtain();
            // need to add this because previous projectiles from pool that were used would be setDestroyed
            projectile.setDestroyed(false);
            // same idea here: need to reset the life count of the projectile from the pool to reuse
            projectile.resetLife();
            projectile.setX(getX());
            projectile.setY(getY());

            fireAngle = (float) rand.nextInt(360);
            projectile.setVX((float) Math.cos(Math.toRadians(fireAngle)));
            projectile.setVY((float) Math.sin(Math.toRadians(fireAngle)));

            state.getActiveProjectiles().add(projectile);
        }

        coolDown(false, 0);
    };
}
