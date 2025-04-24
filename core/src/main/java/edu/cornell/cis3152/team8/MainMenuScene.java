package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
    private SoundEffect click;
    private AudioSource backgroundMusic;

    private MusicQueue music;

    public MainMenuScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        background = new Texture("images/Menu.png");
        Texture play = new Texture("images/PlayButton.png");
        Texture settings = new Texture("images/SettingsButton.png");
        Texture exit = new Texture("images/ExitButton.png");
        playButton = new Button(833, 340, play, 0);
        settingsButton = new Button(833, 240, settings, 0);
        exitButton = new Button(833, 140, exit, 0);
        settingsScreen = new Settings();
        click = assets.getEntry("click", SoundEffect.class);
        //click = Gdx.audio.newSound();
        // System.out.println(click);
        backgroundMusic = assets.getEntry("dodge", AudioSource.class);
        //System.out.println(backgroundMusic);
//        AudioEngine engine = (AudioEngine) Gdx.audio;
//        music = engine.newMusicQueue(false, 44100);
//        music.addSource(backgroundMusic);
//        music.play();
    }

    public void update(float delta) {
        if (playButton.isHovering() && Gdx.input.isTouched()) {
            //click.play();
            game.exitScreen(this, 0);
            dispose();
        }
        if (settingsButton.isHovering() && Gdx.input.isTouched()) {
            //click.play();
            settingsOn = true;
            settingsScreen.update();
        }
        if (exitButton.isHovering() && Gdx.input.isTouched()) {
            //click.play();
            game.exitScreen(this, 1);
            dispose();
        }
        if (settingsOn && Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            //click.play();
            settingsOn = false;
        }
    }

    public void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        game.batch.draw(background, 0, 0);
        playButton.draw(game.batch);
        settingsButton.draw(game.batch);
        exitButton.draw(game.batch);

        if (settingsOn) {
            settingsScreen.draw(game.batch);
        }

        game.batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.update(delta);
        this.draw(delta);
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
