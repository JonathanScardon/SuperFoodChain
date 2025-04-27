package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.companions.*;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;
import edu.cornell.gdiac.physics2.ObstacleSprite;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * The screen for the actual gameplay in the game Heavily inspired by the Optimization lab
 */
public class GameScene implements Screen {

    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private final Texture dim;
    private final Texture pauseBackground;
    private final Button resumeButton;
    private final Button resetButton;

    private final Button levelsButton;
    private final Button settingsButton;
    private final Button exitButton;
    private final Button homeButton;
    private final Button nextButton;
    private final Button replayButton;


    private final Settings settingsScreen;

    private final Texture win;
    private final Texture mouseLose;
    private final Texture chopsticksLose;

    private Array<TextLayout> bossNames;
    private Array<Float> bossStartHealths;

    private int numBosses;

    private float companionAddTimer = 3.0f;
    /**
     * Reference to the game session
     */
    private GameState state;

    /**
     * Companions in the chain
     */
    private Player player;

    /**
     * Minions in the level
     */
    private Array<Minion> minions;

    /**
     * Bosses in the level
     */
    private Array<Boss> bosses;

    /**
     * Companions in the level
     */
    private Array<Companion> companions;

    private Array<Coin> coins;
    private Array<Projectile> projectiles;
    protected World world;

    /**
     * A list of possible minion spawn locations, shuffled after it is looped through
     */
    private Array<MinionSpawnPoint> minionSpawns;
    /**
     * A list of possible companion spawn locations, shuffled after it is looped through
     */
    private Array<Vector2> companionSpawns;
    /**
     * Index of next minion spawn location
     */
    int minionSpawnIdx;
    /**
     * Index of next companion spawn location
     */
    int companionSpawnIdx;

    /**
     * Random number generator
     */
    private static final Random rand = new Random();

    /**
     * List of all the input controllers
     */
    protected InputController playerControls;
    //protected Array<MinionController> minionControls;
    protected Array<BossController> bossControls;

    private boolean start;
    private boolean reset;
    private boolean debug;
    private Array<ObstacleSprite> everything;


    private boolean background;
    // Loaded assets
    /**
     * The constants defining the game behavior
     */
    private JsonValue constants;

    private Texture backgroundTexture;
    private Texture coinCounter;
    private boolean paused;
    private boolean settingsOn;
    private Array<ObstacleSprite> dead;

    private int level;
    private BitmapFont font;
    private AssetDirectory assets;
    private boolean winGame;
    private Texture cost;
    private Texture costGrey;

    /**
     * Creates a GameScene
     *
     * @param game the GDX root
     */
    public GameScene(final GDXRoot game, AssetDirectory assets, int level) {
        this.game = game;
        this.assets = assets;
        coinCounter = new Texture("images/coin-counter.png");
        constants = assets.getEntry("constants", JsonValue.class);
        //System.out.println(constants);
        this.state = new GameState(constants, assets);
        this.level = level;

        minions = state.getMinions();
        companions = state.getCompanions();
        dead = state.getDead();

        dim = new Texture("images/dim.png");
        backgroundTexture = new Texture("images/temp_background.png");
        //backgroundTexture = new Texture("images/background_angled.png");
        pauseBackground = new Texture("images/PauseBackground.png");
        Texture button = new Texture("images/Button.png");
        Texture buttonDark = new Texture("images/ButtonDark.png");
        Texture replay = new Texture("images/Replay_Button.png");
        Texture home = new Texture("images/Home_Button.png");
        Texture next = new Texture("images/Next_Button.png");
        Texture replayLight = new Texture("images/replayLight.png");
        Texture homeLight = new Texture("images/homeLight.png");
        Texture nextLight = new Texture("images/nextLight.png");

        win = new Texture("images/Win.png");
        mouseLose = new Texture("images/LoseRat.png");
        chopsticksLose = new Texture("images/LoseChopsticks.png");
        cost = new Texture("images/cost-ui.png");
        costGrey = new Texture("images/grayed-cost-ui.png");

        font = assets.getEntry("lpc", BitmapFont.class);
        resumeButton = new Button(506, 452, button, buttonDark, 0, 280, 63, "Resume", font);
        resetButton = new Button(506, 381, button, buttonDark, 0, 280, 63, "Reset", font);
        levelsButton = new Button(506, 310, button, buttonDark, 1, 280, 63, "Levels", font);
        settingsButton = new Button(506, 239, button, buttonDark, 0, 280, 63, "Settings", font);
        exitButton = new Button(506, 160, button, buttonDark, 0, 280, 63, "Exit", font);

        replayButton = new Button(0, 0, replayLight, replay, 0, 78, 78);
        homeButton = new Button(0, 0, homeLight, home, 1, 78, 78);
        nextButton = new Button(734, 114, nextLight, next, 2, 78, 78);

        settingsScreen = new Settings();

        bossNames = new Array<>();
        bossStartHealths = new Array<>();
        everything = new Array<>();
        reset();
        for (Boss b : bosses) {
            bossNames.add(new TextLayout(b.getName(), font));
            bossStartHealths.add(b.getStartHealth());
        }
        numBosses = bosses.size;
    }

