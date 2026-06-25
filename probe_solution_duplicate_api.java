import com.comsol.model.*;
import com.comsol.model.util.*;

public class probe_solution_duplicate_api {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558i_stage561_JFO_diagnostics_results.mph");
      model.sol().duplicate("sol86_seed", "sol86");
      System.out.println("DUPLICATED=" + model.sol("sol86_seed").label());
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
