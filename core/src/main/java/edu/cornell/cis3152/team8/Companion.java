package edu.cornell.cis3152.team8;

public abstract class Companion extends GameObject {

    public enum CompanionType {
        AVOCADO,
        GARLIC,
        STRAWBERRY,
        CORN,
        DURIAN,
        TANGERINE,
        BLUE_RASPBERRY
    }

    /** The type of Companion */
    private CompanionType type;

    /** Is companion alive */
    private boolean isAlive;

    /** Cost of companion */
    private int cost;

    /** How long companion must wait until use ability again */
    private int cooldown;

    /** The number of frames until use ability again */
    private int abilityCool;

    public Companion(float x, float y) {
        super(x, y);
        isAlive = true;
        cost = 0;
        cooldown = 5;
        abilityCool = 0;
    }

    // accessors
    @Override

    /** Object is Companion */
    public ObjectType getType() {
        return ObjectType.COMPANION;
    }

    /** Get type of Companion */
    public CompanionType getCompanionType() {return type;};

    /** Set type of Companion */
    public void setCompanionType(CompanionType type) {
        this.type = type;
    }

    /** Get cost of Companion */
    public int getCost() {return cost;};

    /** Set cost of Companion */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /** Get cooldown of ability */
    public int getCooldown() {return cooldown;};

    /** Set cooldown of ability */
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /** Returns whether companion can use ability */
    public boolean canUse() { return abilityCool <= 0; }

    /** Companion uses ability */
    public void useAbility(GameState state) {
        // individual abilities depending on type --> overrided by different types
    };

    /** Resets the cool down of the companion ability
     *
     * If flag is true, the weapon will cool down by one animation frame.
     * Otherwise it will reset to its maximum cooldown.
     *
     * @param flag whether to cooldown or reset
     */
    public void coolDown(boolean flag) {
        if (flag && abilityCool > 0) {
            abilityCool--;
        } else if (!flag) {
            abilityCool = cooldown;
        }
    }


}
