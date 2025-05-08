package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Settings {

    //Gdx root things
    private static GDXRoot game;
    private static GameAudio audio;
    private static Stage stage; //For sliders


    //Visual
    private static Texture background;
    private static Texture dim;
    private final Affine2 transform;
    private final SpriteBatch batch;
    private boolean resetSaveDelay; //Has the resetSave button been pressed
    private float alpha; //For save reset fade-out

    //Buttons/Sliders
    public static Slider musicSlider;
    public static Slider sfxSlider;
    private static Button musicButton;
    private static Button sfxButton;
    private static Button xButton;
    private static Button saveResetButton;
    private static Texture music;
    private static Texture musicHover;
    private static Texture sfx;
    private static Texture sfxHover;
    private static Texture musicMute;
    private static Texture musicHoverMute;
    private static Texture sfxMute;
    private static Texture sfxHoverMute;

    private float soundWait; //For sfx test sounds to delay

    /**
     * Pause before allowing use
     */
    private final float waitTime;
    private float currWait;


    public Settings(GDXRoot game) {
        Settings.game = game;
        audio = game.audio;
        batch = game.batch;
        stage = new Stage(game.viewport, batch);
        Gdx.input.setInputProcessor(stage);

        transform = new Affine2();

        //Constants for centering
        float sliderWidth = 313;
        float sliderHeight = 33;
        float padding = 50;
        float x = 1280 / 2f - sliderWidth / 2f + padding;
        float y = 720f / 2f - padding;
        //Sliders
        Skin s = new Skin(Gdx.files.internal("volumeBar/VolumeBarSkin.json"));
        musicSlider = new Slider(0, 100, 1, false, s);
        musicSlider.setPosition(x,
            y + sliderHeight + padding);
        musicSlider.setWidth(sliderWidth);
        musicSlider.setHeight(sliderHeight);
        sfxSlider = new Slider(0, 100, 1, false, s);
        sfxSlider.setPosition(x, y);
        sfxSlider.setWidth(sliderWidth);
        sfxSlider.setHeight(sliderHeight);
        musicSlider.setValue(100);
        sfxSlider.setValue(100);
        stage.addActor(musicSlider);
        stage.addActor(sfxSlider);

        waitTime = 1f;
        currWait = waitTime;
        soundWait = 1f;

        alpha = 0f;
        resetSaveDelay = false;
    }

    public static void setAssets(AssetDirectory assets) {
        background = assets.getEntry("settingsBackground", Texture.class);
        dim = assets.getEntry("dim", Texture.class);

        float sliderWidth = 313;
        float sliderHeight = 33;
        float padding = 50;
        float buttonSize = 78;
        float x = 1280 / 2f - sliderWidth / 2f - buttonSize;
        float y = (720f / 2f - padding + sliderHeight / 2f) - buttonSize / 2f;
        music = assets.getEntry("music", Texture.class);
        musicHover = assets.getEntry("musicHover", Texture.class);
        sfx = assets.getEntry("sfx", Texture.class);
        sfxHover = assets.getEntry("sfxHover", Texture.class);
        musicMute = assets.getEntry("musicMute", Texture.class);
        musicHoverMute = assets.getEntry("musicHoverMute", Texture.class);
        sfxMute = assets.getEntry("sfxMute", Texture.class);
        sfxHoverMute = assets.getEntry("sfxHoverMute", Texture.class);

        musicButton = new Button(x, y + sliderHeight + padding, music, musicHover, 0, buttonSize,
            buttonSize);
        sfxButton = new Button(x, y, sfx, sfxHover, 0, buttonSize, buttonSize);

        Texture button = assets.getEntry("x", Texture.class);
        Texture buttonHover = assets.getEntry("xHover", Texture.class);
        xButton = new Button(1280 / 2f - background.getWidth() / 2f + 10,
            720 / 2f + background.getHeight() / 2f - buttonSize - 10, button, buttonHover, -1,
            buttonSize, buttonSize);

        button = assets.getEntry("button", Texture.class);
        buttonHover = assets.getEntry("buttonHover", Texture.class);
        float buttonWidth = 250;
        float buttonHeight = 70;
        saveResetButton = new Button(1280 / 2f - buttonWidth / 2f, y - padding * 2f, button,
            buttonHover,
            -100, buttonWidth,
            buttonHeight, "Reset Save");
    }

    public void draw(float page) {
        float move = 1280 * (page - 1);
        SpriteBatch.computeTransform(transform, background.getWidth() / 2f,
            background.getHeight() / 2f, 1280 / 2f + move, 720 / 2f, 0, 1f, 1f);

        batch.draw(dim, move, 0);
        batch.draw(background, transform);
        float sliderWidth = 313;
        float sliderHeight = 33;
        float buttonSize = 78;
        float buttonWidth = 250;
        float x = 1280 / 2f - sliderWidth / 2f - buttonSize;
        musicButton.setPosition(x + move, musicButton.posY);
        sfxButton.setPosition(x + move, sfxButton.posY);
        saveResetButton.setPosition(1280 / 2f + move - buttonWidth / 2f, saveResetButton.posY);
        xButton.setPosition(1280 / 2f - background.getWidth() / 2f + 10 + move, xButton.posY);
        float padding = 50;
        x = 1280 / 2f - sliderWidth / 2f + padding;
        float y = 720f / 2f - padding;
        musicSlider.setPosition(x + move, y + sliderHeight + padding);
        sfxSlider.setPosition(x + move, y);
        musicButton.draw(batch, true);
        sfxButton.draw(batch, true);
        xButton.draw(batch, true);
        saveResetButton.draw(batch, true);
        batch.end();
        stage.draw();
        batch.begin();
        if (resetSaveDelay) {
            batch.setColor(0, 0, 0, alpha);
            batch.fill(move - 500, -500, 1280 * 2, 720 * 2);
            batch.setColor(Color.WHITE);
        }
    }

    public boolean update(float delta, boolean settingsOn) {
        if (currWait > 0.0f && settingsOn) { //Wait
            currWait -= delta;
        } else if (settingsOn) {
            if (resetSaveDelay) {
                alpha += 0.01f;//fade out
                if (alpha >= 2) {
                    resetSaveDelay = false;
                    alpha = 0.0f;
                    game.save.putInteger("unlockedLevels", 1);
                    game.getScreen().dispose();
                    game.exitScreen(game.getScreen(), saveResetButton.getExitCode());
                }
            }
            musicSlider.setDisabled(false);
            sfxSlider.setDisabled(false);
            musicButton.update(delta);
            sfxButton.update(delta);
            xButton.update(delta);
            saveResetButton.update(delta);
            soundWait -= delta;
            if (audio.getMusicVolume() == 0) {
                musicButton.setTexture(musicMute, musicHoverMute);
                musicButton.setExitCode(100);
            } else {
                musicButton.setTexture(music, musicHover);
                musicButton.setExitCode(0);
            }

            if (audio.getSfxVolume() == 0) {
                sfxButton.setTexture(sfxMute, sfxHoverMute);
                sfxButton.setExitCode(100);
            } else {
                sfxButton.setTexture(sfx, sfxHover);
                sfxButton.setExitCode(0);
            }

            if (musicButton.isPressed()) {
                audio.play("click");
                musicSlider.setValue(musicButton.getExitCode());
            }
            if (sfxButton.isPressed()) {
                audio.play("click");
                sfxSlider.setValue(sfxButton.getExitCode());
            }
            if (xButton.isPressed()) {
                audio.play("click");
                return false;
            }

            if (saveResetButton.isPressed() && !resetSaveDelay) {
                audio.play("click");
                resetSaveDelay = true;
                audio.stopMusic();
            }

            if (sfxSlider.isDragging()) {
                if (soundWait <= 0) {
                    audio.play("DURIAN");
                    soundWait = 1f;
                }
            }
            audio.setMusicVolume(musicSlider.getValue() / 100f);
            audio.setSfxVolume(sfxSlider.getValue() / 100f);
        } else {
            musicSlider.setDisabled(true);
            sfxSlider.setDisabled(true);
            currWait = waitTime;
        }
        return settingsOn;
    }
}
