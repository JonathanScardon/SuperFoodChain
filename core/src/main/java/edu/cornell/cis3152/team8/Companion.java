package edu.cornell.cis3152.team8;

import com.badlogic.gdx.math.Vector2;
import static edu.cornell.cis3152.team8.InputController.CONTROL_MOVE_LEFT;
import static edu.cornell.cis3152.team8.InputController.CONTROL_NO_ACTION;

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
    private boolean collected;
    private Vector2 prevVelocity;

    /** How far the player moves in a single turn */
    private static float MOVE_SPEED;

    /** The direction the companion is currently moving in */
    private int direction;

    public Companion(float x, float y) {
        super(x, y);
        isAlive = true;
        cost = 0;
        cooldown = 5;
        abilityCool = 0;
        collected = false;
        prevVelocity = new Vector2();
        direction = InputController.CONTROL_NO_ACTION;
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
//        if (flag && abilityCool > 0) {
//            abilityCool--;
//        } else if (!flag) {
//            abilityCool = COOLDOWN;
//        }
    }

    public void update(int controlCode, long delta){
        if (isDestroyed()) {
            return;
        }
//
        // Determine how we are moving.
        boolean movingLeft  = controlCode ==  1;
        boolean movingRight = controlCode == 2;
        boolean movingUp    = controlCode == 4;
        boolean movingDown  = controlCode == 8;
        //System.out.println("" + movingLeft +movingRight+movingUp+movingDown);

        //System.out.println(controlCode == InputController.CONTROL_MOVE_LEFT);
        int s = 2;
        // Process movement command.
        if (movingLeft) {
            velocity.x = -s;
            velocity.y = 0;
        } else if (movingRight) {
            velocity.x = s;
            velocity.y = 0;
        } else if (movingUp) {
            velocity.y = s;
            velocity.x = 0;
        } else if (movingDown) {
            velocity.y = -s;
            velocity.x = 0;
        } else{
            velocity.x = 0;
            velocity.y = 0;
        }

        if (delta % 10 == 0){
            prevVelocity = velocity;
        }

        //System.out.println(velocity);
        position.add(velocity);
        //System.out.println(position);
    }

    public void follow(long delta){
        if (delta % 120 == 0){
            prevVelocity = velocity;
        }
    }


    public boolean isCollected(){
        return collected;
    }

    public void setCollected(Boolean b){
        collected = b;
    }

    public Vector2 getPrevVelocity() {
        return prevVelocity;
    }

    /**
     * Updates the movement of a companion in the chain
     * @param controlCode
     */
    public void update ( int controlCode){
        if (!isAlive) {
            return;
        }

        // Determine how we are moving.
        boolean movingLeft = (controlCode & InputController.CONTROL_MOVE_LEFT) != 0;
        boolean movingRight = (controlCode & InputController.CONTROL_MOVE_RIGHT) != 0;
        boolean movingUp = (controlCode & InputController.CONTROL_MOVE_UP) != 0;
        boolean movingDown = (controlCode & InputController.CONTROL_MOVE_DOWN) != 0;

        // Process movement command.
        if (movingLeft) {
            this.direction = InputController.CONTROL_MOVE_LEFT;
            velocity.x = -MOVE_SPEED;
            velocity.y = 0;
        } else if (movingRight) {
            this.direction = InputController.CONTROL_MOVE_RIGHT;
            velocity.x = MOVE_SPEED;
            velocity.y = 0;
        } else if (movingUp) {
            this.direction = InputController.CONTROL_MOVE_UP;
            velocity.y = -MOVE_SPEED;
            velocity.x = 0;
        } else if (movingDown) {
            this.direction = InputController.CONTROL_MOVE_DOWN;
            velocity.y = MOVE_SPEED;
            velocity.x = 0;
        }

    }

    /**
     *
     * @return control code of companion's current movement
     */
    public int getDirection () {
        return this.direction;
    }

    public void setDirection ( int direction){
        this.direction = direction;
    }

}
