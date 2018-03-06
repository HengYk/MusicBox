package com.cn.edu.xidian.yk.musicbox.music_play_3;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.edu.xidian.yk.musicbox.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editText;
    private Button button;
    private TextView textView;
    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String dataInfo = HttpUtils.analyzeJSON(msg.obj.toString());
                    textView.setText(dataInfo);
                    Toast.makeText(HttpActivity.this, "获取数据成功", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case FAILURE:
                    Toast.makeText(HttpActivity.this, "获取数据失败", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case ERRORCODE:
                    Toast.makeText(HttpActivity.this, "获取的CODE码不为200！", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textViewContent);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                new Thread() {
                    @Override
                    public void run() {
                        String path = editText.getText().toString().trim();
                        try {
                            URL url = new URL(path);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setConnectTimeout(5000);
                            connection.setRequestMethod("GET");
                            int code = connection.getResponseCode();
                            if (code == 200) {
                                InputStream is = connection.getInputStream();
                                String result = HttpUtils.readMyInputStream(is);

                                Message msg = new Message();
                                msg.obj = result;
                                msg.what = SUCCESS;
                                handler.sendMessage(msg);
                            }else {
                                Message msg = new Message();
                                msg.what = ERRORCODE;
                                handler.sendMessage(msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                            Message msg = new Message();
                            msg.what = FAILURE;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            default:
                break;
        }
    }
}
