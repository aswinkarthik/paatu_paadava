package org.mindapps.paatupaadava.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class MP3Player {
    private static final String TAG = MP3Player.class.getName();

    private MediaPlayer mediaPlayer;

    public void playSong(Uri song, Context applicationContext) throws IOException {
        Log.i(TAG, "Playing song" + song);


        this.mediaPlayer = new MediaPlayer();
        Log.i(TAG, "Creating player");
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(applicationContext, song);
        mediaPlayer.prepare();
        mediaPlayer.start();
        Log.i(TAG, "Started playing");
    }

    public void stopSongIfAnyPlaying() {
        Log.i(TAG, "Stopping song");

        if(mediaPlayer != null) {

            if(mediaPlayer.isPlaying()) mediaPlayer.stop();

            mediaPlayer.release();
            Log.i(TAG, "Released resources");
        }

    }
}
