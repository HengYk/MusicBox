# MusicBox

## Android音乐播放器

---
### 简介

    程序提供了四个实现音乐播放器的基础功能，具体见`详情`。每个music_play_中的Activity都可独立运行。
---
### 详情

  1.music_play_1
  
      基于Service的音乐播放器的实现。
      
![](https://github.com/HengYk/MusicBox/raw/master/raw/music_play_1.png)  
      
  2.music_play_2
  
      仅播放本地音频和断点续传下载的实现。
      
![](https://github.com/HengYk/MusicBox/raw/master/raw/music_play_2.png)  
      
  3.music_play_3
  
      在线请求网络音频资源（json数据解析）的实现。
  
![](https://github.com/HengYk/MusicBox/raw/master/raw/music_play_3.png)  

  4.music_play_4
  
      基于LrcView的歌词滚动控件的实现。（以本地音频资源为例）
      
      引自[https://github.com/wangchenyan/LrcView]
      
![](https://github.com/HengYk/MusicBox/raw/master/raw/music_play_4.png)  
      
  5.music_play_5
  
      基于LrcView的歌词滚动控件的实现。（以网络音频资源为例）
      
      此部分包含两种实现方法，TestActivity直接实现，Test2Activity在PlayerTwo封装的基础上实现。
      
![](https://github.com/HengYk/MusicBox/raw/master/raw/music_play_5.png)   

### 本文链接

[https://github.com/HengYk/MusicBox](https://github.com/HengYk/MusicBox "音乐播放器")
