package edu.cornell.cis3152.team8;

/**
 * Heavily inspired by the Optimization lab
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.TextLayout;
import edu.cornell.gdiac.util.ScreenListener;

import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.cis3152.team8.companions.Durian;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameScene implements Screen {
    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;

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
    private Boss[] bosses;

    private Companion[] companions;

    private LinkedList<Coin> coins;
    private Array<Projectile> projectiles;

    /**
     * List of all the input controllers
     */
    protected InputController playerControls;
    protected InputController[] minionControls;
    protected BossController[] bossControls;

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
        reset();
    }

    private void reset() {
        start = false;
        reset = true;
        state.reset();
        player = new Player(500, 350);
        state.setPlayer(player);
        initMinions(5);
        state.setMinions(minions);
        initCompanionPositions(5);
        // initCoins(5);
        coins = new LinkedList<>();
        bosses = state.getBosses();
        bossControls = new BossController[bosses.length];
        bossControls[0] = new MouseController(bosses[0], state);
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

    //
    // /**
    // * Initializes the minions to new random location.
    // *
    // */
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

    //
    // /**
    // * Initializes the companions to new random location.
    // *
    // */
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
                c = new Durian(x, y);
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

    // public int getPlayerSelection() {
    // return playerControls.getSelection();

    /**
     * Invokes the controller for each Object.
     * <p>
     * Movement actions are determined, but not committed (e.g. the velocity
     * is updated, but not the position). New ability action is processed
     * but photon collisions are not.
     */
    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Keys.R) && !reset) {
            reset();
        }
        setStart();

        if (start && player.isAlive() && !bosses[0].isDestroyed()) {
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
            for (int i = 0; i < bosses.length; i++) {
                bossControls[i].update(delta);
                bosses[i].update(delta, bossControls[i].getAction());
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

            for (Coin c: coins){
                c.update(delta);
            }
        }
    }

    public void draw(float delta) {
        BitmapFont font = new BitmapFont();
        ScreenUtils.clear(Color.WHITE);

        game.batch.begin();

        if (!background){
            drawTiles();
        }

        for (Boss boss : bosses) {
            boss.draw(game.batch);
        }

        for (Minion m : minions) {
            m.draw(game.batch);
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
        String HP = "Boss HP: " + bosses[0].getHealth();
        TextLayout shield;
        TextLayout coinCount = new TextLayout(coins, font, 128);
        TextLayout bossHP = new TextLayout(HP, font, 128);
        //Temp UI
        game.batch.draw(coinTexture, 1150, 65, 50, 50);
        game.batch.drawText(bossHP, 600, 700);
        game.batch.drawText(coinCount, 1200f, 80f);

        if (player.hasShield()) {
            font.setColor(Color.GREEN);
            shield = new TextLayout("Shield: On", font);

        } else {
            font.setColor(Color.RED);
            shield = new TextLayout("Shield: Off", font);
        }
        game.batch.drawText(shield, 600, 20);
        font.setColor(Color.WHITE);

        if (!player.isAlive()) {
            drawLose();
        }

        if (bosses[0].isDestroyed()) {
            drawWin();
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

    private void drawTiles() {
        // technically this should be a call to the draw function inside of level
        int tileSize = 64;
        Texture tileTexture = new Texture("images/Tile.png");
        //Texture tileTexture = new Texture("images/Background.png");
        game.batch.draw(tileTexture, 0, 0, 1280, 720);
//        for (int x = 0; x < 20; x++) {
//            for (int y = 0; y < 12; y++) {
//                float xx = (float) (x) * tileSize;
//                float yy = (float) (y) * tileSize;
//                game.batch.draw(tileTexture, xx, yy, tileSize, tileSize);
//            }
//        }
//        System.out.println("Draw Tiles");
    }

    private void drawLose() {
        Texture texture = new Texture("images/Lose.png");
        game.batch.draw(texture, 250, 150);

    }

    private void drawWin() {
        Texture texture = new Texture("images/Win.png");
        game.batch.draw(texture, 400, 150);

    }

    private void setStart(){
        if (Gdx.input.isKeyPressed(Keys.UP)||Gdx.input.isKeyPressed(Keys.DOWN)||
            Gdx.input.isKeyPressed(Keys.LEFT)||Gdx.input.isKeyPressed(Keys.RIGHT)){
            start = true;
            reset = false;
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

    }
}
