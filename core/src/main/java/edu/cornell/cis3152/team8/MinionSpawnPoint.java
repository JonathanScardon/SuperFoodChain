package edu.cornell.cis3152.team8;

import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class MinionSpawnPoint {
    private static final Random rand = new Random();
    private final float x;
    private final float y;

    private float antSpawnRate;
    private float cricketSpawnRate;
    private float spiderSpawnRate;

    public MinionSpawnPoint(float x, float y, float antSpawnProportion, float cricketSpawnProportion, float spiderSpawnProportion) {
        this.x = x;
        this.y = y;

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

    public Minion spawnMinion(int id, World world, Player player) {
        Minion m = null;
        float probability = rand.nextFloat();
        float cumulative = 0f;
        if ((cumulative += antSpawnRate) > probability) {
            m = new Minion(x, y, id, world, player);
        } else if ((cumulative += spiderSpawnRate) > probability) {
            m = new Spider(x, y, id, world, player);
        } else if ((cumulative += cricketSpawnRate) > probability) {
            m = new Cricket(x, y, id, world, player);
        }

        if (m == null) {
            throw new RuntimeException("No minion was spawned");
        }
        return m;
    }
}
