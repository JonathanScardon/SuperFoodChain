package edu.cornell.cis3152.team8.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.cis3152.team8.CollisionController;
import edu.cornell.cis3152.team8.GameScene;
import edu.cornell.cis3152.team8.Projectile;
import edu.cornell.gdiac.graphics.SpriteBatch;
import edu.cornell.gdiac.physics2.Obstacle;

import java.awt.geom.RectangularShape;

public class BossAreaProjectile extends Projectile {
    public BossAreaProjectile(Obstacle o, World world) {
        super(o, world);

        setMaxLife(1000); // set it to a huge number at first
        life = getMaxLife();
        collisionDie = false;
        setAttack(1);
    }

    public void setFixture(float radius) {
        Shape shape = new CircleShape();
        this.radius = radius;
        shape.setRadius(radius);

        FixtureDef selfFixtureDef = new FixtureDef();
        selfFixtureDef.shape = shape;
        selfFixtureDef.isSensor = true;

        Filter selfFilter = new Filter();
        selfFilter.categoryBits = CollisionController.BOSS_CATEGORY;
        selfFilter.maskBits = CollisionController.PLAYER_CATEGORY;

        Fixture originalFixture = this.getObstacle().getBody().getFixtureList().first();
        Fixture selfFixture = this.getObstacle().getBody().createFixture(selfFixtureDef);
        selfFixture.setFilterData(selfFilter);
        selfFixture.setUserData(this);
        this.getObstacle().getBody().destroyFixture(originalFixture);

        shape.dispose();

        float size = (radius * getObstacle().getPhysicsUnits());
        mesh.set(-size / 2.0f, -size / 2.0f, size, size);
    }

    /**
     * Draws this projectile to the sprite batch
     *
     * @param batch The sprite batch
     */
    @Override
    public void draw(SpriteBatch batch) {
        sprite.setFrame((int) animeFrame);
        SpriteBatch.computeTransform(transform,
            sprite.getRegionWidth() / 2.0f,
            sprite.getRegionHeight() / 2.0f,
            obstacle.getX() * GameScene.PHYSICS_UNITS,
            obstacle.getY() * GameScene.PHYSICS_UNITS,
            0.0f,
            radius * 2.4f * GameScene.PHYSICS_UNITS / sprite.getRegionWidth(),
            radius * 2.4f * GameScene.PHYSICS_UNITS / sprite.getRegionHeight());
        batch.setColor(Color.WHITE);
        batch.draw(sprite, transform);
    }
}
