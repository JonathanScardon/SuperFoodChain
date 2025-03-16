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

    public MainMenuScene viewport;
    private SpriteBatch batch;
    private Texture image;

    private LoadingScene loading;
    private GameplayController gameScene;
    private MainMenuScene menuScene ;

    AssetDirectory directory;
    @Override
    public void create() {
        batch = new SpriteBatch();
        //image = new Texture("libgdx.png");
        loading = new LoadingScene("assets.json", batch,1);
        loading.setScreenListener(this);
        setScreen(loading);

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }



    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    @Override
    public void exitScreen(Screen screen, int exitCode) {
        if (screen == loading) {
            directory = loading.getAssets();
            loading.dispose();
            loading = null;

            // Initialize the three game worlds
            //controllers = new PhysicsScene[3];
            menuScene = new MainMenuScene(batch);
            menuScene.setScreenListener(this);
            setScreen(menuScene);
        } else if (screen == menuScene) {
            gameScene = new GameplayController(batch);
            gameScene.setScreenListener(this);
            setScreen(gameScene);
        } else
            Gdx.app.exit();
        }

    }

