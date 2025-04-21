package edu.cornell.cis3152.team8;
//Heavily inspired by AILab Collision Controller

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics2.ObstacleSprite;
import com.badlogic.gdx.utils.Array;

import java.util.*;

import java.util.List;

import com.badlogic.gdx.math.*;

/**
 * Class to handle basic collisions in the game.
 */
public class CollisionController implements ContactListener {

    /**
     * Reference to the game session
     */
    private GameState state;

    /**
     * Reference to the game world
     */
    protected World world;

    /**
     * List of objects to remove
     */
    private List<ObstacleSprite> removed = new ArrayList<>();

    /**
     * List of coins to add
     */
    private List<ObstacleSprite> coinsAdded = new ArrayList<>();

    private Companion companionAdded = null;

    private List<Body> removedProjectiles = new ArrayList<>();

    private Array<MinionController> minionControls;
    private Array<Companion> deadCompanions;

    /**
     * Cache attribute for calculations
     */
    private Vector2 tmp;

    /**
     * Category bits for Filtering
     */
    public static final short PLAYER_CATEGORY = 0x0001;
    public static final short MINION_CATEGORY = 0x0002;
    public static final short COMPANION_CATEGORY = 0x0004;
    public static final short COIN_CATEGORY = 0x0008;
    public static final short BOSS_CATEGORY = 0x0010;
    public static final short PROJECTILE_CATEGORY = 0x0020;
    public static final short BORDER_CATEGORY = 0x0040;


    /**
     * Creates a CollisionController for the given models.
     */
    public CollisionController(GameState state) {
        tmp = new Vector2();
        this.state = state;
        this.world = state.getWorld();
//         this.minionControls = minionControls;
//         this.deadCompanions = deadCompanions;
    }

    /**
     * Updates minions, bosses, and the player moving them forward. Handles all collisions.
     */
    public void update() {

    }

    // player-minion collision
    // Get the tiles for each creature
//         float mx = minion.getX();
//         float my = minion.getY();

//         for (int i = 0; i < player.companions.size(); i++) {
//             Companion c = player.companions.get(i);
//             float px = c.getX();
//             float py = c.getY();
//             boolean collide = px >= mx && px - 25 <= mx + 25 && py >= my -25 && py <= my + 25;
//             //kill companion and minion if they collided
//             if (collide) {
//                 player.deleteCompanion(c);
//                 minion.setDestroyed(true);
//                 c.setDestroyed(true);
//                 deadCompanions.add(c);
//                 //MOVE TO PROJECTILE DEATH - choose between spawn anyway
//                 coins.add(new Coin(mx, my));

    // minion-projectile collision
    // Get the tiles for minion and projectile
//         float mx = minion.getX();
//         float my = minion.getY();
//         float px = projectile.getX();
//         float py = projectile.getY();
//         boolean collide = mx >= px - 12 && mx <= px + 12 && my >= py - 12 && my <= py + 12;;

//         if (collide) {
//             projectile.setDestroyed(true);
//             minion.removeHealth(1);
//             if (minion.getHealth() <= 0) {
//                     minion.setDestroyed(true);
//                     coins.add(new Coin(mx, my));
//             }
//             minion.setDamage(true);

    // player-boss collision
    //Get the tiles for boss
//         float bx = boss.getX();
//         float by = boss.getY();

//         for (int i = 0; i < player.companions.size(); i++) {
//             Companion c = player.companions.get(i);
//             // Get the tiles for companion
//             float cx = c.getX();
//             float cy = c.getY();
//             boolean collide = bx >= cx - 50 && bx <= cx + 50 && by >= cy - 50 && by <= cy + 50;
//             // kill companion if it collided with boss
//             if (collide) {
//                 player.deleteCompanion(c);
//                 c.setDestroyed(true);
//                 deadCompanions.add(c);
//             }
//         }

