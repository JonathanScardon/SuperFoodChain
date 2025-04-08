package edu.cornell.cis3152.team8;
//Heavily inspired by AILab Collision Controller

import com.badlogic.gdx.utils.Array;
import edu.cornell.cis3152.team8.GameObject.ObjectType;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.*;
import edu.cornell.gdiac.audio.SoundEffect;
import edu.cornell.gdiac.audio.SoundEffectManager;

/**
 * Class to handle basic collisions in the game.
 */
public class CollisionController {

    /**
     * Reference to the game session
     */
    private GameState session;

    /**
     * Cache attribute for calculations
     */
    private Vector2 tmp;

    private Minion[] minions;
    private Player player;
    private Companion[] companions;
    private LinkedList<Coin> coins;
    private Boss[] bosses;
    private Array<Projectile> projectiles;

    /**
     * Creates a CollisionController for the given models.
     */
    public CollisionController(Minion[] minions, Player player, Companion[] companions, LinkedList<Coin> coins, Boss[] bosses, Array<Projectile> projectiles) {
        tmp = new Vector2();
        this.minions = minions;
        this.player = player;
        this.companions = companions;
        this.coins = coins;
        this.bosses = bosses;
        this.projectiles = projectiles;
    }

    /**
     * Updates minions, bosses, and the player moving them forward.
     * Handles all collisions.
     */
    public void update() {

//            // Test minion collisions (player and projectiles).
        for (Minion m : minions) {
            checkForCollision(m, player);
            for (Projectile p : projectiles) {
                checkForCollision(m, p);
            }
        }
//            //Test boss collisions (player and projectiles).
        for (Boss b : bosses) {
            checkForCollision(b, player);
            for (Projectile p : projectiles) {
                checkForCollision(b, p);
            }
        }

//            //Test coin-player collisions.

        Iterator<Coin> iter = coins.iterator();
        while (iter.hasNext()) {
            Coin c = iter.next();
            checkForCollision(c, player);
            if (c.isDestroyed()) {
                iter.remove();
            }
        }

        //Test companion-player collisions.
        for (Companion c : companions) {
            if (!c.isCollected()) {
                checkForCollision(c, player);
            }
        }

    }

    /**
     * Moves the creature (boss, minion, or companion) according to its velocity
     * <p>
     * This only does something if the new position is safe. Otherwise, this
     * creature stays in place.
     *
     * @param creature The creature to move.
     */
    private void move(GameObject creature) {
//            //creature must be boss, minion, or companion
//            assert creature.getType().equals(ObjectType.MINION) || creature.getType().equals(ObjectType.BOSS)
//                || creature.getType().equals(ObjectType.COMPANION);
//
//
//            tmp.set(creature.getX(), creature.getY());
//            boolean safeBefore = board.isSafeAtScreen(tmp.x, tmp.y);
//
//            // Test add velocity
//            tmp.add(creature.getVX(), creature.getVY());
//            boolean safeAfter  = board.isSafeAtScreen(tmp.x, tmp.y);
//
//            if (!(safeBefore && !safeAfter)) {
//                creature.getPosition().set(tmp);
//            }
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

        // Get the tiles for each creature
        float mx = minion.getX();
        float my = minion.getY();


        for (int i = 0; i < player.companions.size(); i++) {
            Companion c = player.companions.get(i);
            float px = c.getX();
            float py = c.getY();
            boolean collide = px >= mx - 10 && px <= mx + 10 && py >= my - 10 && py <= my + 10;
            //kill companion and minion if they collided
            if (collide) {
                player.deleteCompanion(c);
                minion.setDestroyed(true);
                c.setDestroyed(true);
                //MOVE TO PROJECTILE DEATH - choose between spawn anyway
                coins.add(new Coin(mx, my));
            }
        }
    }

