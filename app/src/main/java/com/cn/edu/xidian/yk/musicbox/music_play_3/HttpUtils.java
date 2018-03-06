package com.cn.edu.xidian.yk.musicbox.music_play_3;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by heart_sunny on 2018/3/4.
 */

public class HttpUtils {

    public static String readMyInputStream(InputStream is) {

        byte[] result;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            is.close();
            baos.close();
            result = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            String errorStr;
            errorStr = "获取数据失败。";
            return errorStr;
        }

        return new String(result);
    }

    public static String analyzeJSON(String data) {
        JSONObject object = null;

        try {
            object = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (object != null) {
            JSONObject objInfo = object.optJSONObject("data");
            String audio_name = objInfo.optString("audio_name");
            String song_name = objInfo.optString("song_name");
            String author_name = objInfo.optString("author_name");
            String lyrics = objInfo.optString("lyrics");

            String dataInfo = "\n音频：" + audio_name + "\n歌名：" + song_name + "\n作者：" + author_name + "\n歌词：" + lyrics;
            return dataInfo;
        }

        return null;
    }
}
