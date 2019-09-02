# FFmpeg--音视频编码技术

https://www.jianshu.com/p/ddafe46827b7

## 常用命令

### 主要参数：

-i 设定输入流<br> 
-f 设定输出格式<br> 
-ss 表示开始切割的时间点<br> 
-t 表示要切多长时间<br>
-f flv : -f表示format ，就是强制输出格式为flv，这一步其实
也叫封装(mux)，封装要做的事就是把视频和音频混合在一起，进行同步。

### 视频参数：

-b 设定视频流量(码率)，默认为200Kbit/s<br> 
-r 设定帧速率，默认为25<br>
-s 设定画面的宽与高<br>
-aspect 设定画面的比例<br>
-vf 对视频进行过滤处理<br>
-vn 不处理视频<br>
-vcodec 设定视频编解码器，未设定时则使用与输入流相同的编解码器<br>
-vcodec copy 表示复用源视频的视频编码器<br>
-re : 表示使用文件的原始帧率进行读取，因为ffmpeg读取视频帧的速度很快，
如果不使用这个参数，ffmpeg可以在很短时间就把video.mp4中的视
频帧全部读取完并进行推流，这样就无法体现出视频播放的效果了。<br>
-b:v 800k 表示视频的比特率(bitrate) ，为800k。

### 音频参数：

-ar 设定采样率<br>
-ac 设定声音的Channel数<br>
-an 不处理音频<br>
-acodec 设定声音编解码器，未设定时则使用与输入流相同的编解码器<br>
-acodec copy 表示复用源视频的音频编码器<br>
-b:a 32k 表示音频的比特率为32k

### 图片参数：

-r 表示每一秒几帧<br>
-q:v表示存储jpeg的图像质量，一般2是高质量

### 1.视频格式转换，准确的说，应该是视频容器转换

```
ffmpeg -i input.avi output.mp4
```
```
ffmpeg -i input.mp4 output.ts
```

### 2.下载远程服务器端的视频文件

```
ffmpeg -i "https://vdn.vzuu.com/SD/49c84c7c-c61a-11e8-8bad-0242ac112a0a.mp4" output.mp4
```

### 3.把视频文件的音频数据提取出来

```
ffmpeg -i mov_bbb.mp4 -acodec copy -vn output.aac（默认mp4的audio codec是aac）
```
```
ffmpeg -i mov_bbb.mp4 -acodec aac -vn output.aac（强行指定audio codec是aac）
```

### 4.把视频文件的视频数据提取出来

```
ffmpeg -i mov_bbb.mp4 -vcodec copy -an output.mp4
```

### 5.视频剪切

```
ffmpeg -ss 00:00:15 -t 00:00:05 -i mov_bbb.mp4 -vcodec copy -acodec copy output.mp4
```

注意一个问题，ffmpeg 在切割视频的时候无法做到时间绝对准确，因为视频编码中关
键帧（I帧）和跟随它的B帧、P帧是无法分割开的，否则就需要进行重新帧内编码，会
让视频体积增大。所以，如果切割的位置刚好在两个关键帧中间，那么 ffmpeg 会向
前/向后切割，所以最后切割出的 chunk 长度总是会大于等于应有的长度。

### 6.码率控制

码率控制对于在线视频比较重要。因为在线视频需要考虑其能提供的带宽。
那么，什么是码率？很简单： bitrate = file size / duration
比如一个文件20.8M，时长1分钟，那么，码率就是：
biterate = 20.8M bit/60s = 20.8*1024*1024*8 bit/60s= 2831Kbps
一般音频的码率只有固定几种，比如是128Kbps， 那么，video的就是
video biterate = 2831Kbps -128Kbps = 2703Kbps。

ffmpg控制码率有3种选择，-minrate -b:v -maxrate

-b:v主要是控制平均码率。 比如一个视频源的码率太高了，有10Mbps，文件太大，
想把文件弄小一点，但是又不破坏分辨率。 

```
ffmpeg -i input.mp4 -b:v 2000k output.mp4
```

上面把码率从原码率10Mbps转成2Mbps码率，这样其实也间接让文件变小了。目测接近一半

不过，ffmpeg官方wiki比较建议，设置b:v时，同时加上 -bufsize
-bufsize 用于设置码率控制缓冲器的大小，设置的好处是，让整体的码率更
趋近于希望的值，减少波动。（简单来说，比如1 2的平均值是1.5， 1.49 1.51 
也是1.5, 当然是第二种比较好）  

```
ffmpeg -i input.mp4 -b:v 2000k -bufsize 2000k output.mp4
```

-minrate -maxrate就简单了，在线视频有时候，希望码率波动，不要超过一个阈值，可以设置maxrate

```
ffmpeg -i input.mp4 -b:v 2000k -bufsize 2000k -maxrate 2500k output.mp4
```

