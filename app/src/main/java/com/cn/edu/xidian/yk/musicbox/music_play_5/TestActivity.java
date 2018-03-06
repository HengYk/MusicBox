package com.cn.edu.xidian.yk.musicbox.music_play_5;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cn.edu.xidian.yk.musicbox.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import me.wcy.lrcview.LrcView;

public class TestActivity extends AppCompatActivity {

    private LrcView lrcView;
    private SeekBar seekBar;
    private Button btnPlayPause;
    private TextView pro_text;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
    private Audio audio = null;

    public static final String url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=CB7EE97F4CC11C4EA7A1FA4B516A5D97";

    HttpURLConnection conn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        lrcView = (LrcView) findViewById(R.id.lrc_view);
        seekBar = (SeekBar) findViewById(R.id.progress_bar);
        btnPlayPause = (Button) findViewById(R.id.btn_play_pause);
        pro_text = (TextView) findViewById(R.id.progress_text);

        new Thread(new Runnable() {
            @Override
            public void run() {
                conn = HttpUtil.getConnection(url);
                try {
                    InputStream is = conn.getInputStream();
                    String result = HttpUtil.readMyInputStream(is);
                    audio = HttpUtil.analyzeJSON(result);

                    if (audio != null) {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(audio.getPlayUrl());
                        mediaPlayer.prepareAsync();
                        lrcView.loadLrc(audio.getLyrics());

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                lrcView.updateTime(0);
                seekBar.setProgress(0);
            }
        });


        lrcView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
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

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    handler.post(runnable);
                } else {
                    mediaPlayer.pause();
                    handler.removeCallbacks(runnable);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro_text.setText(progress + " " + mediaPlayer.getDuration() + " " + mediaPlayer.getCurrentPosition());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                lrcView.updateTime(seekBar.getProgress());
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                long time = mediaPlayer.getCurrentPosition();
                lrcView.updateTime(time);
                seekBar.setProgress((int) time);
            }

            handler.postDelayed(this, 300);
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}
