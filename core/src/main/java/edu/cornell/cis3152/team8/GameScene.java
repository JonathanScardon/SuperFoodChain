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
import edu.cornell.cis3152.team8.companions.Pineapple;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;
import edu.cornell.gdiac.util.ScreenListener;

import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.cis3152.team8.companions.Durian;

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
    private Minion[] minions;

    /**
     * Bosses in the level
     */
    private Array<Boss> bosses;

    /**
     * Companions in the level
     */
    private Companion[] companions;

    private LinkedList<Coin> coins;
    private Array<Projectile> projectiles;

    /**
     * List of all the input controllers
     */
    protected InputController playerControls;
    protected InputController[] minionControls;
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

    /**
     * Creates a GameScene
     *
     * @param game the GDX root
     */
    public GameScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        coinTexture = new Texture("images/CoinUI.png");
        constants = assets.getEntry("constants", JsonValue.class);
        this.state = new GameState(constants, assets);
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
        reset();
    }

    private void reset() {
        start = false;
        reset = true;
        paused = false;
        state.reset();
        player = new Player(500, 350);
        state.setPlayer(player);
        initMinions(5);
        state.setMinions(minions);
        initCompanionPositions(5);
        // initCoins(5);
        coins = new LinkedList<>();
        bosses = state.getBosses();
        bossControls = new Array<>();

        projectiles = state.getActiveProjectiles();

        collision = new CollisionController(minions, player, companions, coins, bosses, projectiles);

        // assuming player is a list of Companions btw
        // player = state.getPlayer();
        playerControls = new PlayerController(player);

        // level = state.getLevel();

        // assuming each level has number of enemies assigned?
        minionControls = new InputController[minions.length];
        for (int i = 0; i < minions.length; i++) {
            minionControls[i] = new MinionController(i, minions, player);
        }
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
    private void initMinions(int num_minions) {
        Random rand = new Random();
        minions = new Minion[num_minions];
        for (int i = 0; i < num_minions; i++) {
            int x = rand.nextInt(1280);
            int y = rand.nextInt(720);
            Minion m = new Minion(x, y, i);
            // System.out.println("Id: " + i + " (" + x + ", " + y +")");
            minions[i] = m;
        }
    }


     /**
     * Initializes the companions to new random location.
     */
    private void initCompanionPositions(int numCompanions) {
        Random rand = new Random();
        companions = new Companion[numCompanions];
        for (int i = 0; i < companions.length; i++) {
            Companion c;
            int x = rand.nextInt(1180);
            int y = rand.nextInt(620);
            if (i % 2 == 0) {
                c = new Strawberry(x, y);
            } else {
                c = new Pineapple(x, y);
            }
            companions[i] = c;
        }
    }

    private void initCoins(int numCoins) {
        Random rand = new Random();
        coins = new LinkedList<>();
        for (int i = 0; i < companions.length; i++) {
            int x = rand.nextInt(1280);
            int y = rand.nextInt(720);
            Coin c = new Coin(x, y);
            coins.add(c);
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
            for (Companion c : player.companions) {
                if (c.canUse()) {
                    c.useAbility(state);
                } else {
                    c.coolDown(true, delta);
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
            for (int i = 0; i < minions.length; i++) {
                if (!minions[i].isDestroyed()) {
                    // System.out.println("CONTROL " + i);
                    int action = minionControls[i].getAction();
                    // System.out.println("Id: " + i + " (" + action + ")");
                    minions[i].update(action);
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


        player.draw(game.batch);
        for (Companion c : companions) {
            String cost = "Cost: " + c.getCost();
            TextLayout compCost = new TextLayout(cost, font);
            c.draw(game.batch);
            //temp UI
            if (!player.companions.contains(c)) {
                game.batch.drawText(compCost, c.getX() + 35, c.getY());
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
