package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Ant extends Minion{

    /**
     * Constructs a Minion at the given position
     *
     * @param x      The x-coordinate of the object
     * @param y      The y-coordinate of the object
     * @param world
     * @param player
     */
    public Ant(float x, float y, World world, Player player) {
        super(x, y, world, player);
        setSpriteSheet(new SpriteSheet(new Texture("images/Ant.png"), 1, 1));
        moveSpeed = 1f;
        health = 1;
        animationSpeed = 0.15f;
        size = 0.3f * units;
    }
    @Override
    public void update(boolean moving){
        super.update(moving);
        if (moving) {
            Vector2 pos = obstacle.getPosition().scl(units);
            Vector2 dir = player.getPlayerHead().getObstacle().getPosition().scl(units).sub(pos).nor();
            obstacle.setLinearVelocity(dir.scl(moveSpeed));
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }
    }
}
