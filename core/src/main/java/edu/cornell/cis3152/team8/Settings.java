package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;

public class Settings {

    //Gdx root things
    private static GDXRoot game;
    private static GameAudio audio;
    private static Stage stage; //For sliders
    private final Camera camera;


    //Visual
    private static Texture background;
    private static Texture dim;
    private static Texture black;
    private final Affine2 transform;
    private final SpriteBatch batch;
    private boolean resetSaveDelay; //Has the resetSave button been pressed
    private float alpha; //For save reset fade-out

    //Buttons/Sliders
    public static Slider musicSlider;
    public static Slider sfxSlider;
    public ImageButton[] buttons;

    private float soundWait; //For sfx test sounds to delay

    /**
     * Pause before allowing use
     */
    private final float waitTime;
    private float currWait;

    private boolean settingsOn;


    public Settings(GDXRoot game, AssetDirectory assets) {
        Settings.game = game;
        settingsOn = false;
        audio = game.audio;
        batch = game.batch;
        camera = game.viewport.getCamera();
        stage = new Stage(game.viewport, batch);
        Gdx.input.setInputProcessor(stage);

        background = assets.getEntry("settingsBackground", Texture.class);
        dim = assets.getEntry("dim", Texture.class);
        black = assets.getEntry("black", Texture.class);

        transform = new Affine2();

        buttons = new ImageButton[6];

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
        setButtons();

        waitTime = 1f;
        currWait = waitTime;
        soundWait = 1f;

        alpha = 0f;
        resetSaveDelay = false;
    }

    public void setButtons() {
        float sliderWidth = 313;
        float sliderHeight = 33;
        float padding = 50;
        float buttonWidth = 80;
        float buttonHeight = 88;
        float x = 1280 / 2f - sliderWidth / 2f - buttonWidth;
        float y = (720f / 2f - padding + sliderHeight / 2f) - buttonHeight / 2f;

        Skin s = new Skin(Gdx.files.internal("buttons/Music.json"));
        ImageButton button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y + sliderHeight + padding);
        stage.addActor(button);
        buttons[0] = button;

        s = new Skin(Gdx.files.internal("buttons/Sfx.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        stage.addActor(button);
        buttons[1] = button;

        s = new Skin(Gdx.files.internal("buttons/MusicMute.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y + sliderHeight + padding);
        stage.addActor(button);
        button.setVisible(false);
        buttons[2] = button;

        s = new Skin(Gdx.files.internal("buttons/SfxMute.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        stage.addActor(button);
        button.setVisible(false);
        buttons[3] = button;

        s = new Skin(Gdx.files.internal("buttons/X.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(1280 / 2f - background.getWidth() / 2f + 10,
            720 / 2f + background.getHeight() / 2f - buttonHeight - 10);
        stage.addActor(button);
        buttons[4] = button;
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                settingsOn = false;
            }
        });

        buttonWidth = 250;
        buttonHeight = 70;
        s = new Skin(Gdx.files.internal("buttons/Button.json"));
        TextButton reset = new TextButton("Reset Save", s);
        reset.setSize(buttonWidth, buttonHeight);
        reset.setPosition(1280 / 2f - buttonWidth / 2f, y - padding * 2f);
        stage.addActor(reset);
        reset.setVisible(true);
        buttons[5] = button;
        reset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                resetSaveDelay = true;
                game.batch.setProjectionMatrix(camera.combined);
                game.viewport.setCamera(camera);
                audio.stopMusic();
            }
        });

        buttons[0].addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                musicSlider.setValue(0);
                buttons[0].setVisible(false);
                buttons[2].setVisible(true);
            }
        });
        buttons[1].addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfxSlider.setValue(0);
                buttons[1].setVisible(false);
                buttons[3].setVisible(true);

            }
        });
        buttons[2].addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                musicSlider.setValue(100);
                buttons[0].setVisible(true);
                buttons[2].setVisible(false);
            }
        });
        buttons[3].addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfxSlider.setValue(100);
                audio.setSfxVolume(sfxSlider.getValue() / 100f);
                audio.play("click");
                buttons[1].setVisible(true);
                buttons[3].setVisible(false);
            }
        });


    }

    public void draw() {
        batch.begin();
        SpriteBatch.computeTransform(transform, background.getWidth() / 2f,
            background.getHeight() / 2f, 1280 / 2f, 720 / 2f, 0, 1f, 1f);
        batch.draw(dim, 0, 0);
        batch.draw(background, transform);
        batch.end();
        stage.draw();
        batch.begin();
        if (resetSaveDelay) {
            batch.setColor(0, 0, 0, alpha);
            batch.draw(black, 0, 0);
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

    public void update(float delta) {
        if (settingsOn) {
            Gdx.input.setInputProcessor(stage);
            stage.act();
            if (resetSaveDelay) {
                alpha += 0.01f;//fade out
                if (alpha >= 2) {
                    resetSaveDelay = false;
                    settingsOn = false;
                    game.resetSave();
                    camera.position.set(1280 / 2f, 720 / 2f, 0);
                    game.getScreen().dispose();
                    game.exitScreen(game.getScreen(), ExitCode.HOME);
                    alpha = 0.0f;
                }
            }
            soundWait -= delta;

            if (sfxSlider.isDragging()) {
                if (soundWait <= 0) {
                    audio.play("DURIAN");
                    soundWait = 1f;
                }
            }
            if (audio.getMusicVolume() == 0) {
                buttons[0].setVisible(false);
                buttons[2].setVisible(true);
            } else {
                buttons[0].setVisible(true);
                buttons[2].setVisible(false);
            }

            if (audio.getSfxVolume() == 0) {
                buttons[1].setVisible(false);
                buttons[3].setVisible(true);
            } else {
                buttons[1].setVisible(true);
                buttons[3].setVisible(false);
            }
            audio.setMusicVolume(musicSlider.getValue() / 100f);
            audio.setSfxVolume(sfxSlider.getValue() / 100f);
            if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
                settingsOn = false;
            }
        }
        musicSlider.setDisabled(!settingsOn);
        sfxSlider.setDisabled(!settingsOn);
        for (Button b : buttons) {
            b.setDisabled(!settingsOn);
        }
    }

    public boolean isOn() {
        return settingsOn;
    }

    public void setOn(boolean on) {
        settingsOn = on;
    }

}