    /**
     * Processes minion-projectile collisions.
     *
     * @param minion     The minion
     * @param projectile The projectile
     */
    private void checkForCollision(Minion minion, Projectile projectile) {
        // Do nothing if minion is dead.
        if (minion.isDestroyed()) {
            return;
        }

        // Get the tiles for minion and projectile
        float mx = minion.getX();
        float my = minion.getY();
        float px = projectile.getX();
        float py = projectile.getY();
        boolean collide = mx >= px - 12 && mx <= px + 12 && my >= py - 12 && my <= py + 12;;

        if (collide) {
            projectile.setDestroyed(true);
            minion.removeHealth(1);
            if (minion.getHealth() <= 0) {
                minion.setDestroyed(true);
                coins.add(new Coin(mx, my));
            }
        }
    }

    /**
     * Processes boss-player collisions.
     *
     * @param boss   The boss
     * @param player The player
     */
    private void checkForCollision(Boss boss, Player player) {
        // Do nothing if either creature is dead.
        if (boss.isDestroyed() || player.isDestroyed()) {
            return;
        }

        //Get the tiles for boss
        float bx = boss.getX();
        float by = boss.getY();

        for (int i = 0; i < player.companions.size(); i++) {
            Companion c = player.companions.get(i);
            // Get the tiles for companion
            float cx = c.getX();
            float cy = c.getY();
            boolean collide = bx >= cx - 50 && bx <= cx + 50 && by >= cy - 50 && by <= cy + 50;
            // kill companion if it collided with boss
            if (collide) {
                player.deleteCompanion(c);
                c.setDestroyed(true);
            }
        }
    }

    /**
     * Processes boss-projectile collisions.
     *
     * @param boss       The boss
     * @param projectile The projectile
     */
    private void checkForCollision(Boss boss, Projectile projectile) {
        // Do nothing if boss is dead.
        if (boss.isDestroyed()) {
            return;
        }

        //Get the tiles for boss and projectile
        float bx = boss.getX();
        float by = boss.getY();
        float px = projectile.getX();
        float py = projectile.getY();

        boolean collide = bx >= px - 50 && bx <= px + 50 && by >= py - 50 && by <= py + 50;
        // decrease boss health if hit by projectile
        if (collide) {
            boss.setHealth(boss.getHealth() - 1);
            if (boss.getHealth() <= 0) {
                boss.setDestroyed(true);
            }
            projectile.setDestroyed(true);

        }
    }

    /**
     * Processes coin-player collisions.
     * The player collects coins with its head.
     *
     * @param coin   The coin
     * @param player The player
     */
    private void checkForCollision(Coin coin, Player player) {
        //Do nothing if coin or player are dead
        if (coin.isDestroyed() || !player.isAlive()) {
            return;
        }

        //Get tiles for coin and player
        float cx = coin.getX();
        float cy = coin.getY();
        Companion head = player.companions.get(0);
        float px = head.getX();
        float py = head.getY();
        boolean collide = px >= cx - 15 && px <= cx + 15 && py >= cy - 15 && py <= cy + 15;
        //System.out.println(collide);

        // Add one coin to player and remove coin from screen if they collided
        if (collide) {
            player.setCoins(player.getCoins() + 1);
            coin.setDestroyed(true);
            // coins.remove(coin); ERROR! Sometimes coin being removed while for-loop iterating through coins
        }
    }

    /**
     * Processes companion-player collisions.
     * The player collects companions with its head.
     *
     * @param companion The companion
     * @param player    The player
     */
    private void checkForCollision(Companion companion, Player player) {
        //Do nothing if companion or player are dead
        if (companion.isDestroyed() || !player.isAlive()) {
            return;
        }

        //Get tiles for coin and player
        float cx = companion.getX();
        float cy = companion.getY();
        Companion head = player.companions.get(0);
        float px = head.getX();
        float py = head.getY();

        //System.out.println(cx+", "+cy+"  "+px+", "+py);
        // Player buys companion if enough coins and they collided
        int cost = companion.getCost();
        boolean collide = px >= cx - 10 && px <= cx + 10 && py >= cy - 10 && py <= cy + 10;
        //System.out.println(collide);
        boolean afford = player.getCoins() >= cost;
        //System.out.println(afford);
        if (collide && afford) {
            //System.out.println("collected");
            player.setCoins(player.getCoins() - cost);
            player.addCompanion(companion);
            companion.setCollected(true);
        }
    }
}

