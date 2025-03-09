package edu.cornell.cis3152.team8;

public abstract class Boss extends GameObject {
    public enum BossType {
        MOUSE,
        CHEF,
        CHOPSTICKS
    }

    protected float health;

    public Boss(float x, float y) {
        super(x, y);
    }

    // accessors
    @Override
    public ObjectType getType() {
        return ObjectType.BOSS;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public abstract BossType getBossType();
}
