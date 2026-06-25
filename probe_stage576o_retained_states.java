import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class probe_stage576o_retained_states {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576n12_stage576_full_dynamic_recursive_checked.mph");
      int retained = 0;
      for (String tag : model.sol().tags()) {
        String study = "";
        try { study = model.sol(tag).study(); } catch (Exception ignored) {}
        if (!study.startsWith("std576m_")) continue;
        double[] values;
        try { values = model.sol(tag).getPVals(); }
        catch (Exception error) { values = new double[0]; }
        System.out.println("STATE tag=" + tag + " study=" + study
            + " pvals=" + Arrays.toString(values));
        retained++;
      }
      System.out.println("RETAINED_STAGE576_COUNT=" + retained);
      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
