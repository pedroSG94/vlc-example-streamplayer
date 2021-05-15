# vlc-example-streamplayer

[![Release](https://jitpack.io/v/pedroSG94/vlc-example-streamplayer.svg)](https://jitpack.io/#pedroSG94/vlc-example-streamplayer)

Example code how to play a stream with VLC.

Use this endpoint for test:

```
rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov
```


Consider using the latest official compiled VLC version:

https://mvnrepository.com/artifact/org.videolan.android/libvlc-all

https://code.videolan.org/videolan/vlc-android#build-application


## Gradle
Compile my wrapper:

```gradle
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
dependencies {
  compile 'com.github.pedroSG94.vlc-example-streamplayer:pedrovlc:2.5.14v3'
}
```

Compile only VLC (version 2.5.14):

```gradle
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
dependencies {
  compile 'com.github.pedroSG94.vlc-example-streamplayer:libvlc:2.5.14v3'
}
```
