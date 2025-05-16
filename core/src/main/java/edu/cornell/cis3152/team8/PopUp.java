package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import java.lang.reflect.Method;

public abstract class PopUp {

    protected GDXRoot game;
    protected GameAudio audio;
    protected Stage stage;
    protected GameScene gameScene;
    protected Texture dim;
    protected SpriteSheet background;
    protected float animationSpeed;
    protected float animationFrame;
    protected final Affine2 transform;

    protected final float screenWidth = 1280;
    protected final float screenHeight = 720;
    protected Array<Actor> buttons;


    public PopUp(GDXRoot game, GameScene gameScene, SpriteSheet background) {
        this.game = game;
        audio = game.audio;
        stage = new Stage(game.viewport, game.batch);
        this.gameScene = gameScene;
        dim = game.directory.getEntry("dim", Texture.class);
        this.background = background;
        animationFrame = 0;
        transform = new Affine2();
    }

    public void update(boolean popUpOn) {
        if (popUpOn) {
            if (background != null) {
                animationFrame += animationSpeed;
                if (animationFrame >= background.getSize()) {
                    animationFrame -= background.getSize();
                }
            }
        }
        if (gameScene.getSettingsOn()) {
            for (Actor button : buttons) {
                ((Button) button).setDisabled(false);
            }
        } else {
            for (Actor button : buttons) {
                ((Button) button).setDisabled(!popUpOn);
            }
        }
    }

    public void draw() {
        stage.act();
        background.setFrame((int) animationFrame);
        SpriteBatch.computeTransform(transform, background.getRegionWidth() / 2f,
            background.getRegionHeight() / 2f, screenWidth / 2f, screenHeight / 2f, 0, 1f, 1f);
        game.batch.draw(dim, 0, 0);
        game.batch.draw(background, transform);
        game.batch.end();
        stage.draw();
        game.batch.begin();
    }

    protected void addButton(Button b) {
        stage.addActor(b);
    }

    protected Array<Actor> getButtons() {
        return stage.getActors();
    }

    public Stage getStage() {
        return stage;
    }

}
