package edu.cornell.cis3152.team8;
//Heavily inspired by AILab Board

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.*;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import edu.cornell.gdiac.assets.ParserUtils;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.graphics.obj.*;

/**
 * Class represents a 2D grid of tiles.
 *
 * Most of the work is done by the internal Tile class.  The outer class is
 * really just a container.
 */
public class Board {
    /**
     * The internal tile state
     */
    private static class TileState {
        /** Is this a goal tiles */
        public boolean goal = false;
        /** Has this tile been visited (used for pathfinding)? */
        public boolean visited = false;
    }

    /** The constants definining the board */
    private JsonValue constants;

    // Instance attributes
    /** The board width (in number of tiles) */
    private int width;
    /** The board height (in number of tiles) */
    private int height;
    /** The tile size */
    private float tileSize;
    /** The tile texture*/
    private Texture tileTexture;
    /** The tile grid (with above dimensions) */
    private TileState[] tiles;

    /**
     * Creates a new board from the given set of constants
     *
     * @param constants The board constants
     */
    public Board(JsonValue constants) {
        this.constants = constants;
        width = constants.get("size").getInt(0);
        height = constants.get("size").getInt(1);
        tileSize = constants.getFloat("tile size");

        tiles = new TileState[width * height];
        for (int ii = 0; ii < tiles.length; ii++) {
            tiles[ii] = new TileState();
        }
        resetTiles();
    }

    /**
     * Resets the values of all the tiles on screen.
     */
    public void resetTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState tile = getTileState(x, y);
                tile.goal = false;
                tile.visited = false;
            }
        }
    }

    /**
     * Returns the tile state for the given position (INTERNAL USE ONLY)
     *
     * Returns null if that position is out of bounds.
     *
     * @return the tile state for the given position
     */
    private TileState getTileState(int x, int y) {
        if (!inBounds(x, y)) {
            return null;
        }
        return tiles[x * height + y];
    }

    /**
     * Returns the number of tiles horizontally across the board.
     *
     * @return the number of tiles horizontally across the board.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of tiles vertically across the board.
     *
     * @return the number of tiles vertically across the board.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the size of the tile texture.
     *
     * @return the size of the tile texture.
     */
    public float getTileSize() {
        return tileSize;
    }

    // COORDINATE TRANSFORMS
    // The methods are used by the physics engine to coordinate
    // with the board. You should not need them.

    /**
     * Returns true if a screen location is in bounds
     *
     * @param x The x value in screen coordinates
     * @param y The y value in screen coordinates
     *
     * @return true if a screen location is safe
     */
    public boolean isSafeAtScreen(float x, float y) {
        int bx = screenToBoard(x);
        int by = screenToBoard(y);
        return x >= 0 && y >= 0
            && x < (width * (tileSize))
            && y < (height * (tileSize));
    }

    /**
     * Returns true if a tile location is safe (i.e. there is a tile there)
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if a screen location is safe
     */
    public boolean isSafeAt(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Draws the board to the given canvas.
     *
     * This method draws all of the tiles in this board. It should be the first drawing
     * pass in the GameEngine.
     *
     * @param batch the drawing context
     */
    public void draw(SpriteBatch batch) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                drawTile(x, y, batch);
            }
        }
    }

    /**
     * Draws the individual tile at position (x,y).
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    private void drawTile(int x, int y, SpriteBatch batch) {

        // Compute drawing coordinates
        float sx = boardToScreen(x);
        float sy = boardToScreen(y);

        // draw tile
        batch.draw(tileTexture,sx,sy,tileSize,tileSize);

    }

    // Use these methods to convert between tile coordinates (int) and
    // world coordinates (float).

    /**
     * Returns the board cell index for a screen position.
     *
     * While all positions are 2-dimensional, the dimensions to
     * the board are symmetric. This allows us to use the same
     * method to convert an x coordinate or a y coordinate to
     * a cell index.
     *
     * @param f Screen position coordinate
     *
     * @return the board cell index for a screen position.
     */
    public int screenToBoard(float f) {
        return (int)(f / (tileSize));
    }

    /**
     * Returns the screen position coordinate for a board cell index.
     *
     * While all positions are 2-dimensional, the dimensions to
     * the board are symmetric. This allows us to use the same
     * method to convert an x coordinate or a y coordinate to
     * a cell index.
     *
     * @param n Tile cell index
     *
     * @return the screen position coordinate for a board cell index.
     */
    public float boardToScreen(int n) {
        return (float) (n + 0.5f) * (tileSize);
    }

    // PATHFINDING METHODS
    // Use these methods to implement pathfinding on the board.

    /**
     * Returns true if the given position is a valid tile
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the given position is a valid tile
     */
    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Returns true if the tile has been visited.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the tile has been visited.
     */
    public boolean isVisited(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTileState(x, y).visited;
    }

    /**
     * Marks a tile as visited.
     *
     * A marked tile will return true for isVisited(), until a call to clearMarks().
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    public void setVisited(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Board", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        getTileState(x, y).visited = true;
    }

    /**
     * Returns true if the tile is a goal.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the tile is a goal.
     */
    public boolean isGoal(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTileState(x, y).goal;
    }

    /**
     * Marks a tile as a goal.
     *
     * A marked tile will return true for isGoal(), until a call to clearMarks().
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    public void setGoal(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Board", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        getTileState(x, y).goal = true;
    }

    /**
     * Clears all marks on the board.
     *
     * This method should be done at the beginning of any pathfinding round.
     */
    public void clearMarks() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileState state = getTileState(x, y);
                state.visited = false;
                state.goal = false;
            }
        }
    }
}
