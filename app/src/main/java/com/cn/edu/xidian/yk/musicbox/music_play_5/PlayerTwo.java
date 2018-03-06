package com.cn.edu.xidian.yk.musicbox.music_play_5;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import me.wcy.lrcview.LrcView;

/**
 * Created by heart_sunny on 2018/3/3.
 */

public class PlayerTwo implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener{

    public MediaPlayer mediaPlayer; // 媒体播放器
    private SeekBar seekBar; // 拖动条
    private Timer mTimer = new Timer(); // 计时器
    private LrcView lrcView;
    private TextView currentTime;
    private TextView totalTime;

    public PlayerTwo(SeekBar seekBar, final LrcView lrcView, TextView currentTime, TextView totalTime) {
        this.seekBar = seekBar;
        this.lrcView = lrcView;
        this.currentTime = currentTime;
        this.totalTime = totalTime;

        this.lrcView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                mediaPlayer.seekTo((int) time);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    handler.post(runnable);
                }
                return true;
            }
        });

        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress() * mediaPlayer.getDuration()/ seekBar.getMax());
                lrcView.updateTime(seekBar.getProgress() * mediaPlayer.getDuration()/ seekBar.getMax());
            }
        });

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Handler handler = new Handler() ;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                long time = mediaPlayer.getCurrentPosition();
                lrcView.updateTime(time);
                int currentProgress = seekBar.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
                seekBar.setProgress(currentProgress);
                currentTime.setText(HttpUtil.toTime((int) time));
                totalTime.setText(HttpUtil.toTime(mediaPlayer.getDuration()));
            }

            handler.postDelayed(this, 1000);
        }
    };


    /**
     * 开始或恢复播放
     */
    public void play() {
        mediaPlayer.start();
        handler.post(runnable);
    }

    /**
     * 设置播放源
     */
    private void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IllegalArgumentException | IllegalStateException | SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void start(Audio audio){
        playUrl(audio.getPlayUrl());
        lrcView.loadLrc(audio.getLyrics());
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mediaPlayer.pause();
        handler.removeCallbacks(runnable);
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mediaPlayer != null) {
            handler.removeCallbacks(runnable);
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
        lrcView.updateTime(0);
        seekBar.setProgress(0);
        Log.e("mediaPlayer", "onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        seekBar.setProgress(0);
        Log.e("mediaPlayer", "onPrepared");
    }
}