    private void reset() {
        start = false;
        reset = true;
        paused = false;
        state.reset();
        winGame = false;

        bosses = state.getBosses();
        bossControls = state.getBossControls();
        world = state.getWorld();
        coins = state.getCoins();

        projectiles = state.getActiveProjectiles();

        LevelLoader.getInstance().load(this, "tiled/level_" + level + ".tmx");
        player = state.getPlayer();
        playerControls = new PlayerController(player);
        state.setMinions(minions);
        //minionControls = state.getMinionControls();

        minionSpawns = state.getMinionSpawns();
        companionSpawns = state.getCompanionSpawns();
        minionSpawns.shuffle();
        companionSpawns.shuffle();

        // spawn in the first set of minions and companions so that we can see them before we start moving
        addMinions();
        addCompanions();

        // make the bosses start attacking
        for (BossController bc : bossControls) {
            bc.startAttack();
        }
    }


    /**
     * Spawn minions until we reach the maximum enemy numbers
     */
    private void addMinions() {
        while (minions.size < state.maxMinions) {
            spawnMinion();
        }
    }

    /**
     * Spawn a single minion in the world
     */
    private void spawnMinion() {
        if (minionSpawnIdx >= minionSpawns.size) {
            minionSpawnIdx = 0;
        }
        MinionSpawnPoint sp = minionSpawns.get(minionSpawnIdx);
        Minion m = sp.spawnMinion(world, player);

        minions.add(m);
        minionSpawnIdx++;
    }

    /**
     * Spawn companions until we reach the maximum number for each
     */
    private void addCompanions() {
        while (companions.size < state.maxCompanions) {
            spawnCompanion();
        }
    }

    /**
     * Spawn a single companion in the world
     */
    private void spawnCompanion() {
        if (companionSpawnIdx >= companionSpawns.size) {
            companionSpawnIdx = 0;
        }
        Vector2 pos = companionSpawns.get(companionSpawnIdx);

        Companion c = null;
        if (state.numStrawberries < state.maxStrawberries) {
            c = new Strawberry(pos.x, pos.y, companions.size, world);
            state.numStrawberries++;
        } else if (state.numPineapples < state.maxPineapples) {
            c = new Pineapple(pos.x, pos.y, companions.size, world);
            state.numPineapples++;
        } else if (state.numBlueRaspberries < state.maxBlueRaspberries) {
            c = new BlueRaspberry(pos.x, pos.y, companions.size, world);
            state.numBlueRaspberries++;
        } else if (state.numDurians < state.maxDurians) {
            c = new Durian(pos.x, pos.y, companions.size, world);
            state.numDurians++;
        } else if (state.numAvocados < state.maxAvocados) {
            c = new Avocado(pos.x, pos.y, companions.size, world);
            state.numAvocados++;
        }

        if (c != null) {
            companions.add(c);
            companionSpawnIdx++;
        }
    }

    public GameState getState() {
        return state;
    }

