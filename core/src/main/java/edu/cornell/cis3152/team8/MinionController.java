package edu.cornell.cis3152.team8;
//Heavily inspired by AILab AI Controller

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;
import java.util.LinkedList;
import java.util.Queue;


public class MinionController implements InputController {

    // Instance Attributes
    /**
     * The minion identifier for this minion controller
     */
    private int id;
    /**
     * The minion controlled by this AI
     */
    private Minion minion;
    /**
     * The target companion.
     */
    private Companion target;
    /**
     * The state of the game (needed by the AI)
     */
    private GameState session;
    private int move; // A ControlCode

    private Board board;
    /**
     * The number of ticks since we started this controller
     */
    private long ticks;

    public MinionController(int id, GameState session) {
        this.id = id;
        move = CONTROL_NO_ACTION;
        ticks = 0;
        target = null;
        this.session = session;
        minion = session.getMinions()[id];
        board = new Board(32,20,40);
    }

    /**
     * Returns a non-ship selection of the player
     * <p>
     * The value returned should be a bitmasked combination of the state ints for controlling the
     * game state (e.g. restarting the game, exiting the program). This method is not implemented by
     * AI opponents.
     *
     * @return a non-ship selection of the player
     */
//    public int getSelection() {
//        return SELECT_NONE;
//    }

    /**
     * Returns the action selected by this InputController
     * <p>
     * The returned int is a bit-vector of more than one possible input option. This is why we do
     * not use an enumeration of Control Codes; Java does not & (nicely) provide bitwise operation
     * support for enums.
     * <p>
     * This function tests the environment and uses the FSM to choose the next action of the ship.
     * This function SHOULD NOT need to be modified. It just contains code that drives the functions
     * that you need to implement.
     *
     * @return the action selected by this InputController
     */
    @Override
    public int getAction() {
        // Increment the number of ticks.
        ticks++;

        // Do not need to rework ourselves every frame. Just every 10 ticks.
        if ((minion.getId() + ticks) % 10 == 0) {
            // Pathfinding
            markGoalTiles();
            move = getMoveAlongPathToGoalTile();
        }

        return move;
    }

    // Pathfinding Code (MODIFY ALL THE FOLLOWING METHODS)

    /**
     * Mark all desirable tiles to move to.
     * <p>
     * This method implements pathfinding through the use of goal tiles. It searches for all
     * desirable tiles to move to (there may be more than one), and marks each one as a goal. Then,
     * the pathfinding method getMoveAlongPathToGoalTile() moves the ship towards the closest one.
     * <p>
     * POSTCONDITION: There is guaranteed to be at least one goal tile when completed.
     */
    private void markGoalTiles() {
        // Clear out previous pathfinding data.
        board.clearMarks();
        Player player = session.getPlayer();

        if (player.isAlive()) {
            for (Companion c: player.companions) {
                int targetX = board.screenToBoard(c.getX());
                int targetY = board.screenToBoard(c.getY());
                    board.setGoal(targetX, targetY);
            }
        }
    }

    /**
     * Returns a movement direction that moves towards a goal tile.
     * <p>
     * This is one of the longest parts of the assignment. Implement breadth-first search (from
     * 2110) to find the best goal tile to move to. However, just return the movement direction for
     * the next step, not the entire path.
     * <p>
     * The value returned should be a control code. See PlayerController for more information on how
     * to use control codes.
     *
     * @return a movement direction that moves towards a goal tile.
     */
    private int getMoveAlongPathToGoalTile() {
//        //#region PUT YOUR CODE HERE
//        // bfs starting from ship location and then return starting direction for the first tile we run into
        int minionX = board.screenToBoard(minion.getX());
        int minionY = board.screenToBoard(minion.getY());

        if (board.isGoal(minionX, minionY)) {
            return CONTROL_NO_ACTION; // we're already on a goal tile
        }

        Queue<PositionAndDirection> queue = new LinkedList<>();
        if (board.inBounds( minionX - 1, minionY)) {
            queue.add(new PositionAndDirection(minionX - 1, minionY, CONTROL_MOVE_LEFT));
            board.setVisited(minionX - 1, minionY);
        }
        if (board.inBounds(minionX + 1, minionY)) {
            queue.add(new PositionAndDirection(minionX + 1, minionY, CONTROL_MOVE_RIGHT));
            board.setVisited(minionX + 1, minionY);
        }
        if (board.inBounds(minionX, minionY - 1)) {
            queue.add(new PositionAndDirection(minionX, minionY - 1, CONTROL_MOVE_UP));
            board.setVisited(minionX, minionY - 1);
        }
        if (board.inBounds(minionX, minionY + 1)) {
            queue.add(new PositionAndDirection(minionX, minionY + 1, CONTROL_MOVE_DOWN));
            board.setVisited(minionX, minionY + 1);
        }

        while (!queue.isEmpty()) {
            PositionAndDirection cur = queue.poll();
            if (board.isGoal(cur.x, cur.y)) {
                // System.out.println("ship" + ship.getId() + ": (" + minionX + ", " + minionY + ") -> (" + cur.x + ", " + cur.y + ")");
                return cur.direction;
            }

            int[] dx = {cur.x, cur.x, cur.x + 1, cur.x - 1};
            int[] dy = {cur.y + 1, cur.y - 1, cur.y, cur.y};
            for (int i = 0; i < 4; i++) {
                if (board.inBounds(dx[i], dy[i]) && !board.isVisited(dx[i], dy[i])) {
                    PositionAndDirection next = new PositionAndDirection(dx[i], dy[i],
                        cur.direction);
                    board.setVisited(dx[i], dy[i]);
                    queue.add(next);
                }
            }
        }

        return CONTROL_NO_ACTION;
        //#endregion
    }

    // Add any auxiliary methods or data structures here
    //#region PUT YOUR CODE HERE

    private class Board{
        private static class TileState {
            /** Is this a goal tiles */
            public boolean goal = false;
            /** Has this tile been visited (used for pathfinding)? */
            public boolean visited = false;
        }
        // Instance attributes
        /** The board width (in number of tiles) */
        private int width;
        /** The board height (in number of tiles) */
        private int height;
        /** The tile size */
        private float tileSize;
        /** The tile grid (with above dimensions) */
        private TileState[] tiles;

        /**
         * Creates a new board from the given set of constants
         *
         *
         */
        public Board(int width, int height, int tileSize) {
            this.width = width;
            this.height = height;
            this.tileSize = tileSize;

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

        private TileState getTileState(int x, int y) {
            if (!inBounds(x,y)) {
                return null;
            }
            return tiles[x * height + y];
        }

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

    }

    private class PositionAndDirection {

        public int x, y, direction;

        public PositionAndDirection(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }
    //#endregion




}
