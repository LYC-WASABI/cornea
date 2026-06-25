import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage540_joint_balance {
  static void require(boolean condition, String message) {
    if (!condition) throw new IllegalStateException(message);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "543_stage540_jfo_joint_static_checked.mph");
    require(Math.abs(model.param().evaluate("stage540_revision") - 540)
        < 0.1, "Stage 540 metadata missing");
    require(Math.abs(model.param().evaluate("delta_h_jfo197")
        - 2.4e-6) < 1e-10, "Stage 540 separation calibration changed");
    require(Arrays.equals(
        model.component("comp1").physics("tff").selection().entities(),
        model.component("comp1").selection("sel_film_track").entities(2)),
        "TFF is not local");
    require(Arrays.equals(
        model.component("comp1").physics("solid")
            .feature("load_partitioned_pfilm").selection().entities(),
        model.component("comp1").selection("sel_film_track").entities(2)),
        "film-pressure structural load is not local");
    double[][] x = model.result().numerical("eval540").getReal();
    for (int i = 0; i < x.length; i++) {
      require(Double.isFinite(x[i][0]), "eval540 value is not finite");
      System.out.printf(Locale.US, "RELOAD540[%d]=%.12g%n", i, x[i][0]);
    }
    require(x[0][0] > 0 && x[0][0] < 0.03,
        "film load is outside the joint-load range");
    require(x[1][0] > 0, "solid contact load is not positive");
    require(Math.abs(x[3][0]) < 0.01,
        "joint total-load error exceeds 1 percent");
    require(x[4][0] >= 0 && x[4][0] <= 1,
        "JFO fractional content is invalid");
    System.out.println("STAGE540_RELOAD_VALIDATION=PASS");
    ModelUtil.disconnect();
  }
}
