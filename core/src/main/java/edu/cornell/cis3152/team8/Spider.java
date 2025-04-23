package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class Spider extends Minion {
    private Vector2 direction;
    private static final float ALIVE_DURATION = 5f; // alive_duration of 5 seconds
    private float aliveTimer;

    public Spider (float x, float y, World world, Player player) {
        super(x, y, world, player);
        setSpriteSheet(new SpriteSheet(new Texture("images/Spider.png"),1,1));
        this.aliveTimer = ALIVE_DURATION;
        moveSpeed = 7;
        health = 1;
        animationSpeed = 0.15f;
        size = 0.3f * units;
        Vector2 spawnPos = new Vector2(x, y);
        Vector2 playerPos = new Vector2(player.getPlayerHead().getObstacle().getPosition()).scl(units);
        direction = playerPos.sub(spawnPos).nor();
    }

    @Override
    public void update(boolean moving) {
        if (moving) {
            float delta = Gdx.graphics.getDeltaTime();
            aliveTimer -= delta;
            if (aliveTimer < 0) {
                // should reduce the Spider's health to 0 --> clean-up in CollisionController's postUpdate function
                removeHealth(getHealth());
            }
            obstacle.setLinearVelocity(new Vector2(direction).scl(moveSpeed));
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }
    }
}
