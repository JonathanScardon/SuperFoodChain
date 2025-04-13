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
    private LevelSelect levelSelectScene;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        loadingScene = new LoadingScene("assets.json", batch, 1);
        loadingScene.setScreenListener(this);
        menuScene = new MainMenuScene(this);
        levelSelectScene = new LevelSelect(this);

        this.setScreen(loadingScene);
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
        // loading -> menu -> level select -> game
        if (screen == loadingScene) {
            // we know that we're done loading here, so we can get the assets
            directory = loadingScene.getAssets();
            LevelLoader.getInstance().setAssetDirectory(directory);

            setScreen(menuScene);
            loadingScene.dispose();
            loadingScene = null;

            menuScene = new MainMenuScene(this);
            setScreen(menuScene);
        }else if (screen == menuScene){
            menuScene.dispose();
            if (exitCode == 0) {
                levelSelectScene = new LevelSelect(this);
                this.setScreen(levelSelectScene);
            } else {
                Gdx.app.exit();
            }

            setScreen(levelSelectScene);
        } else if (screen == levelSelectScene) {
            levelSelectScene.dispose();
                gameScene = new GameScene(this, directory, exitCode);
            //LevelLoader.apply(gameScene, directory.getEntry("level1", JsonValue.class));
            this.setScreen(gameScene);
        } else if (screen == gameScene) {
            levelSelectScene.resetWait();
            setScreen(levelSelectScene);

        // } else if (screen == menuScene) {
        //     if (exitCode == 0) {
        //         this.setScreen(levelSelectScene);
        //     } else {
        //         Gdx.app.exit();
        //     }
        // } else if (screen == levelSelectScene) {
        //     gameScene = new GameScene(this, directory);
        //     this.setScreen(gameScene);
        // } else if (screen == gameScene) {
        //     this.setScreen(levelSelectScene);
        } else {
            Gdx.app.exit();
        }
    }
}

