package edu.cornell.cis3152.team8;

/**
 * Heavily inspired by the Optimization lab
 */

import java.util.Random;

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

    /** Minions in the level */
    private Minion[] minions;

    /** Level Boss */
    private Boss boss;

    /** List of all the input controllers */
    protected InputController playerControls;
    protected InputController[] minionControls;
    protected InputController bossControls;

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

        // assuming player is a list of Companions btw
        player = state.getPlayer();
        playerControls = new PlayerController();

        level = state.getLevel();

        // assuming each level has number of enemies assigned?
        minions = state.getMinions();
        minionControls = new InputController[minions.length];
        for(int i = 0; i < minions.length; i++) {
            minionControls[i] = new MinionController(i, state);
        }

        boss = state.getBoss();
        bossControls = new BossController(boss.getId(), state);
    }

    /**
     * Initializes the player to center of the board.
     *
     * UNLESS the player is also at random position.
     */
    private void initPlayerPosition() {
        float px = level.getWidth()/2;
        float py = level.getHeight()/2;

        player.setPosition(px,py);
    }

    /**
     * Initializes the minions to new random location.
     *
     */
    private void initMinionPosition() {
        Random rand = new Random();
        for (int i = 0; i < minions.length; i++) {
            minions[i].setX(rand.nextInt(level.getWidth()));
            minions[i].setY(rand.nextInt(level.getHeight()));
        }
    }

    /**
     * Initializes the companions to new random location.
     *
     */
    private void initCompanionPositions() {
        Random rand = new Random();
        for (int i = 0; i < player.size(); i++) {
            player[i].setX(rand.nextInt(level.getWidth()));
            player[i].setY(rand.nextInt(level.getHeight()));
        }
    }

    public int getPlayerSelection() {
        return playerControls.getSelection();
    }

    /**
     * Invokes the controller for each Object.
     *
     * Movement actions are determined, but not committed (e.g. the velocity
     * is updated, but not the position). New ability action is processed
     * but photon collisions are not.
     */
    public void update() {
        // companion chain uses ability
        for (Companion c : player) {
            if (c.canUse() && !c.isDestroyed()) {
                useAbility(c);
            } else {
                c.coolDown(true);
            }
        }

        // moves enemies - assume always moving (no CONTROL_NO_ACTION)
        for (int i = 0; i < minions.length; i++) {
            if (!minions[i].isDestroyed()) {
                int action = minionControls[i].getAction();
                minions[i].update(action);
            }
        }

        // boss moves and acts
        boss.update(bossControls.getAction());

        // player chain moves
        player.update(playerControls.getAction());

        // if board isn't updating then no point
//        state.getLevel().update();

        // projectiles update
        state.getProjectiles().update();
    }

    /**
     * Creates photons and updates the object's cooldown.
     *
     * Using an ability requires access to all other models? so we have factored
     * this behavior out of the Ship into the GameplayController.
     */
    private void useAbility(Companion c) {
        c.useAbility(state);
        // reset ability cooldown
        c.coolDown(false);
    }

}
