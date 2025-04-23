/**
 * Heavily inspired by GameSession in the AILab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.graphics.*;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics2.ObstacleSprite;

/**
 * This is the base model class for the game which stores all the model objects in it.
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

    private Array<ObstacleSprite> dead;
    /**
     * The party of companions controlled by the player
     */
    private Player player;
    /**
     * The minions
     */
    private Array<Minion> minions;
    //private Array<MinionController> minionControls;
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

    /**
     * The Box2D world
     */
    protected World world;

    /**
     * Gamestate constants
     */
    private JsonValue constants;

    protected int maxMinions;
    private int numCompanions;
    private Array<MinionSpawnPoint> minionSpawns;
    private Array<Vector2> companionSpawns;

    protected int maxCompanions;
    protected int maxStrawberries;
    protected int maxPineapples;
    protected int maxAvocados;
    protected int maxBlueRaspberries;
    protected int maxDurians;
    protected int numStrawberries;
    protected int numPineapples;
    protected int numAvocados;
    protected int numBlueRaspberries;
    protected int numDurians;

    /**
     * Creates a new game session. This method will call reset() to set up the board.
     */
    public GameState(JsonValue constants, AssetDirectory assets) {
        this.constants = constants;

        JsonValue bossConstants = this.constants.get("boss");

        companions = new Array<>();

        minions = new Array<>();
        //minionControls = new Array<>();

        coins = new Array<>();

        minionSpawns = new Array<>();
        companionSpawns = new Array<>();

        Boss.setConstants(bossConstants);

        mouseIdleSprite = assets.getEntry("IdleMouse.animation", SpriteSheet.class);
        mouseDashSprite = assets.getEntry("DashMouse.animation", SpriteSheet.class);
        mouseSpinSprite = assets.getEntry("SpinMouse.animation", SpriteSheet.class);

        idleWarnSprite = assets.getEntry("idleWarn.animation", SpriteSheet.class);
        dashWarnVerticalSprite = assets.getEntry("dashWarnVertical.animation", SpriteSheet.class);
        dashWarnHorizontalSprite = assets.getEntry("dashWarnHorizontal.animation",
            SpriteSheet.class);
        spinWarnSprite = assets.getEntry("spinWarn.animation", SpriteSheet.class);

        projectiles = new Array<Projectile>();

        dead = new Array<>();

        reset();
    }

    public void update() {
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
        world = new World(new Vector2(0, 0), true);
        collision = new CollisionController(this);
        world.setContactListener(collision);

        // Boss
        bosses = new Array<>();
        bossControls = new Array<>();

        minions.clear();
        //minionControls.clear();
        companions.clear();
        projectiles.clear();
        dead.clear();

        coins.clear();

        minionSpawns.clear();
        companionSpawns.clear();

        numAvocados = 0;
        numBlueRaspberries = 0;
        numDurians = 0;
        numPineapples = 0;
        numStrawberries = 0;

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
     * Returns whether an object is in the bounds
     */
    public boolean inBounds(ObstacleSprite o) {
        //TODO: might have to consider radius but idk how to get it from obstacle
        return o.getObstacle().getX() > 0.25 && o.getObstacle().getX() < 1280/64-0.25 && o.getObstacle().getY() > 0.25 && o.getObstacle().getY() < 720/64-0.25;
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
    public void setMinions(Array<Minion> minions) {
        this.minions = minions;
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

//    public Array<MinionController> getMinionControls() {
//        return minionControls;
//    }

    public Array<MinionSpawnPoint> getMinionSpawns() {
        return minionSpawns;
    }

    public Array<Vector2> getCompanionSpawns() {
        return companionSpawns;
    }

    public Array<ObstacleSprite> getDead() {
        return dead;
    }
}
