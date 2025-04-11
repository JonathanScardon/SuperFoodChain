package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.util.ScreenListener;


public class GDXRoot extends Game implements ScreenListener {

    /**
     * AssetManager to load game assets
     */
    AssetDirectory directory;

    public FitViewport viewport;
    public SpriteBatch batch;
    public BitmapFont font;

    // The screens in the game
    private LoadingScene loadingScene;
    private GameScene gameScene;
    private MainMenuScene menuScene;

    private LevelSelect levels;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        menuScene = new MainMenuScene(this);
        loadingScene = new LoadingScene("assets.json", batch, 1);
        loadingScene.setScreenListener(this);

        this.setScreen(menuScene);
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

        if (gameScene != null) {
            gameScene.dispose();
            gameScene = null;
        }

        batch.dispose();
        font.dispose();
        batch = null;

        // Unload all the resources
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
            if (exitCode == 0){
            loadingScene = new LoadingScene("assets.json", batch, 1);
            loadingScene.setScreenListener(this);
            this.setScreen(loadingScene);
            }else{
                Gdx.app.exit();
            }

        } else if (screen == loadingScene) {
            directory = loadingScene.getAssets();
            loadingScene.dispose();
            loadingScene = null;

            levels = new LevelSelect(this);
            setScreen(levels);
        } else if (screen == levels) {
            //levels.dispose();

            gameScene = new GameScene(this, directory, 0);
            //LevelLoader.apply(gameScene, directory.getEntry("level1", JsonValue.class));
            this.setScreen(gameScene);
        } else if (screen == gameScene) {
            setScreen(levels);
        } else {
            Gdx.app.exit();
        }
    }
}

