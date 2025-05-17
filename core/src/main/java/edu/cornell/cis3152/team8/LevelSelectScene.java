package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.assets.AssetDirectory;

public class LevelSelectScene implements Screen {

    /**
     * Reference to the GDX root
     */
    protected final GDXRoot game;
    /**
     * The audio controller
     */
    protected GameAudio audio;
    protected Stage stage;

    /**
     * The scene camera
     */
    protected Camera camera;
    private final OrthographicCamera uiCamera;

    /**
     * Background textures
     */
    private final Texture background;
    private final Texture tray;

    /**
     * Navigation Buttons
     */
    protected final Array<ImageButton> navs;

    /**
     * Level Buttons
     */
    private final TextButton[] page1;
    private final TextButton[] page2;
    private final TextButton[] page3;


    /**
     * Page switching info
     */
    protected int currPage;
    protected int unlockedPages;
    protected int totalPages;

    protected final float moveSpeed;
    private float moveGoal;
    protected boolean moving = false;

    /**
     * Settings
     */
    protected static Settings settingsScreen;
    protected boolean settingsOn;

    public LevelSelectScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        settingsScreen = game.settings;
        settingsOn = false;
        audio = game.audio;
        stage = new Stage(game.viewport, game.batch);
        float width = 1280;
        float height = 720;
        camera = game.viewport.getCamera();
        uiCamera = new OrthographicCamera(width, height);
        uiCamera.position.set(width / 2f, height / 2f, 0);
        uiCamera.update();

        //Constants
        moveSpeed = 40f;
        totalPages = 3;
        unlockedPages = totalPages;

        //Set to first page
        currPage = 1;
        moveGoal = camera.position.x;
        //Background textures
        background = assets.getEntry("levelsBackground", Texture.class);
        tray = assets.getEntry("levelsTray", Texture.class);

        navs = new Array<>();
        addNavButtons();

        //Set up the pages (6 buttons per page)
        page1 = new TextButton[6];
        page2 = new TextButton[6];
        page3 = new TextButton[4];

