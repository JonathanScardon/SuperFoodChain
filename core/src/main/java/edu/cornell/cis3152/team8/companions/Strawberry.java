package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
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
    public Strawberry(float x, float y, World world) {
        super(x, y, world);
        setCompanionType(CompanionType.STRAWBERRY);
        //temp cost (was 3)
        setCost(2);
        setCooldown(3);
        setTexture(new Texture("images/Strawberry.png"));

    }


    @Override
    /**
     * A Strawberry shoots 5 small and quick projectiles in a radius around it
     */
    public void useAbility(GameState state) {
        // Determines direction of projections - 5 random directions
        float fireAngle = 0.0f;
        ProjectilePools.initialize(state.getWorld());

        // need to get from Projectile
        float speed = 2f;

        Random rand = new Random();

        for (int i = 0; i < 4; i++) {
            StrawberryProjectile projectile = ProjectilePools.strawberryPool.obtain();

            // need to add this because previous projectiles from pool that were used would be setDestroyed
            projectile.getObstacle().setActive(true);
            projectile.getObstacle().getBody().setActive(true);
            projectile.getObstacle().markRemoved(false);

            projectile.getObstacle().setX(obstacle.getX());
            projectile.getObstacle().setY(obstacle.getY());

            fireAngle = (float) 90 * i;

            float vx = (float) Math.cos(Math.toRadians(fireAngle)) * speed;
            float vy = (float) Math.sin(Math.toRadians(fireAngle)) * speed;

            projectile.getObstacle().setLinearVelocity(new Vector2(vx, vy));

            state.getActiveProjectiles().add(projectile);
        }

        coolDown(false, 0);
    }
}
