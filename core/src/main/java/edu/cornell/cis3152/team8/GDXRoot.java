package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.util.ScreenListener;
import java.util.prefs.PreferencesFactory;


public class GDXRoot extends Game implements ScreenListener {

    /**
     * AssetManager to load game assets
     */
    AssetDirectory directory;

    public FitViewport viewport;
    public SpriteBatch batch;
    public ShapeRenderer shape;
    public BitmapFont font;

    // The screens in the game
    private LoadingScene loadingScene;
    private GameScene gameScene;
    private MainMenuScene menuScene;
    private LevelSelectScene levelSelectScene;
    private CompanionHandbookScene handbookScene;
    private String prev;
    public GameAudio audio;
    public Settings settings;

    public Preferences save;
    private int totalLevels;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        font = new BitmapFont();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        save = Gdx.app.getPreferences("Save");

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        loadingScene = new LoadingScene("assets.json", batch, 1);
        loadingScene.setScreenListener(this);

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

        if (handbookScene != null) {
            handbookScene.dispose();
            handbookScene = null;
        }

        if (batch != null) {
            batch.dispose();
            batch = null;
        }

        if (font != null) {
            font.dispose();
            font = null;
        }

        audio = null;
        settings = null;
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

            totalLevels = directory.getEntry("save", JsonValue.class)
                .getInt("total_Levels");
            //TODO: change to 1
            int unlockedLevels = directory.getEntry("save", JsonValue.class)
                .getInt("max_level_unlocked");
            save.putInteger("unlockedLevels", unlockedLevels);
            for (int i = 1; i <= totalLevels; i++) {
                save.putBoolean("level" + i + "Won", false);
            }

            int unlockedHandbook = directory.getEntry("save", JsonValue.class)
                .getInt("companions_unlocked");

            resetSave();

            // UNCOMMENT AND ADD THE NUMBER YOU WANT UNLOCKED FOR TESTING PURPOSES
            // int num = 15;
            // save.putInteger("unlockedLevels", num);

            audio = new GameAudio(directory);
            settings = new Settings(this, directory);
            menuScene = new MainMenuScene(this, directory);
            levelSelectScene = new LevelSelectScene(this, directory);
            handbookScene = new CompanionHandbookScene(this);
            menuScene.reset();
            Gdx.input.setInputProcessor(menuScene.getStage());
            setScreen(menuScene);
        } else if (screen == menuScene) {
            menuScene.dispose();
            if (exitCode == ExitCode.LEVELS) { //Level select
                levelSelectScene.reset();
                Gdx.input.setInputProcessor(levelSelectScene.getStage());
                this.setScreen(levelSelectScene);
            } else if (exitCode == ExitCode.RESET_SAVE) {
                menuScene.reset();
                Gdx.input.setInputProcessor(menuScene.getStage());
                setScreen(menuScene);
            } else if (exitCode == ExitCode.EXIT_GAME) { //Exit program
                dispose();
                Gdx.app.exit();
            }
        } else if (screen == levelSelectScene) {
            levelSelectScene.dispose();
            if (exitCode == ExitCode.HOME || exitCode == ExitCode.RESET_SAVE) {
                menuScene.reset();
                Gdx.input.setInputProcessor(menuScene.getStage());
                setScreen(menuScene);
            } else if (exitCode == ExitCode.HANDBOOK) {
                prev = "levels";
                handbookScene.reset();
                Gdx.input.setInputProcessor(handbookScene.getStage());
                setScreen(handbookScene);
            } else {
                gameScene = new GameScene(this, directory, exitCode);
                gameScene.resetMusic();
                this.setScreen(gameScene);
            }
        } else if (screen == gameScene) {
            if (exitCode == ExitCode.LEVELS) {
                levelSelectScene.reset();
                Gdx.input.setInputProcessor(levelSelectScene.getStage());
                setScreen(levelSelectScene);
            } else if (exitCode == ExitCode.NEXT_LEVEL) {
                int next = gameScene.getLevel() + 1;
                gameScene = new GameScene(this, directory, next);
                gameScene.resetMusic();
                setScreen(gameScene);
            } else if (exitCode == ExitCode.HANDBOOK) {
                prev = "game";
                handbookScene.reset();
                Gdx.input.setInputProcessor(handbookScene.getStage());
                setScreen(handbookScene);
            } else if (exitCode == ExitCode.RESET_SAVE) {
                menuScene.reset();
                Gdx.input.setInputProcessor(menuScene.getStage());
                setScreen(menuScene);
            } else if (exitCode == ExitCode.EXIT_GAME) {
                dispose();
                Gdx.app.exit();
            }

        } else if (screen == handbookScene) {
            handbookScene.dispose();
            if (exitCode == ExitCode.BACK) {
                if (prev.equals("levels")) {
                    levelSelectScene.reset();
                    Gdx.input.setInputProcessor(levelSelectScene.getStage());
                    setScreen(levelSelectScene);
                } else if (prev.equals("game")) {
                    gameScene.resetMusic();
                    Gdx.input.setInputProcessor(gameScene.getStage());
                    setScreen(gameScene);
                }
            } else if (exitCode == ExitCode.RESET_SAVE) {
                menuScene.reset();
                Gdx.input.setInputProcessor(menuScene.getStage());
                setScreen(menuScene);
            }
        } else {
            dispose();
            Gdx.app.exit();
        }
    }

    public int getTotalLevels() {
        return totalLevels;
    }

    public void resetSave() {
        save.putInteger("unlockedLevels", 1);

        save.putInteger("unlockedCompanions", 0);
        save.putInteger("unlockedMinions", 0);
        save.putInteger("unlockedBosses", 0);

        save.putBoolean("durian", false);
        save.putBoolean("strawberry", false);
        save.putBoolean("blue raspberry", false);
        save.putBoolean("avocado", false);
        save.putBoolean("garlic", false);

        save.putBoolean("ant", false);
        save.putBoolean("cricket", false);
        save.putBoolean("spider", false);

        save.putBoolean("rat", false);
        save.putBoolean("chopsticks", false);
        save.putBoolean("chef", false);
    }
}
