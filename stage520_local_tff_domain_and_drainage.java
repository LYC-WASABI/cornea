import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage520_local_tff_domain_and_drainage {
  static final String BASE =
      "513_stage510_local_film_track_checked.mph";
  static final String INPUT =
      "520_stage520_local_tff_input.mph";
  static final String SETUP =
      "521_stage520_local_tff_drainage_setup.mph";
  static final String RESULTS =
      "522_stage520_local_tff_drainage_results.mph";
  static final String CHECKED =
      "523_stage520_local_tff_drainage_checked.mph";

  static void require(boolean condition, String message) {
    if (!condition) throw new IllegalStateException(message);
  }

  static void removeFeature(Model model, String tag) {
    try { model.component("comp1").physics("tff").feature().remove(tag); }
    catch (Exception ignored) {}
  }

  static void createZeroPressureBorder(
      Model model, String tag, String label, String selection) {
    removeFeature(model, tag);
    model.component("comp1").physics("tff").create(tag, "Border", 1);
    model.component("comp1").physics("tff").feature(tag).label(label);
    model.component("comp1").physics("tff").feature(tag)
        .selection().named(selection);
    model.component("comp1").physics("tff").feature(tag)
        .set("BorderCondition", "ZeroPressure");
    model.component("comp1").physics("tff").feature(tag).set("pf0", "0[Pa]");
    model.component("comp1").physics("tff").feature(tag).set("theta_0", "0.6");
  }

  static int[] entities(Model model, String selection, int dimension) {
    return model.component("comp1").selection(selection).entities(dimension);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      String comp = "comp1";

      require(Math.abs(model.param().evaluate("stage500_revision") - 500)
          < 0.1, "Stage 500 dependency is missing");
      require(entities(model, "sel_film_track", 2).length >= 1,
          "Stage 510 local film track is missing");
      require(entities(model, "sel_film_edges_all", 1).length >= 4,
          "Stage 510 film-track edges are incomplete");
      model.save(INPUT);

      model.param().set(
          "stage520_revision", "520",
          "Local Thin-Film Flow domain and drainage stage");
      model.param().set(
          "pfilm_ambient520", "0[Pa]",
          "Gauge pressure at local film inlet, outlet, and lateral drains");
      model.param().set(
          "theta_feed520", "0.6",
          "Initial JFO fractional content used at zero-pressure borders");

      try { model.component(comp).physics().remove("tff"); }
      catch (Exception ignored) {}
      model.component(comp).physics().create(
          "tff", "ThinFilmFlowShell", "geom1");
      model.component(comp).physics("tff").label(
          "Stage 520 local tear-film JFO flow");
      model.component(comp).physics("tff")
          .selection().named("sel_film_track");
      model.component(comp).physics("tff").prop("EquationType").set(
          "EquationType", "ReynoldsEquationWithCavitation");
      model.component(comp).physics("tff").prop("EquationType").set(
          "sftransition", "p_cav_transition195");
      model.component(comp).physics("tff").feature("ffp1")
          .set("hw1", "h_jfo197");
      model.component(comp).physics("tff").feature("ffp1")
          .set("hb1", "0");
      model.component(comp).physics("tff").feature("ffp1")
          .set("TangentialBaseVelocity", "Off");
      model.component(comp).physics("tff").feature("ffp1")
          .set("TangentialWallVelocity", "userdef");
      model.component(comp).physics("tff").feature("ffp1")
          .set("vw", new String[] {"vwall_x", "vwall_y", "vwall_z"});
      model.component(comp).physics("tff").feature("ffp1")
          .set("ForceModel", "PressureAndShear");
      model.component(comp).physics("tff").feature("ffp1")
          .set("mure_mat", "userdef");
      model.component(comp).physics("tff").feature("ffp1")
          .set("mure", "eta_tear");
      model.component(comp).physics("tff").feature("ffp1")
          .set("rho_mat", "userdef");
      model.component(comp).physics("tff").feature("ffp1")
          .set("rho", "rho_tear");
      model.component(comp).physics("tff").feature("ffp1")
          .set("UseCompressibilityForDensity", "CompressibilityForm");
      model.component(comp).physics("tff").feature("ffp1")
          .set("rho_c", "rho_tear");
      model.component(comp).physics("tff").feature("ffp1")
          .set("beta", "beta_tear195");
      model.component(comp).physics("tff").feature("init1")
          .set("pfilm", "pfilm_ambient520");

      createZeroPressureBorder(
          model, "bdr_inlet520", "Stage 520 inlet: zero gauge pressure",
          "sel_film_inlet");
      createZeroPressureBorder(
          model, "bdr_outlet520", "Stage 520 outlet: zero gauge pressure",
          "sel_film_outlet");
      createZeroPressureBorder(
          model, "bdr_left520", "Stage 520 left lateral drainage",
          "sel_film_side_left");
      createZeroPressureBorder(
          model, "bdr_right520", "Stage 520 right lateral drainage",
          "sel_film_side_right");
      for (String tag : new String[] {
          "bdr_inlet520", "bdr_outlet520",
          "bdr_left520", "bdr_right520"
      }) {
        model.component(comp).physics("tff").feature(tag)
            .set("pf0", "pfilm_ambient520");
        model.component(comp).physics("tff").feature(tag)
            .set("theta_0", "theta_feed520");
      }

      model.component(comp).physics("tff").feature("dcont1")
          .set("pairSelection", "list");
      model.component(comp).physics("tff").feature("dcont1")
          .set("pairs", new String[] {});
      model.component(comp).cpl("intop_film")
          .selection().named("sel_film_track");

      model.label("Stage 520 local TFF domain and drainage setup");
      model.save(SETUP);

      model.component(comp).geom("geom1").run();
      model.component(comp).mesh("mesh1").run();
      model.label("Stage 520 local TFF domain and drainage results");
      model.save(RESULTS);

      int[] tffDomain =
          model.component(comp).physics("tff").selection().entities();
      int[] ffpDomain = model.component(comp).physics("tff")
          .feature("ffp1").selection().entities();
      int[] initDomain = model.component(comp).physics("tff")
          .feature("init1").selection().entities();
      int[] filmIntegral =
          model.component(comp).cpl("intop_film").selection().entities();
      System.out.println("TFF DOMAIN=" + Arrays.toString(tffDomain));
      System.out.println("FFP DOMAIN=" + Arrays.toString(ffpDomain));
      System.out.println("INIT DOMAIN=" + Arrays.toString(initDomain));
      System.out.println("INTOP_FILM=" + Arrays.toString(filmIntegral));
      require(Arrays.equals(tffDomain, entities(model, "sel_film_track", 2)),
          "TFF root selection is not the local track");
      require(Arrays.equals(ffpDomain, tffDomain),
          "Fluid Film Properties selection is inconsistent");
      require(Arrays.equals(initDomain, tffDomain),
          "TFF initial-values selection is inconsistent");
      require(Arrays.equals(filmIntegral, tffDomain),
          "intop_film is not restricted to the local track");

      String[][] checks = {
        {"bdr_inlet520", "sel_film_inlet"},
        {"bdr_outlet520", "sel_film_outlet"},
        {"bdr_left520", "sel_film_side_left"},
        {"bdr_right520", "sel_film_side_right"}
      };
      for (String[] check : checks) {
        int[] actual = model.component(comp).physics("tff")
            .feature(check[0]).selection().entities();
        int[] expected = entities(model, check[1], 1);
        System.out.println(check[0] + "=" + Arrays.toString(actual));
        require(actual.length >= 1 && Arrays.equals(actual, expected),
            check[0] + " does not select its named edge group");
        require("ZeroPressure".equals(model.component(comp).physics("tff")
            .feature(check[0]).getString("BorderCondition")),
            check[0] + " is not a zero-pressure border");
      }
      require("list".equals(model.component(comp).physics("tff")
          .feature("dcont1").getString("pairSelection")),
          "dcont1 must use an explicit pair list");
      require(model.component(comp).physics("tff").feature("dcont1")
          .getStringArray("pairs").length == 0,
          "dcont1 pair list must be empty");
      require(Arrays.asList(model.component(comp).pair().tags())
          .contains("cp_lid_cornea"), "Contact pair was lost");
      require(Arrays.asList(model.component(comp).pair().tags())
          .contains("ap1"), "Identity mapping pair ap1 was lost");
      System.out.println("CONTACT PAIRS="
          + Arrays.toString(model.component(comp).pair().tags()));
      System.out.println("PARENT_STAGE="
          + model.param().evaluate("stage500_revision"));
      System.out.println("CURRENT_STAGE="
          + model.param().evaluate("stage520_revision"));
      System.out.println("STAGE520 CHECK=PASS");

      model.label("Stage 520 local TFF domain and drainage checked");
      model.save(CHECKED);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
