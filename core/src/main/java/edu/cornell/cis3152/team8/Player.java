package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.gdiac.physics2.ObstacleSprite;

import java.util.LinkedList;

public class Player {

    private class CircularBuffer {
        private class PositionAndDirection {
            protected float x;
            protected float y;
            protected int dir;

            public PositionAndDirection(float x, float y, int dir) {
                this.x = x;
                this.y = y;
                this.dir = dir;
            }
        }

        private final PositionAndDirection[] buffer;
        private final int capacity;
        private int head;
        private int size;

        public CircularBuffer(int capacity) {
            this.capacity = capacity;
            this.buffer = new PositionAndDirection[capacity];
            this.head = -1;
            this.size = 0;
        }

        public void add(float x, float y, int direction) {
            head = (head + 1) % capacity;
            buffer[head] = new PositionAndDirection(x, y, direction);
            if (size < capacity) {
                size++;
            }
        }

        public PositionAndDirection get(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("Invalid index: " + index);
            }
            int actualIndex = (head - index + capacity) % capacity;
            return buffer[actualIndex];
        }

        public PositionAndDirection getSnapshot(int index) {
            return get(index * DELAY);
        }


        public int size() {
            return size;
        }

        public int capacity() {
            return capacity;
        }
    }

    /** Number of instructions to wait before following
     * Also the number of instructions stored per companion
     * */
    private static int DELAY;
    private final static int MAX_COMPANIONS = 15;

    private CircularBuffer controlBuffer;

    /** All companions in the player's current chain */
    protected LinkedList<Companion> companions;

    /** The players coin count, used to purchase companions */
    protected int coins;

    /** Indicates whether the player is attacking */
    protected boolean attacking;

    /** Indicates whether the player has a shield */
    protected boolean shield;

    /** The direction the player is facing */
    protected int forwardDirection;

    private static final float units = 64f;

    private long ticks;

    public Player(float x, float y, World world){
        this.companions = new LinkedList<>();
        this.coins = 0;
        this.attacking = false;
        this.shield = false;
        Companion head = new Strawberry(x,y,0,world);
        head.getObstacle().setName("player");
        companions.add(head);
        head.setCollected(true);
//        radius = 2;
        ticks = 0;
        DELAY = 10;

        Filter filter = head.getObstacle().getFilterData();
        filter.categoryBits = CollisionController.PLAYER_CATEGORY;
        filter.maskBits = CollisionController.MINION_CATEGORY | CollisionController.COMPANION_CATEGORY | CollisionController.COIN_CATEGORY | CollisionController.BOSS_CATEGORY | CollisionController.BORDER_CATEGORY;
        head.getObstacle().setFilterData(filter);

        this.controlBuffer = new CircularBuffer(MAX_COMPANIONS * DELAY);
    }

    /**
     *
     * @return head of player chain
     */
    public Companion getPlayerHead() {
        return companions.get(0);
    }

    /**
     * Updates movement of the chain
     * @param controlCode direction of player input
     */
    public void update(int controlCode){
        // automatically removes "dead" companions --> don't have to individually find in collision
        // can do deadCompanions instead?
        for (int i = 0; i < companions.size(); i ++) {
            Companion c = companions.get(i);
            if (!c.getObstacle().isActive()) {
                deleteCompanion(c);
            }
        }

        if (!isAlive()) {
            return;
        }

        Companion head = this.getPlayerHead();
        controlBuffer.add(head.getObstacle().getX(), head.getObstacle().getY(), head.getDirection());

        for (int i = 0; i < companions.size(); i++){
            Companion c = companions.get(i);
            if (c == getPlayerHead()){
                c.update(controlCode);
            }
            else {
                CircularBuffer.PositionAndDirection prev = controlBuffer.getSnapshot(i);
                c.update(prev.dir);
            }
        }

        for (Companion c: companions){
            c.animationFrame = getPlayerHead().animationFrame;
            if (c.getAnimator() != null) {
                c.animationFrame += c.animationSpeed;
                //System.out.println(animationFrame);
                if (c.animationFrame >= c.getAnimator().getSize()) {
                    c.animationFrame -= c.getAnimator().getSize()-1;
                }
            }
        }

    }

    /**
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch, float delta) {
        if (forwardDirection == 8){
            for (int i = companions.size() - 1; i >= 0; i--){
                companions.get(i).draw(batch, delta);
            }
        } else {
            for (Companion c : companions) {
                c.draw(batch, delta);
            }
        }
    }

    /**
     * Sets the player's attacking status
     * @param x the player's new attacking status
     */
    public void setAttacking(boolean x){
        this.attacking = x;
    }

    /**
     * Retrieves the player's attacking status
     * @return true if the player is attacking, false otherwise
     */
    public boolean isAttacking(){
        return this.attacking;
    }

    /**
     * Checks it the player is alive.
     * @return true when the player has at least one companion,
     * false otherwise
     */
    public boolean isAlive(){
        return !companions.isEmpty();
    }

    /**
     * Retrieves the current number of coins the player has
     * @return the number of coins the player has
     */
    public int getCoins(){
        return this.coins;
    }

    /**
     * Sets the player's coin count to a specified value
     * @param coins the new number of coins the player has
     */
    public void setCoins(int coins){
        this.coins = coins;
    }


    /**
     * Returns whether the player has a shield
     * @return whether the player has a shield
     */
    public boolean hasShield(){
        return this.shield;
    }

    /**
     * Sets whether the player has a shield
     * @param shield true if the player has a shield, false otherwise
     */
    public void setShield(boolean shield){
        this.shield = shield;
    }

    /**
     * Returning the direction the player is facing
     * @return the direction the player is facing
     */
    public int getForwardDirection(){
        return this.forwardDirection;
    }

    /**
     * Sets the direction the player is facing
     * @param forwardDirection the new number of coins the player has
     */
    public void setForwardDirection(int forwardDirection){
        this.forwardDirection = forwardDirection;
    }

    /**
     * Appends a companion to the player's chain
     * @param companion the companion to add
     */
    public void addCompanion(Companion companion){
        //limited num of companions
        if (companions.size() == MAX_COMPANIONS){
            return;
        }

        companion.getObstacle().setName("player");

        Filter filter = companion.getObstacle().getFilterData();
        filter.categoryBits = CollisionController.PLAYER_CATEGORY;
        filter.maskBits = CollisionController.MINION_CATEGORY | CollisionController.COMPANION_CATEGORY | CollisionController.COIN_CATEGORY | CollisionController.BOSS_CATEGORY | CollisionController.BORDER_CATEGORY;
        companion.getObstacle().setFilterData(filter);

        CircularBuffer.PositionAndDirection tail = controlBuffer.getSnapshot(companions.size());
        if (tail != null) {
            companion.getObstacle().setX(tail.x);
            companion.getObstacle().setY(tail.y);
        }
        companions.add(companion);
        companion.setCollected(true);
    }
    /**
     * Removes the companion from the player's chain
     * @param companion the companion to remove
     */
    public void deleteCompanion(Companion companion){
        int index = companions.indexOf(companion);
        //companion out of range
        if (index < 0 || index > companions.size()-1){
            throw new IndexOutOfBoundsException();
        }

        //update positions to fill deleted companion
        for (int i = index+1; i < companions.size(); i++){
            Companion c = companions.get(i);
            CircularBuffer.PositionAndDirection data = controlBuffer.getSnapshot(i-1);
            if (data != null){
                c.getObstacle().setX(data.x);
                c.getObstacle().setY(data.y);
            }
        }
        companions.remove(index);
    }


    /**
     * Returns the LinkedList of companions in the chain
     *
     */
    public LinkedList<Companion> getCompanions() {
        return companions;
    }
}
