package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.ParserUtils;
import edu.cornell.gdiac.graphics.*;
import edu.cornell.gdiac.physics2.BoxObstacle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics2.CapsuleObstacle;
import edu.cornell.gdiac.physics2.ObstacleSprite;

public class Minion extends ObstacleSprite {

    protected static final float units = 64f;

    //drawing fields
    protected float size;
    private boolean damage;
    protected float animationSpeed;
    protected float animationFrame;
    protected SpriteSheet deadMinion;
    private boolean dead;

    //Minion constants
    protected int health;
    protected float moveSpeed;

    //The player to target
    protected Player player;

    //When to remove a dead minion
    private boolean remove;
    private boolean flipHorizontal;


    /**
     * Constructs a Minion at the given position
     *
     * @param x      The x-coordinate of the object
     * @param y      The y-coordinate of the object
     * @param player The player the minion will attack
     */
    public Minion(float x, float y, World world, Player player) {
        super(new BoxObstacle(x / units, y / units, 0.8f, 0.5f), true);

        this.player = player;
        dead = false;
        remove = false;
        damage = false;
        animationFrame = 0;

        Texture texture = new Texture("images/Companion_Death_Universal.png");
        deadMinion = new SpriteSheet(texture, 1, 6);

        obstacle = getObstacle();
        obstacle.setName("minion");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.DynamicBody);

        obstacle.setPhysicsUnits(units);

        obstacle.activatePhysics(world);
        obstacle.setUserData(this);
        obstacle.setSensor(true);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.MINION_CATEGORY;
        filter.maskBits =
            CollisionController.PLAYER_CATEGORY | CollisionController.PROJECTILE_CATEGORY;
        obstacle.setFilterData(filter);

        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
    }

    /**
     * Sets constants for this Coin
     *
     * @param constants The JsonValue of the object
     */
    private void setConstants(JsonValue constants) {
        health = constants.getInt("health");
        moveSpeed = 1;
    }

    /**
     * Updates the animation frames of this Minion.
     *
     * @param moving if the minion should be moving
     */
    public void update(boolean moving) {
        if ((sprite != null && dead) || !obstacle.getLinearVelocity().equals(new Vector2())) {
            animationFrame += animationSpeed;
            if (!dead && animationFrame >= sprite.getSize()) {
                animationFrame -= sprite.getSize();
            }
        }
        if (obstacle.getLinearVelocity().x < 0) {
            flipHorizontal = false;
        } else if (obstacle.getLinearVelocity().x > 0) {
            flipHorizontal = true;
        }
    }

    /**
     * Draws this Minion to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch) {
        if (flipHorizontal) {
            SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
                sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * units,
                obstacle.getPosition().y * units, 0.0f, -size / units, size / units);
        } else {
            SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
                sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * units,
                obstacle.getPosition().y * units, 0.0f, size / units, size / units);
        }
        if (!obstacle.isActive()) { // if destroyed...
            if (!dead) {
                flipHorizontal = false;
                animationFrame = 0;
                animationSpeed = 0.1f;
                dead = true;
                setSpriteSheet(deadMinion);
            }
            if (animationFrame < sprite.getSize()) { // and animation is not over
                sprite.setFrame((int) animationFrame);
                batch.draw(sprite, transform); // draw dead Minion
            } else {
                remove = true;
            }
        } else { // if not destroyed, draw as normal
            sprite.setFrame((int) animationFrame);
            if (damage) {
                batch.setColor(Color.GREEN);
            }
            batch.draw(sprite, transform);
        }
        batch.setColor(Color.WHITE);
    }

    public int getHealth() {
        return health;
    }

    public void removeHealth(int shot) {
        health -= shot;
        damage = true;
    }

    public boolean shouldRemove() {
        return remove;
    }

    public void setDamage(boolean hit) {
        damage = hit;
    }
}
