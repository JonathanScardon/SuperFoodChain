package edu.cornell.cis3152.team8;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.*;

public class LevelLoader {
    /**
     * Apply the level specified in fileName to scene
     *
     * @param scene the scene we are modifying
     * @param path the path to the tmx file config for the level we want to load
     */
    public static void load(GameScene scene, String path) {
        TiledMap map = new TmxMapLoader(new InternalFileHandleResolver()).load(path);

        MapLayer objects = map.getLayers().get("objects");
        for (MapObject obj : objects.getObjects()) {
            parseObject(obj, scene);
        }
    }

    private static void parseObject(MapObject obj, GameScene scene) {
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

    private static void createBoss(MapObject obj, GameScene scene) {
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
                mouse.setSpriteSheet(state.mouseSprite);
                mouse.warnSprites.add(state.idleWarnSprite);
                mouse.warnSprites.add(state.dashWarnSprite);
                state.getBosses().add(mouse);
                scene.bossControls.add(new MouseController(mouse, state, idleX, idleY));
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
