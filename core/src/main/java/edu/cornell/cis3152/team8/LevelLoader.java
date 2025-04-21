package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a singleton class meant to load levels from the tmx file format
 */
public class LevelLoader {
    private static final LevelLoader instance = new LevelLoader();
    private static final int PHYSICS_UNITS = 64;
    private final TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
    private AssetDirectory assets = null;

    // boss sprites
    private SpriteSheet mouseSprite;
    private SpriteSheet chopsticksSprite;

    // warning sprites
    private SpriteSheet idleWarnSprite;
    private SpriteSheet dashWarnVerticalSprite;
    private SpriteSheet dashWarnHorizontalSprite;

    /**
     * @return The single instance of the LevelLoader class
     */
    public static LevelLoader getInstance() {
        return instance;
    }

    /**
     * Sets the asset directory for this LevelLoader
     *
     * @param assets the asset directory to use
     */
    public void setAssetDirectory(AssetDirectory assets) {
        this.assets = assets;
    }

    /**
     * Apply the level specified in fileName to scene
     *
     * @param scene the scene we are modifying
     * @param path  the path to the tmx file config for the level we want to load
     */
    public void load(GameScene scene, String path) {
        if (assets == null) {
            throw new RuntimeException("Asset directory not set");
        }
        GameState state = scene.getState();

        // load assets
        mouseSprite = assets.getEntry("dashMouse.animation", SpriteSheet.class);
        chopsticksSprite = assets.getEntry("chopsticks.animation", SpriteSheet.class);

        idleWarnSprite = assets.getEntry("idleWarn.animation", SpriteSheet.class);
        dashWarnVerticalSprite = assets.getEntry("dashWarnVertical.animation", SpriteSheet.class);
        dashWarnHorizontalSprite = assets.getEntry("dashWarnHorizontal.animation", SpriteSheet.class);

        TiledMap map = this.mapLoader.load(path);

        MapLayer bossLayer = map.getLayers().get("boss");
        MapLayer companionLayer = map.getLayers().get("companion");
        MapLayer minionLayer = map.getLayers().get("minion");

        // create player and companions
        for (MapObject obj : companionLayer.getObjects()) {
            if ("player".equals(obj.getProperties().get("type", String.class))) {
                createPlayer(obj, scene);
            } else if ("companion".equals(obj.getProperties().get("type", String.class))) {
                createCompanionSpawn(obj, scene);
            }
        }

        // create minions
        for (MapObject obj : minionLayer.getObjects()) {
            if ("minion".equals(obj.getProperties().get("type", String.class))) {
                createMinionSpawn(obj, scene);
            }
        }

        // create bosses and attach attacks to them
        for (MapObject obj : bossLayer.getObjects()) {
            if ("boss".equals(obj.getProperties().get("type", String.class))) {
                createBoss(obj, scene);
            }
        }

        if (state.getMinionSpawns().isEmpty()) {
            throw new RuntimeException("No minion spawns found");
        }
        if (state.getCompanionSpawns().isEmpty()) {
            throw new RuntimeException("No companion spawns found");
        }
    }

    /**
     * Create a boss and put it in the game scene
     *
     * @param obj   the boss object
     * @param scene the game scene
     */
    private void createBoss(MapObject obj, GameScene scene) {
        GameState state = scene.getState();
        String bossType = obj.getProperties().get("bossType", String.class);
        MapProperties props = obj.getProperties();

        Boss boss = null;
        BossController bossController = null;
        int id = props.get("id", Integer.class);
        float x = props.get("x", Float.class) / PHYSICS_UNITS;
        float y = props.get("y", Float.class) / PHYSICS_UNITS;
        int health = props.get("health", Integer.class) / PHYSICS_UNITS;

        switch (bossType) {
            case "mouse":
                boss = new Boss(x, y, health, state.getWorld());
                boss.setSpriteSheet(mouseSprite);
                bossController = new BossController(boss, state);
                break;
            case "chef":
                break;
            case "chopsticks":
                boss = new Boss(x, y, health, state.getWorld());
                boss.setSpriteSheet(chopsticksSprite);
                bossController = new BossController(boss, state);
                break;
        }

        if (boss == null) {
            throw new RuntimeException("Boss creation failed");
        }

        // get all attacks
        int attackIdx = 0;
        MapObject attackObj;
        BossAttackPattern attack;
        while (props.containsKey("attack" + attackIdx)) {
            attackObj = props.get("attack" + attackIdx, TiledMapTileMapObject.class);
            attack = createAttack(attackObj, bossController, state.getPlayer());
            bossController.addAttackPattern(attack);

            attackIdx++;
        }

        state.getBosses().add(boss);
        scene.bossControls.add(bossController);
    }

    /**
     * Create an attack object based on the MapObject
     *
     * @param obj        the attack object
     * @param controller the boss that will execute the attack
     */
    private BossAttackPattern createAttack(MapObject obj, BossController controller, Player player) {
        String attackType = obj.getProperties().get("attackType", String.class);
        MapProperties props = obj.getProperties();

        BossAttackPattern attack = null;
        int id = props.get("id", Integer.class);
        float x = props.get("x", Float.class) / PHYSICS_UNITS;
        float y = props.get("y", Float.class) / PHYSICS_UNITS;
        float warnDuration = props.get("warnDuration", Float.class);
        float attackDuration;

        switch (attackType) {
            case "idle":
                attackDuration = props.get("attackDuration", Float.class);
                attack = new IdleAttackPattern(controller, x, y, warnDuration, attackDuration, idleWarnSprite);
                break;
            case "dash":
                String dir = props.get("dir", String.class);
                if (dir.equals("up") || dir.equals("down")) {
                    attack = new DashAttackPattern(controller, x, y, dir, warnDuration, dashWarnVerticalSprite);
                } else if (dir.equals("left") || dir.equals("right")) {
                    attack = new DashAttackPattern(controller, x, y, dir, warnDuration, dashWarnHorizontalSprite);
                }
                break;
            case "snatch":
                attackDuration = props.get("attackDuration", Float.class);
                attack = new SnatchAttackPattern(controller, warnDuration, attackDuration, idleWarnSprite, player);
                break;
        }

        if (attack == null) {
            throw new RuntimeException("Attack creation failed");
        }

        return attack;
    }

    /**
     * Create a player and put it in the game scene
     *
     * @param obj   the player object
     * @param scene the game scene
     */
    private void createPlayer(MapObject obj, GameScene scene) {
        GameState state = scene.getState();
        MapProperties props = obj.getProperties();

        int id = props.get("id", Integer.class);
        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);

        Player player = new Player(x, y, state.getWorld());
        state.setPlayer(player);
    }

    /**
     * Create a companion spawn location and add it to the scene
     *
     * @param obj   the companion spawn object
     * @param scene the game scene
     */
    private void createCompanionSpawn(MapObject obj, GameScene scene) {
        GameState state = scene.getState();
        MapProperties props = obj.getProperties();

        int id = props.get("id", Integer.class);
        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);

        state.getCompanionSpawns().add(new Vector2(x, y));
    }

    /**
     * Create a minion spawn location and add it to the scene
     *
     * @param obj   the minion spawn object
     * @param scene the game scene
     */
    private void createMinionSpawn(MapObject obj, GameScene scene) {
        GameState state = scene.getState();
        MapProperties props = obj.getProperties();

        int id = props.get("id", Integer.class);
        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);

        state.getMinionSpawns().add(new Vector2(x, y));
    }
}
