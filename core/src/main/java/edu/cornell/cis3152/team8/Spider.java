package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Spider extends Minion {
    private static final float units = 64f;
    private Player player;
    private Vector2 direction;
    private int spiderSpeed = 7;
    private static final float ALIVE_DURATION = 5f; // alive_duration of 5 seconds
    private float aliveTimer;

    public Spider (float x, float y, int id, World world, Player player) {
        super(x, y, id, world, player);
        setTexture(new Texture("images/Spider.png"));
        this.player = player;
        this.aliveTimer = ALIVE_DURATION;

        Vector2 spawnPos = new Vector2(x, y);
        Vector2 playerPos = new Vector2(player.getPlayerHead().getObstacle().getPosition()).scl(units);
        direction = playerPos.sub(spawnPos).nor();
    }

    @Override
    public void update(boolean u) {
        if (u) {
            float delta = Gdx.graphics.getDeltaTime();
            aliveTimer -= delta;
            if (aliveTimer < 0) {
                // should reduce the Spider's health to 0 --> clean-up in CollisionController's postUpdate function
                removeHealth(getHealth());
            }
            obstacle.setLinearVelocity(new Vector2(direction).scl(spiderSpeed));
        } else {
            obstacle.setLinearVelocity(new Vector2());
        }
    }
}
