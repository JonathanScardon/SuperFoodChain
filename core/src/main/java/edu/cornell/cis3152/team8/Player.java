package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.cis3152.team8.companions.BlueRaspberry;
import edu.cornell.cis3152.team8.companions.Durian;
import edu.cornell.cis3152.team8.companions.Strawberry;
import edu.cornell.gdiac.assets.AssetDirectory;
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

        /**
         * Updates the head of the buffer, ensuring that members of the chain are reading from the
         * correct positions when the player's head dies
         */
        public void updateHead() {
            head = (head - DELAY + capacity) % capacity;
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

    /**
     * Number of instructions to wait before following Also the number of instructions stored per
     * companion
     */
    private static int DELAY;
    private final static int MAX_COMPANIONS = 15;

    private CircularBuffer controlBuffer;

    /**
     * All companions in the player's current chain
     */
    protected LinkedList<Companion> companions;

    /**
     * The players coin count, used to purchase companions
     */
    protected int coins;

    /**
     * Indicates whether the player is attacking
     */
    protected boolean attacking;

    /**
     * Indicates whether the player has a shield
     */
    protected boolean shield;

    /**
     * The direction the player is facing
     */
    protected int forwardDirection;

    private long ticks;

    private Texture current;
    private static Texture headDirStill;
    private static Texture headDirUp;
    private static Texture headDirDown;
    private static Texture headDirLeft;
    private static Texture headDirRight;

    private float originX;
    private float originY;

    private Affine2 transform;

    public Player(float x, float y, World world) {
        this.companions = new LinkedList<>();
        this.coins = 0;
        this.attacking = false;
        this.shield = false;
        ticks = 0;

        current = headDirStill;
        originX = 0;
        originY = 0;
        transform = new Affine2();

        calculateDelay();

        this.controlBuffer = new CircularBuffer(MAX_COMPANIONS * DELAY);
    }

    public static void setAssets(AssetDirectory assets) {
        headDirStill = assets.getEntry("headDirStill", Texture.class);
        headDirUp = assets.getEntry("headDirUp", Texture.class);
        headDirDown = assets.getEntry("headDirDown", Texture.class);
        headDirLeft = assets.getEntry("headDirLeft", Texture.class);
        headDirRight = assets.getEntry("headDirRight", Texture.class);
    }

    /**
     * @return head of player chain
     */
    public Companion getPlayerHead() {
        return companions.get(0);
    }

    /**
     * Updates movement of the chain
     *
     * @param controlCode direction of player input
     */
    public void update(float delta, int controlCode) {
        //removing dead companions from chain
        for (int i = 0; i < companions.size(); i++) {
            Companion c = companions.get(i);
            if (!c.getObstacle().isActive()) {
                deleteCompanion(c);
            }
        }

        if (!isAlive()) {
            return;
        }

        //updating buffer data
        Companion head = this.getPlayerHead();
        controlBuffer.add(head.getObstacle().getX(), head.getObstacle().getY(),
            head.getDirection());

        //updating companions in the chain
        for (int i = 0; i < companions.size(); i++) {
            Companion c = companions.get(i);
            if (c == getPlayerHead()) {
                c.update(delta, controlCode);
            } else {
                CircularBuffer.PositionAndDirection prev = controlBuffer.getSnapshot(i);
                c.update(delta, InputController.CONTROL_NO_ACTION);
                c.getObstacle().setX(prev.x);
                c.getObstacle().setY(prev.y);
            }
        }

        //updating animation frames
        for (Companion c : companions) {
            c.animationFrame = getPlayerHead().animationFrame;
            if (c.getAnimator() != null && c.isMoving()) {
                c.animationFrame += c.animationSpeed;
                if (c.animationFrame >= c.getAnimator().getSize()) {
                    c.animationFrame -= c.getAnimator().getSize() - 1;
                }
            }
        }
    }

    /**
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        if (isAlive()) {
            switch (forwardDirection) {
                case (InputController.CONTROL_MOVE_LEFT) -> {
                    current = headDirLeft;
                    originX = current.getWidth() / 2.0f + 30f;
                    originY = current.getHeight() / 2.0f + 50f;
                }
                case (InputController.CONTROL_MOVE_RIGHT) -> {
                    current = headDirRight;
                    originX = current.getWidth() / 2.0f - 30f;
                    originY = current.getHeight() / 2.0f + 50f;
                }
                case (InputController.CONTROL_MOVE_UP) -> {
                    current = headDirUp;
                    originX = current.getWidth() / 2.0f;
                    originY = current.getHeight() / 2.0f;
                }
                case (InputController.CONTROL_MOVE_DOWN) -> {
                    current = headDirDown;
                    originX = current.getWidth() / 2.0f;
                    originY = current.getHeight() / 2.0f + 100;
                }
                case (InputController.CONTROL_NO_ACTION) -> {
                    current = headDirStill;
                    originX = current.getWidth() / 2.0f;
                    originY = current.getHeight() / 2.0f + 50;
                }
            }
            Companion head = getPlayerHead();
            SpriteBatch.computeTransform(transform, originX, originY
                , head.getObstacle().getX() * GameScene.PHYSICS_UNITS,
                head.getObstacle().getY() * GameScene.PHYSICS_UNITS, 0, 0.4f, 0.4f);

            batch.draw(current, transform);
        }
    }

    /**
     * Draw the desired position and direction for each companion other than the head
     *
     * @param batch the SpriteBatch to draw with
     */
    public void drawDebug(SpriteBatch batch) {
        for (int i = 1; i < companions.size(); i++) {
            CircularBuffer.PositionAndDirection prev = controlBuffer.getSnapshot(i);

            switch (prev.dir) {
                case (InputController.CONTROL_MOVE_LEFT) -> {
                    current = headDirLeft;
                    originX = current.getWidth() / 2.0f + 30f;
                    originY = current.getHeight() / 2.0f + 50f;
                }
                case (InputController.CONTROL_MOVE_RIGHT) -> {
                    current = headDirRight;
                    originX = current.getWidth() / 2.0f - 30f;
                    originY = current.getHeight() / 2.0f + 50f;
                }
                case (InputController.CONTROL_MOVE_UP) -> {
                    current = headDirUp;
                    originX = current.getWidth() / 2.0f;
                    originY = current.getHeight() / 2.0f;
                }
                case (InputController.CONTROL_MOVE_DOWN) -> {
                    current = headDirDown;
                    originX = current.getWidth() / 2.0f;
                    originY = current.getHeight() / 2.0f + 100;
                }
                case (InputController.CONTROL_NO_ACTION) -> {
                    current = headDirStill;
                    originX = current.getWidth() / 2.0f;
                    originY = current.getHeight() / 2.0f + 50;
                }
            }

            SpriteBatch.computeTransform(transform, originX, originY
                , prev.x * GameScene.PHYSICS_UNITS,
                prev.y * GameScene.PHYSICS_UNITS, 0, 0.4f, 0.4f);
            batch.draw(current, transform);
        }
    }

    /**
     * Sets the player's attacking status
     *
     * @param x the player's new attacking status
     */
    public void setAttacking(boolean x) {
        this.attacking = x;
    }

    /**
     * Retrieves the player's attacking status
     *
     * @return true if the player is attacking, false otherwise
     */
    public boolean isAttacking() {
        return this.attacking;
    }

    /**
     * Checks it the player is alive.
     *
     * @return true when the player has at least one companion, false otherwise
     */
    public boolean isAlive() {
        return !companions.isEmpty();
    }

    /**
     * Retrieves the current number of coins the player has
     *
     * @return the number of coins the player has
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * Sets the player's coin count to a specified value
     *
     * @param coins the new number of coins the player has
     */
    public void setCoins(int coins) {
        this.coins = coins;
    }


    /**
     * Returns whether the player has a shield
     *
     * @return whether the player has a shield
     */
    public boolean hasShield() {
        return this.shield;
    }

    /**
     * Sets whether the player has a shield
     *
     * @param shield true if the player has a shield, false otherwise
     */
    public void setShield(boolean shield) {
        this.shield = shield;
    }

    /**
     * Returning the direction the player is facing
     *
     * @return the direction the player is facing
     */
    public int getForwardDirection() {
        return this.forwardDirection;
    }

    /**
     * Sets the direction the player is facing
     *
     * @param forwardDirection the new number of coins the player has
     */
    public void setForwardDirection(int forwardDirection) {
        this.forwardDirection = forwardDirection;
    }

    /**
     * Appends a companion to the player's chain
     *
     * @param companion the companion to add
     */
    public void addCompanion(Companion companion) {
        //limited num of companions
        if (companions.size() == MAX_COMPANIONS) {
            return;
        }

        //initializing box2d
        companion.getObstacle().setName("player");

        Filter filter = companion.getObstacle().getFilterData();
        filter.categoryBits = CollisionController.PLAYER_CATEGORY;
        filter.maskBits =
            CollisionController.MINION_CATEGORY | CollisionController.COMPANION_CATEGORY
                | CollisionController.COIN_CATEGORY | CollisionController.BOSS_CATEGORY;
        companion.getObstacle().setFilterData(filter);

//        CircleShape shape = new CircleShape();
//        shape.setPosition(companion.getObstacle().getPosition());
//        shape.setRadius(0.3f);
//
//        FixtureDef selfFixtureDef = new FixtureDef();
//        selfFixtureDef.shape = shape;
//        selfFixtureDef.isSensor = true;
//
//        Filter selfFilter = new Filter();
//        selfFilter.categoryBits = CollisionController.PLAYER_CATEGORY;
//        selfFilter.maskBits = CollisionController.PLAYER_CATEGORY;
//
//        Fixture selfFixture = companion.getObstacle().getBody().createFixture(selfFixtureDef);
//        selfFixture.setFilterData(selfFilter);
//        selfFixture.setUserData(this);
//
//        shape.dispose();

        CircularBuffer.PositionAndDirection tail = controlBuffer.getSnapshot(companions.size());

        //only add a companion if there is enough data to do so
        if (tail != null & !companions.isEmpty()) {
            companion.getObstacle().setX(tail.x);
            companion.getObstacle().setY(tail.y);
            companions.add(companion);
            companion.setCollected(true);
        }
        //allowed to add a companion if we are starting the level
        else if (companions.isEmpty()) {
            companions.add(companion);
            companion.setCollected(true);
        }
    }

    /**
     * Removes the companion from the player's chain
     *
     * @param companion the companion to remove
     */
    public void deleteCompanion(Companion companion) {
        int index = companions.indexOf(companion);
        //companion out of range
        if (index < 0 || index > companions.size() - 1) {
            throw new IndexOutOfBoundsException();
        }

        //lose speed boost gained from Blue Raspberry
        if (companion.getCompanionType() == Companion.CompanionType.BLUE_RASPBERRY) {
            ((BlueRaspberry) companion).loseAbility();
        }

        //no catch up needed when head is removed (no gap is created in the chain)
        if (companion == getPlayerHead()) {
            companions.remove(index);
            controlBuffer.updateHead();
            return;
        }

        //update positions to fill deleted companion
        for (int i = index + 1; i < companions.size(); i++) {
            Companion c = companions.get(i);
            CircularBuffer.PositionAndDirection data = controlBuffer.getSnapshot(i - 1);
            if (data != null) {
                c.getObstacle().setX(data.x);
                c.getObstacle().setY(data.y);
            }
        }
        companions.remove(index);
    }

    /**
     * Calculates and sets buffer read delay (used for chaining behavior) based on the speed of the
     * chain
     */
    public static void calculateDelay() {
        int baseSpeed = 225;
        int baseDelay = 15;

        float rawDelay = (baseDelay * baseSpeed) / Companion.getSpeed();
        DELAY = Math.max(1, Math.round(rawDelay));
    }


    /**
     * Returns the LinkedList of companions in the chain
     */
    public LinkedList<Companion> getCompanions() {
        return companions;
    }
}
