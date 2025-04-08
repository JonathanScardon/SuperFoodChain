package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.PineappleProjectile;
import edu.cornell.cis3152.team8.ProjectilePools;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Pineapple extends Companion {
    Texture texture;
    float dx = 0.0f;
    float dy = 0.0f;

    public Pineapple(float x, float y) {
        super(x, y);
        setCompanionType(CompanionType.PINEAPPLE);
        setCost(4);
        setCooldown(2);
        radius = 1;
        texture = new Texture("images/Pineapple.png");
    }

    public void draw(SpriteBatch batch) {
        if (isDestroyed()) {
            batch.setColor(Color.BLACK);
        }
        batch.draw(texture, position.x, position.y, 64, 64);
        batch.setColor(Color.WHITE);
    }

    @Override
    /**
     * Shoots a projectile that explodes, damaging enemies within an explosion radius
     */
    public void useAbility(GameState state) {
        Vector2 directionalVector = utilities.autoshoot(state, getPosition());
        dx = directionalVector.x;
        dy = directionalVector.y;

        if (dx != 0.0f || dy != 0.0f) {
            PineappleProjectile projectile = ProjectilePools.pineapplePool.obtain();
            projectile.setDestroyed(false);
            projectile.resetLife();
            projectile.setX(getX());
            projectile.setY(getY());
            projectile.setVX((float) Math.toRadians(dx));
            projectile.setVY((float) Math.toRadians(dy));
            state.getActiveProjectiles().add(projectile);
        }
        coolDown(false, 0);
    }
}
