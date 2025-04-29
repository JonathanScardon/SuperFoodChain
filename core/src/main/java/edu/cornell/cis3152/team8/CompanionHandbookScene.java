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

public class CompanionHandbookScene implements Screen {

    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private Texture background;
    private TextLayout title;
    private Texture tray;
    private Button arrowRight;
    private Button arrowLeft;

    private Button backButton;
    private Button settingsButton;

    private Texture plate;
    private float wait = 0.0f;
    private int unlocked;
    private GameAudio audio;
    private Camera camera;
    private int currPage;
    private int totalPages;
    private Texture[] pages;

    private float moveGoal;

    private boolean moving = false;
    private float moveSpeed = 40f;

    private Settings settingsScreen;
    private boolean settingsOn;


    public CompanionHandbookScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        settingsScreen = new Settings();
        settingsOn = false;
        audio = new GameAudio(assets);

        Texture arrow = new Texture("images/LevelSelectArrow.png");
        arrowRight = new Button(1280 - arrow.getWidth() * 1.5f, 720 / 2f - arrow.getHeight() / 2f,
            arrow, plate, 2);
        arrowLeft = new Button(arrow.getWidth() * 1.5f, 720 / 2f - arrow.getHeight() / 2f,
            arrow, plate, 0, true);

        Texture page;
        pages = new Texture[5];
        for (int i = 0; i < pages.length; i++) {
            page = assets.getEntry("Handbook" + i, Texture.class);
            pages[i] = page;
        }

        Texture back = new Texture("images/NextButton.png");
        Texture settings = new Texture("images/HandbookButton.png");
        Texture backHover = new Texture("images/NextButtonHover.png");
        Texture settingsHover = new Texture("images/HandbookButtonHover.png");

        float buttonSize = 78;
        float gap = 100;

        backButton = new Button(gap, 720 - gap,
            back, backHover, -1, buttonSize, buttonSize, true);
        settingsButton = new Button(1280 - gap, 720 - gap,
            settings, settingsHover, 0, buttonSize, buttonSize);

        unlocked = assets.getEntry("save", JsonValue.class)
            .getInt("companions_unlocked");

        title = new TextLayout("Select Level", assets.getEntry("lpc", BitmapFont.class));

        currPage = 1;
        //totalPages = 3;

        camera = game.viewport.getCamera();
        moveGoal = camera.position.x;
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
            backButton.update(delta);
            settingsButton.update(delta);

            if (!settingsOn) {
                if (currPage == 1) {
                    rightArrow();
                } else if (currPage == 2) {
                    leftArrow();
                    rightArrow();
                } else if (currPage == 3) {
                    leftArrow();
                }

                if (backButton.isPressed()) {
                    audio.play("click");
                    game.exitScreen(this, backButton.getExitCode());
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
        for (int i = 0; i <= unlocked; i++) {
            if (moving) {
                game.batch.draw(pages[i], (1280 * i), 0, 1280, 720);
            } else {
                if (i == currPage - 1) {
                    game.batch.draw(pages[i], (1280 * i), 0, 1280, 720);
                }
            }
        }

        backButton.draw(game.batch, !settingsOn);
        settingsButton.draw(game.batch, !settingsOn);

        if (currPage == 1) {
            arrowRight.draw(game.batch, !settingsOn);

        } else if (currPage == unlocked) {
            arrowLeft.draw(game.batch, !settingsOn);
        } else {
            arrowRight.draw(game.batch, !settingsOn);
            arrowLeft.draw(game.batch, !settingsOn);
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
        backButton.setPosition(backButton.posX + moveSpeed, backButton.posY);
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

