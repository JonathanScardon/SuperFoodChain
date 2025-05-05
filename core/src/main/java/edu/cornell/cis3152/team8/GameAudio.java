package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.AudioEngine;
import edu.cornell.gdiac.audio.AudioSource;
import edu.cornell.gdiac.audio.MusicQueue;
import edu.cornell.gdiac.audio.SoundEffect;

public class GameAudio {

    private final SoundEffect[] menuSfx;
    private final SoundEffect[] gameSfx;

    private final MusicQueue music;

    private static float sfxVolume;
    private static float musicVolume;

    private int currSfx;
    private int currMusic;

    public GameAudio(AssetDirectory assets) {
        menuSfx = new SoundEffect[2];
        gameSfx = new SoundEffect[17];

        menuSfx[0] = assets.getEntry("click", SoundEffect.class);
        menuSfx[1] = assets.getEntry("clickLevel", SoundEffect.class);

        gameSfx[0] = assets.getEntry("coin", SoundEffect.class);
        gameSfx[1] = assets.getEntry("durian", SoundEffect.class);
        gameSfx[2] = assets.getEntry("pineapple", SoundEffect.class);
        gameSfx[3] = assets.getEntry("strawberry", SoundEffect.class);
        gameSfx[4] = assets.getEntry("dashAttack", SoundEffect.class);
        gameSfx[5] = assets.getEntry("spinAttack", SoundEffect.class);
        gameSfx[6] = assets.getEntry("chopAttack", SoundEffect.class);
        gameSfx[7] = assets.getEntry("snatchAttack", SoundEffect.class);
        gameSfx[8] = assets.getEntry("ratHit", SoundEffect.class);
        gameSfx[9] = assets.getEntry("chefHit", SoundEffect.class);
        gameSfx[10] = assets.getEntry("chopsticksHit", SoundEffect.class);
        gameSfx[11] = assets.getEntry("ratDeath", SoundEffect.class);
        gameSfx[12] = assets.getEntry("chefDeath", SoundEffect.class);
        gameSfx[13] = assets.getEntry("chopsticksDeath", SoundEffect.class);
        gameSfx[14] = assets.getEntry("minionDeath", SoundEffect.class);
        gameSfx[15] = assets.getEntry("companionDeath", SoundEffect.class);
        gameSfx[16] = assets.getEntry("companionRecruitment", SoundEffect.class);

        AudioEngine engine = (AudioEngine) Gdx.audio;
        music = engine.newMusicQueue(false, 44100);
        //menus
        music.addSource(assets.getEntry("menu", AudioSource.class));
        music.addSource(assets.getEntry("levels", AudioSource.class));
        music.addSource(assets.getEntry("handbook", AudioSource.class));

        //game
        music.addSource(assets.getEntry("preLevel", AudioSource.class));
        music.addSource(assets.getEntry("ratLevel", AudioSource.class));
        music.addSource(assets.getEntry("chefLevel", AudioSource.class));
        music.addSource(assets.getEntry("chopsticksLevel", AudioSource.class));
        music.addSource(assets.getEntry("twoRatLevel", AudioSource.class));
        music.addSource(assets.getEntry("twoChefLevel", AudioSource.class));
        music.addSource(assets.getEntry("twoChopsticksLevel", AudioSource.class));
        music.addSource(assets.getEntry("ratChefLevel", AudioSource.class));
        music.addSource(assets.getEntry("ratChopsticksLevel", AudioSource.class));
        music.addSource(assets.getEntry("chefChopsticksLevel", AudioSource.class));
        music.addSource(assets.getEntry("bossRushMode", AudioSource.class));
        music.addSource(assets.getEntry("machineGunMode", AudioSource.class));
        music.addSource(assets.getEntry("win", AudioSource.class));
        music.addSource(assets.getEntry("lose", AudioSource.class));

        music.setOnTransitionListener(new MusicQueue.OnTransitionListener() {
            @Override
            public void onLoopback(MusicQueue buffer, AudioSource source) {
                // Do nothing
            }

            @Override
            public void onTransition(MusicQueue buffer, AudioSource source1, AudioSource source2) {
                //Loop
                music.play();
                music.jumpToSource(currMusic);
            }

            @Override
            public void onCompletion(MusicQueue buffer, AudioSource source) {

            }
        });

        sfxVolume = 1.0f;
        musicVolume = 1.0f;
        currSfx = 0;
        currMusic = 0;

    }

    public void setSfxVolume(float volume) {
        sfxVolume = volume;
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        music.setVolume(volume);
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }


    public void play(String name) {
        switch (name) {
            case ("click") -> {
                menuSfx[0].play(sfxVolume);
                currSfx = 0;
            }
            case ("clickLevel") -> {
                menuSfx[1].play(sfxVolume);
                currSfx = 1;
            }
            case ("coin") -> {
                gameSfx[0].play(sfxVolume);
                currSfx = 0;
            }
            case ("DURIAN") -> {
                gameSfx[1].play(sfxVolume);
                currSfx = 1;
            }
            case ("PINEAPPLE") -> {
                gameSfx[2].play(sfxVolume);
                currSfx = 2;
            }
            case ("STRAWBERRY") -> {
                gameSfx[3].play(sfxVolume);
                currSfx = 3;
            }
            case ("dash") -> {
                gameSfx[4].play(sfxVolume);
                currSfx = 4;
            }
            case ("spin") -> {
                gameSfx[5].play(sfxVolume);
                currSfx = 5;
            }
            case ("chop") -> {
                gameSfx[6].play(sfxVolume);
                currSfx = 6;
            }
            case ("snatch") -> {
                gameSfx[7].play(sfxVolume);
                currSfx = 7;
            }
            case ("mouseHit") -> {
                gameSfx[8].play(sfxVolume);
                currSfx = 8;
            }
            case ("chefHit") -> {
                gameSfx[9].play(sfxVolume);
                currSfx = 9;
            }
            case ("chopsticksHit") -> {
                gameSfx[10].play(sfxVolume);
                currSfx = 10;
            }
            case ("ratDeath") -> {
                gameSfx[11].play(sfxVolume);
                currSfx = 11;
            }
            case ("chefDeath") -> {
                gameSfx[12].play(sfxVolume);
                currSfx = 12;
            }
            case ("chopsticksDeath") -> {
                gameSfx[13].play(sfxVolume);
                currSfx = 13;
            }
            case ("minion") -> {
                gameSfx[14].play(sfxVolume);
                currSfx = 14;
            }
            case ("companionDeath") -> {
                gameSfx[15].play(sfxVolume);
                currSfx = 15;
            }
            case ("companionRecruitment") -> {
                gameSfx[16].play(sfxVolume);
                currSfx = 16;

            }
            case ("menu") -> {
                music.pause();
                music.play();
                music.jumpToSource(0);
                currMusic = 0;

            }
            case ("levels") -> {
                music.pause();
                music.play();
                music.jumpToSource(1);
                currMusic = 1;

            }
            case ("handbook") -> {
                music.pause();
                music.play();
                music.jumpToSource(2);
                currMusic = 2;
            }
            case ("preLevel") -> {
                music.pause();
                music.play();
                music.jumpToSource(3);
                currMusic = 3;
            }
            //TODO: REBRAND TO RAT
            case ("mouse") -> {
                music.pause();
                music.play();
                music.jumpToSource(4);
                currMusic = 4;
            }
            case ("chef") -> {
                music.pause();
                music.play();
                music.jumpToSource(5);
                currMusic = 5;
            }
            case ("chopsticks") -> {
                music.pause();
                music.play();
                music.jumpToSource(6);
                currMusic = 6;
            }
            case ("mousemouse") -> {
                music.pause();
                music.play();
                music.jumpToSource(7);
                currMusic = 7;
            }
            case ("chefchef") -> {
                music.pause();
                music.play();
                music.jumpToSource(8);
                currMusic = 8;
            }
            case ("chopstickschopsticks") -> {
                music.pause();
                music.play();
                music.jumpToSource(9);
                currMusic = 9;
            }
            case ("mousechef"), ("chefmouse") -> {
                music.pause();
                music.play();
                music.jumpToSource(10);
                currMusic = 10;
            }
            case ("mousechopsticks"), ("chopsticksmouse") -> {
                music.pause();
                music.play();
                music.jumpToSource(11);
                currMusic = 11;
            }
            case ("chefchopsticks"), ("chopstickschef") -> {
                music.pause();
                music.play();
                music.jumpToSource(12);
                currMusic = 12;
            }
            case ("bossRush") -> {
                music.pause();
                music.play();
                music.jumpToSource(13);
                currMusic = 13;
            }
            case ("machineGun") -> {
                music.pause();
                music.play();
                music.jumpToSource(14);
                currMusic = 14;
            }
            case ("win") -> {
                music.pause();
                music.play();
                music.jumpToSource(15);
                currMusic = 15;
            }
            case ("lose") -> {
                music.pause();
                music.play();
                music.jumpToSource(16);
                currMusic = 16;
            }
        }
    }

    public void stopSfx() {
        for (SoundEffect s : gameSfx) {
            if (s != null) {
                s.stop();
            }
        }
    }

    public void stopMusic() {
        music.pause();
    }


}
