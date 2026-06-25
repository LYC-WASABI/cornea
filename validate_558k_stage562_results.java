import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_558k_stage562_results {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558k_stage562_JFO_separation_scan_results.mph");
      System.out.println("MODEL=" + model.label());
      System.out.println("STUDY=" + model.study("std7").label());
      System.out.println("HW1=" + model.component("comp1").physics("tff")
          .feature("ffp1").getString("hw1"));
      System.out.println("PLIST=" + Arrays.toString(
          model.study("std7").feature("param562")
              .getStringArray("plistarr")));
      double[][] values =
          model.result().numerical("eval562_scan").getReal();
      System.out.println("ROWS=" + values[0].length);
      for (int i = 0; i < values[0].length; i++) {
        System.out.printf(
            Locale.US,
            "delta=%.6g Wraw=%.12g Wscaled=%.12g thetaAvg=%.12g%n",
            values[0][i], values[2][i], values[3][i], values[4][i]);
      }
      double[][] minH =
          model.result().numerical("min562_hfilm").getReal();
      double[][] maxP =
          model.result().numerical("max562_pfilm").getReal();
      double[][] minTheta =
          model.result().numerical("min562_theta").getReal();
      System.out.println("HMIN=" + Arrays.deepToString(minH));
      System.out.println("PMAX=" + Arrays.deepToString(maxP));
      System.out.println("THETAMIN=" + Arrays.deepToString(minTheta));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
