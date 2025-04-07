package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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

        public LevelSelect(final GDXRoot game) {
            this.game = game;
            background = new Texture("images/LevelSelectBackground.png");
            tray = new Texture("images/LevelSelectTray.png");
            plate = new Texture("images/LevelSelectPlate.png");
            arrow = new Texture("images/LevelSelectArrow.png");

        }

        public void update(float delta) {
//            if (Gdx.input.isTouched()) {
//                game.exitScreen(this, 0);
//                dispose();
//            }
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
            for (int i = 1; i <= 2; i++) {
                for (int j = 1; j <= 3; j++) {
                    int x = 125+ j*220;
                    int y = i*200-60;
                    int cx = Gdx.input.getX();
                    int cy = 720 - Gdx.input.getY();
                    //if (i == 1 && j == 1) {
                        //System.out.println("Plate" + i + "," + j + " " + x + " " + y);
                       // System.out.println("Cursor" + cx + " " + cy);
                       // System.out.println("upper: " + (y + plate.getHeight()));
                        if (cx >= x && cx <= x + plate.getWidth() && cy >= y
                            && cy <= y + plate.getHeight()) {
                            game.batch.setBlendMode(BlendMode.ADDITIVE);
                            if (Gdx.input.isTouched() && i == 2 && j == 1){
                                game.exitScreen(this, 0);
                                dispose();
                            }
                        }
                        game.batch.draw(plate, x, y);
                        game.batch.setBlendMode(BlendMode.ALPHA_BLEND);
                    //}
                }
            }

            game.batch.draw(arrow,1050,220);


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