    // boss-projectile collision
    //Get the tiles for boss and projectile
//         float bx = boss.getX();
//         float by = boss.getY();
//         float px = projectile.getX();
//         float py = projectile.getY();

//         boolean collide = bx >= px - 50 && bx <= px + 50 && by >= py - 50 && by <= py + 50;
//         System.out.println(collide);
//         // decrease boss health if hit by projectile
//         if (collide) {
//             boss.setHealth(boss.getHealth() - 1);
//             if (boss.getHealth() <= 0) {
//                 boss.setDestroyed(true);
//             }
//             projectile.setDestroyed(true);
//             boss.setDamage(true);
//         }

    // player-coin collision
    //Get tiles for coin and player
//         float cx = coin.getX();
//         float cy = coin.getY();
//         Companion head = player.companions.get(0);
//         float px = head.getX();
//         float py = head.getY();
//         boolean collide = px >= cx - 20 && px <= cx + 20 && py >= cy - 20 && py <= cy + 20;
//         //System.out.println(collide);

//         // Add one coin to player and remove coin from screen if they collided
//         if (collide) {
//             player.setCoins(player.getCoins() + 1);
//             coin.setDestroyed(true);
//             // coins.remove(coin); ERROR! Sometimes coin being removed while for-loop iterating through coins

    // player-companion collision
//   private void checkForCollision(Companion companion, Player player) {

//         //Do nothing if companion or player are dead
//         if (companion.isDestroyed() || !player.isAlive()) {
//             return;
//         }
//         companion.setGlow(false);
//         //Get tiles for coin and player
//         float cx = companion.getX();
//         float cy = companion.getY();
//         Companion head = player.companions.get(0);
//         float px = head.getX();
//         float py = head.getY();

//         //System.out.println(cx+", "+cy+"  "+px+", "+py);
//         // Player buys companion if enough coins and they collided
//         int cost = companion.getCost();
//         boolean collide = px >= cx - 25 && px <= cx + 25 && py >= cy - 25 && py <= cy + 25;
//         //System.out.println(collide);
//         boolean afford = player.getCoins() >= cost;
//         //System.out.println(afford);
//         if (collide && afford){
//             companion.setGlow(true);
//             if (Gdx.input.isKeyPressed(Keys.E)) {
//             //System.out.println("collected");
//             player.setCoins(player.getCoins() - cost);
//             player.addCompanion(companion);
//             companion.setCollected(true);

//    /**
//     * Processes minion-projectile collisions.
//     *
//     * @param minion     The minion
//     * @param projectile The projectile
//     */
//    private void checkForCollision(Minion minion, Projectile projectile) {
//        // Do nothing if minion is dead.
//        if (!minion.getObstacle().isActive()) {
//            return;
//        }
//
//        // Get the tiles for minion and projectile
//        float mx = minion.getObstacle().getX();
//        float my = minion.getObstacle().getY();
//        float px = projectile.getX();
//        float py = projectile.getY();
//
//        // kill projectile and minion if they collided
//        boolean collide = mx >= px - 12 && mx <= px + 12 && my >= py - 12 && my <= py + 12;
//
//        if (collide) {
//            projectile.setDestroyed(true);
//            minion.removeHealth(1);
//            if (minion.getHealth() <= 0) {
//                minion.getObstacle().markRemoved(true);
//                coins.add(new Coin(mx, my));
//            }
//        }
//    }

//    /**
//     * Processes boss-projectile collisions.
//     *
//     * @param boss       The boss
//     * @param projectile The projectile
//     */
//    private void checkForCollision(Boss boss, Projectile projectile) {
//        // Do nothing if boss is dead.
//        if (boss.isDestroyed()) {
//            return;
//        }
//
//        //Get the tiles for boss and projectile
//        float bx = boss.getX();
//        float by = boss.getY();
//        float px = projectile.getX();
//        float py = projectile.getY();
//
//        boolean collide = bx >= px - 50 && bx <= px + 50 && by >= py - 50 && by <= py + 50;
//        // decrease boss health if hit by projectile
//        if (collide) {
//            boss.setHealth(boss.getHealth() - 1);
//            if (boss.getHealth() <= 0) {
//                boss.setDestroyed(true);
//            }
//            projectile.setDestroyed(true);
//
//        }
//    }

