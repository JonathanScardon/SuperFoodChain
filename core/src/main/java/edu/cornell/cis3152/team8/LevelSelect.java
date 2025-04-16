package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import edu.cornell.gdiac.graphics.SpriteBatch.BlendMode;
import java.awt.Point;
import java.awt.PointerInfo;

public class LevelSelect implements Screen {
        /**
         * Reference to the GDX root
         */
        private final GDXRoot game;
    private Texture background;
        private Texture tray;
        private Texture plate;
        private Texture arrow;
        private Texture lock;
        private Array<LevelButton> buttons;
        private float wait = 0.0f;




        public LevelSelect(final GDXRoot game) {
            this.game = game;
            background = new Texture("images/LevelSelectBackground.png");
            tray = new Texture("images/LevelSelectTray.png");
            plate = new Texture("images/LevelSelectPlate.png");
            arrow = new Texture("images/LevelSelectArrow.png");
            lock = new Texture("images/Lock.png");

            buttons = new Array<>();
            int level = 1;
            for (int i = 2; i >= 1; i--) {
                for (int j = 1; j <= 3; j++) {
                    int x = 140+ j*210;
                    int y = i*208-73;
                    LevelButton b = new LevelButton(x, y, new Texture("images/"+level+".png"),level);
                    buttons.add(b);
                    if ((i == 2 && j == 1)){
                        b.setLocked(false);
                    }else {
                        b.setLocked(true);
                    }
                    level++;
                }
            }
        }

        public void update(float delta) {
            //System.out.println(delta);
            if (wait > 0.0f) {
                wait -= delta;
            }else {
                for (LevelButton b : buttons) {
                    if (b.isHovering() && Gdx.input.isTouched() && !b.getLocked()) {
                        game.exitScreen(this, b.getExitCode());
                    }
                }
            }
        }

        public void draw(float delta) {
            ScreenUtils.clear(Color.BLACK);

            game.viewport.apply();
            game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

            game.batch.begin();
            game.batch.setColor(Color.WHITE);
            //draw text. Remember that x and y are in meters
            game.batch.draw(background,0,0);
            game.batch.draw(tray,0,0);
            for (Button b: buttons){
                b.draw(game.batch);
            }
            //game.batch.draw(arrow,1050,220);
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
        public void resetWait(){
            wait = 0.5f;
        }
    }

