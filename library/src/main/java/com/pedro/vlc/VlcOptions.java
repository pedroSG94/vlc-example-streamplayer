package com.pedro.vlc;

import android.util.Log;
import org.videolan.libvlc.util.VLCUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedro on 25/06/17.
 */
public class VlcOptions {

  private final String TAG = "VlcOptions";

  private int deblocking = -1;
  private boolean enableFrameSkip = false;
  private boolean enableTimeStretchingAudio = false;
  private int networkCaching = 0;
  private boolean verboseMode = BuildConfig.DEBUG;
  private List<String> extraArgs = null;

  public ArrayList<String> get() {
    ArrayList<String> options = new ArrayList<>(16);

    options.add(enableTimeStretchingAudio ? "--audio-time-stretch" : "--no-audio-time-stretch");
    options.add("--avcodec-skiploopfilter");
    options.add(String.valueOf(getDeblocking(deblocking)));
    options.add("--avcodec-skip-frame");
    options.add(enableFrameSkip ? "2" : "0");
    options.add("--avcodec-skip-idct");
    options.add(enableFrameSkip ? "2" : "0");
    options.add("--audio-resampler");
    options.add(getResampler());

    if (networkCaching > 0) {
      options.add("--network-caching=" + Math.min(60000, networkCaching));
    }

    final List<String> extra = extraArgs;
    if (extra != null) {
      options.addAll(extra);
    }

    options.add(verboseMode ? "-vv" : "-v");
    return options;
  }

  /*
   The below functions are borrowed from the official VLC app:
   */
  private int getDeblocking(int deblocking) {
    int ret = deblocking;
    if (deblocking < 0) {
            /*
              Set some reasonable deblocking defaults:

              Skip all (4) for armv6 and MIPS by default
              Skip non-ref (1) for all armv7 more than 1.2 Ghz and more than 2 cores
              Skip non-key (3) for all devices that don't meet anything above
             */
      VLCUtil.MachineSpecs m = VLCUtil.getMachineSpecs();
      if (m == null) {
        return ret;
      }
      if ((m.hasArmV6 && !(m.hasArmV7)) || m.hasMips) {
        ret = 4;
      } else if (m.frequency >= 1200 && m.processors > 2) {
        ret = 1;
      } else if (m.bogoMIPS >= 1200 && m.processors > 2) {
        ret = 1;
        Log.d(TAG, "Used bogoMIPS due to lack of frequency info");
      } else {
        ret = 3;
      }
    } else if (deblocking > 4) { // sanity check
      ret = 3;
    }
    return ret;
  }

  private String getResampler() {
    final VLCUtil.MachineSpecs m = VLCUtil.getMachineSpecs();
    return (m == null || m.processors > 2) ? "soxr" : "ugly";
  }
}
