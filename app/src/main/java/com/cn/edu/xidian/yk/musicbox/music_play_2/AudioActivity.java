package com.cn.edu.xidian.yk.musicbox.music_play_2;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.edu.xidian.yk.musicbox.R;

import java.io.File;

public class AudioActivity extends AppCompatActivity {

    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;
    private static final String url = "http://abv.cn/music/%E5%85%89%E8%BE%89%E5%B2%81%E6%9C%88.mp3";

    private Button playBtn;
    private Player player; // 播放器
    private SeekBar musicProgress; // 音乐进度

    private Button downloadBtn;
    private Button stopDownloadBtn;
    private ProgressBar progressBar;

    private TextView resultView;

    private Handler handler = new UIHandler();

    private final class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING://正在下载
                    progressBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) progressBar.getProgress()
                            / (float) progressBar.getMax();
                    int result = (int) (num * 100); // 计算进度
                    resultView.setText(result + "%");
                    if (progressBar.getProgress() == progressBar.getMax()) { // 下载完成
                        Toast.makeText(getApplicationContext(), "下载成功",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE://下载失败
                    Toast.makeText(getApplicationContext(), "下载失败",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        ButtonClickListener listener = new ButtonClickListener();

        playBtn = (Button) findViewById(R.id.btn_online_play);
        playBtn.setOnClickListener(listener);

        musicProgress = (SeekBar) findViewById(R.id.music_progress);
        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        downloadBtn = (Button) findViewById(R.id.download_button);
        stopDownloadBtn = (Button) findViewById(R.id.stop_download_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        downloadBtn.setOnClickListener(listener);
        stopDownloadBtn.setOnClickListener(listener);

        resultView = (TextView) findViewById(R.id.resultView);
    }

    private final class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_online_play:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            player.playUrl(url);
                        }
                    }).start();
                    break;

                case R.id.download_button:
                    String filename = url.substring(url.lastIndexOf("/") + 1);
                    Log.e("filename_1", filename);

//                    try {
//                        filename = URLEncoder.encode(filename, "UTF-8");
//                        Log.e("filename_2", filename);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }

                    String pathUrl = url.substring(0, url.lastIndexOf("/") + 1) + filename;
                    Log.e("pathUrl", pathUrl);

                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        File saveDir = Environment.getExternalStorageDirectory();
                        download(pathUrl, saveDir);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "SD卡错误", Toast.LENGTH_LONG).show();
                    }
                    downloadBtn.setEnabled(false);
                    stopDownloadBtn.setEnabled(true);
                    break;

                case R.id.stop_download_button:
                    exit();
                    Toast.makeText(getApplicationContext(),
                            "Now thread is Stopping!!", Toast.LENGTH_LONG).show();
                    downloadBtn.setEnabled(true);
                    stopDownloadBtn.setEnabled(false);
                    break;
            }
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }
    }

    private DownloadTask downloadTask;

    private void download(String path, File saveDir) {
        downloadTask = new DownloadTask(path, saveDir);
        new Thread(downloadTask).start();
    }

    private void exit() {
        if (downloadTask != null)
            downloadTask.exit();
    }

    private final class DownloadTask implements Runnable {

        private String path;
        private File saveDir;
        private FileDownloader fileDownloader;

        public DownloadTask(String path, File saveDir) {
            this.path = path;
            this.saveDir = saveDir;
        }

        DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
            @Override
            public void onDownloadSize(int size) {
                Message msg = new Message();
                msg.what = PROCESSING;
                msg.getData().putInt("size", size);
                handler.sendMessage(msg);
            }
        };

        @Override
        public void run() {
            try {
                // 实例化一个文件下载器
                fileDownloader = new FileDownloader(getApplicationContext(), path,
                        saveDir, 3);
                // 设置进度条最大值
                progressBar.setMax(fileDownloader.getFileSize());
                fileDownloader.download(downloadProgressListener);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(FAILURE)); // 发送一条空消息对象
            }
        }

        public void exit() {
            if (fileDownloader != null)
                fileDownloader.exit();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player = null;
        }
    }
}
