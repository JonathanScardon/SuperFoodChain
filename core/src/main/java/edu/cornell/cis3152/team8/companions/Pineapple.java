package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.*;
import edu.cornell.cis3152.team8.projectiles.PineappleProjectile;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Pineapple extends Companion {
    Texture texture;
    float dx = 0.0f;
    float dy = 0.0f;

    public Pineapple(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.PINEAPPLE);
        setCost(4);
        setCooldown(7);
//        radius = 1;
        texture = new Texture("images/Pineapple.png");
        SpriteSheet pineapple = new SpriteSheet(texture, 1, 8);
        setSpriteSheet(pineapple);
//        origin.y = 117.5f;
        animationSpeed = 0.25f;
//        size = 0.4f;
    }

    @Override
    /**
     * Shoots a projectile that explodes, damaging enemies within an explosion radius
     */
    public void useAbility(GameState state) {
        Vector2 directionalVector = utilities.autoshoot(state, obstacle.getPosition());
        dx = directionalVector.x;
        dy = directionalVector.y;

        if (dx != 0.0f || dy != 0.0f) {
//            PineappleProjectile projectile = ProjectilePools.pineapplePool.obtain();
//            projectile.getObstacle().setActive(true);
//            projectile.reset();
            PineappleProjectile projectile = new PineappleProjectile(0,0,0, 0, state.getWorld());
            projectile.getObstacle().setX(obstacle.getX());
            projectile.getObstacle().setY(obstacle.getY());
//            float vx = (float) Math.toRadians(dx);
//            float vy = (float) Math.toRadians(dy);
            projectile.getObstacle().setLinearVelocity(new Vector2(dx, dy));
            state.getActiveProjectiles().add(projectile);
        }
        coolDown(false, 0);
    }
}
