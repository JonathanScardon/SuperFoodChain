package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Settings {

    private static Texture background;
    private static Texture dim;
    private Affine2 transform;

    private static GameAudio audio;
    public static Slider musicSlider;
    public static Slider sfxSlider;
    private static Button musicButton;
    private static Button sfxButton;
    private static Button saveResetButton;

    private Viewport viewport;
    private SpriteBatch batch;
    private static Stage stage;
    private float soundWait;


    public Settings(GDXRoot game) {
        transform = new Affine2();
        batch = game.batch;
        viewport = game.viewport;
        audio = game.audio;
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

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

        soundWait = 1f;
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

        Texture button = assets.getEntry("music", Texture.class);
        Texture buttonHover = assets.getEntry("sfxHover", Texture.class);
        musicButton = new Button(x, y + sliderHeight + padding, button, buttonHover, 0, buttonSize,
            buttonSize);
        button = assets.getEntry("sfx", Texture.class);
        buttonHover = assets.getEntry("musicHover", Texture.class);
        sfxButton = new Button(x, y, button, buttonHover, 0, buttonSize, buttonSize);
        button = assets.getEntry("button", Texture.class);
        buttonHover = assets.getEntry("buttonHover", Texture.class);
        float buttonWidth = 250;
        float buttonHeight = 70;
        saveResetButton = new Button(1280 / 2f - buttonWidth / 2f, y - padding * 1.5f, button,
            buttonHover,
            -10, buttonWidth,
            buttonHeight, "Reset Save");
    }

    public void draw(SpriteBatch batch, float page) {
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
        float padding = 50;
        x = 1280 / 2f - sliderWidth / 2f + padding;
        float y = 720f / 2f - padding;
        musicSlider.setPosition(x + move, y + sliderHeight + padding);
        sfxSlider.setPosition(x + move, y);
        musicButton.draw(batch, true);
        sfxButton.draw(batch, true);
        saveResetButton.draw(batch, true);
        batch.end();
        stage.draw();
        batch.begin();
    }

    public void update(float delta, boolean settingsOn) {
        if (settingsOn) {
            musicSlider.setDisabled(false);
            sfxSlider.setDisabled(false);
            musicButton.update(delta);
            sfxButton.update(delta);
            saveResetButton.update(delta);
            soundWait -= delta;
            if (audio.getMusicVolume() == 0) {
                musicButton.setExitCode(100);
            } else {
                musicButton.setExitCode(0);
            }

            if (audio.getSfxVolume() == 0) {
                sfxButton.setExitCode(100);
            } else {
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
        }

    }
}
