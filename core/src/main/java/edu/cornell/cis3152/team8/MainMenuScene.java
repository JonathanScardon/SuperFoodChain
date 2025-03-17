package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScene implements Screen {
    /**
     * Reference to the GDX root
     */
    private final GDXRoot game;
    private Texture background;
    private Texture play;
    private Texture settings;
    private Texture exit;

    public MainMenuScene(final GDXRoot game) {
        this.game = game;
        background = new Texture("images/Menu.png");
        play = new Texture("images/PlayButton.png");
        settings = new Texture("images/SettingsButton.png");
        exit = new Texture("images/ExitButton.png");

    }

    public void update(float delta) {
        if (Gdx.input.isTouched()) {
            game.exitScreen(this, 0);
            dispose();
        }
    }

    public void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        //draw text. Remember that x and y are in meters
        game.batch.draw(background,0,0);
        game.batch.draw(play,833,340);
        game.batch.draw(settings,833,240);
        game.batch.draw(exit,833,140);


//        game.font.setColor(Color.WHITE);
//        game.font.draw(game.batch, "Main Menu", 100f, 100f);
        game.batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.update(delta);
        this.draw(delta);
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
}
