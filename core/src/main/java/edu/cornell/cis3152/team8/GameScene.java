package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.companions.*;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.TextLayout;
import edu.cornell.gdiac.physics2.ObstacleSprite;

/**
 * The screen for the actual gameplay in the game Heavily inspired by the Optimization lab
 */
public class GameScene implements Screen {

    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private Stage currStage;

    /**
     * Reference to the game audio
     */
    private GameAudio audio;
    /**
     * Reference to the game session
     */
    private final GameState state;
    /**
     * The drawing camera for the objects in this scene
     */
    public OrthographicCamera worldCamera;
    /**
     * The drawing camera for the UI in this scene
     */
    public OrthographicCamera uiCamera;

    protected World world;

    /**
     * The screen size
     */
    private float screenWidth;
    private float screenHeight;

    /**
     * Backgrounds/Foregrounds
     */
    private final Texture backgroundTexture;
    private final Texture paddingBackgroundTexture;

    /**
     * Pop-ups (win, lose, pause, handbook)
     */
    private WinPopUp win;
    private LosePopUp lose;
    private PausePopUp pause;
    private HandbookPopUp handbook;

    private HandbookOrder handbookOrder;

    /**
     * UI
     */
    private final Texture pressToStart;

    //Bosses
    private final Array<TextLayout> bossNames;
    private final Array<Float> bossStartHealths;
    private final int numBosses;
    private final TextureRegion hpBack;
    private final TextureRegion hpLeft;
    private final TextureRegion hpMiddle;
    private final TextureRegion hpRight;
    private final Texture ratIcon;
    private final Texture chefIcon;
    private final Texture chopsticksIcon;
    private final Color bossTint1;
    private final Color bossTint2;

    //Coins
    private final Texture coinCounter;
    private final Texture cost;
    private final Texture costGray;
    private final BitmapFont font;
    private static final Color fontColor = new Color(89f / 255, 43f / 255, 34f / 255, 100f);
    private final Affine2 transform;

    private final StringBuilder levelMusic;


    /**
     * Settings and Pausing
     */
    private static Settings settingsScreen;
    private boolean settingsOn;
    private boolean paused;

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
    /**
     * Coins in the level
     */
    private Array<Coin> coins;
    /**
     * Projectiles in the level
     */
    private Array<Projectile> projectiles;
    /**
     * A list of possible minion spawn locations, shuffled after it is looped through
     */
    private Array<MinionSpawnPoint> minionSpawns;
    /**
     * A list of possible companion spawn locations, shuffled after it is looped through
     */
    private Array<Vector2> companionSpawns;
    /**
     * Index of next companion spawn location
     */
    int companionSpawnIdx;

    /**
     * List of all the input controllers
     */
    protected InputController playerControls;
    protected Array<BossController> bossControls;


    /**
     * Drawing
     */
    private final Array<ObstacleSprite> everything; // All active obstacles
    private final Array<ObstacleSprite> dead; // All dead obstacles
    private boolean debug;

    /**
     * Whether the level has started
     */
    private boolean handbookPopupsOn;
    private boolean start;
    /**
     * Whether the level has ended
     */
    private boolean winGame;
    private boolean loseGame;

    /**
     * This level's number
     */
    private final int level;

    public final static float PHYSICS_UNITS = 64f;

