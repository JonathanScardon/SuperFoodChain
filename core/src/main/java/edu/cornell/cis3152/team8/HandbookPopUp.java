package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.SpriteSheet;
import com.badlogic.gdx.utils.Array;

public class HandbookPopUp {

    private GDXRoot game;
    private GameScene gameScene;
    private Array<HandbookPopUpPage> pages;
    private int curPopUp;
    private boolean popUpsOn;

    public HandbookPopUp(GDXRoot game, GameScene gameScene) {
        this.game = game;
        this.gameScene = gameScene;
        pages = new Array<>();
        curPopUp = 0;
        popUpsOn = false;
        setPages();
    }

    private void setPages() {
        if (gameScene.getLevel() == 1) {
            pages.add(new HandbookPopUpPage(game, gameScene, "Companions"));
            pages.add(new HandbookPopUpPage(game, gameScene, "Minions"));
            pages.add(new HandbookPopUpPage(game, gameScene, "Bosses"));
        }
        if (gameScene.getLevel() == 2) {
            pages.add(new HandbookPopUpPage(game, gameScene, "Companions"));
        }
        if (gameScene.getLevel() == 3) {
            pages.add(new HandbookPopUpPage(game, gameScene, "Minions"));
        }
        if (gameScene.getLevel() == 4) {
            pages.add(new HandbookPopUpPage(game, gameScene, "Bosses"));
        }
        if (gameScene.getLevel() == 5) {
            pages.add(new HandbookPopUpPage(game, gameScene, "Companions"));
        }
        if (gameScene.getLevel() == 6) {
            pages.add(new HandbookPopUpPage(game, gameScene, "Companions"));
            pages.add(new HandbookPopUpPage(game, gameScene, "Bosses"));
        }
        if (gameScene.getLevel() == 7) {
            pages.add(new HandbookPopUpPage(game, gameScene, "Minions"));
        }
    }

    /**
     * @return are all of the popups closed
     */
    public boolean arePopUpsOn() {
        return popUpsOn;
    }

    public void setPopUpsOn() {
        for (HandbookPopUpPage p : pages) {
            p.setOn(true);
        }
    }

    public void resetCurPopUp() {
        curPopUp = 0;
    }

    public void update() {
        if (!pages.isEmpty()) {
            popUpsOn = false;
            for (HandbookPopUpPage p : pages) {
                if (p.on) {
                    popUpsOn = true;
                    break;
                }
            }
            HandbookPopUpPage p = pages.get(curPopUp);
            if (!p.on) {
                if (curPopUp < pages.size - 1) {
                    curPopUp++;
                }
            }
            Gdx.input.setInputProcessor(p.getStage());
            p.update(popUpsOn);
        }
    }

    public void draw() {
        if (!pages.isEmpty()) {
            pages.get(curPopUp).draw();
        }
    }


    private class HandbookPopUpPage extends PopUp {

        private boolean on;
        private String tab;

        private HandbookOrder handbookOrder;
        private SpriteSheet locked;
        private float alpha;

        public HandbookPopUpPage(GDXRoot game, GameScene gameScene, String tab) {
            super(game, gameScene, game.directory.getEntry("Durian Handbook.animation",
                SpriteSheet.class));

            alpha = 1;
            on = true;
            this.tab = tab;
            handbookOrder = new HandbookOrder();

            float buttonWidth = 80;
            float buttonHeight = 88;
            float x = 1280 / 2f - background.getRegionWidth() / 2f - buttonWidth / 2f;
            float y = 720f / 2 + background.getRegionHeight() / 2f - buttonHeight;

            Skin s = new Skin(Gdx.files.internal("buttons/X.json"));
            ImageButton button = new ImageButton(s);
            button.setSize(buttonWidth, buttonHeight);
            button.setPosition(x, y);
            addButton(button);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    audio.play("click");
                    on = false;
                }
            });

            x += background.getRegionWidth();
            s = new Skin(Gdx.files.internal("buttons/Handbook.json"));
            button = new ImageButton(s);
            button.setSize(buttonWidth, buttonHeight);
            button.setPosition(x, y);
            addButton(button);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    audio.play("click");
                    game.exitScreen(gameScene, ExitCode.HANDBOOK);
                }
            });

            setPage();
            setLockPage();
            buttons = getButtons();
            switch (tab) {
                case "Companions" -> animationSpeed = 0.25f;
                case "Minions" -> animationSpeed = 0.15f;
                case "Bosses" -> animationSpeed = 0.1f;
            }

        }

        private void setPage() {
            switch (tab) {
                case "Companions" -> background = handbookOrder.companionSheet(game.directory,
                    gameScene.getLevel());
                case "Minions" -> background = handbookOrder.minionSheet(game.directory,
                    gameScene.getLevel());
                case "Bosses" ->
                    background = handbookOrder.bossSheet(game.directory, gameScene.getLevel());
            }
        }

        private void setLockPage() {
            if (gameScene.getLevel() == 1) {
                locked = game.directory.getEntry("TwoLock Handbook.animation", SpriteSheet.class);
                return;
            }
            switch (tab) {
                case "Companions" -> {
                    if (gameScene.getLevel() == 2) {
                        locked = handbookOrder.companionSheet(game.directory, 1);
                    } else if (gameScene.getLevel() == 5) {
                        locked = game.directory.getEntry("TwoLock Handbook.animation",
                            SpriteSheet.class);
                    } else if (gameScene.getLevel() == 6) {
                        locked = handbookOrder.companionSheet(game.directory, 5);
                    }
                }
                case "Minions" -> {
                    if (gameScene.getLevel() == 3) {
                        locked = handbookOrder.minionSheet(game.directory, 1);
                    } else if (gameScene.getLevel() == 7) {
                        locked = game.directory.getEntry("OneLock Handbook.animation",
                            SpriteSheet.class);
                    }
                }
                case "Bosses" -> {
                    if (gameScene.getLevel() == 4) {
                        locked = handbookOrder.bossSheet(game.directory, 1);
                    } else if (gameScene.getLevel() == 6) {
                        locked = game.directory.getEntry("OneLock Handbook.animation",
                            SpriteSheet.class);
                    }
                }
            }
        }

        @Override
        public void update(boolean popUpOn) {
            if (alpha <= 0) {
                if (background != null) {
                    animationFrame += animationSpeed;
                    if (animationFrame >= background.getSize()) {
                        animationFrame -= background.getSize();
                    }
                }
            } else {
                alpha -= 0.0075f;
            }
            for (Actor button : buttons) {
                ((Button) button).setDisabled(!popUpOn);
            }
        }

        @Override
        public void draw() {
            stage.act();
            background.setFrame((int) animationFrame);
            game.batch.draw(dim, 0, 0);
            SpriteBatch.computeTransform(transform, background.getRegionWidth() / 2f,
                background.getRegionHeight() / 2f, screenWidth / 2f, screenHeight / 2f, 0, 1f, 1f);
            game.batch.draw(background, transform);
            if (alpha > 0) {
                game.batch.setColor(1, 1, 1, alpha);
                game.batch.draw(locked, transform);
                game.batch.setColor(Color.WHITE);
            }
            game.batch.end();
            stage.draw();
            game.batch.begin();
        }


        public void setOn(boolean on) {
            this.on = on;
        }

        public boolean isOn() {
            return on;
        }
    }

}
