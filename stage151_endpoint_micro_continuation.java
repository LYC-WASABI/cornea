import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage151_endpoint_micro_continuation {
  private static String newest(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "268_lid8mm_stage150_verified_normal_path_Model.mph");
      String c = "comp1", ge = "ge_force_total111";
      String study = "std_endpoint151";

      try { m.study().remove(study); } catch (Exception ignore) {}
      m.study().create(study);
      m.study(study).label("Stage 151 endpoint micro-continuation");
      m.study(study).create("param", "Parametric");
      m.study(study).feature("param").set("pname", new String[]{"phi_qs142"});
      m.study(study).feature("param").set("plistarr",
          new String[]{"-69.45 -69.5 -69.55 -69.6 -69.65 -69.7 -69.75 -69.8 -69.85 -69.9 -69.95 -70"});
      m.study(study).feature("param").set("punit", new String[]{"deg"});
      m.study(study).create("stat", "Stationary");
      m.study(study).feature("stat").set("geometricNonlinearity", "on");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      m.study(study).feature("stat").set("useinitsol", "on");
      m.study(study).feature("stat").set("initmethod", "sol");
      m.study(study).feature("stat").set("initsol", "sol23");
      m.study(study).feature("stat").set("initsoluse", "sol23");
      m.study(study).feature("stat").set("initsolusesolnum", "last");

      String step = study + "/stat";
      m.component(c).physics("solid").feature("dcnt1").set("StudyStep", step);
      m.component(c).physics("solid").feature("disp_lid_time").set("StudyStep", step);
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set("StudyStep", step);
      m.component(c).physics(ge).feature("ge1").set("StudyStep", step);

      String[] before = m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol = newest(m, before);
      SolverFeature s1 = m.sol(sol).feature("s1");
      try { s1.feature().remove("se1"); } catch (Exception ignore) {}
      try { s1.feature().remove("fc1"); } catch (Exception ignore) {}
      s1.create("fc1", "FullyCoupled");
      s1.feature("fc1").set("linsolver", "dDef");
      s1.feature("fc1").set("maxiter", 200);
      m.save("269_lid8mm_stage151_endpoint_micro_continuation_setup_Model.mph");
      System.out.println("RUN_STAGE151 solver=" + sol);
      m.sol(sol).runAll();

      try { m.result().dataset().remove("dset151"); } catch (Exception ignore) {}
      m.result().dataset().create("dset151", "Solution");
      m.result().dataset("dset151").set("solution", sol);
      try { m.result().numerical().remove("eval151"); } catch (Exception ignore) {}
      m.result().numerical().create("eval151", "EvalGlobal");
      m.result().numerical("eval151").set("data", "dset151");
      m.result().numerical("eval151").set("expr", new String[]{
          "phi_qs142", "Fn_contact119", "Fn_film119", "Fn_total119",
          "Fn_error119", "dr_indent119"
      });
      double[][] a = m.result().numerical("eval151").getReal();
      String[] n = {"phi", "Fcontact", "Ffilm", "Ftotal", "err", "indent"};
      for (int j = 0; j < a[0].length; j++) {
        StringBuilder b = new StringBuilder("row=" + j);
        for (int i = 0; i < a.length; i++)
          b.append(" ").append(n[i]).append("=")
              .append(String.format(Locale.US, "%.9g", a[i][j]));
        System.out.println(b);
      }
      m.save("270_lid8mm_stage151_endpoint_micro_continuation_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
