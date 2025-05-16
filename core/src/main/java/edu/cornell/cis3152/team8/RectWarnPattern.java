package edu.cornell.cis3152.team8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class RectWarnPattern extends BossWarnPattern {
    private float w, h;

    public RectWarnPattern(float x, float y, float width, float height) {
        super(x, y);

        this.w = width;
        this.h = height;
    }

    public void setWidth(float w) {
        this.w = w;
    }

    public void setHeight(float h) {
        this.h = h;
    }

    public void drawBorder(ShapeRenderer shape) {
        if (!this.isActive()) {
            return;
        }

        shape.setColor(BossWarnPattern.LINE_COLOR);

        float strokeWidth = 4f;
        float hw = w / 2f;
        float hh = h / 2f;

        float angleRad = (float) Math.toRadians(this.angle);
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);

        // Axes
        Vector2 dx = new Vector2(cos, sin).scl(hw);
        Vector2 dy = new Vector2(-sin, cos).scl(hh);
        Vector2 extend = new Vector2(); // Reused for overlap

        // Corner positions
        Vector2 tr = new Vector2(this.x * GameScene.PHYSICS_UNITS, this.y * GameScene.PHYSICS_UNITS).add(dx).add(dy);
        Vector2 tl = new Vector2(this.x * GameScene.PHYSICS_UNITS, this.y * GameScene.PHYSICS_UNITS).sub(dx).add(dy);
        Vector2 bl = new Vector2(this.x * GameScene.PHYSICS_UNITS, this.y * GameScene.PHYSICS_UNITS).sub(dx).sub(dy);
        Vector2 br = new Vector2(this.x * GameScene.PHYSICS_UNITS, this.y * GameScene.PHYSICS_UNITS).add(dx).sub(dy);

        // Calculate outward directions for extension
        // Top edge: tl -> tr
        extend.set(tr).sub(tl).nor().scl(strokeWidth / 2f);
        shape.rectLine(tl.x - extend.x, tl.y - extend.y, tr.x + extend.x, tr.y + extend.y, strokeWidth);

        // Right edge: tr -> br
        extend.set(br).sub(tr).nor().scl(strokeWidth / 2f);
        shape.rectLine(tr.x - extend.x, tr.y - extend.y, br.x + extend.x, br.y + extend.y, strokeWidth);

        // Bottom edge: br -> bl
        extend.set(bl).sub(br).nor().scl(strokeWidth / 2f);
        shape.rectLine(br.x - extend.x, br.y - extend.y, bl.x + extend.x, bl.y + extend.y, strokeWidth);

        // Left edge: bl -> tl
        extend.set(tl).sub(bl).nor().scl(strokeWidth / 2f);
        shape.rectLine(bl.x - extend.x, bl.y - extend.y, tl.x + extend.x, tl.y + extend.y, strokeWidth);
    }
}
