package edu.cornell.cis3152.team8;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
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
    /**
     * Small class to allow for sorting the attack map
     */
    private record IndexedAttack(int idx, BossAttackPattern attack) { }

    private static final LevelLoader instance = new LevelLoader();
    private final TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
    private AssetDirectory assets = null;

    private Map<Integer, BossController> bossControllerMap;
    private Map<Integer, Array<IndexedAttack>> attackMap;

    // boss sprites
    private SpriteSheet mouseSprite;

    // warning sprites
    private SpriteSheet idleWarnSprite;
    private SpriteSheet dashWarnSprite;

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
        bossControllerMap = new HashMap<>();
        attackMap = new HashMap<>();

        // load assets
        mouseSprite = assets.getEntry("DashMouse.animation", SpriteSheet.class);
        idleWarnSprite = assets.getEntry("idleWarn.animation", SpriteSheet.class);
        dashWarnSprite = assets.getEntry("dashWarnVertical.animation", SpriteSheet.class);

        TiledMap map = this.mapLoader.load(path);

        MapLayer objects = map.getLayers().get("objects");

        // create all bosses first
        for (MapObject obj : objects.getObjects()) {
            if ("boss".equals(obj.getProperties().get("type", String.class))) {
                createBoss(obj, scene);
            }
        }

        // create attacks and attach them to bosses
        for (MapObject obj : objects.getObjects()) {
            if ("attack".equals(obj.getProperties().get("type", String.class))) {
                createAttack(obj, scene);
            }
        }

        for (Map.Entry<Integer, Array<IndexedAttack>> entry : attackMap.entrySet()) {
            int bossId = entry.getKey();
            Array<IndexedAttack> attacks = entry.getValue();

            // sort the array using the attackIdx property stored in each attack's source MapObject
            attacks.sort(Comparator.comparingInt(IndexedAttack::idx));

            // add the attacks to the boss controller
            BossController controller = bossControllerMap.get(bossId);
            for (IndexedAttack ia : attacks) {
                controller.addAttackPattern(ia.attack());
            }

            // automatically start the first attack
            controller.startAttack();
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
        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);

        switch (bossType) {
            case "mouse":
                boss = new Mouse(x, y);
                boss.setSpriteSheet(mouseSprite);
                bossController = new MouseController(boss, state);
                break;
            case "chef":
                break;
            case "chopsticks":
                break;
        }

        if (boss != null) {
            bossControllerMap.put(id, bossController);
            state.getBosses().add(boss);
            scene.bossControls.add(bossController);
        }
    }

    /**
     * Create an attack based on the MapObject, but add it to attackMap to be sorted and added to boss later
     *
     * @param obj   the attack object
     * @param scene the game scene
     */
    private void createAttack(MapObject obj, GameScene scene) {
        GameState state = scene.getState();
        String attackType = obj.getProperties().get("attackType", String.class);
        MapProperties props = obj.getProperties();

        BossAttackPattern attack = null;
        int attackIdx = props.get("attackIdx", Integer.class);
        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);
        float warnDuration = props.get("warnDuration", Float.class);
        int bossId = props.get("boss", TiledMapTileMapObject.class).getProperties().get("id", Integer.class);
        BossController bossController = bossControllerMap.get(bossId);

        switch (attackType) {
            case "idle":
                float attackDuration = props.get("attackDuration", Float.class);
                attack = new IdleAttackPattern(bossController, x, y, warnDuration, attackDuration, idleWarnSprite);
                break;
            case "dash":
                boolean top = props.get("top", Boolean.class);
                attack = new DashAttackPattern(bossController, x, top, warnDuration, dashWarnSprite,true);
                break;
        }

        if (attack != null) {
            if (!attackMap.containsKey(bossId)) {
                attackMap.put(bossId, new Array<>());
            }
            attackMap.get(bossId).add(new IndexedAttack(attackIdx, attack));
        }
    }
}
