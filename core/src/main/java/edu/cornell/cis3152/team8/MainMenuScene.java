package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.util.ScreenListener;

public class MainMenuScene implements Screen {
    //final GDXRoot game;
    private ScreenListener listener;

    public MainMenuScene(SpriteBatch batch) {

    }

    public void update(float delta) {
        if (Gdx.input.isTouched()) {
            //game.setScreen(new GameScene(game));
            dispose();
        }
    }

    public void draw(float delta) {
//        ScreenUtils.clear(Color.BLACK);
//
//        game.viewport.apply();
//        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
//
//        game.batch.begin();
//        //draw text. Remember that x and y are in meters
//        game.font.draw(game.batch, "Finding Friends", 1, 1.5f);
//        game.font.draw(game.batch, "Tap anywhere to begin!", 1, 1);
//        game.batch.end();
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
        //game.viewport.update(width, height, true);
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
    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

}
