package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CircleWarnPattern extends BossWarnPattern {
    protected float radius;

    public CircleWarnPattern(float x, float y, float radius) {
        super(x, y);

        this.radius = radius;
    }

    public void setRadius(float r) {
        this.radius = r;
    }

    public void drawBorder(ShapeRenderer shape) {
        if (!this.isActive()) {
            return;
        }

        int segments = 32;
        float strokeWidth = 4f;

        shape.setColor(LINE_COLOR);
        float angleStep = 360f / segments;
        float prevX = this.x * GameScene.PHYSICS_UNITS + radius;
        float prevY = this.y * GameScene.PHYSICS_UNITS;

        float angleRad;
        float currX, currY;

        for (int i = 1; i <= segments; i++) {
            angleRad = (float) Math.toRadians(i * angleStep);
            currX = this.x * GameScene.PHYSICS_UNITS + radius * (float) Math.cos(angleRad);
            currY = this.y * GameScene.PHYSICS_UNITS + radius * (float) Math.sin(angleRad);

            shape.rectLine(prevX, prevY, currX, currY, strokeWidth);
            prevX = currX;
            prevY = currY;
        }
    }
}
