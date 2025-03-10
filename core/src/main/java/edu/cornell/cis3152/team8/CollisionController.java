package edu.cornell.cis3152.team8;
//Heavily inspired by AILab Collision Controller

import java.util.Random;
import com.badlogic.gdx.math.*;
import edu.cornell.gdiac.audio.SoundEffect;
import edu.cornell.gdiac.audio.SoundEffectManager;
    /*
     * CollisionController.java
     *
     * Unless you are making a point-and-click adventure game, every single game is
     * going to need some sort of collision detection.  In a later lab, we will see
     * how to do this with a physics engine. For now, we use custom physics.
     *
     * This class is an example of subcontroller. A lot of this functionality could
     * go into GameEngine (which is the primary controller). However, we have
     * factored it out into a separate class because it makes sense as a
     * self-contained subsystem.  Unlike Lab 1, this controller stores a lot
     * information as fields. This is to keep us from having to pass the same
     * parameters over and over again.
     *
     * Based on the original AI Game Lab by Yi Xu and Don Holden, 2007
     *
     * @author:  Walker M. White, Cristian Zaloj
     * @version: 1/24/2025
     */

    /**
     * Class to handle basic collisions in the game.
     *

     */
    public class CollisionController {

        /** Reference to the game session */
        private GameState session;

        /** Cache attribute for calculations */
        private Vector2 tmp;
        /** Random number generator for nudging */
        private Random random;

        /**
         * Creates a CollisionController for the given models.
         *
         *  b The list of bosses
         *  m The list of minions
         *  p The play
         *  ap The active photons
         */
        public CollisionController(GameState session) {
            this.session = session;
            tmp = new Vector2();
            random = new Random();
        }

        /**
         * Updates all of the ships and photons, moving them forward.
         *
         * This is part of the collision phase, because movement can cause collisions!
         * That is why we do not combine this with the gameplay controller. When we
         * study the sense-think-act cycle later, we will see another reason for
         * this design.
         */
        public void update() {
            //TODO
            //ShipList ships = session.getShips();
            //PhotonPool photons = session.getPhotons();

//            // Move live ships when possible.
//            for (Minion s : ships) {
//                if (s.isActive()) {
//                    moveIfSafe(s);
//                }
//            }
//
//            // Test collisions between ships.
//            int length = ships.size();
//            for (int ii = 0; ii < length - 1; ii++) {
//                for (int jj = ii + 1; jj < length; jj++) {
//                    checkForCollision(ships.get(ii), ships.get(jj));
//                }
//            }
//
//            // Test collisions between ships and photons.
//            for (Ship s : ships) {
//                for (Photon p : photons) {
//                    checkForCollision(s, p);
//                }
//            }
        }

        /**
         * Moves the ship according to its velocity
         *
         * This only does something if the new position is safe. Otherwise, this
         * ship stays in place.
         *
         * @param ship The ship to move.
         */
        private void moveIfSafe(Minion ship) {
            //TODO
//            Board board = session.getBoard();
//            tmp.set(ship.getX(), ship.getY());
//            boolean safeBefore = board.isSafeAtScreen(tmp.x, tmp.y);
//
//            // Test add velocity
//            tmp.add(ship.getVX(), ship.getVY());
//            boolean safeAfter  = board.isSafeAtScreen(tmp.x, tmp.y);
//
//            if (!(safeBefore && !safeAfter)) {
//                ship.getPosition().set(tmp);
         //   }
        }


        /**
         * Processes collisions between ships, causing them to bounce off one another.
         *
         * This method updates the velocities of both ships: the collider and the
         * collidee. Therefore, you should only call this method for one of the
         * ships, not both. Otherwise, you are processing the same collisions
         * twice.
         *
         * @param ship1 The collider
         * @param ship2 The collidee
         */
        private void checkForCollision(Minion ship1, Companion ship2) {
            //TODO
//            Board board = session.getBoard();
//
//            // Do nothing if either ship is off the board.
//            if (!ship1.isActive() || !ship2.isActive()) {
//                return;
//            }
//
//            // Get the tiles for each ship
//            int s1x = board.screenToBoard(ship1.getX());
//            int s1y = board.screenToBoard(ship1.getY());
//            int s2x = board.screenToBoard(ship2.getX());
//            int s2y = board.screenToBoard(ship2.getY());
//
//            // If the two ships occupy the same tile,
//            if (s1x == s2x && s1y == s2y) {
//                // If they have the same (continuous) location, then nudge them.
//                if (ship1.getX() == ship2.getX() && ship1.getY() == ship2.getY()) {
//                    safeNudge(ship1);
//                    safeNudge(ship2);
//                }
//
//                // If this ship is farther from the tile center than the other one,
//                if (manhattan(ship1.getX(), ship1.getX(), board.boardToScreen(s1x), board.boardToScreen(s1y))
//                    > manhattan(ship2.getX(), ship2.getX(), board.boardToScreen(s2x), board.boardToScreen(s2y))
//                    && board.isSafeAtScreen(ship1.getX() + (ship1.getX() - ship2.getX()),
//                    ship1.getY() + (ship1.getY() - ship2.getY()))) {
//                    // Then push it away.
//                    ship1.getPosition().add(ship1.getX() - ship2.getX(), ship1.getY() - ship2.getY());
//                } else if (board.isSafeAtScreen(ship2.getX() + (ship2.getX() - ship1.getX()),
//                    ship2.getY() + (ship2.getY() - ship1.getY()))) {
//                    // Otherwise, push the other ship away
//                    ship2.getPosition().add(ship2.getX() - ship1.getX(), ship2.getY() - ship1.getY());
//                } else {
//                    // Neither ship can be pushed away in an appropriate
//                    // direction, so nudge them.
//                    safeNudge(ship1);
//                    safeNudge(ship2);
//                }
//            }
        }

        /**
         * Processes collisions between a ship and a photon
         *
         * Recall that when a photon collides with a ship, the tile at that position
         * is destroyed.
         *
         * @param ship   The ship
         * @param photon The photon
         */
        private void checkForCollision(Minion ship, Projectile photon) {
            //TODO
//            Board board = session.getBoard();
//            PhotonPool photons = session.getPhotons();
//
//            // Do nothing if ship is off the board.
//            if (!ship.isActive()) {
//                return;
//            } else if (ship.getId() == photon.getSource()) {
//                // Our own photon; do nothing.
//                return;
//            }
//
//            // Get the tiles for ship and photon
//            int sx = board.screenToBoard(ship.getX());
//            int sy = board.screenToBoard(ship.getY());
//            int px = board.screenToBoard(photon.getX());
//            int py = board.screenToBoard(photon.getY());
//
//            // If the ship and photon occupy the same tile,
//            if (sx == px && sy == py) {
//                // Have the photon push the ship.
//                board.destroyTileAt(sx, sy);
//                float x = photon.getPushX() * (board.getTileSize() + board.getTileSpacing());
//                float y = photon.getPushY() * (board.getTileSize() + board.getTileSpacing());
//                ship.getPosition().add(x,y);
//
//                photons.destroy(photon);
//
//                // We use a manager to esure only one sound at a time
//                // Otherwise, the game gets LOUD
//                SoundEffectManager sounds = SoundEffectManager.getInstance();
//                SoundEffect fire = session.getSound( "bump" );
//                sounds.play( "bump", fire);
//            }
        }

        /**
         * Returns the manhattan distance between two points
         *
         * @return the manhattan distance between two points
         */
        private float manhattan(float x0, float y0, float x1, float y1) {
            return Math.abs(x1 - x0) + Math.abs(y1 - y0);
        }
    }

