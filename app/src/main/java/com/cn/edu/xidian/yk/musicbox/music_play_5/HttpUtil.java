package com.cn.edu.xidian.yk.musicbox.music_play_5;

import android.icu.text.RelativeDateTimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by heart_sunny on 2018/3/5.
 */

public class HttpUtil {

    public static Audio analyzeJSON(String data) {
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
            String play_url = objInfo.optString("play_url");

            Audio audio = new Audio();
            audio.setAudioName(audio_name);
            audio.setAuthorName(author_name);
            audio.setLyrics(lyrics);
            audio.setSongName(song_name);
            audio.setPlayUrl(play_url);

            return audio;
        }

        return null;
    }

    public static HttpURLConnection getConnection(String urlPath) {

        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (200 == connection.getResponseCode()) {
                return connection;
            } else {
                throw new RuntimeException("连接失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

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

    public static String toTime(int dur) {
        int time = dur / 1000;
        int seconds = time % 60;
        int minute = time / 60;
        return String.format("%02d", minute) + ":"
                + String.format("%02d", seconds);
    }
}
