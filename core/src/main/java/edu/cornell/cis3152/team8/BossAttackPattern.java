package edu.cornell.cis3152.team8;

public interface BossAttackPattern {
    enum AttackState {
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
    }

    /**
     * Start the warning process for the boss
     */
    void start();

    void update(float delta);

    /**
     * @return Whether the warning and attack have both ended
     */
    boolean isEnded();
}
