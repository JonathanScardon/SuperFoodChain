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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.*;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;

public class MainMenuScene implements Screen {

    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private final Stage stage;
    private GameAudio audio;
    private Texture background;
    private static Settings settingsScreen;
    private boolean settingsOn;


    public MainMenuScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        stage = new Stage(game.viewport, game.batch);
        background = assets.getEntry("menuBackground", Texture.class);

        Skin s = new Skin(Gdx.files.internal("buttons/BigButton.json"));

        float buttonWidth = 429;
        float buttonHeight = 100;
        float x = 806;
        float y = 320;

        TextButton button = new TextButton("Play", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        stage.addActor(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                dispose();
                game.exitScreen(game.getScreen(), ExitCode.LEVELS);
            }
        });

        y -= 100;

        button = new TextButton("Settings", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        stage.addActor(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                settingsScreen.setOn(true);
                settingsOn = true;
            }
        });

        y -= 100;

        button = new TextButton("Exit", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        stage.addActor(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                dispose();
                game.exitScreen(game.getScreen(), ExitCode.EXIT_GAME);
            }
        });

        settingsScreen = game.settings;
        audio = game.audio;
    }

    public void update(float delta) {
        stage.act();
        if (!settingsOn) {
            Gdx.input.setInputProcessor(stage);
        }
        settingsScreen.update(delta);
        settingsOn = settingsScreen.isOn();
        for (Actor b : stage.getActors()) {
            ((TextButton) b).setDisabled(settingsOn);
        }
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.batch.end();
        stage.draw();

        if (settingsOn) {
            settingsScreen.draw();
        }
    }

    public void reset() {
        settingsScreen.setOn(false);
        settingsOn = false;
        game.viewport.getCamera().position.set(1280 / 2f, 720 / 2f, 0);
        game.viewport.getCamera().update();
        audio.play("menu");
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

    public Stage getStage() {
        return stage;
    }
}
