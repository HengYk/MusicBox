package com.cn.edu.xidian.yk.musicbox.music_play_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.edu.xidian.yk.musicbox.R;

public class MainActivity extends AppCompatActivity {

    Button btnPlayOrPause,btnPre,btnNext;

    //进度条
    static SeekBar skbMusic;

    // 获取界面中显示歌曲标题、作者文本框
    TextView title, author, txt_cur;
    String[] titleStrs = new String[] { "涛声依旧", "油菜花", "You Are The One" };
    String[] authorStrs = new String[] { "毛宁", "成龙", "未知艺术家" };

    //是否正在播放
    boolean isPlaying=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        skbMusic = (SeekBar) findViewById(R.id.skbMusic);
        skbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txt_cur.setText("当前进度值:" + i + "  / 100 ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicService.isChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mediaPlayer.seekTo(seekBar.getProgress());
                MusicService.isChanging = false;
            }
        });

        btnNext=(Button)findViewById(R.id.btnNext);
        btnPlayOrPause=(Button)findViewById(R.id.btnPlayOrPause);
        btnPre=(Button)findViewById(R.id.btnPre);
        btnNext.setOnClickListener(listener);
        btnPlayOrPause.setOnClickListener(listener);
        btnPre.setOnClickListener(listener);

        title = (TextView)findViewById(R.id.title);
        author = (TextView)findViewById(R.id.author);
        txt_cur = (TextView)findViewById(R.id.txt_cur);
        title.setText(titleStrs[0]);
        author.setText(authorStrs[0]);

        MusicBoxReceiver musicBoxReceiver = new MusicBoxReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstUtil.MUSICBOX_ACTION);
        registerReceiver(musicBoxReceiver, filter);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            switch (v.getId()) {
                case R.id.btnNext://下一首
                    btnPlayOrPause.setText("暂停");
                    sendBroadcastToService(ConstUtil.STATE_NEXT);
                    isPlaying=true;
                    break;
                case R.id.btnPlayOrPause://播放或暂停
                    if (!isPlaying) {
                        btnPlayOrPause.setText("暂停");
                        sendBroadcastToService(ConstUtil.STATE_PLAY);
                        isPlaying=true;
                    }else {
                        btnPlayOrPause.setText("播放");
                        sendBroadcastToService(ConstUtil.STATE_PAUSE);
                        isPlaying=false;
                    }
                    break;
                case R.id.btnPre://上一首
                    btnPlayOrPause.setText("暂停");
                    sendBroadcastToService(ConstUtil.STATE_PREVIOUS);
                    isPlaying=true;
                    break;
                default:
                    break;
            }
        }
    };

    protected void sendBroadcastToService(int state) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(ConstUtil.MUSICSERVICE_ACTION);
        intent.putExtra("control", state);
        sendBroadcast(intent);
    }

    class MusicBoxReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getIntExtra("current", -1);
            title.setText(titleStrs[current]);
            author.setText(authorStrs[current]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, ConstUtil.MENU_ABOUT, Menu.NONE, "关于");
        menu.add(Menu.NONE, ConstUtil.MENU_EXIT, Menu.NONE, "退出");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ConstUtil.MENU_ABOUT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("确认对话框");//设置标题
                builder.setIcon(R.mipmap.about);//设置图标
                builder.setMessage("确认对话框提示内容");//设置内容
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "点击了确认按钮" , Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_LONG).show();
                    }
                });

                //用creat()方法创建dialog, show()方法展示出来
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case ConstUtil.MENU_EXIT:
                sendBroadcastToService(ConstUtil.STATE_STOP);
                this.finish();
                break;
             default:
                    break;
        }
        return true;
    }
}
