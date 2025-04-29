package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import edu.cornell.gdiac.graphics.TextLayout;
import java.awt.Point;
import java.awt.PointerInfo;

public class LevelSelect implements Screen {

    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private Texture background;
    private TextLayout title;
    private Texture tray;
    private Button arrowRight;
    private Button arrowLeft;

    private Button settingsButton;
    private Button handbookButton;
    private Button homeButton;

    private Texture plate;
    private LevelButton[] page1;
    private LevelButton[] page2;
    private float wait = 0.0f;
    private int unlocked;
    private GameAudio audio;
    private Camera camera;
    private int currPage;
    private int totalPages;

    private float moveGoal;

    private boolean moving = false;
    private float moveSpeed = 40f;

    private Settings settingsScreen;
    private boolean settingsOn;
    private Texture dim;


    public LevelSelect(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        settingsScreen = new Settings();
        settingsOn = false;
        audio = new GameAudio(assets);

        dim = new Texture("images/dim.png");
        background = new Texture("images/LevelSelectBackground.png");
        tray = new Texture("images/Tray.png");
        plate = new Texture("images/LevelSelectPlate.png");
        Texture arrow = new Texture("images/LevelSelectArrow.png");
        arrowRight = new Button(1280 - arrow.getWidth() * 1.5f, 720 / 2f - arrow.getHeight() / 2f,
            arrow, plate, 2);
        arrowLeft = new Button(arrow.getWidth() * 1.5f, 720 / 2f - arrow.getHeight() / 2f,
            arrow, plate, 0, true);

        Texture home = new Texture("images/HomeButton.png");
        Texture settings = new Texture("images/NextButton.png");
        Texture handbook = new Texture("images/HandbookButton.png");
        Texture homeHover = new Texture("images/HomeButtonHover.png");
        Texture settingsHover = new Texture("images/NextButtonHover.png");
        Texture handbookHover = new Texture("images/HandbookButtonHover.png");

        float x = 1280 / 2f - tray.getWidth() / 2f;
        float y = 720 / 2f - tray.getHeight() / 2f;
        float buttonSize = 78;
        float height = y;
        float gap = 20;
        float span = (buttonSize * 3) + (gap * 2);

        homeButton = new Button(x + (tray.getWidth() / 2f - span / 2), height,
            home, homeHover, -1, 78, 78);
        handbookButton = new Button(homeButton.posX + homeButton.width + gap, height,
            handbook, handbookHover, 0, 78, 78);
        settingsButton = new Button(handbookButton.posX + handbookButton.width + gap, height,
            settings, settingsHover, 0, 78, 78);

        unlocked = assets.getEntry("save", JsonValue.class)
            .getInt("max_level_unlocked");

        title = new TextLayout("Select Level", assets.getEntry("lpc", BitmapFont.class));
        page1 = new LevelButton[6];
        page2 = new LevelButton[6];
        currPage = 1;
        totalPages = 3;

        camera = game.viewport.getCamera();
        moveGoal = camera.position.x;

        gap = 50;
        float spanHorizontal = plate.getWidth() * 3 + (gap * 2);
        float spanVertical = plate.getHeight() * 2 + gap;

        int level = 1;

        y = (720 / 2f) + spanVertical / 2f - plate.getHeight();
        for (int i = 1; i <= page1.length / 3; i++) {
            x = 1280 / 2f - spanHorizontal / 2f;
            for (int j = 1; j <= 3; j++) {
                LevelButton b = new LevelButton(x, y, level, assets);
                page1[level - 1] = b;
                b = new LevelButton(x + 1280, y, level + 6, assets);
                page2[level - 1] = b;
                x = x + plate.getWidth() + gap;
                level++;
            }
            y = y - plate.getHeight() - gap;
        }
        for (int i = 0; i < page1.length; i++) {
            page1[i].setLocked(i >= unlocked);
            page2[i].setLocked(i + 6 >= unlocked);
        }
    }

