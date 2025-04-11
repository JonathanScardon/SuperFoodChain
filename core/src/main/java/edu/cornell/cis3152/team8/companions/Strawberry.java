package edu.cornell.cis3152.team8.companions;

import static java.util.Collections.min;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Minion;
import edu.cornell.cis3152.team8.ProjectilePools;
import edu.cornell.cis3152.team8.StrawberryProjectile;

import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Strawberry extends Companion {

    /**
     * Constructs a Strawberry at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    Texture texture;
    float dx = 0.0f; // make directional components global vars with default value
    float dy = 0.0f;

    public Strawberry(float x, float y, int id) {
        super(x, y, id);
        setCompanionType(CompanionType.STRAWBERRY);
        //temp cost (was 3)
        setCost(2);
        setCooldown(3);
        radius = 1;
        texture = new Texture("images/Strawberry.png");
        SpriteSheet strawberry = new SpriteSheet(texture, 1, 8);
        setSpriteSheet(strawberry);
        animationSpeed = 0.15f;
        size = 0.4f;
        glow = new Texture("images/StrawberryGlow.png");
    }

    @Override
    public void update(float delta) {
        super.update(delta);

    }

//    public void draw(SpriteBatch batch){
//        if (isDestroyed()) {
//            animator.setFrame(1);
//            batch.setColor(Color.BLACK);
//        }else {
//            animator.setFrame((int)animationFrame);
//            batch.setColor( Color.WHITE );
//
//        }
//        SpriteBatch.computeTransform(transform, origin.x, origin.y,
//            position.x, position.y, 0.0f, size
//            , size);
//
//        batch.draw( animator, transform );
//        //batch.draw(texture, position.x, position.y, 64, 64);
//        //batch.draw(texture, position.x, position.y, 64, 64);
//        batch.setColor(Color.WHITE);
//    }


    @Override
    /**
     * A Strawberry shoots 5 small and quick projectiles in a radius around it
     */
    public void useAbility(GameState state) {
        Vector2 directionalVector = utilities.autoshoot(state, getPosition());
        dx = directionalVector.x;
        dy = directionalVector.y;

        if (dx != 0.0f || dy != 0.0f) {

            for (int i = 0; i < 3; i++) {
                final int delay = i * 100; // time-delay before each successive shot
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() { // override 'run' function inside Timer so that this block runs according to the time
                        StrawberryProjectile projectile = ProjectilePools.strawberryPool.obtain();
                        projectile.setDestroyed(false);
                        projectile.resetLife();
                        projectile.setX(getX());
                        projectile.setY(getY());
                        projectile.setVX((float) Math.toRadians(dx));
                        projectile.setVY((float) Math.toRadians(dy));
                        state.getActiveProjectiles().add(projectile);
                    }
                }, delay / 1000f);
            }
        }

        coolDown(false, 0); // put the projectile on cooldown after all three shots were fired
    }
}
