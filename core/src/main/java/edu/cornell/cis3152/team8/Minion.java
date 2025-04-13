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

    private float moveSpeed;
    private static final float units = 64f;

    /**
     * Constructs a Minion at the given position

     * @param x The x-coordinate of the object
     * @param y The y-coordinate of the object
     */
    public Minion(float x, float y, int id, World world) {
        super(new CapsuleObstacle(x/units, y/units, 0.8f, 0.8f), true);
        ((CapsuleObstacle)obstacle).setTolerance( 0.5f );

        this.id = id;
        setConstants(new JsonValue("assets/constants.json"));
        setTexture(new Texture("images/Minion.png"));

        obstacle = getObstacle();
        obstacle.setName("minion");
        obstacle.setFixedRotation(true);
        obstacle.setBodyType(BodyDef.BodyType.DynamicBody);

        obstacle.setPhysicsUnits(units);

        obstacle.activatePhysics(world);
        obstacle.setUserData(this);

        Filter filter = obstacle.getFilterData();
        filter.categoryBits = CollisionController.MINION_CATEGORY;
        filter.maskBits = CollisionController.PLAYER_CATEGORY | CollisionController.PROJECTILE_CATEGORY;
        obstacle.setFilterData(filter);

        mesh.set(-size/2.0f,-size/2.0f,size,size);
    }

    /**
     * Sets constants for this Coin
     *
     * @param constants The JsonValue of the object
     * */
    private void setConstants(JsonValue constants){
        // for some reason, not working??
//        health = constants.getInt("health");
//        size = constants.getInt("size");
//        moveSpeed = constants.getInt("move speed");
        health = 1;
        size = 1 * units;
        moveSpeed = 2;
    }

    public int getHealth(){
        return health;
    }

    public void removeHealth(int shot){
        health -= shot;
    }

    public int getId(){
        return id;
    }
    /**
     * Updates the state of this Minion.
     *
     * This method is only intended to update values that change local state
     * in well-defined ways, like position or a cooldown value. It does not
     * handle collisions (which are determined by the CollisionController). It
     * is not intended to interact with other objects in any way at all.
     *
     * @param controlCode Number of seconds since last animation frame
     */
    public void update(int controlCode){
        // If we are dead do nothing.
        if ((!obstacle.isActive())) {
            return;
        }

        // Determine how we are moving.
        boolean movingLeft  = controlCode ==  1;
        boolean movingRight = controlCode == 2;
        boolean movingUp    = controlCode == 4;
        boolean movingDown  = controlCode == 8;
        //System.out.println("" + movingLeft +movingRight+movingUp+movingDown);

        //System.out.println(controlCode == InputController.CONTROL_MOVE_LEFT);
        int s = 1;
        Vector2 velocity = obstacle.getLinearVelocity();
        // Process movement command.
        if (movingLeft) {
            velocity.x = -s;
            velocity.y = 0;
        } else if (movingRight) {
            velocity.x = s;
            velocity.y = 0;
        } else if (movingUp) {
            velocity.y = -s;
            velocity.x = 0;
        } else if (movingDown) {
            velocity.y = s;
            velocity.x = 0;
        } else{
            velocity.x = 0;
            velocity.y = 0;
        }
        //System.out.println(velocity);
        obstacle.setLinearVelocity(velocity);
        //System.out.println(position);
    }

    /**
     * Draws this Minion to the sprite batch
     *
     * @param batch The sprite batch
     */
    public void draw(SpriteBatch batch){
//        if (!obstacle.isActive()){
//            batch.setColor(Color.BLACK);
//        }
//        batch.draw(texture,position.x,position.y,64,64);
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }
}
