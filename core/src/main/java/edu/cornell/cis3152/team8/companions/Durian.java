package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Boss;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.DurianProjectile;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Minion;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import java.util.Random;

public class Durian extends Companion {
    private int units = 64;
    Texture texture;
    /**
     * Constructs a Durian at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Durian(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.DURIAN);
        setCost(1);
        setCooldown(2);

        texture = new Texture("images/Durian.png");
        SpriteSheet durian = new SpriteSheet(texture, 1, 7);
        setSpriteSheet(durian);
        animationSpeed = 0.25f;
    }

    @Override
    /**
     * TODO: THIS NEEDS TO BE CHANGED --> WILL CAUSE COIN TO NOT SPAWN UPON MINION DEATH
     */
    public void useAbility(GameState state) {
        int numAttacks = 8;
        double angle = Math.toRadians(360.0 / numAttacks);
        for (int i = 0; i < numAttacks; i++) {
            DurianProjectile projectile = new DurianProjectile(0,0,0,0, state.getWorld());
            projectile.getObstacle().setLinearVelocity(new Vector2((float) Math.cos(angle*i) * 8, (float) Math.sin(angle*i) * 8));
            projectile.getObstacle().setX(obstacle.getX());
            projectile.getObstacle().setY(obstacle.getY());
            state.getActiveProjectiles().add(projectile);
        }
        coolDown(false, 0);
    }
}
