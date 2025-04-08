package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class LevelLoader {
    /**
     * Apply the level specified in fileName to scene
     *
     * @param scene the scene we are modifying
     * @param level the config for the level we want to load
     */
    public static void apply(GameScene scene, JsonValue level) {
        GameState state = scene.getState();

        JsonValue layers = level.get("layers");
        for (JsonValue layer : layers) {
            String layerName = layer.getString("name");
            if (layerName.equals("background")) {
                // TODO: use the background given in the level
            } else if (layerName.equals("objects")) {
                JsonValue objects = layer.get("objects");
                for (JsonValue object : objects) {
                    String objectName = object.getString("name");

                    // TODO: probably want to check type of object before name
                    if (objectName.equals("mouse")) {
                        float x = object.getFloat("x");
                        float y = object.getFloat("y");

                        // placeholder values
                        float idleX = 0;
                        float idleY = 0;

                        JsonValue properties = object.get("properties");
                        for (JsonValue property : properties) {
                            String propertyName = property.getString("name");
                            if (propertyName.equals("idleX")) {
                                idleX = property.getFloat("value");
                            } else if (propertyName.equals("idleY")) {
                                idleY = property.getFloat("value");
                            }
                        }

                        Boss mouse = new Mouse(x, y);
                        mouse.setSpriteSheet(state.mouseSprite);
                        mouse.warnSprites.add(state.idleWarnSprite);
                        mouse.warnSprites.add(state.dashWarnSprite);
                        state.getBosses().add(mouse);
                        scene.bossControls.add(new MouseController(mouse, state, idleX, idleY));
                    }
                }
            }
        }
    }
}
