package edu.cornell.cis3152.team8;
//Heavily inspired by AILab Collision Controller

import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics2.ObstacleSprite;
import com.badlogic.gdx.utils.Array;
import edu.cornell.cis3152.team8.GameObject.ObjectType;

import java.util.*;

import com.badlogic.gdx.math.*;
import edu.cornell.gdiac.audio.SoundEffect;
import edu.cornell.gdiac.audio.SoundEffectManager;

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
    }

    /**
     * Updates minions, bosses, and the player moving them forward.
     * Handles all collisions.
     */
    public void update() {

    }

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
            if ((c1 == PLAYER_CATEGORY && c2 == MINION_CATEGORY) || (c2 == PLAYER_CATEGORY && c1 == MINION_CATEGORY)) {
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

            // Player and Boss
            else if ((c1 == PLAYER_CATEGORY && c2 == BOSS_CATEGORY) || (c2 == PLAYER_CATEGORY && c1 == BOSS_CATEGORY)) {
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
            else if ((c1 == PLAYER_CATEGORY && c2 == COIN_CATEGORY) || (c2 == PLAYER_CATEGORY && c1 == COIN_CATEGORY)) {
                System.out.println("ADD COIN");
                state.getPlayer().setCoins(state.getPlayer().getCoins() + 1);
                if (c1 == PLAYER_CATEGORY) {
                    removed.add(s2);
                } else {
                    removed.add(s1);
                }
            }

            // Player and Companion
            else if ((c1 == PLAYER_CATEGORY && c2 == COMPANION_CATEGORY) || (c2 == PLAYER_CATEGORY && c1 == COMPANION_CATEGORY)) {
                System.out.println("P-C CONTACT IS HAPPENING");
                if (c1 == COMPANION_CATEGORY) {
                    for (Companion c : state.getCompanions()) {
                        if (c.getObstacle().getBody() == b1 && c.getCost() <= state.getPlayer().getCoins()) {
                            System.out.println("ADD COMPANION");
                            companionAdded = c;
                            state.getPlayer().setCoins(state.getPlayer().getCoins() - c.getCost());
                        }
                    }
                } else {
                    for (Companion c : state.getCompanions()) {
                        if (c.getObstacle().getBody() == b2 && c.getCost() <= state.getPlayer().getCoins()) {
                            System.out.println("ADD COMPANION");
                            companionAdded = c;
                            state.getPlayer().setCoins(state.getPlayer().getCoins() - c.getCost());
                        }
                    }
                }
            }

            // Projectile Collisions
            // Projectile and Minion
            else if ((c1 == PROJECTILE_CATEGORY && c2 == MINION_CATEGORY) || (c2 == PROJECTILE_CATEGORY && c1 == MINION_CATEGORY)) {
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
            else if ((c1 == PROJECTILE_CATEGORY && c2 == BOSS_CATEGORY) || (c2 == PROJECTILE_CATEGORY && c1 == BOSS_CATEGORY)) {
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
            state.getCompanions().remove(companionAdded);
        }
        companionAdded = null;

        // Remove dead companions/minions from lists
        state.getPlayer().companions.removeIf(c -> !c.getObstacle().isActive());
        state.getMinions().removeIf(m -> !m.getObstacle().isActive());

        // Remove dead projectiles and return them to their pools
        for (int i = state.getActiveProjectiles().size - 1; i >= 0; i--) {
            Projectile p = state.getActiveProjectiles().get(i);
            if (removedProjectiles.contains(p.getObstacle().getBody()) || p.getLife() <= 0) {
                state.getActiveProjectiles().removeIndex(i);
                p.reset();
                if (p instanceof StrawberryProjectile) {
                    ProjectilePools.strawberryPool.free((StrawberryProjectile) p);
                }
            }
        }
    }

    /**
     * Boss loses health
     */
    public void bossHit(Body b) {
        for (Boss boss : state.getBosses()) {
            if (boss.getObstacle().getBody() == b) {
                boss.setHealth(boss.getHealth()-1);
                if (boss.getHealth() <= 0) {
                    removed.add((ObstacleSprite) b.getUserData());
                }
            }
        }
    }
}

