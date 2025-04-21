package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Boss;
import edu.cornell.cis3152.team8.Companion;
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
        setCooldown(1);

        texture = new Texture("images/Durian.png");
        SpriteSheet pineapple = new SpriteSheet(texture, 1, 8);
        setSpriteSheet(pineapple);
        animationSpeed = 0.25f;
    }

    @Override
    /**
     * TODO: THIS NEEDS TO BE CHANGED --> WILL CAUSE COIN TO NOT SPAWN UPON MINION DEATH
     */
    public void useAbility(GameState state) {
        Vector2 durianPos = obstacle.getPosition();

        float radius = 3f;
        int damage = 1;

        for (Minion m : state.getMinions()) {
            Vector2 enemyPos = m.getObstacle().getPosition();
            if (enemyPos.dst(durianPos) <= radius) {
                m.removeHealth(damage);
                System.out.println("Minion killed via durian radius");
            }
        }

        for (Boss b : state.getBosses()) {
            Vector2 enemyPos = b.getObstacle().getPosition();
            if (enemyPos.dst(durianPos) <= radius) {
                b.removeHealth(damage);
                System.out.println("Boss damaged via durian radius");
            }
        }
        coolDown(false, 0);
    }
}