    /**
     * Invokes the controller for each Object.
     * <p>
     * Movement actions are determined, but not committed (e.g. the velocity is updated, but not the
     * position). New ability action is processed but photon collisions are not.
     */
    public void update(float delta) {
        delta = Math.min(delta, 0.25f);
        state.getWorld().step(delta, 6, 2);

//        if (Gdx.input.isKeyPressed(Keys.R) && !reset) {
//            reset();
//        }

        setStart();
        //System.out.println("Update" + bosses);
        winGame = true;
        for (Boss b : bosses) {
            if (b.getObstacle().isActive()) {
                winGame = false;
                break;
            }
        }

        if (paused || winGame || !player.isAlive()) {
            for (Minion m : minions) {
                m.update(false);
            }
            player.update(delta, InputController.CONTROL_NO_ACTION);
            for (Boss b : bosses) {
                b.update(delta, InputController.CONTROL_NO_ACTION);
            }
        }

        if (winGame) {
            if (replayButton.isHovering() && Gdx.input.isTouched()) {
                reset();
            } else if (homeButton.isHovering() && Gdx.input.isTouched()) {
                dispose();
                game.exitScreen(this, homeButton.getExitCode());
            } else if (nextButton.isHovering() && Gdx.input.isTouched()) {
                dispose();
                game.exitScreen(this, nextButton.getExitCode());
            }
        }
        if (!player.isAlive()) {
            if (replayButton.isHovering() && Gdx.input.isTouched()) {
                reset();
            } else if (homeButton.isHovering() && Gdx.input.isTouched()) {
                dispose();
                game.exitScreen(this, homeButton.getExitCode());
            }
        }

        if (Gdx.input.isKeyPressed(Keys.D)) {
            debug = true;
        }

//        if (!player.isAlive() || winGame) {
//            return;
//        }

        if (start && !paused && !winGame && player.isAlive()) {
            state.update();
            addMinions();
            addCompanions();
            bosses.get(0).setDamage(false);

            for (int i = 0; i < companions.size; i++) {
                companions.get(i).setId(i);
            }

            for (Companion c : player.companions) {
                if (c.getObstacle().isActive()) {
                    if (c.canUse()) {
                        c.useAbility(state);
                    } else {
                        c.coolDown(true, delta);
                    }
                }
//                System.out.println(c.getObstacle().getName());
            }

            for (Projectile p : state.getActiveProjectiles()) {
                p.update(delta);
//                 System.out.println(p.getObstacle().getPosition());
            }
//             System.out.println();

            // System.out.println(player.position);
            // moves enemies - assume always moving (no CONTROL_NO_ACTION)
            for (int i = 0; i < minions.size; i++) {
                if (minions.get(i).getObstacle().isActive()) {
                    // System.out.println("CONTROL " + i);
                    //int action = minionControls.get(i).getAction();
                    // System.out.println("Id: " + i + " (" + action + ")");
                    minions.get(i).update(true);
                    minions.get(i).setDamage(false);
                } else {
                    if (minions.get(i).shouldRemove()) {
                        // used to be m.getID but minionControls above needs i
                        minions.removeIndex(i);
                        //minionControls.removeIndex(i);
                    }
                }
            }

            // boss moves and acts
            for (int i = 0; i < bosses.size; i++) {
                bossControls.get(i).update(delta);
                bosses.get(i).update(delta, bossControls.get(i).getAction());
            }
            //
            // // player chain moves
            int a = playerControls.getAction();
            // System.out.println(a);
            player.update(delta, a);

            // // if board isn't updating then no point
            // state.getLevel().update();
            //
            // // projectiles update
            // //state.getProjectiles().update();

            for (Coin c : coins) {
                c.update(delta);
            }

//            for (int i = 0; i < dead.size; i++) {
//                String type = dead.get(i).getName();
//                if (!type.equals("coin")) {
//                    if () {
//                        dead.removeIndex(i);
//                    }
//                }
//            }
//            addMinions();
//            addCompanions();

        }
        if (player.isAlive() && !winGame) {
            state.getCollisionController().postUpdate();
            if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !paused) {
                paused = true;
            }
            if (paused) {
                if (resumeButton.isHovering() && Gdx.input.isTouched()) {
                    paused = false;
                } else if (resetButton.isHovering() && Gdx.input.isTouched()) {
                    reset();
                    paused = false;
                } else if (levelsButton.isHovering() && Gdx.input.isTouched()) {
                    dispose();
                    game.exitScreen(this, levelsButton.getExitCode());
                } else if (settingsButton.isHovering() && Gdx.input.isTouched()) {
                    settingsOn = true;
                    settingsScreen.update();
                } else if (exitButton.isHovering() && Gdx.input.isTouched()) {
                    Gdx.app.exit();
                }
                if (settingsOn && Gdx.input.isKeyPressed(Keys.ESCAPE)) {
                    settingsOn = false;
                }
            }
        }
    }


    public void draw(float delta) {
        ScreenUtils.clear(Color.WHITE);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, 1280, 720);

        for (ObstacleSprite o : dead) {
            String type = o.getName();
            switch (type) {
                case "minion" -> {
                    ((Minion) o).update(false);
                    o.draw(game.batch);
                }
                case "player" -> {
                    ((Companion) o).update(delta, 0);
                    o.draw(game.batch);
                }
                case "boss" -> {
                    ((Boss) o).update(delta, 0);
                    ((Boss) o).draw(game.batch, delta);
                }
            }
        }

