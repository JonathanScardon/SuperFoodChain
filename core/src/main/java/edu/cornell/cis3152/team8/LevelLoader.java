package edu.cornell.cis3152.team8;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.*;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

/**
 * This is a singleton class meant to load levels from the tmx file format
 */
public class LevelLoader {
    private static final LevelLoader instance = new LevelLoader();
    private final TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
    private AssetDirectory assets = null;

    // boss sprites, TODO: this is probably bad structure to be honest
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

        // load assets
        mouseSprite = assets.getEntry("mouse.animation", SpriteSheet.class);
        idleWarnSprite = assets.getEntry("idleWarn.animation", SpriteSheet.class);
        dashWarnSprite = assets.getEntry("dashWarn.animation", SpriteSheet.class);

        TiledMap map = this.mapLoader.load(path);

        MapLayer objects = map.getLayers().get("objects");
        for (MapObject obj : objects.getObjects()) {
            parseObject(obj, scene);
        }
    }

    private void parseObject(MapObject obj, GameScene scene) {
        String type = obj.getProperties().get("type", String.class);

        switch (type) {
            case "boss":
                createBoss(obj, scene);
                break;
            default:
                // something went wrong
                break;
        }
    }

    private void createBoss(MapObject obj, GameScene scene) {
        GameState state = scene.getState();
        String bossType = obj.getProperties().get("bossType", String.class);

        switch (bossType) {
            case "mouse":
                MapProperties properties = obj.getProperties();
                // starting position, only used at very beginning of game
                float x = properties.get("x", Float.class);
                float y = properties.get("y", Float.class);
                // position for idle attack
                float idleX = properties.get("idleX", Float.class);
                float idleY = properties.get("idleY", Float.class);

                Boss mouse = new Mouse(x, y);
                mouse.setSpriteSheet(mouseSprite);

                MouseController mouseController = new MouseController(mouse, state);
                mouseController.addAttackPattern(new IdleAttackPattern(mouseController, idleX, idleY, 2f, 2f, idleWarnSprite));
                int num_attacks = (int) Math.ceil(1280f / 120);
                for (int i = 0; i < num_attacks; i++) {
                    mouseController.addAttackPattern(new DashAttackPattern(mouseController, i * 120 + 60, i % 2 == 1, 2, dashWarnSprite));
                }

                state.getBosses().add(mouse);
                scene.bossControls.add(mouseController);
                mouseController.startAttack();
                break;
            case "chef":
                break;
            case "chopsticks":
                break;
            default:
                // something went wrong
                break;
        }
    }
}
