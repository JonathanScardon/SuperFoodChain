package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class CompanionHandbookScene extends MultiPageScene {

    /**
     * Other buttons
     */
    private final Button backButton;

    /**
     * Companion pages
     */
    private final SpriteSheet[] pages;
    private float animationFrame;
    private final float animationSpeed;

    public CompanionHandbookScene(final GDXRoot game, AssetDirectory assets) {
        super(game, assets);

        //Constants
        totalPages = 5;
        animationSpeed = 0.1f;

        animationFrame = 0;

        //Create handbook pages
        SpriteSheet page;

        pages = new SpriteSheet[totalPages];
        for (int i = 0; i < pages.length; i++) {
            page = assets.getEntry("Handbook" + i + ".animation", SpriteSheet.class);
            pages[i] = page;
        }

        //Button textures
        Texture back = assets.getEntry("arrow", Texture.class);
        Texture backHover = assets.getEntry("arrowHover", Texture.class);

        //Constants for centering
        float buttonSize = 78;
        float gap = 100; //Distance from the walls to the button

        //Create navigation buttons
        backButton = new Button(gap, 720 - gap,
            back, backHover, -1, buttonSize, buttonSize, true);
        settingsButton.setPosition(1280 - gap, 720 - gap);

        unlockedPages = assets.getEntry("save", JsonValue.class)
            .getInt("companions_unlocked");
    }

    @Override
    public void update(float delta) {
        //Update animation frame if settings is off
        if (!settingsOn) {
            animationFrame += animationSpeed;
        }
        if (animationFrame >= pages[0].getSize()) {
            animationFrame -= pages[0].getSize();
        }
        for (SpriteSheet s : pages) {
            s.setFrame((int) animationFrame);
        }
        super.update(delta);
    }

    @Override
    public void move(int direction) {
        super.move(direction);
        backButton.setPosition(backButton.posX + moveSpeed * direction, backButton.posY);
    }

    @Override
    protected void processButtons() {
        if (backButton.isPressed()) {
            audio.play("click");
            game.exitScreen(this, backButton.getExitCode());
        }
    }

    @Override
    protected void updateButtons(float delta) {
        super.updateButtons(delta);
        backButton.update(delta);
    }

    @Override
    protected void drawButtons() {
        super.drawButtons();
        backButton.draw(game.batch, !settingsOn);
    }

    @Override
    protected void drawPages() {
        if (moving) { //Draw all unlocked pages if screen is moving
            for (int i = 0; i <= unlockedPages; i++) {
                game.batch.draw(pages[i], (1280 * i), 0, 1280, 720);
            }
        } else { //Otherwise only draw current page
            game.batch.draw(pages[currPage - 1], (1280 * (currPage - 1)), 0, 1280, 720);
        }
    }
}

