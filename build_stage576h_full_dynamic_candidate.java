import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576h_full_dynamic_candidate {
  private static final String BASE = "576g2_stage576_velocity_threshold_refined_checked.mph";
  private static final String SETUP = "576h_stage576_full_dynamic_candidate_setup.mph";
  private static final String RESULTS = "576h_stage576_full_dynamic_candidate_results.mph";
  private static final String CHECKED = "576h_stage576_full_dynamic_candidate_checked.mph";
  private static final boolean RUN_SAFE_SPEED = true;
  private static final String SAFE_SETUP = "576h2_stage576_full_dynamic_safe_speed_setup.mph";
  private static final String SAFE_RESULTS = "576h2_stage576_full_dynamic_safe_speed_results.mph";
  private static final String SAFE_CHECKED = "576h2_stage576_full_dynamic_safe_speed_checked.mph";
  private static final String SOLID_SOL = "sol298";
  private static final String PRESSURE_INIT = "sol119";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static double min(double[] values) {
    double out = Double.POSITIVE_INFINITY;
    for (double value : values) out = Math.min(out, value);
    return out;
  }

  private static double max(double[] values) {
    double out = Double.NEGATIVE_INFINITY;
    for (double value : values) out = Math.max(out, value);
    return out;
  }

  private static boolean finite(double[] values) {
    for (double value : values) if (!Double.isFinite(value)) return false;
    return true;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      String setupFile = RUN_SAFE_SPEED ? SAFE_SETUP : SETUP;
      String resultsFile = RUN_SAFE_SPEED ? SAFE_RESULTS : RESULTS;
      String checkedFile = RUN_SAFE_SPEED ? SAFE_CHECKED : CHECKED;
      model.param().set("q_scale574", "-9");
      model.param().set("v_blink_avg", RUN_SAFE_SPEED ? "0.03[m/s]" : "0.04[m/s]");
      model.param().set("alpha_pfb576e", "0");
      model.param().set("lambda_h574", "1");
      model.param().set("lambda_v574", "1");
      comp.physics("ge_force_total111").active(false);
      try { comp.physics("solid").feature("load_pfilm576e").active(false); } catch (Exception ignored) {}
      comp.variable("var_dynamic_motion572").set("tau572", "t");
      String study = "std576h_full_dynamic_candidate";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label("Stage 576h full dynamic candidate at 0.04 m/s q=-9");
      model.study(study).create("time", "Transient");
      model.study(study).feature("time").set("tlist", "range(T_pre572,T_slide572/200,T_pre572+T_slide572)");
      model.study(study).feature("time").set("activate", new String[] {
        "solid", "off", "ge_force_total111", "off", "tff", "on",
        "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
      });
      model.study(study).feature("time").set("useinitsol", "on");
      model.study(study).feature("time").set("initmethod", "sol");
      model.study(study).feature("time").set("initsol", PRESSURE_INIT);
      model.study(study).feature("time").set("initsoluse", "current");
      model.study(study).feature("time").set("initsolusesolnum", "last");
      String step = study + "/time";
      for (String tag : comp.physics("tff").feature().tags()) {
        try { comp.physics("tff").feature(tag).set("StudyStep", step); } catch (Exception ignored) {}
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String sol = newest(model, before);
      SolverFeature dep = model.sol(sol).feature("v1");
      dep.set("initmethod", "sol");
      dep.set("initsol", PRESSURE_INIT);
      dep.set("solnum", "last");
      dep.set("notsolmethod", "sol");
      dep.set("notsol", SOLID_SOL);
      dep.set("notsolnum", "last");
      SolverFeature time = model.sol(sol).feature("t1");
      time.set("tlist", "range(T_pre572,T_slide572/200,T_pre572+T_slide572)");
      time.set("consistent", "off");
      time.set("initialstepbdfactive", "on");
      time.set("initialstepbdf", "1e-8");
      time.set("maxstepconstraintbdf", "const");
      time.set("maxstepbdf", "T_slide572/200");
      if (!has(time.feature().tags(), "fc1")) {
        for (String tag : time.feature().tags()) if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
        time.create("fc1", "FullyCoupled");
      }
      time.feature("fc1").set("linsolver", "dDef");
      time.feature("fc1").set("damp", "0.5");
      time.feature("fc1").set("maxiter", 150);
      model.label(RUN_SAFE_SPEED ? "Stage 576h2 full dynamic safe-speed setup" : "Stage 576h full dynamic candidate setup");
      model.save(setupFile);
      System.out.println("RUN_SOL=" + sol);
      model.sol(sol).runAll();
      model.save(resultsFile);
      String data = "dset576h_full";
      try { model.result().dataset().remove(data); } catch (Exception ignored) {}
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", sol);
      String eval = "eval576h_global";
      try { model.result().numerical().remove(eval); } catch (Exception ignored) {}
      model.result().numerical().create(eval, "EvalGlobal");
      model.result().numerical(eval).set("data", data);
      model.result().numerical(eval).set("expr", new String[] {"t"});
      double[] times = model.result().numerical(eval).getReal()[0];
      String integral = "int576h_film";
      try { model.result().numerical().remove(integral); } catch (Exception ignored) {}
      model.result().numerical().create(integral, "IntSurface");
      model.result().numerical(integral).set("data", data);
      model.result().numerical(integral).selection().named("sel_local_cornea_patch574");
      model.result().numerical(integral).set("expr", new String[] {"p_load573", "M_core573", "tff.theta", "1"});
      double[][] patch = model.result().numerical(integral).getReal();
      double area = patch[3][0];
      double fContact = 0.0255953907551;
      double[] fFilm = patch[0];
      double[] fTotal = new double[fFilm.length];
      double[] meanCore = new double[fFilm.length];
      double[] meanTheta = new double[fFilm.length];
      int outside = 0;
      int peakIndex = 0;
      for (int i = 0; i < fFilm.length; i++) {
        fTotal[i] = fContact + fFilm[i];
        meanCore[i] = patch[1][i]/area;
        meanTheta[i] = patch[2][i]/area;
        if (fTotal[i] < 0.025 || fTotal[i] > 0.035) outside++;
        if (fTotal[i] > fTotal[peakIndex]) peakIndex = i;
      }
      String minThetaTag = "min576h_theta";
      model.result().numerical().create(minThetaTag, "MinSurface");
      model.result().numerical(minThetaTag).set("data", data);
      model.result().numerical(minThetaTag).selection().named("sel_local_cornea_patch574");
      model.result().numerical(minThetaTag).set("expr", "tff.theta");
      double[] minTheta = model.result().numerical(minThetaTag).getReal()[0];
      String maxPTag = "max576h_p";
      model.result().numerical().create(maxPTag, "MaxSurface");
      model.result().numerical(maxPTag).set("data", data);
      model.result().numerical(maxPTag).selection().named("sel_local_cornea_patch574");
      model.result().numerical(maxPTag).set("expr", "tff.p-p_amb573");
      double[] maxP = model.result().numerical(maxPTag).getReal()[0];
      boolean stable = finite(fFilm) && finite(fTotal) && finite(meanCore) && finite(meanTheta)
          && finite(minTheta) && finite(maxP) && min(minTheta) >= -1e-8;
      boolean pass = stable && outside == 0;
      System.out.printf(Locale.US, "TIME_RANGE=[%.12g,%.12g] COUNT=%d%n", min(times), max(times), times.length);
      System.out.printf(Locale.US, "FCONTACT=%.12g FFILM_RANGE=[%.12g,%.12g] FTOTAL_RANGE=[%.12g,%.12g] OUTSIDE=%d%n",
          fContact, min(fFilm), max(fFilm), min(fTotal), max(fTotal), outside);
      System.out.printf(Locale.US, "PEAK time=%.12g fraction=%.6f Ffilm=%.12g Ftotal=%.12g MeanCore=%.12g%n",
          times[peakIndex], (times[peakIndex]-times[0])/(times[times.length-1]-times[0]), fFilm[peakIndex], fTotal[peakIndex], meanCore[peakIndex]);
      System.out.printf(Locale.US, "MINTHETA_RANGE=[%.12g,%.12g] MAXP_RANGE=[%.12g,%.12g]%n",
          min(minTheta), max(minTheta), min(maxP), max(maxP));
      System.out.println("CHECKED_STATUS=" + (pass ? "PASS" : "FAIL"));
      if (pass) {
        model.label(RUN_SAFE_SPEED ? "Stage 576h2 full dynamic safe-speed checked" : "Stage 576h full dynamic candidate checked");
        model.save(checkedFile);
        System.out.println("SAVED_CHECKED=" + checkedFile);
      }
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