//        for (Minion m : minions) {
//            m.draw(game.batch);
//        }
//
//        player.draw(game.batch, delta);
//        for (Boss boss : bosses) {
//            boss.draw(game.batch, delta);
//        }
        drawOrder(delta);

        for (Projectile p : state.getActiveProjectiles()) {
            p.draw(game.batch);
//            System.out.println(p.getObstacle().getPosition());
        }
        for (ObstacleSprite o : dead) {
            String type = o.getName();
            switch (type) {
                case "minion" -> {
                    ((Minion) o).update(false);
                    o.draw(game.batch);
                }
                case "player" -> {
                    ((Companion) o).update(delta, 0);
                    o.draw(game.batch);
                }
                case "boss" -> {
                    ((Boss) o).update(delta, 0);
                    ((Boss) o).draw(game.batch, delta);
                }
            }
        }

        // UI Last
        for (Companion c : companions) {
            TextLayout compCost = new TextLayout(c.getCost() + "", font);
            TextLayout pressE = new TextLayout("E", font);
//            c.draw(game.batch);
            //temp UI

            if (!player.companions.contains(c)) {

                if (player.getCoins() >= c.getCost()) {
                    font.setColor(Color.BROWN);
                    game.batch.draw(this.cost,
                        c.getObstacle().getX() * 64f - this.cost.getWidth() / 2f,
                        c.getObstacle().getY() * 64f + 40f);
                } else {
                    game.batch.draw(costGrey,
                        c.getObstacle().getX() * 64f - costGrey.getWidth() / 2f,
                        c.getObstacle().getY() * 64f + 40f);
                }
                game.batch.drawText(compCost, c.getObstacle().getX() * 64f + 12f,
                    c.getObstacle().getY() * 64f + 53f);
                font.setColor(Color.WHITE);
                if (c.highlight) {
                    game.batch.drawText(pressE, c.getObstacle().getX() * 64f,
                        c.getObstacle().getY() * 64f + 35f);
                }
            }
        }

//        System.out.println();

