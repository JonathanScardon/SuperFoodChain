package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;

public class LevelSelect extends MultiPageScene {

    /**
     * Background textures
     */
    private final Texture background;
    private final Texture tray;

    /**
     * Level Buttons
     */
    private final LevelButton[] page1;
    private final LevelButton[] page2;

    /**
     * Other buttons
     */
    private final Button handbookButton;
    private final Button homeButton;


    public LevelSelect(final GDXRoot game, AssetDirectory assets) {
        super(game, assets);

        //Constants
        totalPages = 3;

        //Background textures
        background = assets.getEntry("levelsBackground", Texture.class);
        ;
        tray = assets.getEntry("levelsTray", Texture.class);

        //Buttons textures
        Texture home = assets.getEntry("home", Texture.class);
        Texture handbook = assets.getEntry("handbook", Texture.class);
        Texture homeHover = assets.getEntry("homeHover", Texture.class);
        Texture handbookHover = assets.getEntry("handbookHover", Texture.class);

        //Constants for centering
        float x = 1280 / 2f - tray.getWidth() / 2f;
        float y = 720 / 2f - tray.getHeight() / 2f;
        float buttonSize = 78;
        float gap = 20; // The distance between the buttons
        float span = (buttonSize * 3) + (gap * 2);

        //Create navigation buttons
        homeButton = new Button(x + (tray.getWidth() / 2f - span / 2), y,
            home, homeHover, -1, buttonSize, buttonSize);
        handbookButton = new Button(homeButton.posX + homeButton.width + gap, y,
            handbook, handbookHover, 0, buttonSize, buttonSize);
        settingsButton.setPosition(handbookButton.posX + handbookButton.width + gap, y);

        //Set up the pages (6 buttons per page)
        page1 = new LevelButton[6];
        page2 = new LevelButton[6];

        //Constants for centering
        gap = 50; // The distance between the buttons
        float levelButtonWidth = 175;
        float levelButtonHeight = 174;
        float spanHorizontal = levelButtonWidth * 3 + (gap * 2);
        float spanVertical = levelButtonHeight * 2 + gap;

        //Start with level 1
        int level = 1;

        //Create and add buttons to pages
        y = (720 / 2f) + spanVertical / 2f - levelButtonHeight;
        for (int i = 1; i <= page1.length / 3; i++) {
            x = 1280 / 2f - spanHorizontal / 2f;
            for (int j = 1; j <= 3; j++) {
                LevelButton b = new LevelButton(x, y, level, assets);
                page1[level - 1] = b;
                b = new LevelButton(x + 1280, y, level + 6, assets);
                page2[level - 1] = b;
                x = x + levelButtonWidth + gap;
                level++;
            }
            y = y - levelButtonHeight - gap;
        }

        unlocked = assets.getEntry("save", JsonValue.class)
            .getInt("max_level_unlocked");
    }

    @Override
    public void update(float delta) {
        //Set level lock states
        for (int i = 0; i < page1.length; i++) {
            page1[i].setLocked(i >= unlocked);
            page2[i].setLocked(i + 6 >= unlocked);
        }
        super.update(delta);
    }

    @Override
    public void move(int direction) {
        super.move(direction);
        homeButton.setPosition(homeButton.posX + moveSpeed * direction, homeButton.posY);
        handbookButton.setPosition(handbookButton.posX + moveSpeed * direction,
            handbookButton.posY);
    }

    /**
     * Processes user input of buttons
     */
    protected void processButtons() {
        for (LevelButton b : page1) {
            if (b.isPressed() && b.getUnlocked()) {
                audio.play("clickLevel");
                game.exitScreen(this, b.getExitCode());
            }
        }
        for (LevelButton b : page2) {
            if (b.isPressed() && b.getUnlocked()) {
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
    }

    /**
     * Resets button use
     */
    protected void updateButtons(float delta) {
        super.updateButtons(delta);
        homeButton.update(delta);
        handbookButton.update(delta);
        for (Button b : page1) {
            b.update(delta);
        }
        for (Button b : page2) {
            b.update(delta);
        }

    }

    /**
     * Draws navigation buttons
     */
    @Override
    protected void drawButtons() {
        super.drawButtons();
        homeButton.draw(game.batch, !settingsOn);
        handbookButton.draw(game.batch, !settingsOn);
    }

    /**
     * Draws specific pages
     */
    protected void drawPages() {
        if (moving) { // Draw all backgrounds and level buttons if screen is moving
            //Backgrounds
            for (int i = 0; i < totalPages; i++) {
                game.batch.draw(background, (1280 * i), 0, 1280, 720);
                game.batch.draw(tray, (1280 * i) + 1280 / 2f - tray.getWidth() / 2f,
                    720 / 2f - tray.getHeight() / 2f);
            }
            //Buttons
            for (Button b : page1) {
                b.draw(game.batch, true);
            }
            for (Button b : page2) {
                b.draw(game.batch, true);
            }
        } else { //Otherwise only draw current page
            //Background
            game.batch.draw(background, (1280 * (currPage - 1)), 0, 1280, 720);
            game.batch.draw(tray, (1280 * (currPage - 1)) + 1280 / 2f - tray.getWidth() / 2f,
                720 / 2f - tray.getHeight() / 2f);

            //Level buttons
            switch (currPage) {
                case 1 -> {
                    for (Button b : page1) {
                        b.draw(game.batch, true);
                    }
                }
                case 2 -> {
                    for (Button b : page2) {
                        b.draw(game.batch, true);
                    }
                }
            }
        }
    }

}
