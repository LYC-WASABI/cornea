import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage159_final_minus62_to_minus70 {
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
    for (int i = 0; i <= 40; i++) {
      if (s.length() > 0) s.append(" ");
      s.append(String.format(Locale.US, "%.1f", -62.0 - 0.2 * i));
    }
    return s.toString();
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "stage158_skip_isolated_minus52p4_output_Model.mph");
      String c = "comp1", ge = "ge_force_total111", study = "std_final159";
      try { m.study().remove(study); } catch (Exception ignore) {}
      m.study().create(study);
      m.study(study).label("Stage 159 final -62 to -70 deg");
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
      m.study(study).feature("stat").set("initsol", "sol29");
      m.study(study).feature("stat").set("initsoluse", "sol29");
      m.study(study).feature("stat").set("initsolusesolnum", 46);
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
      s1.feature("fc1").set("maxiter", 260);
      m.save("286_lid8mm_stage159_final_minus62_to_minus70_setup_Model.mph");
      System.out.println("RUN_STAGE159 solver=" + sol);
      m.sol(sol).runAll();
      m.result().dataset().create("dset159", "Solution");
      m.result().dataset("dset159").set("solution", sol);
      m.result().numerical().create("eval159", "EvalGlobal");
      m.result().numerical("eval159").set("data", "dset159");
      m.result().numerical("eval159").set("expr", new String[]{
          "phi_qs142", "Fn_contact119", "Fn_total119", "Fn_error119",
          "dr_indent119", "F_film_physical154", "F_boundary_physical154",
          "F_friction_physical154", "mu_physical154"
      });
      double[][] a = m.result().numerical("eval159").getReal();
      System.out.println("ROWS=" + a[0].length);
      int j = a[0].length - 1;
      System.out.printf(Locale.US,
          "LAST phi=%.8g Fc=%.8g Ft=%.8g err=%.8g d=%.8g Ffilm=%.8g Fb=%.8g Ff=%.8g mu=%.8g%n",
          a[0][j], a[1][j], a[2][j], a[3][j], a[4][j],
          a[5][j], a[6][j], a[7][j], a[8][j]);
      m.save("287_lid8mm_stage159_physical_shear_fullpath_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
