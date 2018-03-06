package com.cn.edu.xidian.yk.musicbox.music_play_1;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    Timer mTimer;
    TimerTask mTimerTask;
    static boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突

    //创建一个媒体播放器的对象
    static MediaPlayer mediaPlayer;

    //创建一个Asset管理器的的对象
    AssetManager assetManager;

    //存放音乐名的数组
    String[]musics=new String[]{"taoshengyijiu-maoning.mp3", "youcaihua-chenglong.mp3","You Are The One.mp3" };

    //当前的播放的音乐
    int current=0;

    //当前播放状态
    int state=ConstUtil.STATE_NON;

    //记录Timer运行状态
    boolean isTimerRunning=false;

    @Override
    public void onCreate() {
        MusicServiceReceiver musicServiceReceiver = new MusicServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstUtil.MUSICSERVICE_ACTION);
        registerReceiver(musicServiceReceiver, filter);

        mediaPlayer = new MediaPlayer();
        assetManager = getAssets();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                current ++;
                prepareAndPlay(current);
            }
        });
    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void prepareAndPlay(int index) {

        if (isTimerRunning) {
            mTimer.cancel();
            isTimerRunning = false;
        }

        if (index > 2) {
            index = 0;
            current = index;
        }
        if (index < 0) {
            index = 2;
            current = index;
        }

        Intent intent = new Intent();
        intent.putExtra("current", index);
        intent.setAction(ConstUtil.MUSICBOX_ACTION);
        sendBroadcast(intent);

        try {
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(musics[index]);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();

            MainActivity.skbMusic.setMax(mediaPlayer.getDuration());

        } catch (IOException e) {
            e.printStackTrace();
        }

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                isTimerRunning = true;
                if (isChanging) {
                    return;
                }
                MainActivity.skbMusic.setProgress(mediaPlayer.getCurrentPosition());
            }
        };
        mTimer.schedule(mTimerTask, 0, 10);
    }

    class MusicServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case ConstUtil.STATE_PLAY:
                    if (state == ConstUtil.STATE_PAUSE) {
                        mediaPlayer.start();
                    }else if (state != ConstUtil.STATE_PLAY) {
                        prepareAndPlay(current);
                    }
                    state = ConstUtil.STATE_PLAY;
                    break;
                case ConstUtil.STATE_PAUSE:
                    if (state == ConstUtil.STATE_PLAY) {
                        mediaPlayer.pause();
                        state = ConstUtil.STATE_PAUSE;
                    }
                    break;
                case ConstUtil.STATE_PREVIOUS:
                    prepareAndPlay(-- current);
                    state = ConstUtil.STATE_PLAY;
                    break;
                case ConstUtil.STATE_NEXT:
                    prepareAndPlay(++ current);
                    state = ConstUtil.STATE_PLAY;
                    break;
                case ConstUtil.STATE_STOP:
                    if (state == ConstUtil.STATE_PLAY | state == ConstUtil.STATE_PAUSE) {
                        mediaPlayer.stop();
                        state = ConstUtil.STATE_STOP;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
