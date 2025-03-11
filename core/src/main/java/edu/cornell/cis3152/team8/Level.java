package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;
public class Level {

    private int width;

    private int height;

    /**
     * Temporary constructor (?)
     * @param w width
     * @param h height
     */
    public Level(int w, int h){
        this.width = w;
        this.height = h;
    }

    /**
     * @return width
     */
    public int getWidth(){
        return this.width;
    }

    /**
     * @return height
     */
    public int getHeight(){
        return this.height;
    }
}
