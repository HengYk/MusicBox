package com.cn.edu.xidian.yk.musicbox.music_play_4;

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

import me.wcy.lrcview.LrcView;

public class LyrActivity extends AppCompatActivity {

    private LrcView lrcView;
    private SeekBar seekBar;
    private Button btnPlayPause;
    private TextView pro_text;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyr);

        lrcView = (LrcView) findViewById(R.id.lrc_view);
        seekBar = (SeekBar) findViewById(R.id.progress_bar);
        btnPlayPause = (Button) findViewById(R.id.btn_play_pause);
        pro_text = (TextView) findViewById(R.id.progress_text);

        try {
            mediaPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd("chengdu.mp3");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.prepareAsync();
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        lrcView.loadLrc(getLrcText("chengdu.lrc"));

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
                pro_text.setText(progress + " " + mediaPlayer.getDuration() +" " + mediaPlayer.getCurrentPosition());
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

    private String getLrcText(String fileName) {
        String lrcText = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lrcText = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcText;
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
