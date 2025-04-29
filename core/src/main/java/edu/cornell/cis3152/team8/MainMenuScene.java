package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.*;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;

public class MainMenuScene implements Screen {

    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private Texture background;
    private Button playButton;
    private Button settingsButton;
    private Button exitButton;
    private Settings settingsScreen;
    private boolean settingsOn;
    private GameAudio audio;

    public MainMenuScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        BitmapFont font = assets.getEntry("lpcBig", BitmapFont.class);
        background = new Texture("images/Menu.png");
        Texture button = new Texture("images/Button.png");
        Texture buttonDark = new Texture("images/ButtonDark.png");

        playButton = new Button(806, 320, button, buttonDark, 0, 429, 100, "Play", font);
        settingsButton = new Button(806, 220, button, buttonDark, 0, 429, 100, "Settings", font);
        exitButton = new Button(806, 120, button, buttonDark, 0, 429, 100, "Exit", font);
        settingsScreen = new Settings();

        audio = new GameAudio(assets);
    }

    public void update(float delta) {
        playButton.update(delta);
        settingsButton.update(delta);
        exitButton.update(delta);
        if (!settingsOn) {
            if (playButton.isPressed()) {
                audio.play("click");
                game.exitScreen(this, 0);
                dispose();
            }
            if (settingsButton.isPressed()) {
                audio.play("click");
                settingsOn = true;
                settingsScreen.update();
            }
            if (exitButton.isPressed()) {
                audio.play("click");
                game.exitScreen(this, 1);
                dispose();
            }
        } else if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            settingsOn = false;
        }
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        game.batch.draw(background, 0, 0);
        playButton.draw(game.batch, !settingsOn);
        settingsButton.draw(game.batch, !settingsOn);
        exitButton.draw(game.batch, !settingsOn);

        if (settingsOn) {
            settingsScreen.draw(game.batch, 1);
        }

        game.batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.update(delta);
        this.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
