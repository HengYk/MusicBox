package com.cn.edu.xidian.yk.musicbox.music_play_2;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

/**
 * Created by heart_sunny on 2018/3/3.
 */

public class Player implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener{

    public MediaPlayer mediaPlayer; // 媒体播放器
    private SeekBar seekBar; // 拖动条
    private Timer mTimer = new Timer(); // 计时器

    public Player(SeekBar seekBar) {
        this.seekBar = seekBar;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTimer.schedule(timerTask, 0, 1000);
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying() && !seekBar.isPressed()) {
                handler.sendEmptyMessage(0);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (duration > 0) {
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);
            }
        }
    };

    /**
     * 开始或恢复播放
     */
//    public void play() {
//        mediaPlayer.start();
//    }

    /**
     * 设置播放源
     */
    public void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IllegalArgumentException | IllegalStateException | SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mediaPlayer.pause();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekBar.setSecondaryProgress(i);

        int currentProgress = seekBar.getMax()
                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", i + " buffer");
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.e("mediaPlayer", "onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Log.e("mediaPlayer", "onPrepared");
    }
}
