package com.reiserx.screenshot.Utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.reiserx.screenshot.R;

import java.net.ContentHandler;

public class CaptureSound {
    private static SoundPool soundPool;
    private static int soundId;

    public void playCaptureSound(Context context) {
        // Initialize SoundPool if it's null
        if (soundPool == null) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1) // Maximum number of simultaneous sounds
                    .setAudioAttributes(attributes)
                    .build();

            // Load the sound from the resource
            soundId = soundPool.load(context, R.raw.capture_shutter, 1);
        }

        // Play the sound
        if (soundPool != null && soundId != 0) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void releaseSoundPool() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
