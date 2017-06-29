package com.pedro.vlc;

import java.util.ArrayList;
import org.videolan.libvlc.util.VLCUtil;

/**
 * Created by pedro on 25/06/17.
 */
public class VlcOptions {

  private boolean verboseMode = BuildConfig.DEBUG;

  public ArrayList<String> getDefaultOptions() {
    ArrayList<String> options = new ArrayList<>();
    options.add("--no-audio-time-stretch");
    options.add("--avcodec-skiploopfilter");
    options.add(String.valueOf(getDeblocking(-1)));
    options.add("--avcodec-skip-frame");
    options.add("0");
    options.add("--avcodec-skip-idct");
    options.add("0");
    options.add("--audio-resampler");
    options.add(getResampler());
    options.add(verboseMode ? "-vv" : "-v");
    //delay = network buffer + file buffer
    //options.add(":file-caching=" + Constants.BUFFER);
    //options.add(":network-caching=" + Constants.BUFFER);
    return options;
  }

  /*
   The below functions are borrowed from the official VLC app:
   */
  private int getDeblocking(int deblocking) {
    if (deblocking < 0) {
      /*
       Set some reasonable deblocking defaults:
       Skip all (4) for armv6 and MIPS by default
       Skip non-ref (1) for all armv7 more than 1.2 Ghz and more than 2 cores
       Skip non-key (3) for all devices that don't meet anything above
       */
      VLCUtil.MachineSpecs machineSpecs = VLCUtil.getMachineSpecs();
      if (machineSpecs == null) {
        return deblocking;
      } else if ((machineSpecs.hasArmV6 && !(machineSpecs.hasArmV7)) || machineSpecs.hasMips) {
        return 4;
      } else if (machineSpecs.frequency >= 1200 && machineSpecs.processors > 2) {
        return 1;
      } else if (machineSpecs.bogoMIPS >= 1200 && machineSpecs.processors > 2) {
        return 1;
      } else {
        return 3;
      }
    } else if (deblocking > 4) {
      return 3;
    } else {
      return deblocking;
    }
  }

  private String getResampler() {
    VLCUtil.MachineSpecs machineSpecs = VLCUtil.getMachineSpecs();
    return (machineSpecs == null || machineSpecs.processors > 2) ? "soxr" : "ugly";
  }
}
