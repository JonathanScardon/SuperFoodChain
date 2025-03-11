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

    /** How long companion must wait until use ability again */
    private static int COOLDOWN;

    /** The type of Companion */
    private CompanionType type;

    /** Is companion alive */
    private boolean isAlive;

    /** The number of frames until use ability again */
    private int abilityCool;

    public Companion(float x, float y) {
        super(x, y);
        isAlive = true;
        abilityCool = 0;

        COOLDOWN = 5;
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

    /** Returns whether companion can use ability */
    public boolean canUse() { return abilityCool <= 0; }

    /** Companion uses ability */
    public void useAbility() {
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
            abilityCool = COOLDOWN;
        }
    }


}
