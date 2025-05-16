package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class LosePopUp extends PopUp {

    public LosePopUp(GDXRoot game, GameScene gameScene) {
        super(game, gameScene, game.directory.getEntry("ratLose.animation", SpriteSheet.class));
        if (gameScene.getBossNames().contains("chef")) {
            background = game.directory.getEntry("ratLose.animation", SpriteSheet.class);
        } else if (gameScene.getBossNames().contains("chopsticks")) {
            background = game.directory.getEntry("chopsticksLose.animation", SpriteSheet.class);
        }

        animationSpeed = 0.1f;

        float buttonWidth = 80;
        float buttonHeight = 88;
        float gap = 40;
        float span = (buttonWidth * 3) + (gap * 2);
        float x = screenWidth / 2f - span / 2f;
        float y = (screenHeight / 2f - background.getRegionHeight() / 2f) - (buttonHeight / 2f);

        //Replay
        Skin s = new Skin(Gdx.files.internal("buttons/Replay.json"));
        ImageButton button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                gameScene.reset();
            }
        });

        //Home
        x += buttonWidth + gap;
        s = new Skin(Gdx.files.internal("buttons/Home.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                gameScene.dispose();
                game.exitScreen(gameScene, ExitCode.LEVELS);
            }
        });

        //Handbook
        x += buttonWidth + gap;
        s = new Skin(Gdx.files.internal("buttons/Handbook.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                game.exitScreen(gameScene, ExitCode.HANDBOOK);
            }
        });
        buttons = getButtons();
    }
}
