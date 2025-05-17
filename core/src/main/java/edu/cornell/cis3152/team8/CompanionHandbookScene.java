package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class CompanionHandbookScene implements Screen {

    private final GDXRoot game;
    private final GameAudio audio;
    private final Stage stage;
    private final Camera camera;

    private final Texture background;

    private final int totalCompanions;
    private int unlockedCompanions;
    private final int totalMinions;
    private int unlockedMinions;
    private final int totalBosses;
    private int unlockedBosses;

    private HandbookOrder handbookOrder;

    /**
     * Companion pages
     */
    private final SpriteSheet[] pagesTab0;
    private final SpriteSheet[] pagesTab1;
    private final SpriteSheet[] pagesTab2;

    private float animationFrame;
    private float animationSpeed;

    private int currPage;
    private int currTab;

    /**
     * Settings
     */
    protected static Settings settingsScreen;
    protected boolean settingsOn;

    public CompanionHandbookScene(final GDXRoot game) {
        this.game = game;
        audio = game.audio;
        stage = new Stage(game.viewport, game.batch);
        camera = game.viewport.getCamera();
        settingsScreen = game.settings;
        settingsOn = false;

        background = game.directory.getEntry("handbookBackground", Texture.class);

        currPage = 0;
        currTab = 0;

        //Constants
        totalCompanions = 3;
        unlockedCompanions = 0;
        totalMinions = 2;
        unlockedMinions = 0;
        totalBosses = 2;
        unlockedBosses = 0;

        handbookOrder = new HandbookOrder();

        animationSpeed = 0.1f;

        animationFrame = 0;

        //Create handbook pages
        pagesTab0 = new SpriteSheet[3];
        pagesTab1 = new SpriteSheet[2];
        pagesTab2 = new SpriteSheet[2];

        pagesTab0[0] = (game.directory.getEntry("TwoLock Handbook.animation", SpriteSheet.class));
        pagesTab0[1] = (game.directory.getEntry("TwoLock Handbook.animation", SpriteSheet.class));
        pagesTab0[2] = (game.directory.getEntry("OneLock Handbook.animation", SpriteSheet.class));
        pagesTab1[0] = (game.directory.getEntry("TwoLock Handbook.animation", SpriteSheet.class));
        pagesTab1[1] = (game.directory.getEntry("OneLock Handbook.animation", SpriteSheet.class));
        pagesTab2[0] = (game.directory.getEntry("TwoLock Handbook.animation", SpriteSheet.class));
        pagesTab2[1] = (game.directory.getEntry("OneLock Handbook.animation", SpriteSheet.class));

        //Constants for centering
        float buttonWidth = 80;
        float buttonHeight = 88;
        float gap = 30; //Distance from the walls to the button

        //Create navigation buttons
        Skin s = new Skin(Gdx.files.internal("buttons/Left.json"));
        ImageButton button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(gap, 720 - gap - buttonHeight);
        stage.addActor(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                game.viewport.getCamera().position.set(640f, 360f,
                    game.viewport.getCamera().position.z);
                game.exitScreen(game.getScreen(), ExitCode.BACK);
            }
        });

        s = new Skin(Gdx.files.internal("buttons/Settings.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(1280 - gap - buttonWidth, 720 - gap - buttonHeight);
        stage.addActor(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                settingsScreen.setOn(true);
                settingsOn = true;
            }
        });

        s = new Skin(Gdx.files.internal("buttons/Right.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(1280 - buttonWidth * 2f, 720 / 2f - buttonHeight / 2f);
        stage.addActor(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                animationFrame = 0;
                currPage++;
            }
        });

        //Left
        s = new Skin(Gdx.files.internal("buttons/Left.json"));
        button = new ImageButton(s);
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(buttonWidth, 720 / 2f - buttonHeight / 2f);
        stage.addActor(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                animationFrame = 0;
                currPage--;
            }
        });

        //Tabs
        ButtonGroup<ImageButton> tabs = new ButtonGroup<>();
        tabs.setMaxCheckCount(1);
        tabs.setMinCheckCount(1);
        tabs.setUncheckLast(true);

        s = new Skin(Gdx.files.internal("buttons/CompanionTab.json"));
        button = new ImageButton(s);
        button.setPosition(969, 461);
        stage.addActor(button);
        tabs.add(button);

        s = new Skin(Gdx.files.internal("buttons/MinionTab.json"));
        button = new ImageButton(s);
        button.setPosition(990, 392);
        stage.addActor(button);
        tabs.add(button);

        s = new Skin(Gdx.files.internal("buttons/BossTab.json"));
        button = new ImageButton(s);
        button.setPosition(1003, 318);
        stage.addActor(button);
        tabs.add(button);

        stage.getActors().get(4).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                animationSpeed = 0.1f;
                currTab = 0;
                currPage = 0;
            }
        });
        stage.getActors().get(5).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                animationSpeed = 0.15f;
                currTab = 1;
                currPage = 0;
            }
        });

        stage.getActors().get(6).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                animationSpeed = 0.1f;
                currTab = 2;
                currPage = 0;
            }
        });
    }

    public void update(float delta) {
        settingsScreen.update(delta);
        settingsOn = settingsScreen.isOn();
        for (Actor b : stage.getActors()) {
            ((ImageButton) b).setDisabled(settingsOn);
        }

        if (!settingsOn) {
            Gdx.input.setInputProcessor(stage);
            int total;
            SpriteSheet[] sheets;

            if (currTab == 0) {
                total = totalCompanions;
                sheets = pagesTab0;
            } else if (currTab == 1) {
                total = totalMinions;
                sheets = pagesTab1;
            } else {
                total = totalBosses;
                sheets = pagesTab2;
            }
            if (currPage == 0) {
                stage.getActors().get(2).setVisible(true);// right arrow
                stage.getActors().get(3).setVisible(false);// left arrow

            } else if (currPage == total - 1) {
                stage.getActors().get(2).setVisible(false); // right arrow
                stage.getActors().get(3).setVisible(true);// left arrow

            } else {
                stage.getActors().get(2).setVisible(true); // left arrow
                stage.getActors().get(3).setVisible(true); // right arrow
            }

            if (currTab == 0) {
                if (currPage == 1) {
                    animationSpeed = 0.2f;
                } else {
                    animationSpeed = 0.1f;
                }
            }

            int temp = game.save.getInteger("unlockedCompanions");
            boolean companionChange = unlockedCompanions != temp;
            unlockedCompanions = temp;

            if (currTab == 0) {
                if (currPage == 1 && unlockedCompanions == 3) {
                    animationSpeed = 0.2f;
                } else {
                    animationSpeed = 0.1f;
                }
            }

            temp = game.save.getInteger("unlockedMinions");
            boolean minionChange = unlockedMinions != temp;
            unlockedMinions = temp;

            temp = game.save.getInteger("unlockedBosses");
            boolean bossChange = unlockedBosses != temp;
            unlockedBosses = temp;

            if (companionChange) {
                if (unlockedCompanions == 1 || unlockedCompanions == 2) {
                    pagesTab0[0] = handbookOrder.companionSheet(game.directory,
                        -unlockedCompanions);
                } else if (unlockedCompanions == 3 || unlockedCompanions == 4) {
                    pagesTab0[1] = handbookOrder.companionSheet(game.directory,
                        -unlockedCompanions);
                } else if (unlockedCompanions == 5) {
                    pagesTab0[2] = handbookOrder.companionSheet(game.directory,
                        -unlockedCompanions);
                }
            }
            if (minionChange) {
                if (unlockedMinions == 1 || unlockedMinions == 2) {
                    pagesTab1[0] = handbookOrder.minionSheet(game.directory, -unlockedMinions);
                } else if (unlockedMinions == 3) {
                    pagesTab1[1] = handbookOrder.minionSheet(game.directory, -unlockedBosses);
                }
            }
            if (bossChange) {
                if (unlockedBosses == 1 || unlockedBosses == 2) {
                    pagesTab2[0] = handbookOrder.bossSheet(game.directory, -unlockedBosses);
                } else if (unlockedBosses == 3) {
                    pagesTab2[1] = handbookOrder.bossSheet(game.directory, -unlockedBosses);
                }
            }
            //Update animation frame
            animationFrame += animationSpeed;
            if (animationFrame >= sheets[currPage].getSize()) {
                animationFrame -= sheets[currPage].getSize();
            }
            sheets[currPage].setFrame((int) animationFrame);
        }
    }

    public void draw() {
        game.viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.viewport.setCamera(camera);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        stage.act();
        SpriteSheet[] sheets;

        if (currTab == 0) {
            sheets = pagesTab0;
        } else if (currTab == 1) {
            sheets = pagesTab1;
        } else {
            sheets = pagesTab2;
        }
        game.batch.draw(background, 0, 0);
        game.batch.draw(sheets[currPage], 1280 / 2f - sheets[currPage].getRegionWidth() / 2f,
            86f);
        for (Actor b : stage.getActors()) {
            if (b.isVisible()) {
                b.draw(game.batch, 1);
            }
        }
        game.batch.end();
        if (settingsOn) {
            settingsScreen.draw();
        }
    }

    public void reset() {
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        camera.update();
        settingsOn = false;
        currPage = 0;
        currTab = 0;
        ((ImageButton) stage.getActors().get(4)).setChecked(true);
        ((ImageButton) stage.getActors().get(5)).setChecked(false);
        ((ImageButton) stage.getActors().get(6)).setChecked(false);
        animationFrame = 0;
        audio.play("handbook");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Stage getStage() {
        return stage;
    }
}

