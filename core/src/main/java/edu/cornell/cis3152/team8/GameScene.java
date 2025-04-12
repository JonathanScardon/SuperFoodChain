package edu.cornell.cis3152.team8;

/**
 * Heavily inspired by the Optimization lab
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.Companion.CompanionType;
import edu.cornell.cis3152.team8.companions.Garlic;
import edu.cornell.cis3152.team8.companions.Pineapple;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;
import edu.cornell.gdiac.util.ScreenListener;

import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.cis3152.team8.companions.Durian;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.w3c.dom.Text;

public class GameScene implements Screen {
    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private final Texture pauseBackground;
    private final Button resetButton;
    private final Button levelsButton;
    private final Button settingsButton;
    private final Button exitButton;
    private final Settings settingsScreen;

    private int maxEnemies;
    private int maxStrawberry;
    private int maxPineapple;
    private int maxAvocado;
    private int curStrawberry;
    private int curPineapple;
    private int curAvocado;


    private Vector2[] minionSpawns;
private Vector2[] companionSpawns;

    /**
     * Reference to the game session
     */
    private GameState state;

    /**
     * The grid of tiles
     */
    private Level level;

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

    private LinkedList<Coin> coins;
    private Array<Projectile> projectiles;

    /**
     * List of all the input controllers
     */
    protected InputController playerControls;
    protected Array<MinionController> minionControls;
    protected Array<BossController> bossControls;

    private CollisionController collision;
    private boolean start;
    private boolean reset;

    private boolean background;
    // Loaded assets
    /**
     * The constants defining the game behavior
     */
    private JsonValue constants;

    private Texture coinTexture;
    private boolean paused;
    private boolean settingsOn;
    private boolean[] minionSpawnTaken;
    private boolean[] companionSpawnTaken;

    /**
     * Creates a GameScene
     *
     * @param game the GDX root
     */
    public GameScene(final GDXRoot game, AssetDirectory assets, int level) {
        this.game = game;
        coinTexture = new Texture("images/CoinUI.png");
        constants = assets.getEntry("level" + level, JsonValue.class);
        //System.out.println(constants);
        this.state = new GameState(constants, assets);

        maxEnemies = state.getMaxEnemies();
        maxStrawberry = state.getMaxStrawberry();
        maxPineapple = state.getMaxPineapple();
        maxAvocado = state.getMaxAvocado();

        minions = state.getMinions();
        minionSpawns = state.getMinionSpawns();
        companions = state.getCompanions();
        companionSpawns = state.getCompanionSpawns();

        pauseBackground = new Texture("images/Paused.png");
        Texture resetT = new Texture("images/ResetButton.png");
        Texture levels = new Texture("images/LevelsButton.png");
        Texture settings = new Texture("images/SettingsButton.png");
        Texture exit = new Texture("images/ExitButton.png");
        resetButton = new Button(399,459,resetT,0,482,120);
        levelsButton = new Button(399,319,levels,1,482,120);
        settingsButton = new Button(399,180,settings,0,482,120);
        exitButton = new Button(399,41,exit,0,482,120);
        settingsScreen = new Settings();

        minionSpawnTaken = new boolean[minionSpawns.length];
        Arrays.fill(minionSpawnTaken, false);
        companionSpawnTaken = new boolean[companionSpawns.length];
        Arrays.fill(companionSpawnTaken, false);


        reset();
       // System.out.println(minionControls);
    }

    private void reset() {
        start = false;
        reset = true;
        paused = false;
        state.reset();
        player = new Player(500, 350);
        state.setPlayer(player);
        state.setMinions(minions);

        curStrawberry = 0;
        curPineapple = 0;
        curAvocado = 0;
        coins = new LinkedList<>();
        bosses = state.getBosses();
        bossControls = state.getBossControls();
        minionControls = state.getMinionControls();
        addMinions();
        addCompanions();

       // bossControls = new Array<>();

        projectiles = state.getActiveProjectiles();
        collision = new CollisionController(minions, player, companions, coins, bosses, projectiles ,minionControls);

        // assuming player is a list of Companions btw
        // player = state.getPlayer();
        playerControls = new PlayerController(player);

        // level = state.getLevel();

        // assuming each level has number of enemies assigned?

        //System.out.println("Reset" + bosses);
    }

    /**
     * Initializes the player to center of the board.
     * <p>
     * UNLESS the player is also at random position.
     */
    private void initPlayerPosition() {
        float px = 15;
        float py = 10;
        Companion head = player.companions.get(0);
        head.setX(px);
        head.setY(py);
    }


     /**
     * Initializes the minions to new random location.
     */
    private void addMinions() {
        Random rand = new Random();
        boolean r = true;
        for (boolean b :minionSpawnTaken){
            if (!b) {
                r = false;
                break;
            }
        }
        if (r){
            Arrays.fill(minionSpawnTaken, false);
        }
        while (minions.size < maxEnemies) {
            int spawn = rand.nextInt(minionSpawns.length);
            if (!minionSpawnTaken[spawn]) {
                float x = minionSpawns[spawn].x;
                float y = minionSpawns[spawn].y;
                Minion m = new Minion(x, y, minions.size);
                minions.add(m);
                minionControls.add(new MinionController(m.getId(), minions, player));
                minionSpawnTaken[spawn] = true;
            }
        }
    }


     /**
     * Initializes the companions to new random location.
     */
    private void addCompanions() {
        Random rand = new Random();
        boolean r = false;
        for (boolean b :companionSpawnTaken){
            if (!b) {
                r = false;
                break;
            }
        }
        if (r){
            Arrays.fill(companionSpawnTaken, false);
        }
        while (companions.size < maxStrawberry+maxPineapple+maxAvocado) {
            int spawn = rand.nextInt(companionSpawns.length);

                float x = companionSpawns[spawn].x;
                float y = companionSpawns[spawn].y;
            for (Companion c: companions) {
                if (c.getX() == x && c.getY() == y) {
                    r = true;
                }else{
                    r = false;
                }
            }
            if (!r)  {
                Companion c;
                if (curStrawberry < maxStrawberry) {
                    c = new Strawberry(x, y, companions.size);
                    curStrawberry++;
                } else if (curPineapple < maxPineapple) {
                    c = new Pineapple(x, y, companions.size);
                    curPineapple++;
                } else {
                    c = new Garlic(x, y, companions.size);
                    curAvocado++;
                }
                companions.add(c);
                companionSpawnTaken[spawn] = true;
            }
        }
    }

    public GameState getState() {
        return state;
    }

    /**
     * Invokes the controller for each Object.
     * <p>
     * Movement actions are determined, but not committed (e.g. the velocity
     * is updated, but not the position). New ability action is processed
     * but photon collisions are not.
     */
    public void update(float delta) {
//        if (Gdx.input.isKeyPressed(Keys.R) && !reset) {
//            reset();
//        }

        setStart();
        //System.out.println("Update" + bosses);

        if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !paused){
            paused = true;
        } else if (Gdx.input.isKeyPressed(Keys.R) && paused){
            paused = false;
        }

        if (paused){
            if (resetButton.isHovering() && Gdx.input.isTouched()){
                reset();
                paused = false;
            } else if (levelsButton.isHovering() && Gdx.input.isTouched()) {
                game.exitScreen(this, levelsButton.getExitCode());
                dispose();
            } else if (settingsButton.isHovering() && Gdx.input.isTouched()){
                settingsOn = true;
                settingsScreen.update();
            }else if (exitButton.isHovering() && Gdx.input.isTouched()) {
                Gdx.app.exit();
            }

            if (settingsOn && Gdx.input.isKeyPressed(Keys.ESCAPE)){
                settingsOn = false;
            }
        }


        if (start && player.isAlive() && !bosses.get(0).isDestroyed() && !paused) {
            // iterate through all companions in the chain
            state.update();
            addMinions();
            addCompanions();
            bosses.get(0).setDamage(false);

            for (Companion c : companions){
                if (c.isCollected()){
                    companions.removeIndex(c.getId());
                    CompanionType type = c.getCompanionType();
                    if (type.equals(CompanionType.STRAWBERRY)){
                        curStrawberry--;
                    }else if (type.equals(CompanionType.PINEAPPLE)){
                        curPineapple--;
                    }else{
                        curAvocado--;
                    }

                }
            }

            for (int i = 0; i < minions.size; i++) {
                minions.get(i).setID(i);
            }
            for (int i = 0; i < companions.size; i++) {
                companions.get(i).setId(i);
            }




            for (Companion c : player.companions) {
                if (!c.isDestroyed()){
                    if (c.canUse()) {
                        c.useAbility(state);
                    } else {
                        c.coolDown(true, delta);
                    }
                }
            }

            for (Projectile p : state.getActiveProjectiles()) {
                p.update(delta);
            }

            // Remove dead projectiles and return them to their pools
            for (int i = state.getActiveProjectiles().size - 1; i >= 0; i--) {
                Projectile p = state.getActiveProjectiles().get(i);
                if (p.isDestroyed() || p.getLife() <= 0) {
                    state.getActiveProjectiles().removeIndex(i);
                    if (p instanceof StrawberryProjectile) {
                        ProjectilePools.strawberryPool.free((StrawberryProjectile) p);
                    }
                }
            }

            // System.out.println(player.position);
            // moves enemies - assume always moving (no CONTROL_NO_ACTION)
            for (int i = 0; i < minions.size; i++) {
                Minion m = minions.get(i);
                if (!m.isDestroyed()) {
                    // System.out.println("CONTROL " + i);
                    int action = minionControls.get(i).getAction();
                    // System.out.println("Id: " + i + " (" + action + ")");
                    m.update(action);
                    m.setDamage(false);
                } else {
                    if (m.shouldRemove()){
                        minions.removeIndex(m.getId());
                        minionControls.removeIndex(m.getId());
                    }
                }
            }
            //
            // boss moves and acts
            for (int i = 0; i < bosses.size; i++) {
                bossControls.get(i).update(delta);
                bosses.get(i).update(delta, bossControls.get(i).getAction());
            }
            //
            // // player chain moves
            int a = playerControls.getAction();
            // System.out.println(a);
            player.update(a);
            //
            // // if board isn't updating then no point
            // state.getLevel().update();
            //
            // // projectiles update
            // //state.getProjectiles().update();
            if (player.isAlive()) {
                collision.update();
            }

            for (Coin c : coins) {
                c.update(delta);
            }
//            addMinions();
//            addCompanions();
        }
    }


    public void draw(float delta) {
        BitmapFont font = new BitmapFont();
        //FileHandle f = new FileHandle("fonts/LePetitCochon/LPC.fnt");
        //FileHandle image = new FileHandle("fonts/LePetitCochon/LPC.fnt");

        //Texture tt = new Texture("fonts/LPC.png");

       // TextureRegion t = new TextureRegion(tt);

        //System.out.println(t);
        //BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/Arial.fnt"));
        ScreenUtils.clear(Color.WHITE);

        game.batch.begin();
        if (!background) {
            Texture tileTexture = new Texture("images/Tile.png");
            game.batch.draw(tileTexture, 0, 0, 1280, 720);
        }

        for (Boss boss : bosses) {
            boss.draw(game.batch);
        }

        for (Minion m : minions) {
            m.draw(game.batch, delta);
        }


        player.draw(game.batch, delta);
        for (Companion c : companions) {
            String cost = "Cost: " + c.getCost();
            TextLayout compCost = new TextLayout(cost, font);
            TextLayout pressE = new TextLayout("E", font);
            c.draw(game.batch, delta);
            //temp UI
            if (!c.isCollected()) {
                game.batch.drawText(compCost, c.getX() + 35, c.getY());
                if (c.highlight){
                game.batch.drawText(pressE,c.getX(),c.getY()+35);
                }
            }

        }

        for (Projectile p : state.getActiveProjectiles()) {
            p.draw(game.batch);
        }

        for (Coin c : coins) {
            c.draw(game.batch);
        }

        String coins = "X" + player.getCoins();
        String HP = "Boss HP: " + bosses.get(0).getHealth();
//        TextLayout shield;
        TextLayout coinCount = new TextLayout(coins, font, 128);
        TextLayout bossHP = new TextLayout(HP, font, 128);
        //Temp UI
        game.batch.draw(coinTexture, 1140, 65, 45, 45);
        game.batch.drawText(bossHP, 600, 700);
        game.batch.drawText(coinCount, 1200f, 80f);

//        if (player.hasShield()) {
//            font.setColor(Color.GREEN);
//            shield = new TextLayout("Shield: On", font);
//
//        } else {
//            font.setColor(Color.RED);
//            shield = new TextLayout("Shield: Off", font);
//        }
//        game.batch.drawText(shield, 600, 20);
        font.setColor(Color.WHITE);


        if (!player.isAlive()) {
            drawLose();
        }

        if (bosses.get(0).isDestroyed()) {
            drawWin();
        }
        if (paused && !settingsOn) {
            drawPause();
        }
        if (settingsOn){
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
        Texture texture = new Texture("images/Lose.png");
        game.batch.draw(texture, 250, 150);

    }

    private void drawWin() {
        Texture texture = new Texture("images/Win.png");
        game.batch.draw(texture, 400, 150);

    }

    private void setStart() {
        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.DOWN) ||
            Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            start = true;
            reset = false;
        }
    }

    private void drawPause(){
        game.batch.setBlendMode(BlendMode.ALPHA_BLEND);
        game.batch.draw(pauseBackground,0,0);
        resetButton.draw(game.batch);
        levelsButton.draw(game.batch);
        settingsButton.draw(game.batch);
        exitButton.draw(game.batch);
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

    }
}
