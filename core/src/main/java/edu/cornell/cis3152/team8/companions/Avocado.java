package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Player;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Avocado extends Companion {
    Texture texture;
    float dx = 0.0f;
    float dy = 0.0f;

    /**
     * Constructor for
     *
     * @param x x-position for the Avocado companion
     * @param y y-position for the Avocado companion
     */
    public Avocado(float x, float y, int id, World world) {
        super(x, y, id, world);
        setCompanionType(CompanionType.AVOCADO);
        setCost(0);
        // need to think about how CD will work with support characters
        setCooldown(7);
//        radius = 1;
        texture = new Texture("images/Avocado.png");
        SpriteSheet avocado = new SpriteSheet(texture, 1, 7);
        setSpriteSheet(avocado);
        animationSpeed = 0.25f;
//        size = 0.4f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void useAbility(GameState state) {
        Player player = state.getPlayer();
        for (Companion c : player.getCompanions()) {
            if (c == this) { // if check for itself in the chain
                continue; // you don't want to reduce your own ability cooldown
            }
            if (!c.canUse()) { // only reduce cooldowns for companions that have abilities on cooldown
                c.coolDown(true, c.getActiveCooldown() / 2); // will reduce the ACTIVE cooldown value
            }
        }
    }
}
