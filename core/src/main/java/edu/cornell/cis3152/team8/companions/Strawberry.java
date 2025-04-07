package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.ProjectilePools;
import edu.cornell.cis3152.team8.StrawberryProjectile;

import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
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
        //temp cost (was 3)
        setCost(2);
        setCooldown(3);
        radius = 1;
        texture = new Texture("images/Strawberry.png");
        SpriteSheet coin = new SpriteSheet(texture, 1, 7);
        setSpriteSheet(coin);
        animationSpeed = 0.25f;
        size = 0.4f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

    }

    public void draw(SpriteBatch batch){
        if (isDestroyed()) {
            animator.setFrame(1);
            batch.setColor(Color.BLACK);
        }else {
            animator.setFrame((int)animationFrame);
            batch.setColor( Color.WHITE );

        }
        SpriteBatch.computeTransform(transform, origin.x, origin.y,
            position.x, position.y, 0.0f, size
            , size);

        batch.draw( animator, transform );
        //batch.draw(texture, position.x, position.y, 64, 64);
        //batch.draw(texture, position.x, position.y, 64, 64);
        batch.setColor(Color.WHITE);
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
    }
}
