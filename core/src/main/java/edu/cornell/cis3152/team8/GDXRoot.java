package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.util.ScreenListener;


public class GDXRoot extends Game implements ScreenListener {

    /** AssetManager to load game assets */
    AssetDirectory directory;

    public FitViewport viewport;
    public SpriteBatch batch;
    public BitmapFont font;

    // The screens in the game
    private LoadingScene loadingScene;
    private GameScene gameScene;
    private MainMenuScene menuScene;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());


        menuScene = new MainMenuScene(this);
        this.setScreen(menuScene);
//        loadingScene = new LoadingScene("assets.json", batch, 1);
//        loadingScene.setScreenListener(this);
//        this.setScreen(loadingScene);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        // Call dispose on our children
        setScreen(null);
        if (loadingScene != null) {
            loadingScene.dispose();
            loadingScene = null;
        }

        if (menuScene != null) {
                menuScene.dispose();
                menuScene = null;
            }

        if (gameScene != null){
            gameScene.dispose();
            gameScene = null;
        }

        batch.dispose();
        font.dispose();
        batch = null;

        // Unload all of the resources
        if (directory != null) {
            directory.unloadAssets();
            directory.dispose();
            directory = null;
        }
        super.dispose();
    }

    @Override
    public void exitScreen(Screen screen, int exitCode) {


        if (screen == menuScene) {
            loadingScene = new LoadingScene("assets.json", batch, 1);
            loadingScene.setScreenListener(this);
            this.setScreen(loadingScene);
        } else if (screen == loadingScene) {
            directory = loadingScene.getAssets();
            loadingScene.dispose();
            loadingScene = null;

            menuScene = new MainMenuScene(this);
            setScreen(menuScene);
        } else if (screen == menuScene) {
            gameScene = new GameScene(this, directory);
            setScreen(gameScene);
        } else {
            Gdx.app.exit();
        }
    }
}