    public void update(float delta) {
        if (wait > 0.0f) {
            wait -= delta;
        } else if (moving) {
            if (camera.position.x < moveGoal) {
                move(moveSpeed);
            } else {
                move(-moveSpeed);
            }
            if (camera.position.x == moveGoal) {
                moving = false;
            }

        } else {
            arrowLeft.update(delta);
            arrowRight.update(delta);
            homeButton.update(delta);
            handbookButton.update(delta);
            settingsButton.update(delta);

            if (currPage == 1) {
                rightArrow();
            } else if (currPage == 2) {
                leftArrow();
                rightArrow();
            } else if (currPage == 3) {
                leftArrow();
            }

            if (!settingsOn) {
                for (LevelButton b : page1) {
                    if (b.isPressed() && !b.getLocked()) {
                        audio.play("clickLevel");
                        game.exitScreen(this, b.getExitCode());
                    }
                }
                for (LevelButton b : page2) {
                    if (b.isPressed() && !b.getLocked()) {
                        audio.play("click");
                        game.exitScreen(this, b.getExitCode());
                    }
                }

                if (homeButton.isPressed()) {
                    audio.play("click");
                    game.exitScreen(this, homeButton.getExitCode());
                }
                if (handbookButton.isPressed()) {
                    audio.play("click");
                    game.exitScreen(this, handbookButton.getExitCode());
                }
                if (settingsButton.isPressed()) {
                    audio.play("click");
                    settingsOn = true;
                    settingsScreen.update();
                }
            }
            if (settingsOn && Gdx.input.isKeyPressed(Keys.ESCAPE)) {
                settingsOn = false;
            }

        }
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        for (int i = 0; i < totalPages; i++) {
            game.batch.draw(background, (1280 * i), 0, 1280, 720);
            game.batch.draw(tray, (1280 * i) + 1280 / 2f - tray.getWidth() / 2f,
                720 / 2f - tray.getHeight() / 2f);
            Affine2 trans = new Affine2();
            trans.setToTrnScl((1280 * i) + 1280 / 2f - title.getWidth() / 2f,
                720 / 2f + tray.getHeight() / 2f, 3f, 3f);
            game.batch.drawText(title, trans);
        }

        if (moving) {
            for (Button b : page1) {
                b.draw(game.batch, true);
            }
            for (Button b : page2) {
                b.draw(game.batch, true);
            }
        } else {
            if (currPage == 1) {
                for (Button b : page1) {
                    b.draw(game.batch, true);
                }
            } else if (currPage == 2) {
                for (Button b : page2) {
                    b.draw(game.batch, true);
                }
            }
        }
        homeButton.draw(game.batch, true);
        handbookButton.draw(game.batch, true);
        settingsButton.draw(game.batch, true);

        if (currPage == 1) {
            arrowRight.draw(game.batch, true);
        } else if (currPage == 2) {
            arrowRight.draw(game.batch, true);
            arrowLeft.draw(game.batch, true);
        } else if (currPage == 3) {
            arrowLeft.draw(game.batch, true);
        }

        if (settingsOn) {
            settingsScreen.draw(game.batch, currPage);
        }

        game.batch.end();
    }

    private void leftArrow() {
        if (arrowLeft.isPressed()) {
            arrowRight.setExitCode(currPage);
            currPage = arrowLeft.getExitCode();
            arrowLeft.setExitCode(currPage - 1);
            moveGoal = camera.position.x - 1280;
            moving = true;
            audio.play("click");
        }
    }

    private void rightArrow() {
        if (arrowRight.isPressed()) {
            arrowLeft.setExitCode(currPage);
            currPage = arrowRight.getExitCode();
            arrowRight.setExitCode(currPage + 1);
            moveGoal = camera.position.x + 1280;
            moving = true;
            audio.play("click");
        }
    }

    private void move(float moveSpeed) {
        camera.position.x += moveSpeed;
        arrowLeft.setPosition(arrowLeft.posX + moveSpeed, arrowLeft.posY);
        arrowRight.setPosition(arrowRight.posX + moveSpeed, arrowRight.posY);
        homeButton.setPosition(homeButton.posX + moveSpeed, homeButton.posY);
        handbookButton.setPosition(handbookButton.posX + moveSpeed, handbookButton.posY);
        settingsButton.setPosition(settingsButton.posX + moveSpeed, settingsButton.posY);
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
        game.viewport.update(width, height, true);
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

    public void resetWait() {
        wait = 0.25f;
    }
}
