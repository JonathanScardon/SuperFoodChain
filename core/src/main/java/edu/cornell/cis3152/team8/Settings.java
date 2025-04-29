package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Settings {

    private Texture background;
    private Texture dim;

    public Settings() {
        background = new Texture("images/SettingsPage.png");
        dim = new Texture("images/dim.png");
    }

    public void draw(SpriteBatch batch, float page) {
        float move = 1280 * (page - 1);
        batch.draw(dim, move, 0);
        batch.draw(background, 1280 * (page - 1), 0);
        Texture bar = new Texture("images/VolumeBar.png");
        batch.draw(bar, move + 430, 405);
        batch.draw(bar, move + 430, 203);
    }

    public void update() {

    }
}
