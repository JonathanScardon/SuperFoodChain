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

    private int health;
    private float size;
    private int id;
    // 5 second death expiration timer
    private float deathExpirationTimer = 3.0f;
    private float moveSpeed;
    private static final float units = 64f;
    private boolean remove;
    private boolean damage;
    private float animationSpeed;
    private float animationFrame;
    private Player player;
    private float MOVESPEED;

    /**
     * Constructs a Minion at the given position
     *
     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Minion(float x, float y, int id, World world, Player player) {
        super(new CapsuleObstacle(x / units, y / units, 0.8f, 0.8f), true);
        ((CapsuleObstacle) obstacle).setTolerance(0.5f);

        this.id = id;
        JsonValue constants = new JsonValue("assets/constants.json");
        Texture texture = new Texture("images/Minion.png");

//        radius = 2;
        this.player = player;
        remove = false;
        damage = false;
        health = 3;
        MOVESPEED = 1f;
        SpriteSheet minion = new SpriteSheet(texture, 1, 1);
        setSpriteSheet(minion);
        animationSpeed = 0.15f;
        size = 0.3f * units;
        animationFrame = 0;

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
//        this.constants = constants;
        health = constants.getInt("health");
        //MOVE_SPEED = constants.getFloat("move speed");
        moveSpeed = 4;
    }

    public int getHealth() {
        return health;
    }

    public void removeHealth(int shot) {
        health -= shot;
    }

    public int getId() {
        return id;
    }

    /**
     * Updates the state of this Minion.
     * <p>
     * This method is only intended to update values that change local state in well-defined ways,
     * like position or a cooldown value. It does not handle collisions (which are determined by the
     * CollisionController). It is not intended to interact with other objects in any way at all.
     *
     * @param u TODO: add param 'u' description since it changed from 'controlCode'
     */
    public void update(boolean u) {
        if (u) {
            Vector2 pos = obstacle.getPosition().scl(units);
            Vector2 dir = player.getPlayerHead().getObstacle().getPosition().scl(units).sub(pos).nor();
            obstacle.setLinearVelocity(dir.scl(MOVESPEED));
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }
//        // If we are dead do nothing.
//        if ((!obstacle.isActive())) {
//            return;
//        }
//
//        // Determine how we are moving.
//        boolean movingLeft  = controlCode == 1;
//        boolean movingRight = controlCode == 2;
//        boolean movingUp    = controlCode == 4;
//        boolean movingDown  = controlCode == 8;
//        //System.out.println("" + movingLeft +movingRight+movingUp+movingDown);
//
//        //System.out.println(controlCode == InputController.CONTROL_MOVE_LEFT);
//        int s = 1;
//        Vector2 velocity = obstacle.getLinearVelocity();
//        // Process movement command.
//        if (movingLeft) {
//            velocity.x = -s;
//            velocity.y = 0;
//        } else if (movingRight) {
//            velocity.x = s;
//            velocity.y = 0;
//        } else if (movingUp) {
//            velocity.y = -s;
//            velocity.x = 0;
//        } else if (movingDown) {
//            velocity.y = s;
//            velocity.x = 0;
//        } else{
//            velocity.x = 0;
//            velocity.y = 0;
//        }
//        //System.out.println(velocity);
//        obstacle.setLinearVelocity(velocity);
//        //System.out.println(position);
//
//
//        if (sprite != null) {
//            animationFrame += animationSpeed;
//            //System.out.println(animationFrame);
//            if (animationFrame >= sprite.getSize()) {
//                animationFrame -= sprite.getSize();
//            }
//        }
    }

    /**
     * Draws this Minion to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch, float delta) {
//         SpriteBatch.computeTransform(transform, origin.x, origin.y,
//             position.x, position.y, 0.0f, size
//             , size);
        SpriteBatch.computeTransform(transform, sprite.getRegionWidth() / 2.0f,
            sprite.getRegionHeight() / 2.0f, obstacle.getPosition().x * units,
            obstacle.getPosition().y * units, 0.0f, size / units, size / units);
        if (!obstacle.isActive()) { // if destroyed...
            sprite.setFrame(0);
            if (deathExpirationTimer > 0.0f) { // and within recent death timer
                batch.setColor(Color.BLACK); // show black shadow
                batch.draw(sprite, transform); // draw the blackened corpse
                deathExpirationTimer -= delta;
                // decrement timer with the delta value passed in GameScene

            } else {
                remove = true;
            }
            // if it's pass, say 3 seconds, don't draw the dead corpse to free up screen real estate
        } else { // if not destroyed, draw as normal
            sprite.setFrame((int) animationFrame);
            if (damage) {
                batch.setColor(Color.RED);
            }

            batch.draw(sprite, transform);

        }
        batch.setColor(Color.WHITE);
    }

    public boolean shouldRemove() {
        return remove;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setDamage(boolean hit) {
        damage = hit;
    }
}
