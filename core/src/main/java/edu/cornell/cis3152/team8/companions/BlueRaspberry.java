package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class BlueRaspberry extends Companion {

    private static int COST;
    private static int COOLDOWN;
    private static float ANIMATION_SPEED;
    private static float BOOST;
    private boolean usedBoost;
    private static SpriteSheet texture;

    /**
     * Constructor for Blue Raspberry companion
     *
     * @param x x-position for the Blue Raspberry companion
     * @param y y-position for the Blue Raspberry companion
     */
    public BlueRaspberry(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.BLUE_RASPBERRY);

        setOriginalCost(COST);
        setCost(COST);
        setCooldown(COOLDOWN);

        animationSpeed = ANIMATION_SPEED;
        usedBoost = false;

        setSpriteSheet(texture);
    }

    /**
     * Loads BlueRaspberry-specific constants from JSON
     */
    public static void setConstants(JsonValue constants) {
        COST = constants.getInt("cost", 1);
        COOLDOWN = constants.getInt("cooldown", 7);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.25f);
        BOOST = constants.getFloat("boost", 25f);
    }

    /**
     * Sets BlueRaspberry assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("BLUE_RASPBERRY.animation", SpriteSheet.class);
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
