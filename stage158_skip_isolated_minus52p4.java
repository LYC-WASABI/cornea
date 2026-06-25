import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage158_skip_isolated_minus52p4 {
  private static String newest(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  private static String angles() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i <= 87; i++) {
      if (s.length() > 0) s.append(" ");
      s.append(String.format(Locale.US, "%.1f", -52.6 - 0.2 * i));
    }
    return s.toString();
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "stage157_micro_minus51_to_minus70_output_Model.mph");
      String c = "comp1", ge = "ge_force_total111";
      String study = "std_skip158";
      try { m.study().remove(study); } catch (Exception ignore) {}
      m.study().create(study);
      m.study(study).label("Stage 158 skip isolated -52.4 deg branch");
      m.study(study).create("param", "Parametric");
      m.study(study).feature("param").set("pname", new String[]{"phi_qs142"});
      m.study(study).feature("param").set("plistarr", new String[]{angles()});
      m.study(study).feature("param").set("punit", new String[]{"deg"});
      m.study(study).create("stat", "Stationary");
      m.study(study).feature("stat").set("geometricNonlinearity", "on");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study(study).feature("stat").set("useinitsol", "on");
      m.study(study).feature("stat").set("initmethod", "sol");
      m.study(study).feature("stat").set("initsol", "sol28");
      m.study(study).feature("stat").set("initsoluse", "sol28");
      m.study(study).feature("stat").set("initsolusesolnum", 7);
      String step = study + "/stat";
      for (String tag : new String[]{
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm",
          "load_shear_cornea73", "load_shear_lid73"})
        m.component(c).physics("solid").feature(tag).set("StudyStep", step);
      m.component(c).physics(ge).feature("ge1").set("StudyStep", step);
      String[] before = m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol = newest(m, before);
      SolverFeature s1 = m.sol(sol).feature("s1");
      try { s1.feature().remove("se1"); } catch (Exception ignore) {}
      try { s1.feature().remove("fc1"); } catch (Exception ignore) {}
      s1.create("fc1", "FullyCoupled");
      s1.feature("fc1").set("linsolver", "dDef");
      s1.feature("fc1").set("maxiter", 240);
      m.save("284_lid8mm_stage158_skip_minus52p4_setup_Model.mph");
      System.out.println("RUN_STAGE158 solver=" + sol);
      m.sol(sol).runAll();
      m.result().dataset().create("dset158", "Solution");
      m.result().dataset("dset158").set("solution", sol);
      m.result().numerical().create("eval158", "EvalGlobal");
      m.result().numerical("eval158").set("data", "dset158");
      m.result().numerical("eval158").set("expr", new String[]{
          "phi_qs142", "Fn_contact119", "Fn_total119", "Fn_error119",
          "dr_indent119", "F_friction_physical154", "mu_physical154"
      });
      double[][] a = m.result().numerical("eval158").getReal();
      System.out.println("ROWS=" + a[0].length);
      System.out.printf(Locale.US,
          "LAST phi=%.8g Fc=%.8g Ft=%.8g err=%.8g d=%.8g Ff=%.8g mu=%.8g%n",
          a[0][a[0].length-1], a[1][a[0].length-1],
          a[2][a[0].length-1], a[3][a[0].length-1],
          a[4][a[0].length-1], a[5][a[0].length-1],
          a[6][a[0].length-1]);
      m.save("285_lid8mm_stage158_physical_shear_complete_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