### 7.视频编码格式转换

将一个MP4文件，默认视频编码格式是MPEG4，转换成H264

```
ffmpeg -i input.mp4 -vcodec h264 output.mp4
```
```
ffmpeg -i input.mp4 -vcodec mpeg4 output.mp4
```

当然了，如果ffmpeg当时编译时，添加了外部的x265或者X264，那也可以用外部的
编码器来编码。（不知道什么是X265，可以Google一下，简单的说，就是她不
包含在ffmpeg的源码里，是独立的一个开源代码，用于编码HEVC，ffmpeg编
码时可以调用它。当然了，ffmpeg自己也有编码器）

```
ffmpeg -i input.mp4 -c:v libx265 output.mp4
```
```
ffmpeg -i input.mp4 -c:v libx264 output.mp4
```

### 8.只提取视频ES数据

```
ffmpeg –i input.mp4 –vcodec copy –an –f m4v output.h264
```

### 9.将输入的1920x1080缩小到960x540输出:

```
ffmpeg -i input.mp4 -vf scale=960:540 output.mp4
```

ps: 如果540不写，写成-1，即scale=960:-1, 那也是可以的，ffmpeg会通知缩
放滤镜在输出时保持原始的宽高比

#### 10.为视频添加logo

```
ffmpeg -i input.mp4 -i iQIYI_logo.png -filter_complex overlay output.mp4
```

### 11.抓取视频的一些帧，存为jpeg图片

```
ffmpeg -i input.mp4 -r 1 -q:v 2 -f image2 pic.jpeg
```

如此，ffmpeg会把input.mp4，每隔一秒，存一张图片下来。假设有60s，
那会有60张。60张？什么？这么多？不要不要。。。。。不要咋办？？
可以设置开始的时间，和你想要截取的时间呀。

```
ffmpeg -i input.mp4 -ss 00:00:20 -t 10 -r 1 -q:v 2 -f image2 pic.jpeg
```

如此，ffmpeg会从input.mp4的第20s时间开始，往下10s，即20~30s这10秒钟之间，
每隔1s就抓一帧，总共会抓10帧。

### 12.输出YUV420原始数据

```
ffmpeg -i input.mp4 output.yuv
```

RawPlayer可以播放yuv的数据

那如果我只想要抽取某一帧YUV呢？ 简单，你先用上面的方法，先抽出jpeg图片，
然后把jpeg转为YUV。 比如： 你先抽取10帧图片。 

```
ffmpeg -i input.mp4 -ss 00:00:20 -t 10 -r 1 -q:v 2 -f image2 pic.jpeg
```

然后，你就随便挑一张，转为YUV: 

```
ffmpeg -i pic-001.jpeg -s 1440x1440 -pix_fmt yuv420p xxx3.yuv
```

如果-s参数不写，则输出大小与输入一样。当然了，YUV还有yuv422p啥的，你在-pix_fmt 换成yuv422p就行啦！

### 13.H264编码profile & level控制

```
ffmpeg -i input.mp4 -profile:v baseline -level 3.0 output.mp4
```
```
ffmpeg -i input.mp4 -profile:v main -level 4.2 output.mp4
```
```
ffmpeg -i input.mp4 -profile:v high -level 5.1 output.mp4
```
如果ffmpeg编译时加了external的libx264，那就这么写：

```
ffmpeg -i input.mp4 -c:v libx264 -x264-params "profile=high:level=3.0" output.mp4
```

从压缩比例来说，baseline< main < high，对于带宽比较局限的在线视频，
可能会选择high，但有些时候，做个小视频，希望所有的设备基本都能解
码（有些低端设备或早期的设备只能解码baseline），那就牺牲文件大小吧，用baseline。自己取舍吧！

### 14.小丸工具箱

小丸工具箱是一款用于处理音视频等多媒体文件的软件。是一款x264、ffmpeg等命
令行程序的图形界面。它的目标是让视频压制变得简单、轻松。

主要功能：

高质量的H264+AAC视频压制<br>
ASS/SRT字幕内嵌到视频<br>
AAC/WAV/FLAC/ALAC音频转换<br>
MP4/MKV/FLV的无损抽取和封装

### 15.使用ffmpeg推RTMP直播流

```
ffmpeg -re -i 1.mp4 -vcodec copy -f flv rtmp://localhost/live
```
```
ffmpeg -re -i 1.mp4 -vcodec copy -acodec copy -b:v 800k -b:a 32k -f flv rtmp://localhost/live
```
紧跟在后面的rtmp://localhost/live 表示输出的"文件名"，这个文
件名可以是一个本地的文件，也可以指定为rtmp流媒体地址。指定
为rtmp流媒体地址后，则ffmpeg就可以进行推流。

可以使用VLC或ffplay进行播放了。
