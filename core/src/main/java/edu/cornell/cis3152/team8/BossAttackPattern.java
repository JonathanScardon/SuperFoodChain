package edu.cornell.cis3152.team8;

public interface BossAttackPattern {
    public enum AttackState {
        /**
         * The boss is about to attack and is providing a warning to the player
         */
        WARN,
        /**
         * The boss is attacking
         */
        ATTACK,
    }

    void warn();
    void attack();
    void update();
    boolean attackEnded();
}
