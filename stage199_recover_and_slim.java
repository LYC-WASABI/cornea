import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage199_recover_and_slim {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "383_lid8mm_stage199_jfo_joint_balance_setup_Model.mph");
      System.out.println("RECOVER_RUN sol49");
      model.sol("sol49").runAll();

      try { model.result().dataset().remove("dset199"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset199", "Solution");
      model.result().dataset("dset199").set("solution", "sol49");
      try { model.result().numerical().remove("eval199"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval199", "EvalGlobal");
      model.result().numerical("eval199").set("data", "dset199");
      model.result().numerical("eval199").set(
          "expr",
          new String[] {
            "delta_h_jfo197",
            "withsol('sol48',intop_film(h_jfo197)/intop_film(1))",
            "Fn_contact119",
            "Wfilm199",
            "Ftotal199",
            "dr_indent119",
            "FshearFilm199",
            "FshearFilm199/Ftotal199",
            "thetaAvg199"
          });
      model.result().numerical("eval199").set(
          "unit",
          new String[] {
            "um", "um", "N", "N", "N", "mm", "N", "1", "1"
          });
      double[][] x = model.result().numerical("eval199").getReal();
      System.out.printf(
          Locale.US,
          "STAGE199 separation=%.12g havg=%.12g Fc=%.12g"
              + " Wfilm=%.12g Ftotal=%.12g d=%.12g"
              + " Fshear=%.12g muFilm=%.12g thetaAvg=%.12g%n",
          x[0][0], x[1][0], x[2][0], x[3][0], x[4][0],
          x[5][0], x[6][0], x[7][0], x[8][0]);

      for (String tag : model.sol().tags()) {
        if (!tag.equals("sol48") && !tag.equals("sol49")) {
          try { model.sol(tag).clearSolution(); }
          catch (Exception ignored) {}
        }
      }
      model.save("384_lid8mm_stage199_jfo_joint_balance_results_slim_Model.mph");
      System.out.println("SAVED_STAGE199_SLIM");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