    /**
     * Creates a GameScene
     *
     * @param game the GDX root
     */
    public GameScene(final GDXRoot game, AssetDirectory assets, int level) {
        this.game = game;
        this.state = new GameState(assets.getEntry("constants", JsonValue.class), assets);
        this.level = level;
        audio = game.audio;
        state.setAudio(audio);

        handbook = new HandbookPopUp(game, this);

        start = false;
        resize(1280, 720);
        reset();

        //Backgrounds
        backgroundTexture = assets.getEntry("gameBackground", Texture.class);
        paddingBackgroundTexture = assets.getEntry("chefBackground", Texture.class);

        //UI
        font = assets.getEntry("lpcBig", BitmapFont.class);
        bossNames = new Array<>();
        bossStartHealths = new Array<>();
        for (Boss b : bosses) {
            bossNames.add(new TextLayout(b.getName(), font));
            bossStartHealths.add(b.getStartHealth());
        }
        numBosses = bosses.size;
        hpBack = assets.getEntry("hp.back", TextureRegion.class);
        hpLeft = assets.getEntry("hp.foreleft", TextureRegion.class);
        hpMiddle = assets.getEntry("hp.foreground", TextureRegion.class);
        hpRight = assets.getEntry("hp.foreright", TextureRegion.class);
        ratIcon = assets.getEntry("ratIcon", Texture.class);
        chefIcon = assets.getEntry("chefIcon", Texture.class);
        chopsticksIcon = assets.getEntry("chopsticksIcon", Texture.class);
        pressToStart = assets.getEntry("pressToStartUI", Texture.class);
        //TODO: Change colors
        bossTint1 = new Color(1f, 1f, 1, 1f);
        bossTint2 = new Color(1f, 0.8f, 0.8f, 1f);

        coinCounter = assets.getEntry("coinCounter", Texture.class);
        cost = assets.getEntry("costUI", Texture.class);
        costGray = assets.getEntry("costUIGray", Texture.class);

        transform = new Affine2();

        everything = new Array<>();

        dead = state.getDead();

        levelMusic = new StringBuilder();
        for (TextLayout name : bossNames) {
            levelMusic.append(name.getText());
        }

        //Pop-ups
        pause = new PausePopUp(game, this);
        win = new WinPopUp(game, this);
        lose = new LosePopUp(game, this);
        handbook = new HandbookPopUp(game, this);

        handbookOrder = new HandbookOrder();

        settingsScreen = game.settings;

        unlockHandbook();
    }

    public void reset() {
        state.reset();
        if (!start) {
            handbookPopupsOn = true;
            handbook.setPopUpsOn();
        }
        handbook.resetCurPopUp();
        start = false;
        paused = false;
        winGame = false;
        loseGame = false;

        minions = state.getMinions();
        companions = state.getCompanions();

        bosses = state.getBosses();
        bossControls = state.getBossControls();
        world = state.getWorld();
        coins = state.getCoins();
        worldCamera.position.x = screenWidth / 2f;
        worldCamera.position.y = screenHeight / 2f;
        worldCamera.update();

        projectiles = state.getActiveProjectiles();

        LevelLoader.getInstance().load(this, "tiled/level_" + level + ".tmx");
        player = state.getPlayer();
        playerControls = new PlayerController(player);
        state.setMinions(minions);

        minionSpawns = state.getMinionSpawns();
        companionSpawns = state.getCompanionSpawns();
        minionSpawns.shuffle();
        companionSpawns.shuffle();

        // spawn in the first set of minions and companions so that we can see them before we start moving
        while (companions.size < state.maxCompanions) {
            spawnCompanion();
        }
        state.companionSpawnTime = state.companionSpawnCooldown;

        // make the bosses start attacking
        for (BossController bc : bossControls) {
            bc.startAttack();
        }

        audio.play("preLevel");
    }

    /**
     * Spawn a single companion in the world
     */
    private void spawnCompanion() {
        // find the first empty spawn point
        Vector2 pos = null;
        int attempts = 0;
        while (attempts < companionSpawns.size) {
            Vector2 candidate = companionSpawns.get(companionSpawnIdx);
            boolean occupied = false;

            for (Companion existing : companions) {
                Vector2 existingPos = existing.getObstacle().getPosition().cpy().scl(PHYSICS_UNITS);
                if (existingPos.epsilonEquals(candidate, 0.01f)) {
                    occupied = true;
                    break;
                }
            }

            companionSpawnIdx = (companionSpawnIdx + 1) % companionSpawns.size;
            attempts++;

            if (!occupied) {
                pos = candidate;
                break;
            }
        }

        if (pos == null) {
            return; // all spawn points are occupied
        }

        // Count how many are left for each type
        int r1 = state.maxStrawberries - state.numStrawberries;
        int r2 = state.maxPineapples - state.numPineapples;
        int r3 = state.maxBlueRaspberries - state.numBlueRaspberries;
        int r4 = state.maxDurians - state.numDurians;
        int r5 = state.maxAvocados - state.numAvocados;

        int total = r1 + r2 + r3 + r4 + r5;
        if (total == 0) {
            return; // we are already at the max for each type
        }

        int choice = (int) (Math.random() * total);

        Companion c;
        if (choice < r1) {
            c = new Strawberry(pos.x, pos.y, companions.size, world);
            state.numStrawberries++;
        } else if ((choice -= r1) < r2) {
            c = new Pineapple(pos.x, pos.y, companions.size, world);
            state.numPineapples++;
        } else if ((choice -= r2) < r3) {
            c = new BlueRaspberry(pos.x, pos.y, companions.size, world);
            state.numBlueRaspberries++;
        } else if (choice - r3 < r4) {
            c = new Durian(pos.x, pos.y, companions.size, world);
            state.numDurians++;
        } else {
            c = new Avocado(pos.x, pos.y, companions.size, world);
            state.numAvocados++;
        }

        companions.add(c);
    }

