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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.cis3152.team8.companions.Durian;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.TextLayout;
import edu.cornell.gdiac.physics2.ObstacleSprite;
import edu.cornell.gdiac.util.ScreenListener;

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

    /** The grid of tiles */
    private Level level;

    /** Companions in the chain */
    private Player player;

    /** Minions in the level */
    private LinkedList<Minion> minions;

//    /** Bosses in the level */
    private Boss[] bosses;

    private LinkedList<Companion> companions;

    private LinkedList<Coin> coins;
    private Array<Projectile> projectiles;
    protected World world;

    /** List of all the input controllers */
    protected InputController playerControls;
    protected InputController[] minionControls;
    protected InputController[] bossControls;
    private ScreenListener listener;

    private Texture coinTexture;

    private boolean start;
    private boolean reset;
    private boolean debug;


    /**
     * Creates a GameScene
     *
     * @param game the GDX root
     */
    public GameScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        this.state = new GameState(assets);
        coinTexture = new Texture("images/coin.png");
        reset();
    }

    /**
     * Initializes the player to center of the board.
     *
     * UNLESS the player is also at random position.
     */
    private void initPlayerPosition() {
        float px = 15;
        float py = 10;
        Companion head = player.companions.get(0);
        head.getObstacle().setX(px);
        head.getObstacle().setY(py);
    }

    //
    // /**
    // * Initializes the minions to new random location.
    // *
    // */
    private void initMinions(int num_minions) {
        Random rand = new Random();
        minions = new LinkedList<>();
        for (int i = 0; i < num_minions; i++) {
            int x = rand.nextInt(1280);
            int y = rand.nextInt(720);
            Minion m = new Minion(x, y, i, world);
            // System.out.println("Id: " + i + " (" + x + ", " + y +")");
            minions.add(m);
        }
    }


     /**
     * Initializes the companions to new random location.
     *
     */
    private void initCompanionPositions(int numCompanions) {
        Random rand = new Random();
        companions = new LinkedList<>();
        for (int i = 0; i < numCompanions; i++) {
            Companion c;
            int x = rand.nextInt(1180);
            int y = rand.nextInt(620);
            if (i%2 == 0){
                c = new Strawberry(x,y, world);
            }else{
                c = new Durian(x, y, world);
            }
            companions.add(c);
        }
    }

    private void initCoins(int numCoins) {
        Random rand = new Random();
        coins = new LinkedList<>();
        for (int i = 0; i < companions.size(); i++) {
            int x = rand.nextInt(1280);
            int y = rand.nextInt(720);
            Coin c = new Coin(x, y, world);
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
        float frameTime = Math.min(delta, 0.25f);
        state.getWorld().step(frameTime, 6, 2);

        if (Gdx.input.isKeyPressed(Keys.R) && !reset){
            reset();
        }
        if (Gdx.input.isTouched()) {
            start = true;
            reset = false;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            debug = true;
        }

        if (start && player.isAlive() && bosses[0].getObstacle().isActive()) {
//        if (start && player.isAlive()) {
            // iterate through all companions in the chain
            for (Companion c : player.companions) {
                if (c.canUse()) {
                    c.useAbility(state);
                } else {
                    c.coolDown(true, delta);
                }
//                System.out.println(c.getObstacle().getName());
            }

            for (Projectile p : state.getActiveProjectiles()) {
                p.update(delta);
                System.out.println(p.getObstacle().getPosition());
            }
            System.out.println();

            // System.out.println(player.position);
            // moves enemies - assume always moving (no CONTROL_NO_ACTION)
            for (int i = 0; i < minions.size(); i++) {
                if (minions.get(i).getObstacle().isActive()) {
                    // System.out.println("CONTROL " + i);
                    int action = minionControls[i].getAction();
                    // System.out.println("Id: " + i + " (" + action + ")");
                    minions.get(i).update(action);
//                    System.out.println(minions[i].getObstacle().getName());
                }
            }
//            //
//            // boss moves and acts
            for (int i = 0; i < bosses.length; i++) {
                bosses[i].update(bossControls[i].getAction());
            }
            //
            // // player chain moves
            int a = playerControls.getAction();
            // System.out.println(a);
            player.update(a);
        }

        state.getCollisionController().postUpdate();
    }

    public void draw(float delta) {
        BitmapFont font = new BitmapFont();
        ScreenUtils.clear(Color.WHITE);

        game.batch.begin();
        drawTiles();

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
            if(!player.companions.contains(c)) {
                game.batch.drawText(compCost, c.getObstacle().getX() * 64f, c.getObstacle().getY() * 64f + 32f);
            }

        }

        for (Projectile p : state.getActiveProjectiles()) {
            p.draw(game.batch);
        }

        for (Coin c : coins) {
            c.draw(game.batch);
        }

        if (debug) {
            // Draw the outlines
            LinkedList<ObstacleSprite> sprites = new LinkedList<>();
            sprites.addAll(player.companions);
            sprites.addAll(minions);
            sprites.addAll(coins);
            sprites.addAll(companions);
            sprites.add(bosses[0]);
            for (ObstacleSprite obj : sprites) {
                obj.drawDebug( game.batch );
            }
        }

        String coins = "X" + player.getCoins();
        String HP = "Boss HP: " + bosses[0].getHealth();
        TextLayout shield;
        TextLayout coinCount = new TextLayout(coins, font, 128);
        TextLayout bossHP = new TextLayout(HP, font, 128);
        //Temp UI
        game.batch.draw(coinTexture, 1150, 65, 50, 50);
        game.batch.drawText(bossHP,600,700);
        game.batch.drawText(coinCount, 1200f, 80f);

        if (player.hasShield()){
            font.setColor(Color.GREEN);
            shield = new TextLayout("Shield: On", font);

        } else {
            font.setColor(Color.RED);
            shield = new TextLayout("Shield: Off", font);
        }
        game.batch.drawText(shield,600,20);
        font.setColor(Color.WHITE);

        if (!player.isAlive()) {
            drawLose();
        }

        if (!bosses[0].getObstacle().isActive()) {
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
        //int tileSize = 64;
        Texture tileTexture = new Texture("images/Background.png");
        game.batch.draw(tileTexture, 0, 0, 1280, 720);
//        for (int x = 0; x < 20; x++) {
//            for (int y = 0; y < 12; y++) {
//                float xx = (float) (x) * tileSize;
//                float yy = (float) (y) * tileSize;
//                game.batch.draw(tileTexture, xx, yy, tileSize, tileSize);
//            }
//        }
    }

    private void drawLose() {
        Texture texture = new Texture("images/Lose.png");
        game.batch.draw(texture, 250, 150);

    }

    private void drawWin() {
        Texture texture = new Texture("images/Win.png");
        game.batch.draw(texture, 400, 150);

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

    private void reset(){
        start = false;
        reset = true;
        state.reset();

        world = state.getWorld();

        player = new Player(500, 350, world);
        state.setPlayer(player);
        initMinions(5);
        state.setMinions(minions);
        initCompanionPositions(5);
        state.setCompanions(companions);
//        // initCoins(5);
        coins = state.getCoins();
        coins.clear();
        bosses = state.getBosses();
        bossControls = new InputController[bosses.length];
        bossControls[0] = new MouseController(bosses[0], state);
        projectiles = state.getActiveProjectiles();

        playerControls = new PlayerController(player);

        // level = state.getLevel();

        // assuming each level has number of enemies assigned?
        minionControls = new InputController[minions.size()];
        for (int i = 0; i < minions.size(); i++) {
            minionControls[i] = new MinionController(i, minions, player);
        }
    }
}
