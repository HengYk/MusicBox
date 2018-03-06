package com.cn.edu.xidian.yk.musicbox.music_play_5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cn.edu.xidian.yk.musicbox.R;
import com.cn.edu.xidian.yk.musicbox.music_play_2.Player;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import me.wcy.lrcview.LrcView;

public class Test2Activity extends AppCompatActivity {

    private PlayerTwo playerTwo;
    private LrcView lrcView;
    private SeekBar seekBar;
    private Button btnPlayPause;
    private TextView currentTime;
    private TextView totalTime;

    private HttpURLConnection conn = null;

    public static final String url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=CB7EE97F4CC11C4EA7A1FA4B516A5D97";

    private Audio audio = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        lrcView = (LrcView) findViewById(R.id.lrc_view);
        seekBar = (SeekBar) findViewById(R.id.progress_bar);
        currentTime = (TextView) findViewById(R.id.current_time);
        totalTime = (TextView) findViewById(R.id.total_time);

        playerTwo = new PlayerTwo(seekBar, lrcView, currentTime, totalTime);
        btnPlayPause = (Button) findViewById(R.id.btn_play_pause);


        new Thread() {
            @Override
            public void run() {
                conn = HttpUtil.getConnection(url);
                try {
                    InputStream is = conn.getInputStream();
                    String result = HttpUtil.readMyInputStream(is);
                    audio = HttpUtil.analyzeJSON(result);

                    if (audio != null) {
                        playerTwo.start(audio);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playerTwo.mediaPlayer.isPlaying()) {
                    playerTwo.play();
                }else {
                    playerTwo.pause();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        playerTwo.stop();
        super.onDestroy();
    }
}
