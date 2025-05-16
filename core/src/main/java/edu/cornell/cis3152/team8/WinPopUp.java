package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class WinPopUp extends PopUp {

    public WinPopUp(GDXRoot game, GameScene gameScene) {
        super(game, gameScene, game.directory.getEntry("win.animation", SpriteSheet.class));

        animationSpeed = 0.15f;

        float buttonWidth = 80;
        float buttonHeight = 88;

        //Replay
        Skin s = new Skin(Gdx.files.internal("buttons/Replay.json"));
        ImageButton button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                gameScene.reset();
            }
        });

        //Home
        s = new Skin(Gdx.files.internal("buttons/Home.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
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
        s = new Skin(Gdx.files.internal("buttons/Handbook.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                game.exitScreen(gameScene, ExitCode.HANDBOOK);
            }
        });

        //Next
        s = new Skin(Gdx.files.internal("buttons/Right.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        addButton(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                gameScene.dispose();
                game.exitScreen(gameScene, ExitCode.NEXT_LEVEL);
            }
        });
        buttons = getButtons();
    }

    private void setButtonPositions() {
        float buttonWidth = buttons.get(0).getWidth();
        float buttonHeight = buttons.get(0).getHeight();
        float x = screenWidth / 2f;
        float y = (screenHeight / 2f - background.getRegionHeight() / 2f) - (buttonHeight / 3f);
        float gap = 40;
        float span;
        if (gameScene.getLevel() != game.getTotalLevels()) {
            span = (buttonWidth * 4) + (gap * 3);
            x -= span / 2f;
            buttons.get(0).setPosition(x, y);
            x += buttonWidth + gap;
            buttons.get(1).setPosition(x, y);
            x += buttonWidth + gap;
            buttons.get(2).setPosition(x, y);
            x += buttonWidth + gap;
            buttons.get(3).setPosition(x, y);
            buttons.get(3).setVisible(true);
        } else { //Last level no next button
            span = (buttonWidth * 3) + (gap * 2);
            x -= span / 2f;
            buttons.get(0).setPosition(x, y);
            x += buttonWidth + gap;
            buttons.get(1).setPosition(x, y);
            x += buttonWidth + gap;
            buttons.get(2).setPosition(x, y);
            buttons.get(3).setVisible(false);
        }
    }

    @Override
    public void update(boolean popUpOn) {
        super.update(popUpOn);
        setButtonPositions();
    }
}
