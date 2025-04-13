package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.cis3152.team8.Companion;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Avocado extends Companion {
    Texture texture;
    float dx = 0.0f;
    float dy = 0.0f;

    /**
     * Constructor for
     * @param x x-position for the Avocado companion
     * @param y y-position for the Avocado companion
     */
    public Avocado(float x, float y) {
        super(x,y);
        setCompanionType(CompanionType.AVOCADO);
        setCost(2);
        // need to think about how CD will work with support characters
        setCooldown(7);
        radius = 1;
        texture = new Texture("images/Avocado.PNG");
        SpriteSheet avocado = new SpriteSheet(texture, 1, 7);
        setSpriteSheet(avocado);
        animationSpeed = 0.25f;
        size = 0.4f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void useAbility(GameState state) {
        
    }
}
