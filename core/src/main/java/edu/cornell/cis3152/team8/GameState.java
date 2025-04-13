/**
 * Heavily inspired by GameSession in the AILab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
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
    // Graphics assets

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
    private Minion[] minions;
    /**
     * The bosses
     */
    private Array<Boss> bosses;
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

    /**
     * Creates a new game session. This method will call reset() to set up the
     * board.
     */
    public GameState(JsonValue constants, AssetDirectory assets) {
        this.constants = constants;

        projectiles = new Array<Projectile>();

        Boss.setConstants(this.constants.get("boss")); // TODO: maybe move this to LevelLoader?

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

        // Coins - none at the beginning

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
    public Minion[] getMinions() {
        return minions;
    }

    public void setMinions(Minion[] m) {
        minions = m;
    }

    /**
     * @return the array of bosses in the level
     */
    public Array<Boss> getBosses() {
        return bosses;
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
}
