package edu.cornell.cis3152.team8;
//Heavily inspired by AILab Collision Controller

import edu.cornell.cis3152.team8.GameObject.ObjectType;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.math.*;
import edu.cornell.gdiac.audio.SoundEffect;
import edu.cornell.gdiac.audio.SoundEffectManager;

    /**
     * Class to handle basic collisions in the game.
     */
    public class CollisionController {

        /** Reference to the game session */
        private GameState session;

        /** Cache attribute for calculations */
        private Vector2 tmp;

        /**
         * Creates a CollisionController for the given models.
         *
         *  @param b The list of bosses
         *  @param m The list of minions
         *  @param p The player
         *  @param ap The active projectiles
         */
        public CollisionController(GameState session) {
            this.session = session;
            tmp = new Vector2();
        }

        /**
         * Updates minions, bosses, and the player moving them forward.
         * Handles all collisions.
         */
        public void update() {
            //Get level information
            Minion[] minions = session.getMinions();
            ProjectilePool projectiles = session.getProjectiles();
            Boss boss = session.getBoss();
            Player player = session.getPlayer();
            Boss[] bosses = new Boss[1];
            bosses[0] = boss;
            Coin[] coins = session.getCoins;

            //Move player.
            for (Companion c : player){
                if (!c.isDestroyed()){
                    move(c);
                }
            }

           // Move live minions.
            for (Minion m : minions) {
                if (!m.isDestroyed()) {
                    move(m);
                }
            }

            //Move boss.
            for (Boss b : bosses){
                if (!b.isDestroyed()){
                    move(b);
                }
            }

            // Test minion collisions (player and projectiles).
            for (Minion m : minions) {
                checkForCollision(m, player);
                for (Projectile p : projectiles) {
                    checkForCollision(m, p);
                }
            }
            //Test boss collisions (player and projectiles).
            for (Boss b : bosses){
                checkForCollision(b, player);
                for (Projectile p : projectiles){
                    checkForCollision(b, p);
                }
            }

            //Test coin-player collisions.
            for (Coin c : coins){
                checkForCollision(c,player);
            }
        }

        /**
         * Moves the creature (boss, minion, or companion) according to its velocity
         *
         * This only does something if the new position is safe. Otherwise, this
         * creature stays in place.
         *
         * @param creature The creature to move.
         */
        private void move(GameObject creature) {
            //creature must be boss, minion, or companion
            assert creature.getType().equals(ObjectType.MINION) || creature.getType().equals(ObjectType.BOSS)
                || creature.getType().equals(ObjectType.COMPANION);

            Board board = session.getBoard();
            tmp.set(creature.getX(), creature.getY());
            boolean safeBefore = board.isSafeAtScreen(tmp.x, tmp.y);

            // Test add velocity
            tmp.add(creature.getVX(), creature.getVY());
            boolean safeAfter  = board.isSafeAtScreen(tmp.x, tmp.y);

            if (!(safeBefore && !safeAfter)) {
                creature.getPosition().set(tmp);
            }
        }


        /**
         * Processes minion-player collisions.
         *
         * @param minion The minion
         * @param player The player
         */
        private void checkForCollision(Minion minion, Player player) {
            // Do nothing if either creature is dead.
            if (minion.isDestroyed() || player.isDestroyed()) {
                return;
            }

            Board board = session.getBoard();

            // Get the tiles for each creature
            int mx = board.screenToBoard(minion.getX());
            int my = board.screenToBoard(minion.getY());
            int px = board.screenToBoard(player.getX());
            int py = board.screenToBoard(player.getY());


            for (Companion c : player){
                //kill companion and minion if they collided
                if (mx == px && my == py) {
                    minion.setDestroyed(true);
                    player.deleteCompanion(c);
                }
            }
        }

        /**
         * Processes minion-projectile collisions.
         *
         * @param minion   The minion
         * @param projectile The projectile
         */
        private void checkForCollision(Minion minion, Projectile projectile) {
            // Do nothing if minion is dead.
            if (minion.isDestroyed()) {
                return;
            }

            Board board = session.getBoard();

          // Get the tiles for minion and projectile
            int mx = board.screenToBoard(minion.getX());
            int my = board.screenToBoard(minion.getY());
            int px = board.screenToBoard(projectile.getX());
            int py = board.screenToBoard(projectile.getY());

            // kill projectile and minion if they collided
            if (mx == px && my == py) {
                projectile.destroy();
                minion.setDestroyed(true);
            }
        }

        /**
         * Processes boss-player collisions.
         *
         * @param boss   The boss
         * @param player The player
         */
        private void checkForCollision(Boss boss, Player player){
            // Do nothing if either creature is dead.
            if (boss.isDestroyed() || player.isDestroyed()) {
                return;
            }

            Board board = session.getBoard();

                //Get the tiles for boss
                int bx = board.screenToBoard(boss.getX());
                int by = board.screenToBoard(boss.getY());


                for (Companion c : player){
                    // Get the tiles for companion
                    int cx = board.screenToBoard(c.getX());
                    int cy = board.screenToBoard(c.getY());
                    // kill companion if it collided with boss
                    if (cx == bx && cy == by){
                        player.deleteCompanion(c);
                    }
                }
        }

        /**
         * Processes boss-projectile collisions.
         *
         * @param boss   The boss
         * @param projectile The projectile
         */
        private void checkForCollision(Boss boss, Projectile projectile){
            // Do nothing if boss is dead.
            if (boss.isDestroyed()) {
                return;
            }

            Board board = session.getBoard();

            //Get the tiles for boss and projectile
            int bx = board.screenToBoard(boss.getX());
            int by = board.screenToBoard(boss.getY());
            int px = board.screenToBoard(projectile.getX());
            int py = board.screenToBoard(projectile.getY());

            // decrease boss health if hit by projectile
            if (bx == px && by == py){
                boss.setHealth(boss.getHealth()-projectile.getPower());
                projectile.destroy();
            }
        }

        /**
         * Processes coin-player collisions.
         * The player collects coins with its head.
         *
         * @param coin   The coin
         * @param player The player
         */
        private void checkForCollision(Coin coin, Player player){
            //Do nothing if coin or player are dead
            if (coin.isDestroyed() || player.isDestroyed()) {
                return;
            }

            Board board = session.getBoard();

            //Get tiles for coin and player
            int cx = board.screenToBoard(coin.getX());
            int cy = board.screenToBoard(coin.getY());
            int px = board.screenToBoard(player.getX());
            int py = board.screenToBoard(player.getY());

            // Add one coin to player and remove coin from screen if they collided
            if (cx == px && cy == py){
                player.setCoins(player.getCoins()+1);
                coin.setDestroyed(true);
            }
        }
    }

