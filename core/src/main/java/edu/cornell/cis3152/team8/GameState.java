/**
 * Heavily inspired by GameSession in the AILab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.obj.Material;
import edu.cornell.gdiac.graphics.obj.Model;
import edu.cornell.gdiac.graphics.obj.ModelRef;

/**
 * This is the base model class for the game which stores all the model objects in it.
 */

public class GameState {
    /** A reference to the asset directory (for on demand assets) */
    private AssetDirectory assets;

    /** The grid of tiles */
    private Level level;
    /** The party of companions controlled by the player */
    private Player player;
    /** The minions */
    private Minion[] minions;
    /** The boss */
    private Boss boss;
    /** The coins on the map */
    private Coin[] coins;
    /** The companions on the map */
    private Companion[] companions;
    /** Collection of projectiles on the screen */
    private Array<Projectile> projectiles;

    /**
     * Creates a new game session. This method will call reset() to set up the board.
     *
     * @param assets   The associated asset directory
     */
    public GameState(AssetDirectory assets) {
        this.assets = assets;
        reset();
    }

    /**
     * Generates the level and everything in it.
     */
    public void reset() {
        // are we using json?
        level = new Level();
        // tile information

        // Player --> first companion (not a list yet?)
        player = new Player();
        // player texture

        // Minions - requires information of number of minions
        for (int i = 0; i < num_enemies; i++ ) {
            minions[i] = new Minion(assets);
            // minion texture
        }

        // Boss
        boss = new Boss(assets);
        // boss texture

        // Coins - none at the beginning

        // Companions - requires information of number of companions
        for (int i = 0; i < num_companions; i++) {
            companions[i] = new Companion(assets);
            // companion texture
        }

//        // Projectives
//        projectiles = new ProjectilePool(assets);
//        // projectile texture
    }

    /**
     * @return the current level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @return the list of ships in the game
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the list of minions in the game
     */
    public Minion[] getMinions() {
        return minions;
    }

    /**
     * @return the boss in the game
     */
    public Boss getBoss() {
        return boss;
    }

    /**
     * @return the list of coins in the game
     */
    public Coin[] getCoins() {
        return coins;
    }

    /**
     * @return the list of companions in the game
     */
    public Companion[] getCompanions() {
        return companions;
    }

    /**
     * @return the set of active projectiles
     */
    public Array<Projectile> getProjectiles() {
        return projectiles;
    }
}
