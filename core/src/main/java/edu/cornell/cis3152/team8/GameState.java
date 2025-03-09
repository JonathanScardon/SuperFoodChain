/**
 * Heavily inspired by GameSession in the AILab
 */

package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
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
    /** Collection of projectiles on the screen */
    private ProjectilePool projectiles;

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
     * @return the set of active projectiles
     */
    public ProjectilePool getProjectiles() {
        return projectiles;
    }
}