    // Contact Listener Methods
    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        Body b1 = f1.getBody();
        Body b2 = f2.getBody();

        System.out.println("BEGIN CONTACT");

        try {
            System.out.println("TRYING CONTACT");
            ObstacleSprite s1 = (ObstacleSprite) b1.getUserData();
            ObstacleSprite s2 = (ObstacleSprite) b2.getUserData();

            short c1 = f1.getFilterData().categoryBits;
            short c2 = f2.getFilterData().categoryBits;

            // Player Collisions
            // Player and Minion
            if ((c1 == PLAYER_CATEGORY && c2 == MINION_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == MINION_CATEGORY)) {
                System.out.println("P-M CONTACT IS HAPPENING");
                if (state.getPlayer().hasShield()) {
                    System.out.println("BREAKS SHIELD");
                    state.getPlayer().setShield(false);

                    if (c1 == PLAYER_CATEGORY) {
                        removed.add(s2);
                        coinsAdded.add(s2);
                    } else {
                        removed.add(s1);
                        coinsAdded.add(s1);
                    }
                } else {
                    System.out.println("DEATH");
                    removed.add(s1);
                    removed.add(s2);
                    if (c1 == PLAYER_CATEGORY) {
                        coinsAdded.add(s2);
                    } else {
                        coinsAdded.add(s1);
                    }
                }
            }

//            // Player and Boss
            else if ((c1 == PLAYER_CATEGORY && c2 == BOSS_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == BOSS_CATEGORY)) {
                System.out.println("P-B CONTACT IS HAPPENING");
                if (state.getPlayer().hasShield()) {
                    state.getPlayer().setShield(false);
                    System.out.println("BOSS HIT");
                    if (c1 == BOSS_CATEGORY) {
                        bossHit(b1);
                    } else {
                        bossHit(b2);
                    }
                } else {
                    System.out.println("DEATH");
                    if (c1 == PLAYER_CATEGORY) {
                        removed.add(s1);
                    } else {
                        removed.add(s2);
                    }
                }
            }

            // Player and Coin
            else if ((c1 == PLAYER_CATEGORY && c2 == COIN_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == COIN_CATEGORY)) {
                System.out.println("ADD COIN");
                state.getPlayer().setCoins(state.getPlayer().getCoins() + 1);
                if (c1 == PLAYER_CATEGORY) {
                    removed.add(s2);
                } else {
                    removed.add(s1);
                }
            }

            // Player and Companion
            else if ((c1 == PLAYER_CATEGORY && c2 == COMPANION_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == COMPANION_CATEGORY)) {
                System.out.println("P-C CONTACT IS HAPPENING");
                if (c1 == COMPANION_CATEGORY) {
                    for (Companion c : state.getCompanions()) {
                        if (Gdx.input.isKeyPressed(Input.Keys.E) && c.getObstacle().getBody() == b1
                            && c.getCost() <= state.getPlayer().getCoins()) {
                            System.out.println("ADD COMPANION");
                            companionAdded = c;
                            state.getPlayer().setCoins(state.getPlayer().getCoins() - c.getCost());
                        }
                    }
                } else {
                    for (Companion c : state.getCompanions()) {
                        if (Gdx.input.isKeyPressed(Input.Keys.E) && c.getObstacle().getBody() == b2
                            && c.getCost() <= state.getPlayer().getCoins()) {
                            System.out.println("ADD COMPANION");
                            companionAdded = c;
                            state.getPlayer().setCoins(state.getPlayer().getCoins() - c.getCost());
                        }
                    }
                }
            }

            // Projectile Collisions
            // Projectile and Minion
            else if ((c1 == PROJECTILE_CATEGORY && c2 == MINION_CATEGORY) || (
                c2 == PROJECTILE_CATEGORY && c1 == MINION_CATEGORY)) {
                System.out.println("PR-M CONTACT IS HAPPENING");
                if (c1 == PROJECTILE_CATEGORY) {
                    System.out.println("KILL MINION");
                    removedProjectiles.add(b1);
                    removed.add(s2);
                    coinsAdded.add(s2);
                } else {
                    System.out.println("KILL MINION");
                    removedProjectiles.add(b2);
                    removed.add(s1);
                    coinsAdded.add(s1);
                }
            }
            // Projectile and Boss
            else if ((c1 == PROJECTILE_CATEGORY && c2 == BOSS_CATEGORY) || (
                c2 == PROJECTILE_CATEGORY && c1 == BOSS_CATEGORY)) {
                System.out.println("PR-B CONTACT IS HAPPENING");
                if (c1 == PROJECTILE_CATEGORY) {
                    System.out.println("BOSS HIT");
                    removedProjectiles.add(b1);
                    bossHit(b2);
                } else {
                    System.out.println("BOSS HIT");
                    removedProjectiles.add(b2);
                    bossHit(b1);
                }
            }

            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // not used? only interactions when first get contact
    @Override
    public void endContact(Contact contact) {
    }

    // called just before Box2D resolves a collision --> resolve sound?
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    // called just after Box2D resolves a collision
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * Adds or Removes objects from the world.
     */
    public void postUpdate() {
        for (Minion m : state.getMinions()) {
            if (m.getHealth() <= 0) {
                removed.add(m);
            }
        }
        for (Boss b : state.getBosses()) {
            if (b.getHealth() <= 0) {
                removed.add(b);
            }
        }
        // Removes objects
        for (ObstacleSprite o : removed) {
            o.getObstacle().setActive(false);
            o.getObstacle().markRemoved(true);
            o.getObstacle().deactivatePhysics(world);
        }
        removed.clear();

        // Add coins
        for (ObstacleSprite c : coinsAdded) {
            state.getCoins().add(new Coin(c.getObstacle().getX(), c.getObstacle().getY(), world));
        }
        coinsAdded.clear();

        // Add companions
        if (companionAdded != null) {
            state.getPlayer().addCompanion(companionAdded);
            state.getCompanions().removeValue(companionAdded, false);
        }
        companionAdded = null;

        // Remove dead companions/minions/coins/boss from lists
        state.getPlayer().companions.removeIf(c -> !c.getObstacle().isActive());
        for (Minion m : state.getMinions()) {
            if (!m.getObstacle().isActive()) {
                state.getMinions().removeValue(m, false);
            }
        }
        for (Coin c : state.getCoins()) {
            if (!c.getObstacle().isActive()) {
                state.getCoins().removeValue(c, false);
            }
        }
        for (Boss b : state.getBosses()) {
            if (!b.getObstacle().isActive()) {
                state.getBosses().removeValue(b, false);
            }
        }

        // Remove dead projectiles and return them to their pools
        for (int i = state.getActiveProjectiles().size - 1; i >= 0; i--) {
            Projectile p = state.getActiveProjectiles().get(i);
            if (removedProjectiles.contains(p.getObstacle().getBody()) || p.getLife() <= 0) {
                // Reset physics state without destroying
//                p.reset();
//                if (p instanceof StrawberryProjectile) {
//                    ProjectilePools.strawberryPool.free((StrawberryProjectile) p);
//                }
                p.getObstacle().setActive(false);
                p.getObstacle().markRemoved(true);
                p.getObstacle().deactivatePhysics(world);
            }
        }
        removedProjectiles.clear();
        for (Projectile p : state.getActiveProjectiles()) {
            if (!p.getObstacle().isActive()) {
                state.getActiveProjectiles().removeValue(p, false);
            }
        }
    }

    /**
     * Boss loses health
     */
    public void bossHit(Body b) {
        for (Boss boss : state.getBosses()) {
            if (boss.getObstacle().getBody() == b) {
                boss.removeHealth(1);
                boss.setDamage(true);
                if (boss.getHealth() <= 0) {
                    removed.add((ObstacleSprite) b.getUserData());
                }
            }
        }
    }
}