    public GameState getState() {
        return state;
    }

    /**
     * Invokes the controller for each Object.
     * <p>
     * Movement actions are determined but not committed (e.g., the velocity is updated, but not the
     * position). New ability action is processed but photon collisions are not.
     */
    public void update(float delta) {
        delta = Math.min(delta, 0.25f);
        state.getWorld().step(delta, 6, 2);

        pause.update(paused);
        if (loseGame) {
            for (ObstacleSprite o : dead) {
                String type = o.getName();
                if (type.equals("player")) {
                    currStage = lose.getStage();
                    Gdx.input.setInputProcessor(currStage);
                    lose.update(((Companion) o).shouldRemove());
                }
            }
        } else if (winGame) {
            for (ObstacleSprite o : dead) {
                String type = o.getName();
                if (type.equals("mouse") || type.equals("chef") || type.equals("chopsticks")) {
                    currStage = win.getStage();
                    Gdx.input.setInputProcessor(currStage);
                    win.update(((Boss) o).shouldRemove());
                }
            }
        }

        //System.out.println(handbookPopupsOn);
        if (handbookPopupsOn && !paused) {
            handbook.update();
        }
        handbookPopupsOn = handbook.arePopUpsOn();
        setStart();
        setWin();
        setLose();

        if (paused || winGame || loseGame) {
            audio.stopSfx();
            for (Minion m : minions) {
                m.update(false);
            }
            player.update(delta, InputController.CONTROL_NO_ACTION);
            for (Boss b : bosses) {
                b.update(delta, InputController.CONTROL_NO_ACTION);
            }
            pause.update(true);
            settingsScreen.update(delta);
            boolean temp = settingsScreen.isOn();
            if (settingsOn) {
                if (!temp) {
                    Gdx.input.setInputProcessor(currStage);
                }
            }
            settingsOn = temp;
        }

        if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
            debug = !debug;
        }

