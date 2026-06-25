import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage555_shift_positive_at_eps40 {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "557s1_stage555_alpha0p825_eps40.mph");
      String comp = "comp1";
      String ge = "ge_force_total111";
      model.param().set("alpha_gap555", "0.825");
      model.param().set("eps_q_regular555", "40");
      String study = "std555_qnegative40";
      model.study().create(study);
      model.study(study).label(
          "Stage 555 scan indentation reference negative at eps 40");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"q_ref555"});
      model.study(study).feature("param").set(
          "plistarr", new String[] {
            "0.061 0.03 0 -0.03 -0.06 -0.10 -0.15 -0.20 -0.30 -0.40 -0.50 -0.70 -1.00"
          });
      model.study(study).feature("param").set(
          "punit", new String[] {"1"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set(
          "geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {"solid", "on", "tff", "on", ge, "on"});
      String step = study + "/stat";
      for (String tag : new String[] {
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
      }) {
        model.component(comp).physics("solid").feature(tag)
            .set("StudyStep", step);
      }
      for (String tag :
          model.component(comp).physics("tff").feature().tags()) {
        try {
          model.component(comp).physics("tff").feature(tag)
              .set("StudyStep", step);
        } catch (Exception ignored) {}
      }
      model.component(comp).physics(ge).feature("ge1")
          .set("StudyStep", step);
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      Set<String> old = new HashSet<>(Arrays.asList(before));
      String solution = null;
      for (String tag : model.sol().tags()) {
        if (!old.contains(tag)) solution = tag;
      }
      SolverFeature variables = model.sol(solution).feature("v1");
      variables.set("initmethod", "sol");
      variables.set("initsol", "sol76");
      variables.set("solnum", "last");
      variables.set("notsolmethod", "sol");
      variables.set("notsol", "sol76");
      variables.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("maxiter", 500);
      System.out.println("RUN_QNEGATIVE40=" + solution);
      model.sol(solution).runAll();
      model.save("557s2b_stage555_alpha0p825_qref_negative_scan.mph");
      System.out.println("STAGE555_QNEGATIVE40_PASS solution=" + solution);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
