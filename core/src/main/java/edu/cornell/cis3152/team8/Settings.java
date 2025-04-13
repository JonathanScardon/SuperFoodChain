package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Settings {
        private Texture background;
    public Settings(){
        background = new Texture("images/SettingsPage.png");
    }

    public void draw(SpriteBatch batch){
        batch.draw(background,0,0);
        Texture bar = new Texture("images/VolumeBar.png");
        batch.draw(bar,430,405);
        batch.draw(bar,430,203);
    }
    public void update(){

    }
}
