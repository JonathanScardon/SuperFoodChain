package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class BlueRaspberry extends Companion {

    private static int COST;
    private static int COOLDOWN;
    private static float ANIMATION_SPEED;
    private static float BOOST;

    private Texture texture;
    private boolean usedBoost;

    /**
     * Constructor for Blue Raspberry companion
     *
     * @param x x-position for the Blue Raspberry companion
     * @param y y-position for the Blue Raspberry companion
     */
    public BlueRaspberry(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.BLUE_RASPBERRY);

        setCost(COST);
        setCooldown(COOLDOWN);

        texture = new Texture("images/BlueRaspberry.png");
        SpriteSheet blueRaspberry = new SpriteSheet(texture, 1, 8);
        setSpriteSheet(blueRaspberry);

        animationSpeed = ANIMATION_SPEED;
        usedBoost = false;
    }

    /** Loads BlueRaspberry-specific constants from JSON */
    public static void setConstants(JsonValue constants) {
        COST = constants.getInt("cost", 1);
        COOLDOWN = constants.getInt("cooldown", 7);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.25f);
        BOOST = constants.getFloat("boost", 25f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public boolean canUse() {
        return !usedBoost;
    }

    /**
     * Blue Raspberry adds a speed boost usedBoost is set to true, preventing the speed increase
     * from being used more than once
     */
    @Override
    public void useAbility(GameState state) {
        usedBoost = true;
        Companion.increaseBoost(BOOST);
        Player.calculateDelay();
    }

    /**
     * Removes associated speed boost from Blue Raspberry
     */
    public void loseAbility() {
        Companion.decreaseBoost(BOOST);
        Player.calculateDelay();
    }
}
