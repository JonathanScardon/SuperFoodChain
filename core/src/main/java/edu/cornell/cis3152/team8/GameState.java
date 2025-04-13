/**
 * Heavily inspired by GameSession in the AILab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.graphics.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.cis3152.team8.companions.Strawberry;

import java.util.LinkedList;

/**
 * This is the base model class for the game which stores all the model objects
 * in it.
 */

public class GameState {
    // Graphics assets
    private SpriteSheet mouseSprite;
    private SpriteSheet warningSprite;

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
    private LinkedList<Minion> minions;
    /** The bosses */
    private Boss[] bosses;
    /** The coins on the map */
    private LinkedList<Coin> coins;
    /**
     * The companions on the map
     */
    private LinkedList<Companion> companions;
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

    /**
     * Creates a new game session. This method will call reset() to set up the
     * board.
     *
     */
    public GameState(AssetDirectory assets) {
        mouseSprite = assets.getEntry("mouse.animation", SpriteSheet.class);
        warningSprite = assets.getEntry("mouseWarn.animation", SpriteSheet.class);
        projectiles = new Array<Projectile>();

        reset();
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
        bosses = new Boss[1];
        bosses[0] = new Mouse(100f, 100f, world);
        bosses[0].setSpriteSheet(mouseSprite);
        bosses[0].warnSprites.add(warningSprite);

        // Coins - none at the beginning
        coins = new LinkedList<>();

        // Projectiles
        projectiles = new Array<>();

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
    public LinkedList<Minion> getMinions() {
        return minions;
    }

    /**
     * Sets the minions in the level
     *
     * @param minions The minions to set
     */
    public void setMinions(LinkedList<Minion> minions) {this.minions = minions; }

    /**
     * @return the array of bosses in the level
     */
    public Boss[] getBosses() {
        return bosses;
    }

    /**
     * @return the array of coins in the level
     */
    public LinkedList<Coin> getCoins() {
        return coins;
    }

    /**
     * @return the array of companions in the level
     */
    public LinkedList<Companion> getCompanions() {
        return companions;
    }

    /**
     * Sets the companions in the level
     *
     * @param companions The companions to set
     */
    public void setCompanions(LinkedList<Companion> companions) {
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
}