//        for (Coin c : coins) {
//            c.draw(game.batch);
//        }

        if (debug) {
            // Draw the outlines
            Array<ObstacleSprite> sprites = new Array<>();
//                sprites.addAll(player.companions);
            sprites.addAll(minions);
            sprites.addAll(coins);
            sprites.addAll(companions);
            sprites.addAll(bosses);
            sprites.addAll(projectiles);
            for (ObstacleSprite obj : sprites) {
                obj.drawDebug(game.batch);
            }
            player.drawDebug(game.batch);
        }

        //Coin Counter
        TextLayout coinCount = new TextLayout("" + player.getCoins(), font);
        game.batch.draw(coinCounter, 1050, 50);
        game.batch.drawText(coinCount, 1150f, 79f);

        drawHPBars();

        //Coin collection UI
        for (ObstacleSprite o : dead) {
            String type = o.getName();
            if (type.equals("coin")) {
                o.update(delta);
                o.draw(game.batch);
            }
        }

        font.setColor(Color.WHITE);

        if (!player.isAlive()) {
            drawLose();
        }

        if (winGame) {
            drawWin();
        }
        if (paused && !settingsOn) {
            drawPause();
        }
        if (settingsOn) {
            game.batch.draw(dim, 0, 0);
            settingsScreen.draw(game.batch);
        }
        game.batch.end();
    }

    // /**
    // * Creates photons and updates the object's cooldown.
    // *
    // * Using an ability requires access to all other models? so we have factored
    // * this behavior out of the Ship into the GameplayController.
    // */
    // private void useAbility(Companion c) {
    // c.useAbility(state);
    // // reset ability cooldown
    // c.coolDown(false);
    // }

    private void drawLose() {
        game.batch.draw(dim, 0, 0);
        if (bosses.get(0).getName().equals("mouse")) {
            game.batch.draw(mouseLose, 112, 60);
        } else if (bosses.get(0).getName().equals("chopsticks")) {
            game.batch.draw(chopsticksLose, 112, 60);
        }
        replayButton.setPosition(546, 108);
        homeButton.setPosition(666, 108);
        replayButton.draw(game.batch);
        homeButton.draw(game.batch);

    }

    private void drawWin() {
        game.batch.draw(dim, 0, 0);
        game.batch.draw(win, 112, 60);
        replayButton.setPosition(494, 114);
        homeButton.setPosition(613, 114);
        replayButton.draw(game.batch);
        homeButton.draw(game.batch);
        nextButton.draw(game.batch);
    }

    private void setStart() {
        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.DOWN) ||
            Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            start = true;
            reset = false;
        }
    }

    private void drawPause() {
        game.batch.setBlendMode(BlendMode.ALPHA_BLEND);
        game.batch.draw(dim, 0, 0);
        game.batch.draw(pauseBackground, 111.5f, 60.1f);
        resumeButton.draw(game.batch);
        resetButton.draw(game.batch);
        levelsButton.draw(game.batch);
        settingsButton.draw(game.batch);
        exitButton.draw(game.batch);
        font.setColor(Color.WHITE);
    }

    private void drawHPBars() {
        float w = 523;
        float cx;
        float cy = 650;

        for (int i = 0; i < numBosses; i++) {
            if (numBosses == 1) {
                cx = 367;
            } else {
                if (i == 0) {
                    cx = 71;
                } else {
                    cx = 662;
                }
            }
            TextureRegion region1, region2, region3;
            float ratio = bosses.get(i).getHealth() / bossStartHealths.get(i);
            game.batch.drawText(bossNames.get(i), cx + (w / 2 - (bossNames.get(i).getWidth() / 2)),
                cy + 50);

            // "3-patch" the background
            game.batch.setColor(Color.WHITE);
            region1 = assets.getEntry("progress.back", TextureRegion.class);
            game.batch.draw(region1, cx, cy, region1.getRegionWidth(),
                region1.getRegionHeight());

            // "3-patch" the foreground

            if (ratio > 0) {
                region1 = assets.getEntry("progress.foreleft", TextureRegion.class);
                game.batch.draw(region1, cx, cy, region1.getRegionWidth(),
                    region1.getRegionHeight());
                region2 = assets.getEntry("progress.foreright", TextureRegion.class);
                float span = ratio * (w - (region1.getRegionWidth() + region2.getRegionWidth()));

                game.batch.draw(region2, cx + region1.getRegionWidth() + span, cy,
                    region2.getRegionWidth(), region2.getRegionHeight());

                region3 = assets.getEntry("progress.foreground", TextureRegion.class);
                game.batch.draw(region3, cx + region1.getRegionWidth(), cy,
                    span, region3.getRegionHeight());
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.update(delta);
        this.draw(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (state.getWorld() != null) {
            state.getWorld().dispose();
        }
    }

    public int getLevel() {
        return level;
    }

    private void drawOrder(float delta) {
        everything.addAll(coins);
        everything.addAll(minions);
        everything.addAll(bosses);
        everything.addAll(companions);
        for (Companion c : player.companions) {
            everything.add(c);
        }
        ObstacleSprite[] temp = new ObstacleSprite[everything.size];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = everything.get(i);
        }

        Comparator<ObstacleSprite> comparator = (o1, o2) -> {
            float o1Y = o1.getObstacle().getY();
            float o2Y = o2.getObstacle().getY();
            return Float.compare(720 - o1Y, 720 - o2Y);
        };
        Arrays.sort(temp, comparator);
        for (ObstacleSprite o : temp) {
            switch (o.getName()) {
                case ("minion"), ("companion"), ("coin") -> o.draw(game.batch);
                case ("player") -> {
                    player.draw(game.batch);
                    o.draw(game.batch);
                }
                case ("mouse"), ("chopsticks") -> ((Boss) o).draw(game.batch, delta);

            }
        }
        everything.clear();
    }
}
