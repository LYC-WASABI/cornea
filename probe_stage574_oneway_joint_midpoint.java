import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_oneway_joint_midpoint {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574a_stage574_midpoint_source_jfo_setup.mph");
      ModelNode comp = model.component("comp1");
      try { model.sol().remove("sol95"); } catch (Exception ignored) {}
      try { model.study().remove("std574_mid_source_jfo"); }
      catch (Exception ignored) {}
      model.param().set("phi_qs574", "-35[deg]");
      model.param().set("lambda_v574", "1e-4");
      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "Y*(cos(phi_qs574)-1)-Z*sin(phi_qs574)"
                + "-dr_indent570*(Y*cos(phi_qs574)-Z*sin(phi_qs574))"
                + "/sqrt(Y^2+Z^2)",
            "Y*sin(phi_qs574)+Z*(cos(phi_qs574)-1)"
                + "-dr_indent570*(Y*sin(phi_qs574)+Z*cos(phi_qs574))"
                + "/sqrt(Y^2+Z^2)"
          });
      comp.physics("tff").feature("ffp1").set(
          "hw1", "nojac(h_true573)");
      comp.physics("tff").feature("ffp1").set(
          "vw", new String[] {
            "0", "-lambda_v574*omega_qs574*Z",
            "lambda_v574*omega_qs574*Y"
          });
      comp.physics("tff").feature("init1").set("pfilm", "0[Pa]");
      String study = "std574_joint_probe";
      model.study().create(study);
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat").set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate", new String[] {
            "solid", "on", "ge_force_total111", "on", "tff", "on",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol94");
      model.study(study).feature("stat").set("initsoluse", "current");
      String step = study + "/stat";
      for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
        comp.physics("solid").feature(tag).set("StudyStep", step);
      }
      comp.physics("ge_force_total111").feature("ge1")
          .set("StudyStep", step);
      for (String tag : comp.physics("tff").feature().tags()) {
        try { comp.physics("tff").feature(tag).set("StudyStep", step); }
        catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol94");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol94");
      dependent.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      SolverFeature segregated = stationary.feature("se1");
      segregated.feature("ss1").label(
          "Structure displacement and total-load indentation");
      segregated.feature("ss1").set(
          "segvar", new String[] {"comp1_u", "comp1_ODE1"});
      segregated.feature("ss1").set(
          "segcomp", new String[] {
            "comp1.u", "comp1.v", "comp1.w",
            "comp1.q_force_total111"
          });
      segregated.feature("ss1").set("linsolver", "d2");
      segregated.feature("ss2").label("Source-side JFO pressure");
      segregated.feature("ss2").set(
          "segvar", new String[] {"comp1_pfilm"});
      segregated.feature("ss2").set(
          "segcomp", new String[] {"comp1.pfilm"});
      segregated.feature("ss2").set("linsolver", "d1");
      System.out.println("RUN=" + solution);
      model.sol(solution).runAll();
      System.out.println("ONEWAY_JOINT=PASS");
      model.save("probe_stage574_oneway_joint_midpoint.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
