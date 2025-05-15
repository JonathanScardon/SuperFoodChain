package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.graphics.*;

public class PlayerController implements InputController {

    private int forwardDirection;
    Player player;

    public PlayerController(Player p) {
        forwardDirection = 1;
        player = p;
    }

    public int getAction() {
        //TODO
        int code = CONTROL_NO_ACTION;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) code |= CONTROL_MOVE_UP;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) code |= CONTROL_MOVE_LEFT;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) code |= CONTROL_MOVE_DOWN;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) code |= CONTROL_MOVE_RIGHT;

        // Prevent diagonal movement.
        if ((code & CONTROL_MOVE_UP) != 0 && (code & CONTROL_MOVE_LEFT) != 0) {
            code ^= CONTROL_MOVE_UP;
        }

        if ((code & CONTROL_MOVE_UP) != 0 && (code & CONTROL_MOVE_RIGHT) != 0) {
            code ^= CONTROL_MOVE_RIGHT;
        }

        if ((code & CONTROL_MOVE_DOWN) != 0 && (code & CONTROL_MOVE_RIGHT) != 0) {
            code ^= CONTROL_MOVE_DOWN;
        }

        if ((code & CONTROL_MOVE_DOWN) != 0 && (code & CONTROL_MOVE_LEFT) != 0) {
            code ^= CONTROL_MOVE_LEFT;
        }

        // Cancel out conflicting movements.
        if ((code & CONTROL_MOVE_LEFT) != 0 && (code & CONTROL_MOVE_RIGHT) != 0) {
            code ^= (CONTROL_MOVE_LEFT | CONTROL_MOVE_RIGHT);
        }

        if ((code & CONTROL_MOVE_UP) != 0 && (code & CONTROL_MOVE_DOWN) != 0) {
            code ^= (CONTROL_MOVE_UP | CONTROL_MOVE_DOWN);
        }

        // Prevent backwards movement
        if (player.getCompanions().size() > 1) {
            if (forwardDirection == CONTROL_MOVE_UP && code == CONTROL_MOVE_DOWN) {
                code = CONTROL_NO_ACTION;
            }

            if (forwardDirection == CONTROL_MOVE_LEFT && code == CONTROL_MOVE_RIGHT) {
                code = CONTROL_NO_ACTION;
            }

            if (forwardDirection == CONTROL_MOVE_DOWN && code == CONTROL_MOVE_UP) {
                code = CONTROL_NO_ACTION;
            }

            if (forwardDirection == CONTROL_MOVE_RIGHT && code == CONTROL_MOVE_LEFT) {
                code = CONTROL_NO_ACTION;
            }
        }

        // Forced movement (if no action)
        if (code == CONTROL_NO_ACTION) {
            code = forwardDirection;
            player.setForwardDirection(code);
        }

        // Update forwardDirection
        else {
            player.setForwardDirection(code);
            forwardDirection = code;
        }

        return code;
    }

    public int getForwardDirection() {
        return forwardDirection;
    }
}
