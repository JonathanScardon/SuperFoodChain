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

    private Array<MinionController> minionControls;
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
    private Array<Companion> companions;
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
    private Vector2[] companionSpawns;
    private int numMinionSpawns;
    private int numCompanionSpawns;
    private int maxStrawberry;
    private int maxPineapple;
    private int maxAvocado;

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

        companions = new Array<>();

        minions = new Array<>();
        minionControls = new Array<>();
        numMinionSpawns = minionConstants.getInt("Number of Spawns");
        numCompanionSpawns = companionConstants.getInt("Number of Spawns");

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
        Boss b = bosses.get(0);
        String state = b.getState();
        if (state.equals("Idle")){
            b.setSpriteSheet(mouseIdleSprite);
            b.setAnimationSpeed(0.1f);
        }else if (state.equals("Dash")){
            b.setSpriteSheet(mouseDashSprite);
            b.setAnimationSpeed(0.1f);
        } else if (state.equals("Spin")) {
            b.setSpriteSheet(mouseSpinSprite);
            b.setAnimationSpeed(0.5f);
        }

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
        mouse.setSpriteSheet(mouseDashSprite);
        mouse.warnSprites.add(idleWarnSprite);
        mouse.warnSprites.add(dashWarnVerticalSprite);
        mouse.warnSprites.add(dashWarnHorizontalSprite);
        mouse.warnSprites.add(spinWarnSprite);

        bosses.add(mouse);

        bossControls.add(new MouseController(mouse,this,640,360, bossAttack));

        minions.clear();
        minionControls.clear();
        companions.clear();
        projectiles.clear();
        deadCompanions.clear();


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
    public Array<Companion> getCompanions() {
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
