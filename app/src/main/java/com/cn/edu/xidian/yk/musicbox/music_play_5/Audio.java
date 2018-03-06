package com.cn.edu.xidian.yk.musicbox.music_play_5;

/**
 * Created by heart_sunny on 2018/3/5.
 */

public class Audio {

    private String audioName;
    private String authorName;
    private String songName;
    private String lyrics;
    private String playUrl;

    public Audio() {

    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String play_url) {
        this.playUrl = play_url;
    }
}
