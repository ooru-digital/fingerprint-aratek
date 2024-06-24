package com.aratek.trustfinger.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class MediaPlayerHelper {

    public final static MediaPlayer mediaPlayer = new MediaPlayer();
    public static long currentPlayId;

    public static void payMedia(Context context, int id) {
        if (currentPlayId == id && mediaPlayer.isPlaying()) {
            return;
        }
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(id);
        if (afd == null) {
            return;// resource not found
        }
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentPlayId = id;
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
