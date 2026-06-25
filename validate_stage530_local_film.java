import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage530_local_film {
  static void require(boolean condition, String message) {
    if (!condition) throw new IllegalStateException(message);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "533_stage530_local_film_stationary_checked.mph");
    require(Math.abs(model.param().evaluate("stage530_revision") - 530)
        < 0.1, "Stage 530 metadata missing");
    require(Arrays.equals(
        model.component("comp1").physics("tff").selection().entities(),
        model.component("comp1").selection("sel_film_track").entities(2)),
        "TFF is not restricted to the local track");
    double[][] x = model.result().numerical("eval530").getReal();
    for (int i = 0; i < x.length; i++) {
      require(Double.isFinite(x[i][0]), "eval530 value is not finite");
      System.out.printf(Locale.US, "RELOAD530[%d]=%.12g%n", i, x[i][0]);
    }
    require(x[0][0] > 0, "film area must be positive");
    require(x[1][0] >= 3.0 && x[1][0] < 5.0,
        "mean film thickness is outside the expected range");
    require(x[2][0] >= 0 && x[2][0] < 0.03,
        "local net film load is outside the validation range");
    System.out.println("STAGE530_RELOAD_VALIDATION=PASS");
    ModelUtil.disconnect();
  }
}
