package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Avocado extends Companion {

    private static int COST;
    private static int COOLDOWN;
    private static float ANIMATION_SPEED;
    private static float COOLDOWN_REDUCTION_RATE;
    private static SpriteSheet texture;


    public Avocado(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.AVOCADO);

        setOriginalCost(COST);
        setCost(COST);
        setCooldown(COOLDOWN);

        animationSpeed = ANIMATION_SPEED;

        setSpriteSheet(texture);
    }

    /**
     * Loads Avocado-specific constants from JSON
     */
    public static void setConstants(JsonValue constants) {
        COST = constants.getInt("cost", 0);
        COOLDOWN = constants.getInt("cooldown", 7);
        ANIMATION_SPEED = constants.getFloat("animationSpeed", 0.25f);
        COOLDOWN_REDUCTION_RATE = constants.getFloat("cooldownReductionRate", 5.0f);
    }

    /**
     * Sets Avocado assets
     */
    public static void setAssets(AssetDirectory assets) {
        texture = assets.getEntry("AVOCADO.animation", SpriteSheet.class);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void useAbility(GameState state) {
        Player player = state.getPlayer();
        float delta = Gdx.graphics.getDeltaTime();
        for (Companion c : player.getCompanions()) {
            if (c == this) {
                continue; // Don't reduce own cooldown
            }
            if (!c.canUse()) {
                c.coolDown(true, delta / COOLDOWN_REDUCTION_RATE);
            }
        }
    }
}
