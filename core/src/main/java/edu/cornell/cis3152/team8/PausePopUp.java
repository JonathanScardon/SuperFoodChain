package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.graphics.SpriteSheet;
import java.util.Arrays;

public class PausePopUp extends PopUp {

    public PausePopUp(GDXRoot game, GameScene gameScene) {
        super(game, gameScene,
            game.directory.getEntry("pauseBackground.animation", SpriteSheet.class));

        animationSpeed = 0f;

        int numRowButtons = 2;
        int numColButtons = 3;
        float buttonWidth = 250;
        float buttonHeight = 70;
        float paddingX = 50;
        float paddingY = 100;
        float gapX =
            ((background.getRegionWidth() - paddingX * 2) - (numRowButtons * buttonWidth)) / (
                numRowButtons - 1);
        float gapY =
            ((background.getRegionHeight() - paddingY * 2) - (numColButtons * buttonHeight)) / (
                numColButtons - 1);
        float spanX = (buttonWidth * numRowButtons) + (gapX * (numRowButtons - 1));
        float spanY = (buttonHeight * numColButtons) + (gapY * (numColButtons - 1));

        float x = (screenWidth / 2f - spanX / 2f) + paddingX;
        float y = (screenHeight / 2f - spanY / 2f) - paddingY / 4;

        Skin s = new Skin(Gdx.files.internal("buttons/Button.json"));
        TextButton button = new TextButton("Exit", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                gameScene.dispose();
                game.exitScreen(gameScene, ExitCode.EXIT_GAME);
            }
        });

        button = new TextButton("Restart", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y + (gapY + buttonHeight));
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                gameScene.setPaused(false);
                gameScene.reset();
            }
        });

        button = new TextButton("Resume", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y + (gapY + buttonHeight) * 2);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                gameScene.setPaused(false);
            }
        });

        x = (screenWidth / 2f + spanX / 2f) - paddingX - buttonWidth;

        button = new TextButton("Settings", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                gameScene.setSettingsOn(true);
            }
        });

        button = new TextButton("Handbook", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y + (gapY + buttonHeight));
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                //Do not dispose so can return to level
                game.exitScreen(gameScene, ExitCode.HANDBOOK);
            }
        });

        button = new TextButton("Levels", s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(x, y + (gapY + buttonHeight) * 2);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                gameScene.dispose();
                game.exitScreen(gameScene, ExitCode.LEVELS);
            }
        });

        buttons = getButtons();
    }
}
