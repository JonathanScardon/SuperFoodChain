/**
 * Heavily inspired by GameSession in the AILab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.graphics.*;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.cis3152.team8.companions.Strawberry;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * This is the base model class for the game which stores all the model objects
 * in it.
 */

public class GameState {
    // Graphics assets
    // TODO: these should probably be private, but they are public for the level loader right now
    public SpriteSheet mouseIdleSprite;
    public SpriteSheet mouseDashSprite;
    public SpriteSheet mouseSpinSprite;
    public SpriteSheet dashWarnVerticalSprite;
    public SpriteSheet dashWarnHorizontalSprite;
    public SpriteSheet spinWarnSprite;
    public SpriteSheet idleWarnSprite;

    private Array<Companion> deadCompanions;
    /**
     * The party of companions controlled by the player
     */
    private Player player;
    /**
     * The minions
     */
    private Array<Minion> minions;
    private Array<MinionController> minionControls;
    /**
     * The bosses
     */
    private Array<Boss> bosses;
    private Array<BossController> bossControls;
    /**
     * The coins on the map
     */
    private Array<Coin> coins;
    /**
     * The companions on the map
     */
    private Array<Companion> companions;
    /**
     * Collection of projectiles on the screen
     */
    private Array<Projectile> projectiles;

    /**
     * The collision controller for the world
     */
    private CollisionController collision;

    /** The Box2D world */
    protected World world;

    /** Gamestate constants */
    private JsonValue constants;

    private int maxEnemies;
    private int numCompanions;
    private Vector2[] minionSpawns;
    private Vector2[] companionSpawns;
    private int numMinionSpawns;
    private int numCompanionSpawns;
    private int maxStrawberry;
    private int maxPineapple;
    private int maxAvocado;
    private int maxBlueRaspberry;

    private String bossAttack;

    /**
     * Creates a new game session. This method will call reset() to set up the
     * board.
     */
    public GameState(JsonValue constants, AssetDirectory assets) {
        this.constants = constants;

        JsonValue minionConstants = this.constants.get("Minion Spawns");
        JsonValue companionConstants = this.constants.get("Companion Spawns");
        JsonValue bossConstants = this.constants.get("boss");


        maxEnemies = minionConstants.getInt("Max Enemies");
        maxStrawberry = companionConstants.getInt("Max Strawberry");
        maxPineapple = companionConstants.getInt("Max Pineapple");
        maxAvocado = companionConstants.getInt("Max Avocado");
        maxBlueRaspberry = companionConstants.getInt("Max Blue Raspberry");

        companions = new Array<>();

        minions = new Array<>();
        minionControls = new Array<>();
        numMinionSpawns = minionConstants.getInt("Number of Spawns");
        numCompanionSpawns = companionConstants.getInt("Number of Spawns");

        coins = new Array<>();

        initMinionSpawns(this.constants.get("Minion Spawns"));
        initCompanionSpawns(this.constants.get("Companion Spawns"));
        Boss.setConstants(bossConstants);

        bossAttack =  bossConstants.getString("attack");

        mouseIdleSprite = assets.getEntry("IdleMouse.animation", SpriteSheet.class);
        mouseDashSprite = assets.getEntry("DashMouse.animation", SpriteSheet.class);
        mouseSpinSprite = assets.getEntry("SpinMouse.animation", SpriteSheet.class);

        idleWarnSprite = assets.getEntry("idleWarn.animation", SpriteSheet.class);
        dashWarnVerticalSprite = assets.getEntry("dashWarnVertical.animation", SpriteSheet.class);
        dashWarnHorizontalSprite = assets.getEntry("dashWarnHorizontal.animation", SpriteSheet.class);
        spinWarnSprite = assets.getEntry("spinWarn.animation", SpriteSheet.class);

        projectiles = new Array<Projectile>();

        deadCompanions = new Array<>();

        reset();
    }

