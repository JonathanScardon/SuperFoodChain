package edu.cornell.cis3152.team8;
/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem. If you have a lot of sound or
 * images, it can take a long time to decompress them and load them into memory.
 * If you just have code at the start to load all your assets, your game will
 * look like it is hung at the start.
 *
 * The alternative is asynchronous asset loading. In asynchronous loading, you
 * load a little bit of the assets at a time, but still animate the game while
 * you are loading. This way the player knows the game is not hung, even though
 * he or she cannot do anything until loading is complete. You know those
 * loading screens with the inane tips that want to be helpful? That is
 * asynchronous loading.
 *
 * This player mode provides a basic loading screen. While you could adapt it
 * for between level loading, it is currently designed for loading all assets
 * at the start of the game.
 *
 * @author: Walker M. White
 * @date: 11/21/2024
 */

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.math.PathExtruder;
import edu.cornell.gdiac.math.Poly2;
import edu.cornell.gdiac.util.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;

import javax.swing.Box;

/**
 * Class that provides a loading screen for the state of the game.
 * <p>
 * This is a fairly generic loading screen that shows the GDIAC logo and a progress bar. Once all
 * assets are loaded, the progress bar is replaced by a play button. You are free to adopt this to
 * your needs.
 */
public class LoadingScene implements Screen {

    /**
     * Default budget for asset loader (do nothing but load 60 fps)
     */
    private static int DEFAULT_BUDGET = 15;

    // There are TWO asset managers.
    // One to load the loading screen. The other to load the assets
    /**
     * Internal assets for this loading screen
     */
    private AssetDirectory internal;
    /**
     * The actual assets to be loaded
     */
    private AssetDirectory assets;

    /**
     * The drawing camera for this scene
     */
    private OrthographicCamera camera;
    /**
     * Reference to sprite batch created by the root
     */
    private SpriteBatch batch;
    /**
     * Affine transform for displaying images
     */
    private Affine2 affine;
    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;

    /**
     * The width of this scene
     */
    private int width;
    /**
     * The height of this scene
     */
    private int height;

    /**
     * The constants for arranging images on the screen
     */
    JsonValue constants;

    /**
     * Scaling factor for when the student changes the resolution.
     */
    private float scale;
    /**
     * Current progress (0 to 1) of the asset manager
     */
    private float progress;
    /**
     * The amount of time to devote to loading assets (as opposed to on screen hints, etc.)
     */
    private int budget;

    /**
     * Whether this player mode is still active
     */
    private boolean active;

    /**
     * Returns the budget for the asset loader.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation frame. This
     * allows you to do something other than load assets. An animation frame is ~16 milliseconds. So
     * if the budget is 10, you have 6 milliseconds to do something else. This is how game companies
     * animate their loading screens.
     *
     * @return the budget in milliseconds
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Sets the budget for the asset loader.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation frame. This
     * allows you to do something other than load assets. An animation frame is ~16 milliseconds. So
     * if the budget is 10, you have 6 milliseconds to do something else. This is how game companies
     * animate their loading screens.
     *
     * @param millis the budget in milliseconds
     */
    public void setBudget(int millis) {
        budget = millis;
    }

    /**
     * Returns true if all assets are loaded
     *
     * @return true if all assets are loadedl
     */
    public boolean isReady() {
        return progress >= 1.0f;
    }

    /**
     * Returns the asset directory produced by this loading screen
     * <p>
     * This asset loader is NOT owned by this loading scene, so it persists even after the scene is
     * disposed. It is your responsbility to unload the assets in this directory.
     *
     * @return the asset directory produced by this loading screen
     */
    public AssetDirectory getAssets() {
        return assets;
    }

    /**
     * Creates a LoadingMode with the default budget, size and position.
     *
     * @param file  The asset directory to load in the background
     * @param batch The sprite batch to draw to
     */
    public LoadingScene(String file, SpriteBatch batch) {
        this(file, batch, DEFAULT_BUDGET);
    }

    /**
     * Creates a LoadingMode with the default size and position.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation frame. This
     * allows you to do something other than load assets. An animation frame is ~16 milliseconds. So
     * if the budget is 10, you have 6 milliseconds to do something else. This is how game companies
     * animate their loading screens.
     *
     * @param file   The asset directory to load in the background
     * @param batch  The game canvas to draw to
     * @param millis The loading budget in milliseconds
     */
    public LoadingScene(String file, SpriteBatch batch, int millis) {
        this.batch = batch;
        budget = millis;

        // Load files for loading screen immediately
        internal = new AssetDirectory("loading/boot.json");
        internal.loadAssets();
        internal.finishLoading();

        constants = internal.getEntry("constants", JsonValue.class);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // No progress so far
        progress = 0;

        affine = new Affine2();

        // Start loading the REAL assets
        assets = new AssetDirectory(file);
        assets.loadAssets();
        active = true;
    }

