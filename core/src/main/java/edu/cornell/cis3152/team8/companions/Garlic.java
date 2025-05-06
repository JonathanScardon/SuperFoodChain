package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.*;
import edu.cornell.cis3152.team8.projectiles.DurianProjectile;
import edu.cornell.cis3152.team8.projectiles.GarlicProjectile;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Garlic extends Companion {

    private static int COST;
    private static int COOLDOWN;
    private static float ANIMATION_SPEED;
    private static SpriteSheet texture;

    /**
     * Constructs a Garlic at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Garlic(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.GARLIC);

        setOriginalCost(COST);
        setCost(COST);
        setCooldown(COOLDOWN);

        animationSpeed = ANIMATION_SPEED;
        setSpriteSheet(texture);
    }

    /**
     * Loads Garlic-specific constants from JSON
     */
    public static void setConstants(JsonValue constants) {
        COST = constants.getInt("cost", 1);
        COOLDOWN = constants.getInt("cooldown", 2);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.25f);
    }

    /**
     * Sets Garlic assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("GARLIC.animation", SpriteSheet.class);
    }


    @Override
    /**
     * A Garlic shoots a poison cloud behind it
     */
    public void useAbility(GameState state) {

        GarlicProjectile projectile = ProjectilePools.garlicPool.obtain();
        projectile.getObstacle().getBody().setActive(true);

        projectile.getObstacle().setX(obstacle.getX());
        projectile.getObstacle().setY(obstacle.getY());
        projectile.getObstacle().setLinearVelocity(new Vector2(0,0));

        state.getActiveProjectiles().add(projectile);

        coolDown(false, 0);
    }

    ;
}