        addLevelButtons();
    }

    private void addNavButtons() {
        //Constants for centering
        float buttonWidth = 80;
        float buttonHeight = 88;
        //Create navigation buttons
        //Right
        Skin s = new Skin(Gdx.files.internal("buttons/Right.json"));
        ImageButton b = new ImageButton(s);
        b.setSize(buttonWidth, buttonHeight);
        stage.addActor(b);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                currPage += 1;
                moveGoal = camera.position.x + 1280;
                moving = true;
            }
        });
        navs.add(b);

        //Left
        s = new Skin(Gdx.files.internal("buttons/Left.json"));
        b = new ImageButton(s);
        b.setSize(buttonWidth, buttonHeight);
        stage.addActor(b);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currPage -= 1;
                moveGoal = camera.position.x - 1280;
                moving = true;
                audio.play("click");
            }
        });
        navs.add(b);

        //Home
        s = new Skin(Gdx.files.internal("buttons/Home.json"));
        b = new ImageButton(s);
        b.setSize(buttonWidth, buttonHeight);
        stage.addActor(b);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                game.viewport.getCamera().position.set(640f, 360f,
                    game.viewport.getCamera().position.z);
                game.exitScreen(game.getScreen(), ExitCode.HOME);
            }
        });
        navs.add(b);

        //Handbook
        s = new Skin(Gdx.files.internal("buttons/Handbook.json"));
        b = new ImageButton(s);
        b.setSize(buttonWidth, buttonHeight);
        stage.addActor(b);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                game.viewport.getCamera().position.set(640f, 360f,
                    game.viewport.getCamera().position.z);
                game.exitScreen(game.getScreen(), ExitCode.HANDBOOK);
            }
        });
        navs.add(b);

        //Settings
        s = new Skin(Gdx.files.internal("buttons/Settings.json"));
        b = new ImageButton(s);
        b.setSize(buttonWidth, buttonHeight);
        stage.addActor(b);
        b.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                settingsScreen.setOn(true);
                settingsOn = true;
            }
        });
        navs.add(b);
        setButtonPositions();
    }

    private void setButtonPositions() {
        float buttonWidth = navs.get(0).getWidth();
        float buttonHeight = navs.get(0).getWidth();
        float gap = 20; // The distance between the buttons
        float span = (buttonHeight * 3) + (gap * 2);
        float x = (1280 / 2f - tray.getWidth() / 2f) + (tray.getWidth() / 2f - span / 2);
        float y = 720 / 2f - tray.getHeight() / 2f - buttonHeight / 3f;

        navs.get(0).setPosition(1280 - buttonWidth * 2f, 720 / 2f - buttonHeight / 2f);
        navs.get(1).setPosition(buttonWidth, 720 / 2f - buttonHeight / 2f);
        navs.get(2).setPosition(x, y);
        x += buttonWidth + gap;
        navs.get(3).setPosition(x, y);
        x += buttonWidth + gap;
        navs.get(4).setPosition(x, y);
    }

    private void addLevelButtons() {
        Skin s = new Skin(Gdx.files.internal("buttons/Plate.json"));
        //Constants for centering
        float gap = 50; // The distance between the buttons
        float levelButtonWidth = 175;
        float levelButtonHeight = 174;
        float spanHorizontal = levelButtonWidth * 3 + (gap * 2);
        float spanVertical = levelButtonHeight * 2 + gap;

        //Start with level 1
        int level = 1;

        //Create and add buttons to pages
        float x;
        float y = (720 / 2f) + spanVertical / 2f - levelButtonHeight;
        for (int i = 1; i <= page1.length / 3; i++) {
            x = 1280 / 2f - spanHorizontal / 2f;
            for (int j = 1; j <= 3; j++) {
                TextButton button = new TextButton("" + level, s);
                button.setSize(levelButtonWidth, levelButtonHeight);
                button.setPosition(x, y);
                addLevelButton(level - 1, level, button, page1);

                button = new TextButton("" + (level + 6), s);
                button.setSize(levelButtonWidth, levelButtonHeight);
                button.setPosition(x + 1280, y);
                addLevelButton(level - 1, level + 6, button, page2);

                if (level - 1 < 4) {
                    button = new TextButton("" + (level + 12), s);
                    button.setSize(levelButtonWidth, levelButtonHeight);
                    button.setPosition(x + 2560, y);
                    addLevelButton(level - 1, level + 12, button, page3);
                }

                x = x + levelButtonWidth + gap;
                level++;
            }
            y = y - levelButtonHeight - gap;
        }
    }

    private void addLevelButton(int idx, int level, TextButton button, TextButton[] page) {
        stage.addActor(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.play("click");
                audio.stopMusic();
                game.viewport.getCamera().position.set(640f, 360f,
                    game.viewport.getCamera().position.z);
                game.exitScreen(game.getScreen(), level);
            }
        });
        page[idx] = button;
    }


    public void update(float delta) {
        //Set level lock states
        int unlocked = game.save.getInteger("unlockedLevels");
        for (int i = 0; i < page1.length; i++) {
            page1[i].setDisabled(i >= unlocked);
            page2[i].setDisabled(i + 6 >= unlocked);
            if (i < page3.length) {
                page3[i].setDisabled(i + 12 >= unlocked);
            }
        }

        if (moving) { //Move
            for (ImageButton b : navs) {
                b.setDisabled(true);
            }
            navs.get(0).setVisible(true);
            navs.get(1).setVisible(true);

            if (camera.position.x < moveGoal) {
                move(1);
            } else {
                move(-1);
            }
            if (camera.position.x == moveGoal) {
                moving = false;
            }
        } else { //Use page
            for (ImageButton b : navs) {
                b.setDisabled(false);
            }
            if (!settingsOn) { //Level page off when settings is on
                //Process arrows when on screen
                Gdx.input.setInputProcessor(stage);
                if (unlockedPages > 1) {
                    if (currPage == 1) {
                        navs.get(0).setDisabled(false);
                        navs.get(0).setVisible(true);

                        navs.get(1).setVisible(false);
                    } else if (currPage == unlockedPages) {
                        navs.get(0).setDisabled(true);
                        navs.get(0).setVisible(false);

                        navs.get(1).setDisabled(false);
                        navs.get(1).setVisible(true);

                    } else {
                        navs.get(0).setDisabled(false);
                        navs.get(0).setVisible(true);

                        navs.get(1).setDisabled(false);
                        navs.get(1).setVisible(true);
                    }
                }
            } else {
                for (ImageButton b : navs) {
                    b.setDisabled(true);
                }
            }
            settingsScreen.update(delta);
            settingsOn = settingsScreen.isOn();

        }
    }

    /**
     * Draws specific pages
     */
    protected void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.viewport.setCamera(camera);
        game.batch.setColor(Color.WHITE);
        game.batch.begin();

        stage.act();
        if (moving) { // Draw all backgrounds and level buttons if screen is moving
            //Backgrounds
            for (int i = 0; i < totalPages; i++) {
                game.batch.draw(background, (1280 * i), 0, 1280, 720);
                game.batch.draw(tray, (1280 * i) + 1280 / 2f - tray.getWidth() / 2f,
                    720 / 2f - tray.getHeight() / 2f);
            }
            for (TextButton b : page1) {
                b.setVisible(true);
                b.draw(game.batch, 1);
            }
            for (TextButton b : page2) {
                b.setVisible(true);
                b.draw(game.batch, 1);
            }
            for (TextButton b : page3) {
                b.setVisible(true);
                b.draw(game.batch, 1);
            }

        } else { //Otherwise only draw current page
            //Background
            game.batch.draw(background, (1280 * (currPage - 1)), 0, 1280, 720);
            game.batch.draw(tray, (1280 * (currPage - 1)) + 1280 / 2f - tray.getWidth() / 2f,
                720 / 2f - tray.getHeight() / 2f);

            //Level buttons
            boolean draw1;
            boolean draw2;
            boolean draw3;
            if (currPage == 1) {
                draw1 = true;
                draw2 = false;
                draw3 = false;
            } else if (currPage == 2) {
                draw1 = false;
                draw2 = true;
                draw3 = false;
            } else {
                draw1 = false;
                draw2 = false;
                draw3 = true;
            }

            for (TextButton b : page1) {
                b.setVisible(draw1);
                if (draw1 && b.isVisible()) {
                    b.draw(game.batch, 1);
                }
            }
            for (TextButton b : page2) {
                b.setVisible(draw2);
                if (draw2 && b.isVisible()) {
                    b.draw(game.batch, 1);
                }
            }
            for (TextButton b : page3) {
                b.setVisible(draw3);
                if (draw3 && b.isVisible()) {
                    b.draw(game.batch, 1);
                }
            }

        }
        for (ImageButton b : navs) {
            if (b.isVisible()) {
                b.draw(game.batch, 1);
            }
        }
        game.batch.end();

        //Draw settings
        if (settingsOn) {
            game.batch.setProjectionMatrix(uiCamera.combined);
            game.viewport.setCamera(uiCamera);
            settingsScreen.draw();
        }
    }

    private void move(int direction) {
        float speed = moveSpeed * direction;
        camera.position.x += speed;
        camera.update();
        for (Button b : navs) {
            b.setPosition(b.getX() + speed, b.getY());
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void reset() {
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        camera.update();
        currPage = 1;
        settingsOn = false;
        audio.play("levels");
        setButtonPositions();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.update(delta);
        this.draw();
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
}
