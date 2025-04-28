package edu.cornell.cis3152.team8;

import com.badlogic.gdx.utils.Array;

public abstract class BossAttackPattern {

    protected enum AttackState {
        /**
         * The boss is not currently executing this attack pattern
         */
        INACTIVE,
        /**
         * The boss is about to attack and is providing a warning to the player
         */
        WARN,
        /**
         * The boss is attacking
         */
        ATTACK,
        /**
         * The boss has finished this attack
         */
        ENDED,
    }

    protected final BossController controller;
    protected final Boss boss;
    protected final Array<MinionSpawnPoint> minionSpawnPointArray;

    protected AttackState state;
    protected String attackName;

    protected BossAttackPattern(BossController controller) {
        this.controller = controller;
        this.boss = controller.boss;
        this.minionSpawnPointArray = new Array<>();
        this.state = AttackState.INACTIVE;
    }

    /**
     * Start the warning process for the boss
     */
    public abstract void start();

    public abstract void update(float delta);

    /**
     * @return Whether the warning and attack have both ended
     */
    public boolean isEnded() {
        return this.state == AttackState.ENDED;
    }

    public String getName() {
        return attackName;
    }

    /**
     * Add a spawn point for the attack to spawn minions add
     *
     * @param spawnPoint the spawn point to add
     */
    void addMinionSpawnPoint(MinionSpawnPoint spawnPoint) {
        minionSpawnPointArray.add(spawnPoint);
    }

    /**
     * Trigger spawning at every spawn point associated with this attack
     */
    void spawnMinions() {
        for (MinionSpawnPoint spawn : minionSpawnPointArray) {
            spawn.spawnMinion();
        }
    }
}
