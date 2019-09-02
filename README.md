# FFmpeg--音视频编码技术

## 一、FFmpeg常用命令

https://www.jianshu.com/p/ddafe46827b7

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

## 二、FFplay常用命令

https://www.cnblogs.com/renhui/p/8458802.html

### 命令格式
```
ffplay [选项] ['输入文件']
```

### 1. 主要选项

```
'-x width'        强制以 "width" 宽度显示
'-y height'       强制以 "height" 高度显示
'-an'             禁止音频
'-vn'             禁止视频
'-ss pos'         跳转到指定的位置(秒)
'-t duration'     播放 "duration" 秒音/视频
'-bytes'          按字节跳转
'-nodisp'         禁止图像显示(只输出音频)
'-f fmt'          强制使用 "fmt" 格式
'-window_title title'  设置窗口标题(默认为输入文件名)
'-loop number'    循环播放 "number" 次(0将一直循环)
'-showmode mode'  设置显示模式
可选的 mode ：
'0, video'    显示视频
'1, waves'    显示音频波形
'2, rdft'     显示音频频带
默认值为 'video'，你可以在播放进行时，按 "w" 键在这几种模式间切换
'-i input_file'   指定输入文件
```

### 2. 一些高级选项

```
'-sync type'          设置主时钟为音频、视频、或者外部。默认为音频。主时钟用来进行音视频同步
'-threads count'      设置线程个数
'-autoexit'           播放完成后自动退出
'-exitonkeydown'      任意键按下时退出
'-exitonmousedown'    任意鼠标按键按下时退出
'-acodec codec_name'  强制指定音频解码器为 "codec_name"
'-vcodec codec_name'  强制指定视频解码器为 "codec_name"
'-scodec codec_name'  强制指定字幕解码器为 "codec_name"
```

### 3. 一些快捷键

```
'q, ESC'            退出
'f'                 全屏
'p, SPC'            暂停
'w'                 切换显示模式(视频/音频波形/音频频带)
's'                 步进到下一帧
'left/right'        快退/快进 10 秒
'down/up'           快退/快进 1 分钟
'page down/page up' 跳转到前一章/下一章(如果没有章节，快退/快进 10 分钟)
'mouse click'       跳转到鼠标点击的位置(根据鼠标在显示窗口点击的位置计算百分比)
```

### 4.ffplay 播放音频

播放音频文件的命令：
```
ffplay shy.mp3
```
这时候就会弹出来一个窗口，一边播放MP3文件，一边将播放音频的图画到该窗口上。针对该窗口的操作如下：

1.点击该窗口的任意一个位置，ffplay会按照点击的位置计算出时间的进度，然后seek到计算出来的时间点继续播放。<br>
2.按下键盘的左键默认快退10s，右键默认快进10s，上键默认快进1min，下键默认快退1min。<br>
3.按ESC就退出播放进程，按W会绘制音频的波形图。

### 5.ffplay 播放视频

播放视频文件的命令：
```
ffplay pm.mp4
```
这时候，就会在新弹出的窗口上播放该视频了。

1.如果想要同时播放多个文件，只需在多个命令行下同时执行ffplay就可以了。<br>
2.如果按s键就可以进入frame-step模式，即按s键一次就会播放下一帧图像。<br>

### 6.ffplay 高级使用方式

#### 6.1 循环播放
```
ffplay pm.mp4 -loop 10
```
上述命令代表播放视频结束之后会从头再次播放，共循环播放10次。

#### 6.2 播放 pm.mp4 ，播放完成后自动退出
```
ffplay -autoexit pm.mp4
```

#### 6.3 以 320 x 240 的大小播放 test.mp4
```
ffplay -x 320 -y 240 pm.mp4
```

#### 6.4 将窗口标题设置为 "myplayer"，循环播放 2 次
```
ffplay -window_title myplayer -loop 2 pm.mp4
```

#### 6.5 播放 双通道 32K 的 PCM 音频数据
```
ffplay -f s16le -ar 32000 -ac 2 test.pcm
```

### 7.ffplay音画同步

ffplay也是一个视频播放器，所以不得不提出来的一个问题是：音画同步。ffplay的音画同步的实现方式其实有三种，分别是：以音频为主时间轴作为同步源，以视频为主时间轴作为同步源，以外部时钟为主时间轴作为同步源。

下面就以音频为主时间轴来作为同步源来作为案例进行讲解，而且ffplay默认也是以音频为基准进行对齐的，那么以音频作为对齐基准是如何实现的呢？

首先需要说明的是，播放器接收到的视频帧或者音频帧，内部都是会有时间戳（PTS时钟）来标识它实际应该在什么时刻展示，实际的对齐策略如下：比较视频当前的播放时间和音频当前的播放时间，如果视频播放过快，则通过加大延迟或者重复播放来降低视频播放速度，如果视频播放满了，则通过减小延迟或者丢帧来追赶音频播放的时间点。关键就在于音视频时间的比较和延迟的计算，当前在比较的过程中会设置一个阈值，如果超过预设的阈值就应该作出调整（丢帧或者重复渲染），这就是整个对齐策略。

在使用ffplay的时候，我们可以明确的指定使用那种对齐方式，比如：
```
ffplay pm.mp4 -sync audio
```
上面这个命令显式的指定了使用以音频为基准进行音视频同步的方式播放视频文件，当然这也是ffplay的默认播放设置。
```
ffplay pm.mp4 -sync video
```
上面这个命令显式的指定了使用以视频为基准进行音视频同步的方式播放视频文件。
```
ffplay pm.mp4 -sync ext
```
上面这个命令显式的指定了使用外部时钟为基准进行音视频同步的方式播放视频文件。

大家可以分别使用这三种方式进行播放，尝试听一听，做一些快进或者seek的操作，看看不同的对齐策略对最终的播放会产生什么样的影响。
