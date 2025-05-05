package edu.cornell.cis3152.team8;
//Heavily inspired by AILab Collision Controller

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.cis3152.team8.projectiles.DurianProjectile;
import edu.cornell.cis3152.team8.projectiles.StrawberryProjectile;
import edu.cornell.gdiac.physics2.Obstacle;
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

    //private Array<MinionController> minionControls;
    private Array<Companion> deadCompanions;

    /**
     * Cache attribute for calculations
     */
    private Vector2 tmp;

    //Game audio controller
    private GameAudio audio;

    /**
     * Category bits for Filtering
     */
    public static final short PLAYER_CATEGORY = 0x0001;
    public static final short MINION_CATEGORY = 0x0002;
    public static final short COMPANION_CATEGORY = 0x0004;
    public static final short COIN_CATEGORY = 0x0008;
    public static final short BOSS_CATEGORY = 0x0010;
    public static final short PROJECTILE_CATEGORY = 0x0020;

    /**
     * Creates a CollisionController for the given models.
     */
    public CollisionController(GameState state) {
        tmp = new Vector2();
        this.state = state;
        this.world = state.getWorld();
        audio = state.getAudio();
    }

    // Contact Listener Methods
    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        Body b1 = f1.getBody();
        Body b2 = f2.getBody();

//        System.out.println("BEGIN CONTACT");

        try {
//            System.out.println("TRYING CONTACT");
            ObstacleSprite s1 = (ObstacleSprite) b1.getUserData();
            ObstacleSprite s2 = (ObstacleSprite) b2.getUserData();

            short c1 = f1.getFilterData().categoryBits;
            short c2 = f2.getFilterData().categoryBits;

            // Player Collisions

            // Player and Player
            if (c1 == PLAYER_CATEGORY && c2 == PLAYER_CATEGORY) {
                System.out.println("P-P CONTACT IS HAPPENING");
                if (state.getPlayer().getPlayerHead().getObstacle().getBody() == b1) {
                    removed.add(s1);
                }
                else {
                    removed.add(s2);
                }
            }

            // Player and Minion
            if ((c1 == PLAYER_CATEGORY && c2 == MINION_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == MINION_CATEGORY)) {
//                System.out.println("P-M CONTACT IS HAPPENING");
                if (c1 == PLAYER_CATEGORY) {
//                    System.out.println("DEATH");
                    removed.add(s1);
                    removed.add(s2);
                    coinsAdded.add(s2);
                } else {
                    removed.add(s2);
                    removed.add(s1);
                    coinsAdded.add(s1);
                }
                audio.play("minionDeath");
                audio.play("companionDeath");
            }

//            // Player and Boss
            else if ((c1 == PLAYER_CATEGORY && c2 == BOSS_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == BOSS_CATEGORY)) {
//                System.out.println("P-B CONTACT IS HAPPENING");
//                System.out.println("BOSS HIT");
                if (c1 == BOSS_CATEGORY) {
                    removed.add(s2);
                    bossHit(b1);
                } else {
                    removed.add(s1);
                    bossHit(b2);
                }
                audio.play("mouseDeath");
                audio.play("companionDeath");
            }

            // Player and Coin
            else if ((c1 == PLAYER_CATEGORY && c2 == COIN_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == COIN_CATEGORY)) {
//                System.out.println("ADD COIN");
                state.getPlayer().setCoins(state.getPlayer().getCoins() + 1);
                if (c1 == PLAYER_CATEGORY) {
                    removed.add(s2);
                } else {
                    removed.add(s1);
                }
                audio.play("coin");
            }

            // Player and Companion
            else if ((c1 == PLAYER_CATEGORY && c2 == COMPANION_CATEGORY) || (c2 == PLAYER_CATEGORY
                && c1 == COMPANION_CATEGORY)) {
//                System.out.println("P-C CONTACT IS HAPPENING");
                if (c1 == COMPANION_CATEGORY) {
                    for (Companion c : state.getCompanions()) {
                        if (c.getCost() <= state.getPlayer().getCoins()
                            && c.getObstacle().getBody() == b1) {
                            c.setGlow(true);
                            //if (Gdx.input.isKeyPressed(Input.Keys.E)) {
//                                System.out.println("ADD COMPANION");
                            companionAdded = c;
                            audio.play("companionRecruitment");
                            state.getPlayer()
                                .setCoins(state.getPlayer().getCoins() - c.getCost());
                            //}

                        }
                    }
                } else {
                    for (Companion c : state.getCompanions()) {
                        if (c.getObstacle().getBody() == b2
                            && c.getCost() <= state.getPlayer().getCoins()) {
                            c.setGlow(true);
                            // if (Gdx.input.isKeyPressed(Input.Keys.E)) {
//                                System.out.println("ADD COMPANION");
                            companionAdded = c;
                            audio.play("companionRecruitment");
                            state.getPlayer()
                                .setCoins(state.getPlayer().getCoins() - c.getCost());
                            //}
                        }
                    }
                }
            }

            // Projectile Collisions

            // Projectile and Minion
            else if ((c1 == PROJECTILE_CATEGORY && c2 == MINION_CATEGORY) || (
                c2 == PROJECTILE_CATEGORY && c1 == MINION_CATEGORY)) {
//                System.out.println("PR-M CONTACT IS HAPPENING");
                if (c1 == PROJECTILE_CATEGORY) {
//                    System.out.println("KILL MINION");
                    for (Projectile p : state.getActiveProjectiles()) {
                        if (p.getObstacle().getBody() == b1 && p.collisionDie) {
                            removedProjectiles.add(b1);
                        }
                        else if (p.getObstacle().getBody() == b1 && !p.collisionDie) {
                            System.out.println("Garlic Projectile Hit");
                        }
                    }
                    minionHit(b2, b1);
                } else {
//                    System.out.println("KILL MINION");
                    for (Projectile p : state.getActiveProjectiles()) {
                        if (p.getObstacle().getBody() == b2 && p.collisionDie) {
                            removedProjectiles.add(b2);
                        }
                        else if (p.getObstacle().getBody() == b2 && !p.collisionDie){
                            System.out.println("Garlic Projectile Hit");
                        }
                    }
                    minionHit(b1, b2);
                }

            }
            // Projectile and Boss
            else if ((c1 == PROJECTILE_CATEGORY && c2 == BOSS_CATEGORY) || (
                c2 == PROJECTILE_CATEGORY && c1 == BOSS_CATEGORY)) {
//                System.out.println("PR-B CONTACT IS HAPPENING");
                if (c1 == PROJECTILE_CATEGORY) {
//                    System.out.println("BOSS HIT");
                    removedProjectiles.add(b1);
                    bossHit(b2);
                } else {
//                    System.out.println("BOSS HIT");
                    removedProjectiles.add(b2);
                    bossHit(b1);
                }
            }

//            System.out.println();
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
        // Kills player if out of bounds
        if (!state.inBounds(state.getPlayer().getPlayerHead())) {
            removed.add(state.getPlayer().getPlayerHead());
            audio.play("companionDeath");
        }
        // Kills minions if no health
        for (Minion m : state.getMinions()) {
            if (m.getHealth() <= 0) {
                removed.add(m);
            }
        }
        // Kills boss if no health
        for (Boss b : state.getBosses()) {
            if (b.getHealth() <= 0) {
                removed.add(b);
            }
        }
        // Removes coins if time limit
        for (Coin c : state.getCoins()) {
            if (c.getLife() <= 0) {
                removed.add(c);
            }
        }
        // Removes objects
        for (ObstacleSprite o : removed) {
            o.getObstacle().setActive(false);
            o.getObstacle().markRemoved(true);
            o.getObstacle().deactivatePhysics(world);
            state.getDead().add(o);
        }
        removed.clear();

//        System.out.println("[");
//        for (ObstacleSprite o : state.getDead()){
//            System.out.println(o.getName());
//        }
//        System.out.println("]");

        // Add coins
        for (ObstacleSprite c : coinsAdded) {
            state.getCoins().add(new Coin(c.getObstacle().getX(), c.getObstacle().getY(), world));
        }
        coinsAdded.clear();

        // Add companions
        if (companionAdded != null) {
            state.getPlayer().addCompanion(companionAdded);
            switch (companionAdded.getCompanionType()) {
                case DURIAN -> state.numDurians--;
                case STRAWBERRY -> state.numStrawberries--;
                case AVOCADO -> state.numAvocados--;
                case BLUE_RASPBERRY -> state.numBlueRaspberries--;
                case PINEAPPLE -> state.numPineapples--;
            }
            state.getCompanions().removeValue(companionAdded, false);
        }
        companionAdded = null;

        // Remove dead companions/minions/coins/boss from lists
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
//        for (Boss b : state.getBosses()) {
//            if (!b.getObstacle().isActive()) {
//                //state.getBosses().removeValue(b, false);
//            }
//        }
        for (ObstacleSprite o : state.getDead()) {
            switch (o.getName()) {
                case ("minion") -> {
                    if (((Minion) o).shouldRemove()) {
                        state.getDead().removeValue(o, false);
                    }
                }
                case ("player") -> {
                    if (((Companion) o).shouldRemove()) {
                        state.getDead().removeValue(o, false);
                    }
                }
                case ("boss") -> {
                    if (((Boss) o).shouldRemove()) {
                        state.getDead().removeValue(o, false);
                    }
                }
                case ("coin") -> {
                    if (((Coin) o).shouldRemove()) {
                        state.getDead().removeValue(o, false);
                    }
                }
            }

        }

        // Remove dead projectiles and return them to their pools
        for (int i = state.getActiveProjectiles().size - 1; i >= 0; i--) {
            Projectile p = state.getActiveProjectiles().get(i);
            if (removedProjectiles.contains(p.getObstacle().getBody()) || p.getLife() <= 0) {
                // Reset physics state without destroying
                p.reset();
                if (p instanceof StrawberryProjectile) {
                    ProjectilePools.strawberryPool.free((StrawberryProjectile) p);
                }
                if (p instanceof DurianProjectile) {
                    ProjectilePools.durianPool.free((DurianProjectile) p);
                }
//                if (p.getObstacle().getBody() != null
//                    && p.getObstacle().getBody().getWorld() != null) {
//                    p.getObstacle().setActive(false);
//                    p.getObstacle().markRemoved(true);
//                    p.getObstacle().deactivatePhysics(world);
//                }
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
     * Minion loses health (projectile collision)
     */
    public void minionHit(Body b1, Body b2) {
        for (Minion m : state.getMinions()) {
            if (m.getObstacle().getBody() == b1) {
                for (Projectile p : state.getActiveProjectiles()) {
                    if (p.getObstacle().getBody() == b2) {
                        m.removeHealth(p.getAttack());
                        if (m.getHealth() <= 0) {
                            ObstacleSprite s = (ObstacleSprite) b1.getUserData();
                            coinsAdded.add(s);
                            removed.add(s);
                            audio.play("minion");
                        }
                    }
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
                if (boss.getHealth() > 0) {
                    audio.play(boss.getName() + "Hit");
                }
                boss.removeHealth(1);
                boss.setDamage(true);
                if (boss.getHealth() <= 0) {
                    audio.play(boss.getName() + "Death");
                    removed.add((ObstacleSprite) b.getUserData());
                }

            }
        }
    }
}

