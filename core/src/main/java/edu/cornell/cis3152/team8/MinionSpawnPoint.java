package edu.cornell.cis3152.team8;

import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class MinionSpawnPoint {
    private static final Random rand = new Random();
    private final float x;
    private final float y;

    private final boolean bossOnly;
    private final float antSpawnRate;
    private final float cricketSpawnRate;
    private final float spiderSpawnRate;

    private final GameState gameState;
    private final World world;
    private final Player player;

    public MinionSpawnPoint(GameState state, float x, float y, boolean bossOnly, float antSpawnProportion, float cricketSpawnProportion, float spiderSpawnProportion) {
        this.gameState = state;
        this.world = state.getWorld();
        this.player = state.getPlayer();
        this.x = x;
        this.y = y;
        this.bossOnly = bossOnly;

        float totalSpawnProportion = antSpawnProportion + cricketSpawnProportion + spiderSpawnProportion;

        if (totalSpawnProportion <= 0) {
            throw new RuntimeException("Total minion spawn proportions less than 0");
        }

        this.cricketSpawnRate = cricketSpawnProportion / totalSpawnProportion;
        this.spiderSpawnRate = spiderSpawnProportion / totalSpawnProportion;
        this.antSpawnRate = 1 - (this.cricketSpawnRate + this.spiderSpawnRate);

        if (this.cricketSpawnRate < 0 || this.spiderSpawnRate < 0 || this.antSpawnRate < 0 || this.cricketSpawnRate + this.spiderSpawnRate + this.antSpawnRate > 1 || this.cricketSpawnRate + this.spiderSpawnRate + this.antSpawnRate <= 0) {
            throw new RuntimeException("Invalid minion spawn proportions");
        }
    }

    /**
     * Creates a minion at this point
     */
    public void spawnMinion() {
        Minion m = null;
        float probability = rand.nextFloat();
        float cumulative = 0f;
        if ((cumulative += antSpawnRate) > probability) {
            m = new Ant(x, y, world, player);
        } else if ((cumulative += spiderSpawnRate) > probability) {
            m = new Spider(x, y, world, player);
        } else if ((cumulative += cricketSpawnRate) > probability) {
            m = new Cricket(x, y, world, player);
        }

        if (m == null) {
            throw new RuntimeException("No minion was spawned");
        }

        gameState.getMinions().add(m);
    }

    /**
     * Returns whether this spawn point is only triggered by the boss and not automatically
     * @return true if this spawn point is only for the boss, false otherwise
     */
    public boolean isBossOnly() {
        return bossOnly;
    }
}
