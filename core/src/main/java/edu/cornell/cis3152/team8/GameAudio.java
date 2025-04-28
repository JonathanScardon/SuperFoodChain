package edu.cornell.cis3152.team8;

import com.badlogic.gdx.Gdx;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.AudioEngine;
import edu.cornell.gdiac.audio.AudioSource;
import edu.cornell.gdiac.audio.MusicQueue;
import edu.cornell.gdiac.audio.SoundEffect;

public class GameAudio {

    private SoundEffect[] menuSfx;
    private SoundEffect[] gameSfx;
    private AudioSource[] menuMusic;
    private AudioSource[] gameMusic;

    private MusicQueue music;

    private static float sfxVolume;
    private static float musicVolume;

    private int currSfx;
    private int currMusic;

    public GameAudio(AssetDirectory assets) {
        menuSfx = new SoundEffect[2];
        gameSfx = new SoundEffect[13];
        menuMusic = new AudioSource[3];
        gameMusic = new AudioSource[6];

        menuSfx[0] = assets.getEntry("click", SoundEffect.class);
        menuSfx[1] = assets.getEntry("clickLevel", SoundEffect.class);

        gameSfx[0] = assets.getEntry("coin", SoundEffect.class);
        gameSfx[1] = assets.getEntry("durian", SoundEffect.class);
        gameSfx[2] = assets.getEntry("pineapple", SoundEffect.class);
        gameSfx[3] = assets.getEntry("strawberry", SoundEffect.class);
        gameSfx[4] = assets.getEntry("mouseAttack", SoundEffect.class);
        gameSfx[5] = assets.getEntry("chefAttack", SoundEffect.class);
        gameSfx[6] = assets.getEntry("chopsticksAttack", SoundEffect.class);
        gameSfx[7] = assets.getEntry("mouseDeath", SoundEffect.class);
        gameSfx[8] = assets.getEntry("chefDeath", SoundEffect.class);
        gameSfx[9] = assets.getEntry("chopsticksDeath", SoundEffect.class);
        gameSfx[10] = assets.getEntry("minionDeath", SoundEffect.class);
        gameSfx[11] = assets.getEntry("companionDeath", SoundEffect.class);
        gameSfx[12] = assets.getEntry("companionRecruitment", SoundEffect.class);

        menuMusic[0] = assets.getEntry("menu", AudioSource.class);
        menuMusic[1] = assets.getEntry("levels", AudioSource.class);
        menuMusic[2] = assets.getEntry("handbook", AudioSource.class);

        gameMusic[0] = assets.getEntry("preLevel", AudioSource.class);
        gameMusic[1] = assets.getEntry("mouseLevel", AudioSource.class);
        gameMusic[2] = assets.getEntry("chefLevel", AudioSource.class);
        gameMusic[3] = assets.getEntry("chopsticksLevel", AudioSource.class);
        gameMusic[4] = assets.getEntry("win", AudioSource.class);
        gameMusic[5] = assets.getEntry("lose", AudioSource.class);

        AudioEngine engine = (AudioEngine) Gdx.audio;
        music = engine.newMusicQueue(false, 44100);
        music.addSource(menuMusic[0]);
        music.addSource(menuMusic[1]);
        music.addSource(menuMusic[2]);
        music.addSource(gameMusic[0]);
        music.addSource(gameMusic[1]);
        music.addSource(gameMusic[2]);
        music.addSource(gameMusic[3]);
        music.addSource(gameMusic[4]);
        music.addSource(gameMusic[5]);

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
    }

    public void play(String name) {
//        if (currSfx < menuSfx.length) {
//            menuSfx[currSfx].stop();
//        }
//        gameSfx[currSfx].stop();
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
            case ("pineapple") -> {
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
                gameSfx[4].play(sfxVolume);
                currSfx = 4;
            }
            case ("chefAttack") -> {
                gameSfx[5].play(sfxVolume);
                currSfx = 5;
            }
            case ("snatch") -> {
                gameSfx[6].play(sfxVolume);
                currSfx = 6;
            }
            case ("mouseHit") -> {
                gameSfx[7].play(sfxVolume);
                currSfx = 7;
            }
            case ("chefHit") -> {
                gameSfx[8].play(sfxVolume);
                currSfx = 8;
            }
            case ("chopsticksHit") -> {
                gameSfx[9].play(sfxVolume);
                currSfx = 9;
            }
            case ("mouseDeath") -> {
                gameSfx[7].play(sfxVolume);
                currSfx = 7;
            }
            case ("chefDeath") -> {
                gameSfx[8].play(sfxVolume);
                currSfx = 8;
            }
            case ("chopsticksDeath") -> {
                gameSfx[9].play(sfxVolume);
                currSfx = 9;
            }
            case ("minion") -> {
                gameSfx[10].play(sfxVolume);
                currSfx = 10;
            }
            case ("companionDeath") -> {
                gameSfx[11].play(sfxVolume);
                currSfx = 11;
            }
            case ("companionRecruitment") -> {
                gameSfx[12].play(sfxVolume);
                currSfx = 12;
                // case ("menu") -> menuMusic.play
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
}
