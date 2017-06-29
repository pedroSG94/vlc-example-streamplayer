package com.pedro.vlc;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

/**
 * Created by pedro on 25/06/17.
 * Play and stop need be in other thread or app can freeze
 */
public class VlcVideoLibrary implements MediaPlayer.EventListener {

  private LibVLC vlcInstance;
  private MediaPlayer player;
  private VlcListener vlcListener;
  private boolean playing = false;
  //The library will select one of this class for rendering depend of constructor called
  private SurfaceView surfaceView;
  private TextureView textureView;
  private SurfaceTexture surfaceTexture;
  private Surface surface;
  private SurfaceHolder surfaceHolder;

  public VlcVideoLibrary(Context context, VlcListener vlcListener, SurfaceView surfaceView) {
    this.vlcListener = vlcListener;
    this.surfaceView = surfaceView;
    vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
  }

  public VlcVideoLibrary(Context context, VlcListener vlcListener, TextureView textureView) {
    this.vlcListener = vlcListener;
    this.textureView = textureView;
    vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
  }

  public VlcVideoLibrary(Context context, VlcListener vlcListener, SurfaceTexture surfaceTexture) {
    this.vlcListener = vlcListener;
    this.surfaceTexture = surfaceTexture;
    vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
  }

  public VlcVideoLibrary(Context context, VlcListener vlcListener, Surface surface) {
    this.vlcListener = vlcListener;
    this.surface = surface;
    surfaceHolder = null;
    vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
  }

  public VlcVideoLibrary(Context context, VlcListener vlcListener, Surface surface,
      SurfaceHolder surfaceHolder) {
    this.vlcListener = vlcListener;
    this.surface = surface;
    this.surfaceHolder = surfaceHolder;
    vlcInstance = new LibVLC(context, new VlcOptions().getDefaultOptions());
  }

  public boolean isPlaying() {
    return playing;
  }

  public void play(final String endPoint) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        if (!playing) {
          playing = true;
          setMedia(new Media(vlcInstance, Uri.parse(endPoint)));
        }
      }
    }).start();
  }

  public void stop() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        if (playing) {
          playing = false;
          player.stop();
          player.release();
          player = null;
        }
      }
    }).start();
  }

  private void setMedia(Media media) {
    //delay = network buffer + file buffer
    //media.addOption(":network-caching=" + Constants.BUFFER);
    //media.addOption(":file-caching=" + Constants.BUFFER);
    //media.addOption(":clock-jitter=0");
    //media.addOption(":clock-synchro=0");
    //media.addOption(":codec=all");

    player = new MediaPlayer(vlcInstance);
    player.setMedia(media);
    player.setEventListener(this);

    IVLCVout vlcOut = player.getVLCVout();
    //set correct class for render depend of constructor called
    if (surfaceView != null) {
      vlcOut.setVideoView(surfaceView);
    } else if (textureView != null) {
      vlcOut.setVideoView(textureView);
    } else if (surfaceTexture != null) {
      vlcOut.setVideoSurface(surfaceTexture);
    } else if (surface != null) {
      vlcOut.setVideoSurface(surface, surfaceHolder);
    } else {
      throw new RuntimeException("You cant set a null render object");
    }
    vlcOut.attachViews();
    player.setVideoTrackEnabled(true);
    player.play();
  }

  @Override
  public void onEvent(MediaPlayer.Event event) {
    switch (event.type) {
      case MediaPlayer.Event.EndReached:
        vlcListener.onComplete();
        break;
      case MediaPlayer.Event.EncounteredError:
        vlcListener.onError();
        break;
      default:
        break;
    }
  }
}
