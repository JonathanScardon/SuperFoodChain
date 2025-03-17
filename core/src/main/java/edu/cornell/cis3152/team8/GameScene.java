package edu.cornell.cis3152.team8;

/**
 * Heavily inspired by the Optimization lab
 */

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.util.ScreenListener;

import java.util.LinkedList;
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
//    private Level level;
//
//    /** Companions in the chain */
//    private Player player;
//
//    /** Minions in the level */
//    private Minion[] minions;
//
    /** Bosses in the level */
    private Boss[] bosses;
//
//    /** List of all the input controllers */
//    protected InputController playerControls;
//    protected InputController[] minionControls;
    protected InputController[] bossControls;
//    private ScreenListener listener;

    /**
     * Creates a GameScene
     *
     * @param game the GDX root
     */
    public GameScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        this.state = new GameState(assets);

        //initPlayerPosition();
        //initMinionPosition();
        //initCompanionPositions();

        // assuming player is a list of Companions btw
//        player = state.getPlayer();
//        playerControls = new PlayerController();
//
//        level = state.getLevel();
//
//        // assuming each level has number of enemies assigned?
//        minions = state.getMinions();
//        minionControls = new InputController[minions.length];
//        for(int i = 0; i < minions.length; i++) {
//            minionControls[i] = new MinionController(i, state);
//        }

        bosses = state.getBosses();
        bossControls = new InputController[bosses.length];
        bossControls[0] = new MouseController(bosses[0], state);
    }

//    /**
//     * Initializes the player to center of the board.
//     *
//     * UNLESS the player is also at random position.
//     */
//    private void initPlayerPosition() {
//        float px = level.getWidth()/2;
//        float py = level.getHeight()/2;
//
//        //player.setPosition(px,py);
//    }
//
//    /**
//     * Initializes the minions to new random location.
//     *
//     */
//    private void initMinionPosition() {
//        Random rand = new Random();
//        for (int i = 0; i < minions.length; i++) {
//            minions[i].setX(rand.nextInt(level.getWidth()));
//            minions[i].setY(rand.nextInt(level.getHeight()));
//        }
//    }
//
//    /**
//     * Initializes the companions to new random location.
//     *
//     */
//    private void initCompanionPositions() {
//        Random rand = new Random();
//        LinkedList<Companion> comps = player.companions;
//        for (int i = 0; i < comps.size(); i++) {
//            comps.get(i).setX(rand.nextInt(level.getWidth()));
//            comps.get(i).setY(rand.nextInt(level.getHeight()));
//        }
//    }

    //public int getPlayerSelection() {
    // return playerControls.getSelection();


    /**
     * Invokes the controller for each Object.
     * <p>
     * Movement actions are determined, but not committed (e.g. the velocity
     * is updated, but not the position). New ability action is processed
     * but photon collisions are not.
     */
    public void update(float delta) {
//        // companion chain uses ability
//        for (Companion c : player.companions) {
//            if (c.canUse() && !c.isDestroyed()) {
//                useAbility(c);
//            } else {
//                c.coolDown(true);
//            }
//        }
//
//        // moves enemies - assume always moving (no CONTROL_NO_ACTION)
//        for (int i = 0; i < minions.length; i++) {
//            if (!minions[i].isDestroyed()) {
//                int action = minionControls[i].getAction();
//                minions[i].update(action);
//            }
//        }
//
        // boss moves and acts
        for (int i = 0; i < bosses.length; i++) {
            bosses[i].update(bossControls[i].getAction());
        }
//
//        // player chain moves
//        player.update(playerControls.getAction());
//
//        // if board isn't updating then no point
//        state.getLevel().update();
//
//        // projectiles update
//        //state.getProjectiles().update();
    }

    public void draw(float delta) {
        ScreenUtils.clear(Color.WHITE);

        game.batch.begin();
        drawTiles();

        for (Boss boss : bosses) {
            boss.draw(game.batch);
        }
        game.batch.end();
    }

//    /**
//     * Creates photons and updates the object's cooldown.
//     *
//     * Using an ability requires access to all other models? so we have factored
//     * this behavior out of the Ship into the GameplayController.
//     */
//    private void useAbility(Companion c) {
//        c.useAbility(state);
//        // reset ability cooldown
//        c.coolDown(false);
//    }


    private void drawTiles() {
        // technically this should be a call to the draw function inside of level
        int tileSize = 40;
        Texture tileTexture = new Texture("images/Tile1.png");
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 20; y++) {
                float xx = (float) (x) * tileSize;
                float yy = (float) (y) * tileSize;
                game.batch.draw(tileTexture, xx, yy, tileSize, tileSize);
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

    }
}
