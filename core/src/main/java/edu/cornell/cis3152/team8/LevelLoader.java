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

    private static final LevelLoader instance = new LevelLoader();
    private final TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
    private AssetDirectory assets = null;

    // boss sprites
    private SpriteSheet mouseSprite;

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

        // load assets
        mouseSprite = assets.getEntry("dashMouse.animation", SpriteSheet.class);
        idleWarnSprite = assets.getEntry("idleWarn.animation", SpriteSheet.class);
        dashWarnVerticalSprite = assets.getEntry("dashWarnVertical.animation", SpriteSheet.class);
        dashWarnHorizontalSprite = assets.getEntry("dashWarnHorizontal.animation", SpriteSheet.class);

        TiledMap map = this.mapLoader.load(path);

        MapLayer objects = map.getLayers().get("objects");

        // create bosses and attach attacks to them
        for (MapObject obj : objects.getObjects()) {
            if ("boss".equals(obj.getProperties().get("type", String.class))) {
                createBoss(obj, scene);
            }
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
                boss = new Mouse(x, y,state.getWorld());
                boss.setSpriteSheet(mouseSprite);
                bossController = new MouseController(boss, state);
                break;
            case "chef":
                break;
            case "chopsticks":
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
            attack = createAttack(attackObj, bossController);
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
    private BossAttackPattern createAttack(MapObject obj, BossController controller) {
        String attackType = obj.getProperties().get("attackType", String.class);
        MapProperties props = obj.getProperties();

        BossAttackPattern attack = null;
        int id = props.get("id", Integer.class);
        float x = props.get("x", Float.class);
        float y = props.get("y", Float.class);
        float warnDuration = props.get("warnDuration", Float.class);

        switch (attackType) {
            case "idle":
                float attackDuration = props.get("attackDuration", Float.class);
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
        }

        if (attack == null) {
            throw new RuntimeException("Attack creation failed");
        }

        return attack;
    }
}
