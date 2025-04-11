/**
 * Heavily inspired by GameSession in the AILab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.cis3152.team8.companions.Strawberry;

import java.util.ArrayList;

/**
 * This is the base model class for the game which stores all the model objects
 * in it.
 */

public class GameState {

    private final Array<MinionController> minionControls;
    // Graphics assets
    // TODO: these should probably be private, but they are public for the level loader right now
    public SpriteSheet mouseSprite;
    public SpriteSheet dashWarnSprite;
    public SpriteSheet idleWarnSprite;

    /**
     * The grid of tiles
     */
    private Level level;
    /**
     * The party of companions controlled by the player
     */
    private Player player;
    /**
     * The minions
     */
    private Array<Minion> minions;
    /**
     * The bosses
     */
    private Array<Boss> bosses;
    private Array<BossController> bossControls;
    /**
     * The coins on the map
     */
    private Coin[] coins;
    /**
     * The companions on the map
     */
    private Companion[] companions;
    /**
     * Collection of projectiles on the screen
     */
    private Array<Projectile> projectiles;

    /**
     * Gamestate constants
     */
    private JsonValue constants;

    private int maxEnemies;
    private int numCompanions;
    private Vector2[] minionSpawns;
    private int numSpawns;

    /**
     * Creates a new game session. This method will call reset() to set up the
     * board.
     */
    public GameState(JsonValue constants, AssetDirectory assets) {
        this.constants = constants;

        mouseSprite = assets.getEntry("mouse.animation", SpriteSheet.class);
        idleWarnSprite = assets.getEntry("idleWarn.animation", SpriteSheet.class);
        dashWarnSprite = assets.getEntry("dashWarn.animation", SpriteSheet.class);
        projectiles = new Array<Projectile>();
        maxEnemies = 5;
        numCompanions = 5;
        minions = new Array<>();
        minionControls = new Array<>();
        numSpawns = 5;
        initMinionSpawns(this.constants.get("Minion Spawns"));
        Boss.setConstants(this.constants.get("boss"));

        reset();
    }

    /**
     * Generates the level and everything in it.
     */
    public void reset() {
        // // are we using json?
        // level = new Level(25,25);
        // // tile information
        //
        // // Player --> first companion (not a list yet?)
        // player = new Player(13,13);
        // // player texture
        //
        // // Minions - requires information of number of minions
        // num_enemies = 0;
        // for (int i = 0; i < num_enemies; i++ ) {
        // minions[i] = new Minion(0,0);
        // // minion texture
        // }



        // Boss
        bosses = new Array<>();
        bossControls = new Array<>();
        Boss mouse = new Mouse(-100f, -100f);
        mouse.setSpriteSheet(mouseSprite);
        mouse.warnSprites.add(idleWarnSprite);
        mouse.warnSprites.add(dashWarnSprite);
        bosses.add(mouse);
        bossControls.add(new MouseController(mouse,this,640,360));



        // Coins - none at the beginning

        // Companions - requires information of number of companions
        // for (int i = 0; i < num_companions; i++) {
        // companions[i] = new Companion(assets);
        // // companion texture
    }

    // // Projectives
    // projectiles = new ProjectilePool(assets);
    // // projectile texture
    // }

    /**
     * @return the current level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @return the player in the level
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player in the level
     *
     * @param player The player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return the array of minions in the level
     */
    public Array<Minion> getMinions() {
        return minions;
    }

    public void setMinions(Array<Minion> m) {
        minions = m;
    }

    /**
     * @return the array of bosses in the level
     */
    public Array<Boss> getBosses() {
        return bosses;
    }
    public Array<BossController> getBossControls() {
        return bossControls;
    }

    /**
     * @return the array of coins in the level
     */
    public Coin[] getCoins() {
        return coins;
    }

    /**
     * @return the array of companions in the level
     */
    public Companion[] getCompanions() {
        return companions;
    }

    /**
     * @return the array of active projectiles
     */
    public Array<Projectile> getActiveProjectiles() {
        return projectiles;
    }

    public int getMaxEnemies() {
        return maxEnemies;
    }public int getNumCompanions() {
        return numCompanions;
    }
    public Array<MinionController> getMinionControls() {
        return minionControls;
    }
    public Vector2[] getMinionSpawns() {
        return minionSpawns;
    }

    private void initMinionSpawns(JsonValue spawns){
        minionSpawns = new Vector2[numSpawns];
        for (int i = 0; i < minionSpawns.length; i++) {
            float x = spawns.get("Spawn " + i ).getFloat(0);
            float y = spawns.get("Spawn " + i).getFloat(1);
            minionSpawns[i] = new Vector2(x,y);
        }
    }
}
