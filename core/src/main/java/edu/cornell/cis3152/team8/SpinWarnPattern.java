package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SpinWarnPattern extends CircleWarnPattern {
    public SpinWarnPattern(float x, float y, float radius) {
        super(x, y, radius);
    }

    public void drawBorder(ShapeRenderer shape) {
        if (!this.isActive()) {
            return;
        }

        super.drawBorder(shape);
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
