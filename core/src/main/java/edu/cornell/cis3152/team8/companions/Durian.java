package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.ProjectilePools;
import edu.cornell.cis3152.team8.projectiles.DurianProjectile;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.projectiles.StrawberryProjectile;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Durian extends Companion {

    private static int COST;
    private static int COOLDOWN;
    private static float ANIMATION_SPEED;
    private static int NUM_ATTACKS;
    private static float PROJECTILE_SPEED;
    private static SpriteSheet texture;

    public Durian(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.DURIAN);

        setOriginalCost(COST);
        setCost(COST);
        setCooldown(COOLDOWN);

        animationSpeed = ANIMATION_SPEED;

        setSpriteSheet(texture);
    }

    /**
     * Loads Durian-specific constants from JSON
     */
    public static void setConstants(JsonValue constants) {
        COST = constants.getInt("cost", 1);
        COOLDOWN = constants.getInt("cooldown", 2);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.25f);
        NUM_ATTACKS = constants.getInt("numAttacks", 8);
        PROJECTILE_SPEED = constants.getFloat("projectileSpeed", 8f);
    }

    /**
     * Sets Durian assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("DURIAN.animation", SpriteSheet.class);
    }

    @Override
    public void useAbility(GameState state) {
        double angleStep = Math.toRadians(360.0 / NUM_ATTACKS);
        for (int i = 0; i < NUM_ATTACKS; i++) {
            DurianProjectile projectile = ProjectilePools.durianPool.obtain();
            projectile.getObstacle().getBody().setActive(true);

            float dx = (float) Math.cos(angleStep * i) * PROJECTILE_SPEED;
            float dy = (float) Math.sin(angleStep * i) * PROJECTILE_SPEED;
            projectile.getObstacle().setLinearVelocity(new Vector2(dx, dy));
            projectile.setAngle((float) Math.toDegrees(
                Math.atan2(dy, dx)));
            projectile.getObstacle().setX(obstacle.getX());
            projectile.getObstacle().setY(obstacle.getY());
            state.getActiveProjectiles().add(projectile);
        }
        coolDown(false, 0);
    }
}
