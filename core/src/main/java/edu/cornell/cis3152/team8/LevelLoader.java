package edu.cornell.cis3152.team8;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.cis3152.team8.companions.Avocado;
import edu.cornell.cis3152.team8.companions.BlueRaspberry;
import edu.cornell.cis3152.team8.companions.Durian;
import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

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
    private GameState state;

    // boss sprites
    private SpriteSheet mouseIdleSprite;
    private SpriteSheet mouseDashVerticalSprite;
    private SpriteSheet mouseDashHorizontalSprite;
    private SpriteSheet mouseSpinSprite;
    private SpriteSheet mouseDeathSprite;

    private SpriteSheet chopsticksIdleSprite;
    private SpriteSheet chopsticksDashSprite;

    private SpriteSheet chefIdleSprite;

    // warning sprites
    private SpriteSheet warnIconSprite;
    private SpriteSheet spinWarnSprite;

    // map of ids to minion spawn points
    private Map<Integer, MinionSpawnPoint> minionSpawns;

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
        state = scene.getState();

        // load assets
        mouseIdleSprite = assets.getEntry("idleMouse.animation", SpriteSheet.class);
        mouseDashVerticalSprite = assets.getEntry("dashMouseVertical.animation", SpriteSheet.class);
        mouseDashHorizontalSprite = assets.getEntry("dashMouseHorizontal.animation",
            SpriteSheet.class);
        mouseSpinSprite = assets.getEntry("spinMouse.animation", SpriteSheet.class);
        mouseDeathSprite = assets.getEntry("deathMouse.animation", SpriteSheet.class);

        chopsticksIdleSprite = assets.getEntry("idleChopsticks.animation", SpriteSheet.class);
        chopsticksDashSprite = assets.getEntry("dashChopsticks.animation", SpriteSheet.class);

        chefIdleSprite = assets.getEntry("idleChef.animation", SpriteSheet.class);

        warnIconSprite = assets.getEntry("warnIcon.animation", SpriteSheet.class);
        spinWarnSprite = assets.getEntry("spinWarn.animation", SpriteSheet.class);

        TiledMap map = this.mapLoader.load(path);

        MapLayer companionLayer = map.getLayers().get("companion");
        MapLayer bossLayer = map.getLayers().get("boss");
        minionSpawns = new HashMap<>();

        loadCompanionLayer(companionLayer, state);
        loadBossLayer(bossLayer, state, scene);

        if (state.getBosses().isEmpty()) {
            throw new RuntimeException("No bosses found");
        }
        if (state.getMinionSpawns().isEmpty()) {
            throw new RuntimeException("No minion spawns found");
        }
        if (state.getCompanionSpawns().isEmpty()) {
            throw new RuntimeException("No companion spawns found");
        }
    }

    private void loadCompanionLayer(MapLayer companionLayer, GameState state) {
        MapProperties layerProps = companionLayer.getProperties();
        state.maxCompanions = layerProps.get("maxCompanions", 0, Integer.class);
        state.maxAvocados = layerProps.get("maxAvocado", 0, Integer.class);
        state.maxDurians = layerProps.get("maxDurian", 0, Integer.class);
        state.maxPineapples = layerProps.get("maxPineapple", 0, Integer.class);
        state.maxStrawberries = layerProps.get("maxStrawberry", 0, Integer.class);
        state.maxBlueRaspberries = layerProps.get("maxBlueRaspberry", 0, Integer.class);
        for (MapObject obj : companionLayer.getObjects()) {
            if ("playerSpawn".equals(obj.getProperties().get("type", String.class))) {
                createPlayer(obj, state);
            } else if ("companionSpawn".equals(obj.getProperties().get("type", String.class))) {
                createCompanionSpawn(obj, state);
            }
        }
    }

    private void loadBossLayer(MapLayer bossLayer, GameState state, GameScene scene) {
        MapProperties layerProps = bossLayer.getProperties();
        state.maxMinions = layerProps.get("maxMinions", 0, Integer.class);

        // create minion spawners first
        for (MapObject obj : bossLayer.getObjects()) {
            if ("minionSpawn".equals(obj.getProperties().get("type", String.class))) {
                createMinionSpawn(obj, state);
            }
        }

        for (MapObject obj : bossLayer.getObjects()) {
            if ("boss".equals(obj.getProperties().get("type", String.class))) {
                createBoss(obj, state, scene);
            }
        }
    }

    /**
     * Create a boss and put it in the game state
     *
     * @param obj   the boss object
     * @param state the game state
     * @param scene the game scene
     */
    private void createBoss(MapObject obj, GameState state, GameScene scene) {
        String bossType = obj.getProperties().get("bossType", String.class);
        MapProperties props = obj.getProperties();

        Boss boss = null;
        BossController bossController;
        float x = props.get("x", Float.class) / PHYSICS_UNITS;
        float y = props.get("y", Float.class) / PHYSICS_UNITS;
        int health = props.get("health", 0, Integer.class);

        switch (bossType) {
            case "mouse":
                boss = new Boss(x, y, 1.5f, 1.5f, health, bossType, state.getWorld());
                boss.addAnimation("default", mouseDashVerticalSprite);
                boss.addAnimation("idle", mouseIdleSprite);
                boss.addAnimation("dashVertical", mouseDashVerticalSprite);
                boss.addAnimation("dashHorizontal", mouseDashHorizontalSprite);
                boss.addAnimation("spin", mouseSpinSprite);
                boss.addAnimation("death", mouseDeathSprite);
                boss.spriteScale.set(0.4f, 0.4f);
                break;
            case "chopsticks":
                boss = new Boss(x, y, 1.5f, 1.5f, health, bossType, state.getWorld());
                boss.addAnimation("default", chopsticksIdleSprite);
                boss.addAnimation("idle", chopsticksIdleSprite);
                boss.addAnimation("snatch", chopsticksDashSprite);
                boss.addAnimation("death", mouseDeathSprite);
                boss.spriteScale.set(0.4f, 0.4f);
                break;
            case "chef":
                boss = new Boss(x, y, 1.5f, 1.5f, health, bossType, state.getWorld());
                boss.addAnimation("default", chefIdleSprite);
                boss.addAnimation("idle", chefIdleSprite);
                boss.addAnimation("death", mouseDeathSprite);
                boss.spriteScale.set(1f, 1f);
                break;
        }

        if (boss == null) {
            throw new RuntimeException("Boss creation failed");
        }
        bossController = new BossController(boss, state);
        boss.setAnimation("default");

        // get all attacks
        int attackIdx = 0;
        MapObject attackObj;
        BossAttackPattern attack;
        while (props.containsKey("attack" + attackIdx)) {
            attackObj = props.get("attack" + attackIdx, MapObject.class);
            attack = createAttack(attackObj, bossController, state.getPlayer(), scene);
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
    private BossAttackPattern createAttack(MapObject obj, BossController controller,
                                           Player player, GameScene scene) {
        String attackType = obj.getProperties().get("attackType", String.class);
        MapProperties props = obj.getProperties();

        BossAttackPattern attack = null;
        float x = props.get("x", Float.class) / PHYSICS_UNITS;
        float y = props.get("y", Float.class) / PHYSICS_UNITS;
        float warnDuration = props.get("warnDuration", 0f, Float.class);
        float attackDuration;
        float moveSpeed;

        switch (attackType) {
            case "idle":
                attackDuration = props.get("attackDuration", 0f, Float.class);
                Boolean flipHorizontal = props.get("flipHorizontal", false, Boolean.class);
                attack = new IdleAttackPattern(controller, x, y, warnDuration, attackDuration, flipHorizontal,
                    warnIconSprite);
                break;
            case "dash":
                String dir = props.get("dir", String.class);
                moveSpeed = props.get("moveSpeed", 0f, Float.class);
                attack = new DashAttackPattern(controller, x, y, dir, warnDuration, moveSpeed,
                    warnIconSprite);
                break;
            case "spin":
                moveSpeed = props.get("moveSpeed", 0f, Float.class);
                attack = new SpinAttackPattern(controller, warnDuration, moveSpeed, spinWarnSprite,
                    player, state);
                break;
            case "snatch":
                attackDuration = props.get("attackDuration", 0f, Float.class);
                attack = new SnatchAttackPattern(controller, warnDuration, attackDuration,
                    warnIconSprite, player);
                break;
            case "camera":
                attack = new CameraAttackPattern(controller, x * PHYSICS_UNITS, y * PHYSICS_UNITS, warnDuration, scene.getWorldCamera());
                break;
            case "multi":
                Array<BossAttackPattern> attackPatterns = new Array<>();

                // get all attacks
                int attackIdx = 0;
                MapObject attackObj;
                BossAttackPattern subAttack;
                while (props.containsKey("attack" + attackIdx)) {
                    attackObj = props.get("attack" + attackIdx, MapObject.class);
                    attack = createAttack(attackObj, controller, state.getPlayer(), scene);
                    attackPatterns.add(attack);

                    attackIdx++;
                }

                attack = new MultiAttackPattern(controller, warnDuration, attackPatterns, warnIconSprite);
                break;
        }

        if (attack == null) {
            throw new RuntimeException("Attack creation failed");
        }

        // get all minion spawns
        int spawnIdx = 0;
        int spawnId;
        MinionSpawnPoint spawn;
        while (props.containsKey("minion" + spawnIdx)) {
            spawnId = props.get("minion" + spawnIdx, MapObject.class).getProperties()
                .get("id", Integer.class);
            spawn = minionSpawns.get(spawnId);
            attack.addMinionSpawnPoint(spawn);

            spawnIdx++;
        }

        return attack;
    }

    /**
     * Create a player and put it in the game state
     *
     * @param obj   the player object
     * @param state the game state
     */
    private void createPlayer(MapObject obj, GameState state) {
        MapProperties props = obj.getProperties();

        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);
        String companionType = props.get("companionType", String.class);

        Player player = new Player(x, y, state.getWorld());

        Companion head = switch (companionType) {
            case "strawberry" -> new Strawberry(x, y, 0, state.getWorld());
            case "durian" -> new Durian(x, y, 0, state.getWorld());
            case "avocado" -> new Avocado(x, y, 0, state.getWorld());
            case "blueRaspberry" -> new BlueRaspberry(x, y, 0, state.getWorld());
            default -> null;
        };
        player.addCompanion(head);

        Companion.resetBoost();

        state.setPlayer(player);
    }

    /**
     * Create a companion spawn location and add it to the state
     *
     * @param obj   the companion spawn object
     * @param state the game state
     */
    private void createCompanionSpawn(MapObject obj, GameState state) {
        MapProperties props = obj.getProperties();

        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);

        state.getCompanionSpawns().add(new Vector2(x, y));
    }

    /**
     * Create a minion spawn location and add it to the state
     *
     * @param obj   the minion spawn object
     * @param state the game state
     */
    private void createMinionSpawn(MapObject obj, GameState state) {
        MapProperties props = obj.getProperties();

        int id = props.get("id", Integer.class);
        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);

        boolean bossOnly = props.get("bossOnly", false, Boolean.class);
        float antSpawnProportion = props.get("antSpawnProportion", 0f, Float.class);
        float cricketSpawnProportion = props.get("cricketSpawnProportion", 0f, Float.class);
        float spiderSpawnProportion = props.get("spiderSpawnProportion", 0f, Float.class);

        MinionSpawnPoint spawn = new MinionSpawnPoint(state, x, y, bossOnly, antSpawnProportion,
            cricketSpawnProportion, spiderSpawnProportion);

        state.getMinionSpawns().add(spawn);
        this.minionSpawns.put(id, spawn);
    }
}
