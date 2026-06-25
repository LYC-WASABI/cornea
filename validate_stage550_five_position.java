import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage550_five_position {
  static double[] evalDataset(
      Model model, String tag, String data, String wfilm, String total) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", new String[] {
      wfilm, "Fn_contact119", total,
      "(" + total + "-F_total_target)/F_total_target",
      "dr_indent119"
    });
    double[][] raw = model.result().numerical(tag).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < raw.length; i++) values[i] = raw[i][0];
    return values;
  }

  static void print(String key, double[] values) {
    System.out.printf(Locale.US,
        "VALIDATE550 %s Wfilm=%.12g Fcontact=%.12g Ftotal=%.12g"
            + " err=%.12g indent=%.12g%n",
        key, values[0], values[1], values[2], values[3], values[4]);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      if (Math.abs(model.param().evaluate("stage550_revision") - 550) > 0.1) {
        throw new IllegalStateException("Stage 550 revision markers missing");
      }

      LinkedHashMap<String, double[]> values = new LinkedHashMap<>();
      values.put("plus35", evalDataset(
          model, "v550p35", "dset550_plus35",
          "Wfilm550_plus35", "Ftotal550_plus35"));
      values.put("plus17p5", evalDataset(
          model, "v550p17", "dset550_plus17p5",
          "Wfilm550_plus17p5", "Ftotal550_plus17p5"));

      double[][] midpointRaw = model.result().numerical("eval540").getReal();
      double[] midpoint = new double[] {
        midpointRaw[0][0], midpointRaw[1][0], midpointRaw[2][0],
        midpointRaw[3][0], midpointRaw[7][0]
      };
      values.put("mid", midpoint);
      values.put("minus17p5", evalDataset(
          model, "v550m17", "dset550_minus17p5",
          "Wfilm550_minus17p5", "Ftotal550_minus17p5"));
      values.put("minus35", evalDataset(
          model, "v550m35", "dset550_minus35",
          "Wfilm550_minus35", "Ftotal550_minus35"));

      double maxError = 0;
      for (Map.Entry<String, double[]> entry : values.entrySet()) {
        print(entry.getKey(), entry.getValue());
        maxError = Math.max(maxError, Math.abs(entry.getValue()[3]));
      }
      if (maxError > 0.02) {
        throw new IllegalStateException(
            "Maximum five-position load error exceeds 2 percent: " + maxError);
      }
      System.out.printf(Locale.US,
          "STAGE550_VALIDATION_PASS maxAbsRelativeError=%.12g%n", maxError);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
