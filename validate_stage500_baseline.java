import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage500_baseline {
  static void require(boolean condition, String message) {
    if (!condition) throw new IllegalStateException(message);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "503_stage500_baseline_checked.mph");
    require(Arrays.asList(model.component("comp1").physics().tags())
        .containsAll(Arrays.asList("solid", "tff", "ge_force_total111")),
        "physics inventory changed");
    require(Arrays.asList(model.component("comp1").pair().tags())
        .contains("cp_lid_cornea"), "contact pair missing");
    require(Arrays.asList(model.sol().tags())
        .containsAll(Arrays.asList("sol48", "sol49")),
        "stored Stage 200 solutions missing");
    require(model.component("comp1").physics("solid")
        .selection().entities().length == 2, "solid domains changed");
    require(model.component("comp1").physics("tff")
        .selection().entities().length == 4, "TFF boundaries changed");
    double[][] values =
        model.result().numerical("eval500_audit").getReal();
    System.out.printf(Locale.US,
        "STAGE500_RELOAD total=%.12g target=%.12g film=%.12g contact=%.12g%n",
        values[5][0], values[6][0], values[4][0], values[3][0]);
    require(Math.abs(values[5][0] - values[6][0]) < 5e-5,
        "total load audit failed after reload");
    System.out.println("STAGE500_RELOAD_VALIDATION=PASS");
    ModelUtil.disconnect();
  }
}
