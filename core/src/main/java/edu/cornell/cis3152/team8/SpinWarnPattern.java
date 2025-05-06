package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SpinWarnPattern extends BossWarnPattern {
    private float radius;

    public SpinWarnPattern(float x, float y, float radius) {
        super(x, y);

        this.radius = radius;
    }

    public void setRadius(float r) {
        this.radius = r;
    }

    public void drawBorder(ShapeRenderer shape) {
        if (!active) {
            return;
        }

        int segments = 32;
        float strokeWidth = 4f;

        shape.setColor(lineColor);
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

        drawArrow(shape);
    }

    public void drawArrow(ShapeRenderer shape) {
        float strokeWidth = 4f;
        float spreadAngle = 30f; // degrees between the lines
        float length = 50f; // how long the triangle legs are

        float centerX = this.x * GameScene.PHYSICS_UNITS;
        float centerY = this.y * GameScene.PHYSICS_UNITS;

        float angleRad = (float) Math.toRadians(angle);
        float leftAngleRad = angleRad - (float) Math.toRadians(spreadAngle / 2f);
        float rightAngleRad = angleRad + (float) Math.toRadians(spreadAngle / 2f);

        // Starting points on the circle border
        float startX1 = centerX + radius * (float) Math.cos(leftAngleRad);
        float startY1 = centerY + radius * (float) Math.sin(leftAngleRad);

        float startX2 = centerX + radius * (float) Math.cos(rightAngleRad);
        float startY2 = centerY + radius * (float) Math.sin(rightAngleRad);

        // Common tip point outside the circle
        float tipX = centerX + (radius + length) * (float) Math.cos(angleRad);
        float tipY = centerY + (radius + length) * (float) Math.sin(angleRad);

        // Draw lines
        shape.rectLine(startX1, startY1, tipX, tipY, strokeWidth);
        shape.rectLine(startX2, startY2, tipX, tipY, strokeWidth);
    }
}
