package edu.cornell.cis3152.team8;

import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.graphics.SpriteSheet;

public class HandbookOrder {

    public String companion(int level) {
        String companion = "";
        if (level == 1) {
            companion = "durian";
        } else if (level == 3) {
            companion = "strawberry";
        } else if (level == 5) {
            companion = "blue raspberry";
        } else if (level == 8) {
            companion = "avocado";
        } else if (level == 10) {
            companion = "garlic";
        }
        return companion;
    }

    public String minion(int level) {
        String minion = "";
        if (level == 1) {
            minion = "ant";
        } else if (level == 3) {
            minion = "cricket";
        } else if (level == 7) {
            minion = "spider";
        }
        return minion;
    }

    public String boss(int level) {
        String boss = "";
        if (level == 1) {
            boss = "rat";
        } else if (level == 4) {
            boss = "chopsticks";
        } else if (level == 6) {
            boss = "chef";
        }
        return boss;
    }

    /**
     * @param assets
     * @param level  the current level, or the number of unlocked companions (but negative)
     * @return
     */
    public SpriteSheet companionSheet(AssetDirectory assets, int level) {
        if (level == -1) {
            level = 1;
        } else if (level == -2) {
            level = 3;
        } else if (level == -3) {
            level = 5;
        } else if (level == -4) {
            level = 8;
        } else if (level == -5) {
            level = 10;
        }
        SpriteSheet sheet = new SpriteSheet();
        if (level == 1) {
            sheet = assets.getEntry("Durian Handbook.animation",
                SpriteSheet.class);
        } else if (level == 3) {
            sheet = assets.getEntry("Durian/Strawberry Handbook.animation",
                SpriteSheet.class);
        } else if (level == 5) {
            sheet = assets.getEntry("Avocado Handbook.animation",
                SpriteSheet.class);
        } else if (level == 8) {
            sheet = assets.getEntry(
                "Avocado/BlueRaspberry Handbook.animation",
                SpriteSheet.class);
        } else if (level == 10) {
            sheet = assets.getEntry(
                "Garlic Handbook.animation",
                SpriteSheet.class);
        }
        return sheet;
    }

    public SpriteSheet minionSheet(AssetDirectory assets, int level) {
        if (level == -1) {
            level = 1;
        } else if (level == -2) {
            level = 3;
        } else if (level == -3) {
            level = 6;
        }
        SpriteSheet sheet = new SpriteSheet();
        if (level == 1) {
            sheet = assets.getEntry("Ant Handbook.animation",
                SpriteSheet.class);
        } else if (level == 3) {
            sheet = assets.getEntry("Ant/Cricket Handbook.animation",
                SpriteSheet.class);
        } else if (level == 6) {
            sheet = assets.getEntry("Spider Handbook.animation",
                SpriteSheet.class);
        }
        return sheet;
    }

    public SpriteSheet bossSheet(AssetDirectory assets, int level) {
        if (level == -1) {
            level = 1;
        } else if (level == -2) {
            level = 4;
        } else if (level == -3) {
            level = 9;
        }
        SpriteSheet sheet = new SpriteSheet();
        if (level == 1) {
            sheet = assets.getEntry("Rat Handbook.animation",
                SpriteSheet.class);
        } else if (level == 4) {
            sheet = assets.getEntry("Rat/Chopsticks Handbook.animation",
                SpriteSheet.class);
        } else if (level == 9) {
            sheet = assets.getEntry("Chef Handbook.animation",
                SpriteSheet.class);
        }
        return sheet;
    }

}
