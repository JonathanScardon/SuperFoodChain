/**
 * Heavily inspired by the Optimization lab
 */

package edu.cornell.cis3152.ailab;

public class GameplayController {
    /** How close to the center of the tile we need to be to stop drifting */
    private static final float DRIFT_TOLER = 1.0f;
    /** How fast we drift to the tile center when paused */
    private static final float DRIFT_SPEED = 0.325f;

    /** Reference to the game session */
    private GameState state;

    /** The grid of tiles */
    private Level level;

    /** Companions in the chain */
    private Player player;

    /** List of all the input controllers */
    protected InputController playerControls;
    protected InputController[] controls;

    /**
     * Creates a GameplayController for the given models.
     *
     * @param state   The game state
     */
    public GameplayController(GameState state) {
        this.state = state;

        initPlayerPosition();
        initMinionPosition();
        initCompanionPositions();

        player = state.getPlayer();
        playerControls = new PlayerController();

        level = state.getLevel();
        // assuming each level has number of enemies assigned?
        controls = new InputController[level.getEnemies()];
        for(int ii = 0; ii < controls.size(); ii++) {
            if (MINION) {
                controls[ii] = new MinionController(ii, session);
            }
            else {
                controls[ii] = new BossController(ii, session);
            }
        }
    }

    /**
     * Initializes the player to new random location.
     *
     * UNLESS the player is always at the center of the board.
     */
    private void initShipPositions() {
    }

    /**
     * Initializes the minions to new random location.
     *
     */
    private void initMinionPosition() {
    }

    /**
     * Initializes the companions to new random location.
     *
     */
    private void initCompanionPositions() {
    }

    public int getPlayerSelection() {
        return playerControls.getSelection();
    }

    public void update() {

    }

    /**
     * Creates photons and updates the object's cooldown.
     *
     * Using an ability requires access to all other models? so we have factored
     * this behavior out of the Ship into the GameplayController.
     */
    private void createsPhotons(GameObject obj) {

    }

}
