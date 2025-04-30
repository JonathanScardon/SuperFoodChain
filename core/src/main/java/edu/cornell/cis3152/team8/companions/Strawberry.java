package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.projectiles.StrawberryProjectile;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Strawberry extends Companion {

    private static int COST;
    private static int COOLDOWN;
    private static float ANIMATION_SPEED;
    private static int NUM_PROJECTILES;
    private static int DELAY_PER_SHOT;
    private static float PROJECTILE_SPEED;

    /**
     * Constructs a Strawberry at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    private float dx = 0.0f;
    private float dy = 0.0f;

    public Strawberry(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.STRAWBERRY);

        setCost(COST);
        setCooldown(COOLDOWN);

        animationSpeed = ANIMATION_SPEED;
    }

    /**
     * Loads Strawberry-specific constants from JSON
     */
    public static void setConstants(JsonValue constants) {
        COST = constants.getInt("cost", 2);
        COOLDOWN = constants.getInt("cooldown", 4);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.25f);
        NUM_PROJECTILES = constants.getInt("numProjectiles", 3);
        DELAY_PER_SHOT = constants.getInt("delayPerShot", 100);
        PROJECTILE_SPEED = constants.getFloat("projectileSpeed", 7f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void useAbility(GameState state) {
        Vector2 directionalVector = utilities.autoshoot(state, obstacle.getPosition());
        dx = directionalVector.x;
        dy = directionalVector.y;

        if (dx != 0.0f || dy != 0.0f) {
            for (int i = 0; i < NUM_PROJECTILES; i++) {
                final int delay = i * DELAY_PER_SHOT;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        StrawberryProjectile projectile = new StrawberryProjectile(0, 0, 0, 0,
                            state.getWorld());
                        projectile.getObstacle().setLinearVelocity(
                            new Vector2(dx * PROJECTILE_SPEED, dy * PROJECTILE_SPEED));
                        projectile.getObstacle().setX(obstacle.getX());
                        projectile.getObstacle().setY(obstacle.getY());
                        state.getActiveProjectiles().add(projectile);
                    }
                }, delay / 1000f);
            }
        }

        coolDown(false, 0); // put the projectile on cooldown after all three shots were fired
    }
}
