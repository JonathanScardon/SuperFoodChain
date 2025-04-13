package edu.cornell.cis3152.team8.companions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.cis3152.team8.GameState;
import edu.cornell.cis3152.team8.Minion;
import edu.cornell.cis3152.team8.Boss;

public class utilities {
    public static float manhattan(Vector2 pos1, Vector2 pos2) {
        return Vector2.dst(pos1.x, pos1.y, pos2.x, pos2.y);
    };

    /**
     *
     * @param state GameState "image" that will be used to extract important info like active enemies
     * @param pos Vector2 representation of current companion position
     * @return Vector2 (dx, dy) which are the normalized directional components used for projectile heading
     */
    public static Vector2 autoshoot(GameState state, Vector2 pos) {
        float dx = 0.0f;
        float dy = 0.0f;
        Array<Minion> minionList = state.getMinions();
        Array<Boss> bossList = state.getBosses();
        float closestEnemyDist = Float.POSITIVE_INFINITY;

        if (minionList != null) {
            // if there are minions, iterate through and find the closest one
            for (Minion m : minionList) {
                // consider active/non-destroyed minions only
                if (!m.isDestroyed()) {
                    float pairwiseDist = utilities.manhattan(m.getPosition(), pos);
                    if (pairwiseDist < closestEnemyDist) {
                        closestEnemyDist = pairwiseDist;
                        dx = (m.getX() - pos.x)
                        ; //  x-directional vector component
                        dy = (m.getY() - pos.y)
                        ; //  y-directional vector component
                        // euclidean distance
                        double length = Math.hypot(dx, dy);
                        // normalized x-directional component
                        dx /= (float) length;
                        // normalized y-directional component
                        dy /= (float) length;
                    }
                }
            }

            for (Boss b : bossList) {
                if (!b.isDestroyed()) {
                    float pairwiseDist = utilities.manhattan(b.getPosition(), pos);
                    if (pairwiseDist < closestEnemyDist) {
                        closestEnemyDist = pairwiseDist;
                        dx = (b.getX() - pos.x)
                        ; //  x-directional vector component
                        dy = (b.getY() - pos.y)
                        ; //  y-directional vector component
                        // euclidean distance
                        double length = Math.hypot(dx, dy);
                        // normalized x-directional component
                        dx /= (float) length;
                        // normalized y-directional component
                        dy /= (float) length;
                    }
                }
            }
        }

        return new Vector2(dx, dy);
    };
}