    /**
     * Called when this screen should release all resources.
     */
    public void dispose() {
        internal.unloadAssets();
        internal.dispose();
    }

    /**
     * Updates the status of this scene
     * <p>
     * We prefer to separate update and draw from one another as separate methods, instead of using
     * the single render() method that LibGDX does. We will talk about why we prefer this in
     * lecture.
     *
     * @param delta Number of seconds since last animation frame
     */
    private void update(float delta) {
        if (progress < 1.0f) {
            assets.update(budget);
            this.progress = assets.getProgress();
            if (progress >= 1.0f) {
                this.progress = 1.0f;
            }
        }
    }

    /**
     * Draws the status of this player mode.
     * <p>
     * We prefer to separate update and draw from one another as separate methods, instead of using
     * the single render() method that LibGDX does. We will talk about why we prefer this in
     * lecture.
     */
    private void draw() {
        // Cornell colors
        ScreenUtils.clear(173, 153, 191, 1.0f);

        batch.begin(camera);
        batch.setColor(173, 153, 191, 1.0f);

        // Height lock the logo
        Texture texture = internal.getEntry("splash", Texture.class);

        batch.draw(texture, 0, 0, width, height);

        if (progress < 1.0f) {
            drawProgress();
        }
        batch.end();
    }

    /**
     * Updates the progress bar according to loading progress
     * <p>
     * The progress bar is composed of parts: two rounded caps on the end, and a rectangle in a
     * middle. We adjust the size of the rectangle in the middle to represent the amount of
     * progress.
     */
    private void drawProgress() {

        float cx = width / 2f;
        float cy = (int) (constants.getFloat("bar.height") * height);
        TextureRegion region1, region2, region3;

        // "3-patch" the background
        batch.setColor(Color.WHITE);

        region1 = internal.getEntry("progress.background", TextureRegion.class);
        float w = region1.getRegionWidth();
        batch.draw(region1, cx - w / 2f, cy);

        // "3-patch" the foreground
        region1 = internal.getEntry("progress.foreleft", TextureRegion.class);
        batch.draw(region1, cx - w / 2, cy, region1.getRegionWidth(),
            region1.getRegionHeight());

        if (progress > 0) {
            region2 = internal.getEntry("progress.foreright", TextureRegion.class);
            float span =
                progress * (w - (region1.getRegionWidth() + region2.getRegionWidth()));

            batch.draw(region2, cx - w / 2 + region1.getRegionWidth() + span, cy,
                region2.getRegionWidth(), region2.getRegionHeight());

            region3 = internal.getEntry("progress.foreground", TextureRegion.class);
            batch.draw(region3, cx - w / 2 + region1.getRegionWidth(), cy,
                span, region3.getRegionHeight());
        } else {
            region2 = internal.getEntry("progress.foreright", TextureRegion.class);
            batch.draw(region2, cx - w / 2 + region1.getRegionWidth(), cy,
                region2.getRegionWidth(), region2.getRegionHeight());
        }

    }

    // ADDITIONAL SCREEN METHODS

    /**
     * Called when the Screen should render itself.
     * <p>
     * We defer to the other methods update() and draw(). However, it is VERY important that we only
     * quit AFTER a draw.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void render(float delta) {
        if (active) {
            update(delta);
            draw();

            // We are ready, notify our listener
            if (isReady() && listener != null) {
                listener.exitScreen(this, 0);
            }
        }
    }

    /**
     * Called when the Screen is resized.
     * <p>
     * This can happen at any point during a non-paused state but will never happen before a call to
     * show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        // Compute the drawing scale
        scale = ((float) height) / constants.getFloat("height");

        this.width = width;
        this.height = height;
        if (camera == null) {
            camera = new OrthographicCamera(width, height);
        } else {
            camera.setToOrtho(false, width, height);
        }
    }

    /**
     * Called when the Screen is paused.
     * <p>
     * This is usually when it's not active or visible on screen. An Application is also paused
     * before it is destroyed.
     */
    public void pause() {
        // TODO Auto-generated method stub
    }

    /**
     * Called when the Screen is resumed from a paused state.
     * <p>
     * This is usually when it regains focus.
     */
    public void resume() {
        // TODO Auto-generated method stub
    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    public void show() {
        // Useless if called in outside animation loop
        active = true;
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        // Useless if called in outside animation loop
        active = false;
    }

    /**
     * Sets the ScreenListener for this mode
     * <p>
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
}