    public void update(){
//        Boss b = bosses.get(0);
//        String state = b.getState();
//        if (state.equals("Idle")){
//            b.setSpriteSheet(mouseIdleSprite);
//            b.setAnimationSpeed(0.1f);
//        }else if (state.equals("Dash")){
//            b.setSpriteSheet(mouseDashSprite);
//            b.setAnimationSpeed(0.1f);
//        } else if (state.equals("Spin")) {
//            b.setSpriteSheet(mouseSpinSprite);
//            b.setAnimationSpeed(0.5f);
//        }

    }
    /**
     * Generates the level and everything in it.
     */
    public void reset() {
        if (world != null) {
            Array<Body> bodies = new Array<>();
            world.getBodies(bodies);
            for (Body b : bodies) {
                world.destroyBody(b);
            }
            world.dispose();
        }

        // will doSleep cause companions (inactive) to not contactListener
        world = new World(new Vector2(0,0), true);
        collision = new CollisionController(this);
        world.setContactListener(collision);

        // Boss
        bosses = new Array<>();
        bossControls = new Array<>();

        minions.clear();
        minionControls.clear();
        companions.clear();
        projectiles.clear();
        deadCompanions.clear();

        coins.clear();

        // Projectiles
        projectiles = new Array<>();
        ProjectilePools.initialize(world);

        // Companions - requires information of number of companions
        // for (int i = 0; i < num_companions; i++) {
        // companions[i] = new Companion(assets);
        // }
        // // companion texture

        // // Projectiles
        // projectiles = new ProjectilePool(assets);
        // // projectile texture
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

    /**
     * Sets the minions in the level
     *
     * @param minions The minions to set
     */
    public void setMinions(Array<Minion> minions) {this.minions = minions; }

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
    public Array<Coin> getCoins() {
        return coins;
    }

    /**
     * @return the array of companions in the level
     */
    public Array<Companion> getCompanions() {
        return companions;
    }

    /**
     * Sets the companions in the level
     *
     * @param companions The companions to set
     */
    public void setCompanions(Array<Companion> companions) {
        this.companions = companions;
    }

    /**
     * @return the array of active projectiles
     */
    public Array<Projectile> getActiveProjectiles() {
        return projectiles;
    }

    /**
     * @return CollisionController of current world
     */
    public CollisionController getCollisionController() {
        return collision;
    }

    /**
     * @return Box2D world
     */
    public World getWorld() {
        return world;
    }

    public int getMaxEnemies() {
        return maxEnemies;
    }

    public int getMaxStrawberry(){
        return maxStrawberry;
    }
    public int getMaxPineapple(){
        return maxPineapple;
    }
    public int getMaxAvocado(){
        return maxAvocado;
    }

    public int getMaxBlueRaspberry(){
        return maxBlueRaspberry;
    }

    public Array<MinionController> getMinionControls() {
        return minionControls;
    }
    public Vector2[] getMinionSpawns() {
        return minionSpawns;
    }
    public Vector2[] getCompanionSpawns() {
        return companionSpawns;
    }

    private void initMinionSpawns(JsonValue spawns){
        minionSpawns = new Vector2[numMinionSpawns];
        for (int i = 0; i < minionSpawns.length; i++) {
            float x = spawns.get("Spawn " + i ).getFloat(0);
            float y = spawns.get("Spawn " + i).getFloat(1);
            minionSpawns[i] = new Vector2(x,y);
        }
    }
    private void initCompanionSpawns(JsonValue spawns){
        companionSpawns = new Vector2[numCompanionSpawns];
        for (int i = 0; i < companionSpawns.length; i++) {
            float x = spawns.get("Spawn " + i ).getFloat(0);
            float y = spawns.get("Spawn " + i).getFloat(1);
            companionSpawns[i] = new Vector2(x,y);
        }
    }

    public Array<Companion>  getDeadCompanions(){
        return deadCompanions;
    }
}
