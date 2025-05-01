package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.assets.AssetDirectory;

public abstract class MultiPageScene implements Screen {

    /**
     * Reference to the GDX root
     */
    protected final GDXRoot game;

    /**
     * Arrow buttons
     */
    protected final Button arrowRight;
    protected final Button arrowLeft;

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
    private final Settings settingsScreen;
    protected boolean settingsOn;
    protected Button settingsButton;

    /**
     * Pause before allowing use
     */
    private final float waitTime;
    private float currWait;

    /**
     * The audio controller
     */
    protected final GameAudio audio;

    /**
     * The scene camera
     */
    private final Camera camera;

    public MultiPageScene(final GDXRoot game, AssetDirectory assets) {
        this.game = game;
        settingsScreen = new Settings();
        settingsOn = false;
        audio = new GameAudio(assets);

        //Constants
        moveSpeed = 40f;
        waitTime = 0.25f;

        //Set to first page
        currPage = 1;
        camera = game.viewport.getCamera();
        moveGoal = camera.position.x;

        //Buttons textures
        Texture arrow = assets.getEntry("arrow", Texture.class);
        Texture arrowHover = assets.getEntry("arrowHover", Texture.class);
        Texture settings = assets.getEntry("settings", Texture.class);
        Texture settingsHover = assets.getEntry("settingsHover", Texture.class);

        float buttonSize = 78;

        //Create arrow buttons
        arrowRight = new Button(1280 - buttonSize * 2f, 720 / 2f - buttonSize / 2f,
            arrow, arrowHover, 2, buttonSize, buttonSize);
        arrowLeft = new Button(buttonSize * 2f, 720 / 2f - buttonSize / 2f,
            arrow, arrowHover, 0, buttonSize, buttonSize, true);

        //Create settings button
        settingsButton = new Button(-50, -50,
            settings, settingsHover, 0, buttonSize, buttonSize);
    }

    public void update(float delta) {
        if (currWait > 0.0f) { //Wait
            currWait -= delta;
        } else if (moving) { //Move
            if (camera.position.x < moveGoal) {
                move(1);
            } else {
                move(-1);
            }
            if (camera.position.x == moveGoal) {
                moving = false;
            }
        } else { //Use page
            //Reset button use
            updateButtons(delta);

            if (!settingsOn) { //Level page off when settings is on
                //Process arrows when on screen
                if (currPage == 1 && unlockedPages > 1) {
                    rightArrow();
                } else if (currPage == unlockedPages && unlockedPages > 1) {
                    leftArrow();
                } else {
                    leftArrow();
                    rightArrow();
                }
                //Process user inputs
                processButtons();
                if (settingsButton.isPressed()) {
                    audio.play("click");
                    settingsOn = true;
                    settingsScreen.update();
                }
            }
            //Exit settings with escape
            if (settingsOn && Gdx.input.isKeyPressed(Keys.ESCAPE)) {
                settingsOn = false;
            }

        }
    }

    public void draw() {
        //Clear previous frame
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.batch.setColor(Color.WHITE);

        //Draw page specifics
        drawPages();

        //Always draw navigation buttons
        drawButtons();

        if (currPage == 1) {
            arrowRight.draw(game.batch, !settingsOn);
        } else if (currPage == unlockedPages) {
            arrowLeft.draw(game.batch, !settingsOn);
        } else {
            arrowLeft.draw(game.batch, !settingsOn);
            arrowRight.draw(game.batch, !settingsOn);
        }

        if (settingsOn) {
            settingsScreen.draw(game.batch, currPage);
        }

        game.batch.end();
    }

    /**
     * Sets up to move pages left if left arrow is pressed
     */
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

    /**
     * Sets up to move pages right if right arrow is pressed
     */
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

    /**
     * Moves pages
     */
    protected void move(int direction) {
        float speed = moveSpeed * direction;
        camera.position.x += speed;
        arrowLeft.setPosition(arrowLeft.posX + speed, arrowLeft.posY);
        arrowRight.setPosition(arrowRight.posX + speed, arrowRight.posY);
        settingsButton.setPosition(settingsButton.posX + speed, settingsButton.posY);
    }

    /**
     * Processes user input of buttons
     */
    protected abstract void processButtons();


    /**
     * Resets button use
     */
    protected void updateButtons(float delta) {
        arrowLeft.update(delta);
        arrowRight.update(delta);
        settingsButton.update(delta);
    }

    /**
     * Draws navigation buttons
     */
    protected void drawButtons() {
        settingsButton.draw(game.batch, !settingsOn);
    }

    /**
     * Draws specific pages
     */
    protected abstract void drawPages();

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
        currWait = waitTime;
    }
}