        if (start && !paused && !winGame && player.isAlive()) {
            state.update();
            if (companions.size < state.maxCompanions) {
                if (state.companionSpawnTime <= 0) {
                    spawnCompanion();
                    state.companionSpawnTime = state.companionSpawnCooldown;
                } else {
                    state.companionSpawnTime -= delta;
                }
            }

            // spawn minions
            for (MinionSpawnPoint spawn : minionSpawns) {
                spawn.update(delta);
            }

            for (int i = 0; i < companions.size; i++) {
                companions.get(i).setId(i);
                companions.get(i).setCost(companions.get(i).getOriginalCost() * (int) Math.ceil(
                    player.companions.size() / 3.0f));
            }

            // Player Companions use Ability
            for (Companion c : player.companions) {
                if (c.getObstacle().isActive()) {
                    if (c.canUse()) {
                        c.useAbility(state);
                        audio.play(c.getCompanionType().name());
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

            // System.out.println(player.position);
            // moves enemies - assume always moving
            for (int i = 0; i < minions.size; i++) {
                if (minions.get(i).getObstacle().isActive()) {
                    minions.get(i).update(true);
                } else {
                    if (minions.get(i).shouldRemove()) {
                        minions.removeIndex(i);
                    }
                }
            }

            // boss moves and acts
            for (int i = 0; i < bosses.size; i++) {
                if (bosses.get(i).getObstacle().isActive()) {
                    boolean play = bossControls.get(i).update(delta);
                    if (play) {
                        audio.play(bossControls.get(i).getAttackName());
                    }
                    bosses.get(i).update(delta, bossControls.get(i).getAction());
                }
            }

            // player chain moves
            int a = playerControls.getAction();
            player.update(delta, a);

            for (Coin c : coins) {
                c.update(delta);
            }

        }
        if (player.isAlive() && !winGame) {
            state.getCollisionController().postUpdate();
            if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !paused) {
                paused = true;
                currStage = pause.getStage();
                Gdx.input.setInputProcessor(currStage);
            }
        }
        //System.out.println(time);
    }


    public void draw(float delta) {
        ScreenUtils.clear(Color.WHITE);

        // Draw background first

        float backgroundWidth = backgroundTexture.getWidth() * (state.levelWidth / screenWidth);
        float backgroundHeight = backgroundTexture.getHeight() * (state.levelHeight / screenHeight);
        float backgroundX = (state.levelWidth - backgroundWidth) / 2f;
        float backgroundY = (state.levelHeight - backgroundHeight) / 2f;

        game.batch.begin(worldCamera);

        game.batch.draw(backgroundTexture, backgroundX, backgroundY, backgroundWidth,
            backgroundHeight);

        if (state.levelWidth != screenWidth) {
            // we need to add horizontal padding
            game.batch.draw(paddingBackgroundTexture, -paddingBackgroundTexture.getWidth(), 0);
            game.batch.draw(paddingBackgroundTexture, state.levelWidth, 0);
        }

        game.batch.end();

        // Draw warning sprite borders
        game.shape.setProjectionMatrix(worldCamera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        for (Boss b : bosses) {
            b.drawWarningBorders(game.shape);
        }

        game.shape.end();

        game.batch.begin(worldCamera);

        drawOrder(delta);

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
                case "mouse", "chef", "chopsticks" -> {
                    ((Boss) o).update(delta, InputController.CONTROL_NO_ACTION);
                    ((Boss) o).draw(game.batch, delta);
                }
            }
        }

        for (Projectile p : state.getActiveProjectiles()) {
            p.draw(game.batch);
        }

        // THIS IS FOR RED BLINKING FOR DAMAGE
        for (Minion m : minions) {
            m.setDamage(false);
        }
        for (Boss b : bosses) {
            b.setDamage(false);
        }
        game.batch.end();

        // Draw game objects
        game.batch.begin(worldCamera);

        // Warning symbols
        for (Boss b : bosses) {
            b.drawWarningIcons(game.batch);
        }

        // Companion costs
        for (Companion c : companions) {
            TextLayout compCost = new TextLayout(c.getCost() + "", font);
            TextLayout pressE = new TextLayout("E", font);
            float numScale = 0.6f;
            if (!player.companions.contains(c)) {
                if (player.getCoins() >= c.getCost()) {
                    font.setColor(fontColor);
                    game.batch.draw(cost,
                        c.getObstacle().getX() * PHYSICS_UNITS - cost.getWidth() / 2f,
                        c.getObstacle().getY() * PHYSICS_UNITS + 40f);
                } else {
                    game.batch.draw(costGray,
                        c.getObstacle().getX() * PHYSICS_UNITS - costGray.getWidth() / 2f,
                        c.getObstacle().getY() * PHYSICS_UNITS + 40f);
                }
                SpriteBatch.computeTransform(transform, compCost.getWidth() / 2.0f,
                    0,
                    c.getObstacle().getX() * PHYSICS_UNITS + 12f,
                    c.getObstacle().getY() * PHYSICS_UNITS + 53f, 0.0f,
                    numScale, numScale);
                game.batch.drawText(compCost, transform);
                font.setColor(Color.WHITE);
                if (c.highlight) {
                    game.batch.drawText(pressE, c.getObstacle().getX() * PHYSICS_UNITS,
                        c.getObstacle().getY() * PHYSICS_UNITS + 35f);
                }
            }
        }

        // Coin collection UI
        for (ObstacleSprite o : dead) {
            String type = o.getName();
            if (type.equals("coin")) {
                o.update(delta);
                o.draw(game.batch);
            }
        }

        if (debug) {
            // Draw the outlines
            Array<ObstacleSprite> sprites = new Array<>();
            for (Companion c : player.companions) {
                sprites.add(c);
            }
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

        game.batch.end();

        // Draw UI elements that follow the camera
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin(uiCamera);

        // Coin Counter
        float numScale = 0.7f;
        TextLayout coinCount = new TextLayout("" + player.getCoins(), font);
        //To shrink the number
        SpriteBatch.computeTransform(transform, coinCount.getWidth() / 2.0f,
            coinCount.getFont().getXHeight() / 2.0f, 1150,
            85, 0.0f, numScale, numScale);
        game.batch.draw(coinCounter, 1050, 50);
        game.batch.drawText(coinCount, transform);

        drawHPBars();

        font.setColor(Color.WHITE);

        // draw press to start controls reminder
        if (!start) {
            SpriteBatch.computeTransform(transform, pressToStart.getWidth() / 2.0f,
                pressToStart.getHeight() / 2.0f,
                player.getPlayerHead().getObstacle().getX() * PHYSICS_UNITS,
                player.getPlayerHead().getObstacle().getY() * PHYSICS_UNITS
                    - pressToStart.getHeight(),
                0.0f, 1f, 1f);
            game.batch.draw(pressToStart, transform);
        }

        if (loseGame) {
            for (ObstacleSprite o : dead) {
                String type = o.getName();
                if (type.equals("player")) {
                    if (((Companion) o).shouldRemove()) {
                        lose.draw();
                        break;
                    }
                }
            }
        } else if (winGame) {
            for (ObstacleSprite o : dead) {
                String type = o.getName();
                if (type.equals("mouse") || type.equals("chef") || type.equals("chopsticks")) {
                    if (((Boss) o).shouldRemove()) {
                        win.draw();
                    }
                }
            }
        }

        if (handbookPopupsOn) {
            handbook.draw();
        }

        if (paused && !settingsOn) {
            pause.draw();
        }

        game.batch.end();
        if (settingsOn) {
            settingsScreen.draw();
        }
    }

    private void setStart() {
        if ((Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.DOWN) ||
            Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT) ||
            Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.S) ||
            Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.D)) && !start
            && !handbookPopupsOn) {
            start = true;
            audio.play(levelMusic.toString());
        }
    }

    private void drawHPBars() {
        float w = 523;
        float cx;
        float cy = 650;
        float padding = 10;
        float center = (ratIcon.getWidth() + padding + w) / 2;

        for (int i = 0; i < numBosses; i++) {
            if (numBosses == 1) {
                cx = screenWidth / 2 - center;
            } else {
                if (i == 0) {
                    if (bossNames.get(0).getText().equals(bossNames.get(1).getText())) {
                        game.batch.setColor(bossTint1);
                    }
                    cx = screenWidth / 2 - (center * 2) - padding;
                } else {
                    if (bossNames.get(0).getText().equals(bossNames.get(1).getText())) {
                        game.batch.setColor(bossTint2);
                    }
                    cx = screenWidth / 2 + padding;
                }
            }
            float ratio = bosses.get(i).getHealth() / bossStartHealths.get(i);
            Texture icon;
            if (bossNames.get(i).getText().equals("mouse")) {
                icon = ratIcon;
            } else if (bossNames.get(i).getText().equals("chef")) {
                icon = chefIcon;
            } else {
                icon = chopsticksIcon;
            }
            game.batch.draw(icon, cx, cy + hpBack.getRegionHeight() / 2f - (icon.getHeight() / 2f));
            game.batch.setColor(Color.WHITE);

            cx += icon.getWidth() + padding;
            // "3-patch" the background
            game.batch.setColor(Color.WHITE);
            game.batch.draw(hpBack, cx, cy, hpBack.getRegionWidth(),
                hpBack.getRegionHeight());

            // "3-patch" the foreground

            if (ratio > 0) {

                game.batch.draw(hpLeft, cx, cy, hpLeft.getRegionWidth(),
                    hpLeft.getRegionHeight());

                float span = ratio * (w - (hpLeft.getRegionWidth() + hpRight.getRegionWidth()));

                game.batch.draw(hpRight, cx + hpLeft.getRegionWidth() + span, cy,
                    hpRight.getRegionWidth(), hpRight.getRegionHeight());

                game.batch.draw(hpMiddle, cx + hpLeft.getRegionWidth(), cy,
                    span, hpMiddle.getRegionHeight());
            }
        }
    }

    //To only play sound once.
    private void setWin() {
        if (!winGame) { //Only play sound once
            winGame = true;
            String s = "level" + level + "Won";
            if (!game.save.getBoolean(s)) {
                game.save.putBoolean(s, true);
                int newUnlock = game.save.getInteger("unlockedLevels") + 1;
                if (newUnlock <= game.getTotalLevels()) {
                    game.save.putInteger("unlockedLevels", newUnlock);
                }
            }
            for (Boss b : bosses) {
                if (b.getObstacle().isActive()) {
                    winGame = false;
                }
            }
            if (winGame) {
                audio.play("win");
            }
        }
    }

    private void setLose() {
        if (!loseGame) { //Only play sound once
            loseGame = !player.isAlive();
            if (loseGame) {
                audio.play("lose");
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

    /**
     * Called when the Screen is resized.
     * <p>
     * This can happen at any point during a non-paused state but will never happen before a call to
     * show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    @Override
    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        if (worldCamera == null) {
            worldCamera = new OrthographicCamera(width, height);
            state.worldCamera = worldCamera;
            worldCamera.position.set(width / 2f, height / 2f, 0);
            worldCamera.update();
        } else {
            worldCamera.setToOrtho(false, width, height);
        }
        if (uiCamera == null) {
            uiCamera = new OrthographicCamera(width, height);
            uiCamera.position.set(width / 2f, height / 2f, 0);
            uiCamera.update();
        } else {
            uiCamera.setToOrtho(false, width, height);
        }
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
            state.setWorld(null);
        }
    }

    public int getLevel() {
        return level;
    }

    /**
     * Draw all objects in order based on y coordinate
     *
     * @param delta the time in seconds since the last frame
     */
    private void drawOrder(float delta) {
        everything.addAll(coins);
        everything.addAll(minions);
        if (bossControls.get(0).getAttackName().equals("spin") && bosses.get(0).getState()
            .equals("spinning")) {
            if (bosses.get(0).getObstacle().isActive() && bosses.get(1).getObstacle().isActive()) {
                everything.add(bosses.get(0));
            } else if (!bosses.get(0).getObstacle().isActive()) {
                everything.add(bosses.get(1));
            } else {
                everything.add(bosses.get(0));
            }
        } else if (bosses.size == 2) {
            if (bosses.get(0).getObstacle().isActive()) {
                everything.add(bosses.get(0));
            }
            if (bosses.get(1).getObstacle().isActive()) {
                everything.add(bosses.get(1));
            }
        } else {
            if (bosses.get(0).getObstacle().isActive()) {
                everything.addAll(bosses);
            }
        }
        everything.addAll(companions);
        for (Companion c : player.companions) {
            everything.add(c);
        }

        everything.sort((o1, o2) -> Float.compare(screenHeight - o1.getObstacle().getY(),
            screenHeight - o2.getObstacle().getY()));

        // draw movement indicator first
        player.draw(game.batch);

        for (ObstacleSprite o : everything) {
            switch (o.getName()) {
                case "minion", "companion", "coin", "player" -> o.draw(game.batch);
                case "mouse", "chef", "chopsticks" -> {
                    if (bosses.size == 2 && !bosses.get(0).getState()
                        .equals("spinning") && bossNames.get(0).getText()
                        .equals(bossNames.get(1).getText())) {
                        if (o.equals(bosses.get(0))) {
                            game.batch.setColor(bossTint1);
                        } else {
                            game.batch.setColor(bossTint2);
                        }
                    }
                    ((Boss) o).draw(game.batch, delta);
                    game.batch.setColor(Color.WHITE);
                }
            }
        }

        everything.clear();
    }

    /**
     * @return The camera used in this scene
     */
    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public void resetMusic() {
        if (winGame) {
            audio.play("win");
        } else if (loseGame) {
            audio.play("lose");
        } else if (start) {
            audio.play(levelMusic.toString());
        } else {
            audio.play("preLevel");
        }

    }

    private void unlockHandbook() {
        //TODO: Manually decide which level unlocks which thing
        String companion = handbookOrder.companion(level);
        String minion = handbookOrder.minion(level);
        String boss = handbookOrder.boss(level);

        if (!companion.isEmpty()) {
            boolean alreadyUnlocked = game.save.getBoolean(companion);
            if (!alreadyUnlocked) {
                int newUnlock = game.save.getInteger("unlockedCompanions") + 1;
                game.save.putInteger("unlockedCompanions", newUnlock);
                game.save.putBoolean(companion, true);
            }
        }
        if (!minion.isEmpty()) {
            boolean alreadyUnlocked = game.save.getBoolean(minion);
            if (!alreadyUnlocked) {
                int newUnlock = game.save.getInteger("unlockedMinions") + 1;
                game.save.putInteger("unlockedMinions", newUnlock);
                game.save.putBoolean(minion, true);
            }
        }
        if (!boss.isEmpty()) {
            boolean alreadyUnlocked = game.save.getBoolean(boss);
            if (!alreadyUnlocked) {
                int newUnlock = game.save.getInteger("unlockedBosses") + 1;
                game.save.putInteger("unlockedBosses", newUnlock);
                game.save.putBoolean(boss, true);
            }
        }
    }

    public boolean getPaused() {
        return paused;
    }

    public boolean getSettingsOn() {
        return settingsOn;
    }

    public void setPaused(boolean b) {
        paused = b;
    }

    public void setSettingsOn(boolean b) {
        settingsOn = b;
        settingsScreen.setOn(settingsOn);
    }

    public Stage getStage() {
        return currStage;
    }

    private void setStage() {
        Gdx.input.setInputProcessor(currStage);
    }

    public String getBossNames() {
        return levelMusic.toString();
    }

}
