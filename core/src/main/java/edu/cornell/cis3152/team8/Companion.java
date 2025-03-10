package edu.cornell.cis3152.team8;

public abstract class Companion extends GameObject {

    public enum CompanionType {
        AVOCADO,
        GARLIC,
        STRAWBERRY,
        CORN,
        DURIAN,
        TANGERINE,
        BLUE RASBERRY
    }


    public Companion(float x, float y) {
        super(x, y);
    }

    // accessors
    @Override
    public ObjectType getType() {
        return ObjectType.COMPANION;
    }

    public abstract CompanionType getCompanionType();

}
