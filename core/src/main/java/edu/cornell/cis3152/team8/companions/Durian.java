package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.ProjectilePools;
import edu.cornell.cis3152.team8.StrawberryProjectile;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import java.util.Random;

public class Durian extends Companion {

    Texture texture;

    /**
     * Constructs a Durian at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Durian(float x, float y) {
        super(x, y);
        //System.out.println(animator);
        //System.out.println(origin);
        setCompanionType(CompanionType.DURIAN);
        //temp cost (was 10)
        setCost(1);
        setCooldown(10);
        texture = new Texture("images/Pineapple.png");
        SpriteSheet pineapple = new SpriteSheet(texture, 1, 7);
        setSpriteSheet(pineapple);
        animationSpeed = 0.25f;
        size = 0.4f;
    }

//    public void draw(SpriteBatch batch) {
//        if (isDestroyed()) {
//            batch.setColor(Color.BLACK);
//        }
//        batch.draw(texture, position.x, position.y, 64, 64);
//        batch.setColor(Color.WHITE);
//    }

    @Override
    /**
     * A Durian creates a shield for the player
     */
    public void useAbility(GameState state) {
        state.getPlayer().setShield(true);
        coolDown(false, 0);
    }
}
