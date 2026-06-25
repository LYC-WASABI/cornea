import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage500_baseline_freeze_audit {
  static final String BASE =
      "385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph";
  static final String INPUT =
      "500_stage500_baseline_input.mph";
  static final String SETUP =
      "501_stage500_baseline_freeze_setup.mph";
  static final String RESULTS =
      "502_stage500_baseline_audit_results.mph";
  static final String CHECKED =
      "503_stage500_baseline_checked.mph";

  static boolean contains(String[] values, String wanted) {
    return Arrays.asList(values).contains(wanted);
  }

  static void require(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      String comp = "comp1";

      model.save(INPUT);

      model.param().set(
          "stage500_revision", "500",
          "Frozen baseline stage number; metadata only");
      model.param().set(
          "stage500_target_normal_load", "0.03[N]",
          "Target total lid normal load retained from Stage 200");
      model.param().set(
          "stage500_film_reference_thickness", "3[um]",
          "Reference tear-film thickness retained for later local-track work");
      model.label("Stage 500 frozen Stage 200 baseline");
      model.save(SETUP);

      try { model.result().numerical().remove("eval500_audit"); }
      catch (Exception ignored) {}
      model.result().numerical().create("eval500_audit", "EvalGlobal");
      model.result().numerical("eval500_audit").label(
          "Stage 500 frozen-baseline audit");
      model.result().numerical("eval500_audit").set("data", "dset200s");
      model.result().numerical("eval500_audit").set(
          "expr", new String[] {
            "withsol('sol48',intop_film(h_jfo197)/intop_film(1))",
            "withsol('sol48',intop_film(tff.p))",
            "withsol('sol48',intop_film(tff.theta)/intop_film(1))",
            "Fn_contact119",
            "Wfilm199",
            "Ftotal199",
            "F_total_target",
            "FshearFilm199",
            "FshearFilm199/Ftotal199"
          });
      model.result().numerical("eval500_audit").set(
          "unit", new String[] {
            "um", "N", "1", "N", "N", "N", "N", "N", "1"
          });
      model.result().numerical("eval500_audit").set(
          "descr", new String[] {
            "Mean effective film thickness",
            "Integrated film pressure load",
            "Mean JFO fractional film content",
            "Solid contact normal load",
            "Stage 200 film normal load",
            "Total normal load",
            "Target normal load",
            "Film shear force",
            "Film-only friction coefficient"
          });
      double[][] audit =
          model.result().numerical("eval500_audit").getReal();
      for (int i = 0; i < audit.length; i++) {
        System.out.printf(Locale.US, "AUDIT500[%d]=%.12g%n", i, audit[i][0]);
      }
      model.label("Stage 500 frozen baseline audit results");
      model.save(RESULTS);

      require(contains(model.component(comp).physics().tags(), "solid"),
          "Solid Mechanics missing");
      require(contains(model.component(comp).physics().tags(), "tff"),
          "Thin-Film Flow missing");
      require(contains(model.component(comp).pair().tags(), "cp_lid_cornea"),
          "Explicit lid-cornea contact pair missing");
      require(contains(model.study().tags(), "std_jfofilm199"),
          "Stage 200 film study std_jfofilm199 missing");
      require(contains(model.study().tags(), "std_jfobalance199"),
          "Stage 200 structural study std_jfobalance199 missing");
      require(contains(model.sol().tags(), "sol48"),
          "Stage 200 film solution sol48 missing");
      require(contains(model.sol().tags(), "sol49"),
          "Stage 200 structural solution sol49 missing");
      require(model.component(comp).physics("solid")
          .selection().entities().length == 2,
          "Solid Mechanics must retain two solid domains");
      require(model.component(comp).physics("tff")
          .selection().entities().length == 4,
          "Stage 200 TFF must retain four whole-cornea boundaries");
      require(Math.abs(audit[5][0] - audit[6][0]) < 5e-5,
          "Frozen total normal load is not close to target");
      System.out.println("GEOM=" + model.component(comp).geom("geom1")
          .feature("fin").getString("buildmessage"));
      System.out.println("PHYSICS="
          + Arrays.toString(model.component(comp).physics().tags()));
      System.out.println("PAIRS="
          + Arrays.toString(model.component(comp).pair().tags()));
      System.out.println("STUDIES=" + Arrays.toString(model.study().tags()));
      System.out.println("SOLUTIONS=" + Arrays.toString(model.sol().tags()));
      System.out.println("STAGE500 CHECK=PASS");

      model.label("Stage 500 frozen baseline checked");
      model.save(CHECKED);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
