package edu.cornell.cis3152.team8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.util.ScreenListener;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GDXRoot extends Game implements ScreenListener {

    private SpriteBatch batch;

    private LoadingScene loading;
    private GameplayController gameScene;
    private MainMenuScene menuScene ;

    AssetDirectory directory;
    @Override
    public void create() {
        batch = new SpriteBatch();
        loading = new LoadingScene("assets.json", batch,1);
        loading.setScreenListener(this);
        setScreen(loading);
    }

    @Override
    public void dispose() {
        // Call dispose on our children
        setScreen(null);
        if (loading != null) {
            loading.dispose();
            loading = null;
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
        if (screen == loading) {
            directory = loading.getAssets();
            loading.dispose();
            loading = null;

            gameScene = new GameplayController(batch);
            gameScene.setScreenListener(this);
            setScreen(gameScene);
//            menuScene = new MainMenuScene(batch);
//            menuScene.setScreenListener(this);
//            setScreen(menuScene);
        } else if (screen == menuScene) {
            gameScene = new GameplayController(batch);
            gameScene.setScreenListener(this);
            setScreen(gameScene);
        } else
            Gdx.app.exit();
        }

    }

