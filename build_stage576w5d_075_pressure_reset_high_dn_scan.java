import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576w5d_075_pressure_reset_high_dn_scan {
  private static final String INPUT =
      "576w4z_stage576_0625_schedule_confirmation_checked.mph";
  private static final String RESULTS =
      "576w5d_stage576_075_pressure_reset_high_dn_scan_results.mph";
  private static final String CHECKED =
      "576w5d_stage576_075_pressure_reset_high_dn_scan_checked.mph";
  private static final String SWEPT = "sel_film_swept571";
  private static final String PATCH = "sel_local_cornea_patch574";
  private static final String PAIR_SOURCE = "sel576w5d_pair_source";
  private static final String PAIR_DESTINATION = "sel576w5d_pair_destination";
  private static final String BASE_SOLID = "sol282";
  private static final String RESET_PRESSURE_0625 = "sol493";
  private static final String RESET_SOLID_0625 = "sol494";
  private static final String REUSE_PRESSURE_0625 = "sol503";
  private static final String REUSE_SOLID_0625 = "sol504";
  private static final String P_POS = "max(tff.p-p_amb573,0[Pa])";
  private static final String B_HIGH =
      "(0.5*(1-tanh((g_pair_safe573-20[um])/5[um])))";
  private static final String BASE_FACTORS =
      "(g_pair_valid573*B_low573*" + B_HIGH + ")";
  private static final String B_PRESS =
      "(M_core573*" + BASE_FACTORS + ")";
  private static final String B_WALL = "(M_drain573*Bfilm573)";
  private static final String SUPPORT =
      "(" + B_PRESS + "*min(" + P_POS + ",6.5[kPa]))";
  private static String pairSourceGap = "";
  private static String pairDestinationGap = "";
  private static final String DST2SRC = "dst2src_cp_lid_cornea";
  private static final double[] STARTS = new double[] {0.625, 0.6875};
  private static final double[] TARGETS = new double[] {0.6875, 0.750};
  private static final String[] SEGMENTS = new String[] {"06875", "0750"};
  private static final double[] DN_SCAN_UM = new double[] {22.0, 23.0, 24.0};
  private static final double SCHEDULE_DN_UM = 20.0;

  private static final double LOAD_PASS_LO = 0.030;
  private static final double LOAD_PASS_HI = 0.033;
  private static final double LOAD_HARD_LO = 0.028;
  private static final double LOAD_HARD_HI = 0.035;
  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static boolean sameEntities(int[] a, int[] b) {
    int[] aa = a.clone();
    int[] bb = b.clone();
    Arrays.sort(aa);
    Arrays.sort(bb);
    return Arrays.equals(aa, bb);
  }

  private static boolean tffSelectionEqualsSwept(ModelNode comp) {
    try {
      return sameEntities(
          comp.physics("tff").selection().entities(),
          comp.selection(SWEPT).entities(2));
    } catch (Exception ignored) {
      return false;
    }
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static void removeStudy(Model model, String tag) {
    try { model.study().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static String dataset(Model model, String tag, String sol) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", sol);
    return tag;
  }

  private static double surface(
      Model model, String data, String tag, String type, String expr, String selection) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double global(Model model, String data, String tag, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double avgSurface(
      Model model, String data, String tag, String expr, String selection) {
    double area = surface(model, data, tag + "_area", "IntSurface", "1", selection);
    return div(surface(model, data, tag + "_int", "IntSurface", expr, selection), area);
  }

  private static double div(double numerator, double denominator) {
    if (!Double.isFinite(numerator) || !Double.isFinite(denominator)
        || Math.abs(denominator) < 1e-30) return Double.NaN;
    return numerator / denominator;
  }

  private static String csv(double value) {
    if (Double.isNaN(value)) return "NaN";
    if (Double.isInfinite(value)) return value > 0 ? "Infinity" : "-Infinity";
    return String.format(Locale.US, "%.12g", value);
  }

  private static String safeTag(String text) {
    return text.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "_");
  }

  private static double slideAt(double fraction) {
    return 0.5 - 0.5 * Math.cos(Math.PI * fraction);
  }

  private static double thetaPhysicalDegrees(double fraction) {
    return -35.0 + 70.0 * slideAt(fraction);
  }

  private static String thetaSpatialExpr(double fraction) {
    return String.format(Locale.US, "(%.16g[deg]+lid_mask_aoffset572)",
        thetaPhysicalDegrees(fraction));
  }

  private static String coreExpr(double fraction) {
    String theta = thetaSpatialExpr(fraction);
    return "(M_lid_x572*0.25*(1+tanh((theta_surface572-(" + theta
        + ")+lid_mask_ahalf572)/lid_mask_epsa572))"
        + "*(1+tanh(((" + theta + ")+lid_mask_ahalf572"
        + "-theta_surface572)/lid_mask_epsa572)))";
  }

  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static void ensurePairSelections(ModelNode comp) {
    Pair pair = comp.pair("cp_lid_cornea");
    pairSourceGap = pair.gapName(false);
    pairDestinationGap = pair.gapName(true);
    removeSelection(comp, PAIR_SOURCE);
    comp.selection().create(PAIR_SOURCE, "Explicit");
    comp.selection(PAIR_SOURCE).label("Stage 576w5d contact pair source surface");
    comp.selection(PAIR_SOURCE).geom("geom1", 2);
    comp.selection(PAIR_SOURCE).set(pair.source().entities());
    removeSelection(comp, PAIR_DESTINATION);
    comp.selection().create(PAIR_DESTINATION, "Explicit");
    comp.selection(PAIR_DESTINATION).label("Stage 576w5d contact pair destination surface");
    comp.selection(PAIR_DESTINATION).geom("geom1", 2);
    comp.selection(PAIR_DESTINATION).set(pair.destination().entities());
  }

  private static String bpressExpr(boolean useCore0100) {
    if (!useCore0100) return "Bpress576w5d_actual";
    return "Bpress576w5d_core0100";
  }

  private static String supportExpr(boolean useCore0100) {
    if (!useCore0100) return "p_support576w5d_actual";
    return "p_support576w5d_core0100";
  }

  private static String classify(double total, double maxP, double minTheta,
      double lowTheta02, double avgH, double maxTn, double activeContactRatio,
      double tnAreaGt100k, double tnLoadFracGt100k, double avgTnActive,
      boolean tffLocal) {
    if (!tffLocal || !Double.isFinite(total) || !Double.isFinite(maxP)
        || !Double.isFinite(minTheta) || !Double.isFinite(lowTheta02)
        || !Double.isFinite(avgH) || !Double.isFinite(maxTn)
        || !Double.isFinite(tnAreaGt100k)
        || !Double.isFinite(tnLoadFracGt100k)) {
      return "FAIL";
    }
    if (avgH < 2.5e-6 || avgH > 5.0e-6 || minTheta < 0.9
        || lowTheta02 > 1e-4 || total < LOAD_HARD_LO || total > 0.036
        || maxP > 1.0e6 || tnAreaGt100k > 1e-5
        || tnLoadFracGt100k > 1e-3) {
      return "FAIL";
    }
    if (total >= LOAD_PASS_LO && total <= LOAD_PASS_HI
        && maxTn < 5.0e5) {
      return "PASS";
    }
    return "MARGINAL";
  }

  private static void deactivateLegacyFeedback(ModelNode comp) {
    for (String feature : comp.physics("solid").feature().tags()) {
      if (feature.startsWith("load_pfilm576") && !feature.equals("load_pfilm576w5d")) {
        try { comp.physics("solid").feature(feature).active(false); }
        catch (Exception ignored) {}
      }
    }
    for (String physics : comp.physics().tags()) {
      if (physics.startsWith("bode576")) {
        try { comp.physics(physics).active(false); } catch (Exception ignored) {}
      }
    }
  }

  private static void ensureVariables(ModelNode comp) {
    String vars = "var_state576w5d";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(SWEPT);
    comp.variable(vars).set("h_calc576w5d", "h_calc573");
    comp.variable(vars).set("p_pos576w5d", P_POS);
    comp.variable(vars).set("M_core576w5d_core0100", coreExpr(0.100));
    comp.variable(vars).set("Bpress576w5d", B_PRESS);
    comp.variable(vars).set("Bpress576w5d_actual", B_PRESS);
    comp.variable(vars).set("Bpress576w5d_core0100",
        "(" + coreExpr(0.100) + "*" + BASE_FACTORS + ")");
    comp.variable(vars).set("Bwall576w5d", B_WALL);
    comp.variable(vars).set("p_support576w5d", SUPPORT);
    comp.variable(vars).set("p_support576w5d_actual",
        "(Bpress576w5d_actual*min(p_pos576w5d,6.5[kPa]))");
    comp.variable(vars).set("p_support576w5d_core0100",
        "(Bpress576w5d_core0100*min(p_pos576w5d,6.5[kPa]))");
  }

  private static void setCompensatedLidDisplacement(
      ModelNode comp, String[] baseU0, double dnExtraUm) {
    comp.physics("solid").feature("disp_lid_time").set("U0", new String[] {
      "(" + baseU0[0] + ")",
      "((" + baseU0[1] + ")-dn_extra576w5d*Y/sqrt(Y^2+Z^2))",
      "((" + baseU0[2] + ")-dn_extra576w5d*Z/sqrt(Y^2+Z^2))"
    });
  }

  private static void configureTff(Model model) {
    ModelNode comp = model.component("comp1");
    ensurePairSelections(comp);
    deactivateLegacyFeedback(comp);
    comp.physics("ge_force_total111").active(false);
    comp.physics("solid").prop("StructuralTransientBehavior").set(
        "StructuralTransientBehavior", "Quasistatic");
    ensureVariables(comp);
    int[] edges = comp.physics("tff").feature("bdr1").selection().entities();
    comp.physics("tff").feature("bdr_inlet520").active(true);
    comp.physics("tff").feature("bdr_inlet520").selection().set(edges);
    comp.physics("tff").feature("bdr_outlet520").active(false);
    comp.physics("tff").feature("bdr_left520").active(false);
    comp.physics("tff").feature("bdr_right520").active(false);
    comp.physics("tff").feature("wc_open_anchor573").active(false);
    PhysicsFeature ffp = comp.physics("tff").feature("ffp1");
    ffp.set("hw1", "h_calc576w5d");
    ffp.set("vw", new String[] {
      "0",
      "-lambda_v574*Bwall576w5d*omega_lid_rot572*Z",
      "lambda_v574*Bwall576w5d*omega_lid_rot572*Y"
    });
  }

  private static String frac(double value) {
    return String.format(Locale.US, "%.12g", value);
  }

  private static String buildTff(Model model, ModelNode comp, String label,
      String pressureInit, int index, double start, double target) {
    model.param().set("t_position576p2", "T_pre572+" + frac(target) + "*T_slide572");
    model.param().set("t0_576w5d", "T_pre572+" + frac(start) + "*T_slide572");
    model.param().set("t1_576w5d", "T_pre572+" + frac(target) + "*T_slide572");
    model.param().set("dt_576w5d", frac(target - start) + "*T_slide572");

    String study = "std576w5d_tff_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576w5d TFF " + label);
    model.study(study).create("time", "Transient");
    String tlist = "range(t0_576w5d,dt_576w5d/4,t1_576w5d)";
    model.study(study).feature("time").set("tlist", tlist);
    model.study(study).feature("time").set("activate", new String[] {
      "solid", "off", "ge_force_total111", "off", "tff", "on",
      "bode576w3l", "off", "frame:spatial1", "on",
      "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("time").set("useinitsol", "on");
    model.study(study).feature("time").set("initmethod", "sol");
    model.study(study).feature("time").set("initsol", pressureInit);
    model.study(study).feature("time").set("initsoluse", "current");
    model.study(study).feature("time").set("initsolusesolnum", "last");
    String step = study + "/time";
    for (String feature : comp.physics("tff").feature().tags()) {
      try { comp.physics("tff").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dep = model.sol(solution).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", pressureInit);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", BASE_SOLID);
    dep.set("notsolnum", "last");
    SolverFeature time = model.sol(solution).feature("t1");
    time.set("tlist", tlist);
    try { time.set("consistent", "off"); } catch (Exception ignored) {}
    try { time.set("initialstepbdfactive", "on"); } catch (Exception ignored) {}
    try { time.set("initialstepbdf", "dt_576w5d/200"); } catch (Exception ignored) {}
    try { time.set("maxstepconstraintbdf", "const"); } catch (Exception ignored) {}
    try { time.set("maxstepbdf", "dt_576w5d/10"); } catch (Exception ignored) {}
    if (!has(time.feature().tags(), "fc1")) {
      for (String tag : time.feature().tags()) {
        if (tag.startsWith("se")) try { time.feature().remove(tag); } catch (Exception ignored) {}
      }
      time.create("fc1", "FullyCoupled");
    }
    time.feature("fc1").set("linsolver", "dDef");
    time.feature("fc1").set("damp", "0.5");
    time.feature("fc1").set("maxiter", 150);
    return solution;
  }

  private static void setStructuralLoad(ModelNode comp, String pressureSol, String supportName) {
    String vars = "var_feedback576w5d";
    try { comp.variable().remove(vars); } catch (Exception ignored) {}
    comp.variable().create(vars);
    comp.variable(vars).selection().named(PATCH);
    comp.variable(vars).set("p_feedback576w5d",
        "alpha_pfb576w5d*withsol('" + pressureSol + "'," + supportName + ")");
    String load = "load_pfilm576w5d";
    try { comp.physics("solid").feature().remove(load); } catch (Exception ignored) {}
    comp.physics("solid").create(load, "BoundaryLoad", 2);
    comp.physics("solid").feature(load).label(
        "Stage 576w5d capped pressure feedback with bounded normal-position compensation");
    comp.physics("solid").feature(load).selection().named(PATCH);
    comp.physics("solid").feature(load).set("LoadType", "ForceArea");
    comp.physics("solid").feature(load).set("FperArea", new String[] {
      "-p_feedback576w5d*nx", "-p_feedback576w5d*ny", "-p_feedback576w5d*nz"
    });
  }

  private static String buildSolid(
      Model model, ModelNode comp, String label, String initSol, int index) {
    String study = "std576w5d_solid_" + index;
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label("Stage 576w5d solid " + label);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set("activate", new String[] {
      "solid", "on", "ge_force_total111", "off", "tff", "off",
      "bode576w3l", "off", "frame:spatial1", "on",
      "frame:material1", "on", "comp1", "on"
    });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");
    String step = study + "/stat";
    for (String feature : new String[] {"dcnt1", "disp_lid_time", "load_pfilm576w5d"}) {
      try { comp.physics("solid").feature(feature).set("StudyStep", step); }
      catch (Exception ignored) {}
    }
    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution = newest(model, before);
    SolverFeature dep = model.sol(solution).feature("v1");
    dep.set("initmethod", "sol");
    dep.set("initsol", initSol);
    dep.set("solnum", "last");
    dep.set("notsolmethod", "sol");
    dep.set("notsol", initSol);
    dep.set("notsolnum", "last");
    SolverFeature stat = model.sol(solution).feature("s1");
    for (String tag : stat.feature().tags()) {
      if (tag.startsWith("se")) try { stat.feature().remove(tag); } catch (Exception ignored) {}
    }
    if (!has(stat.feature().tags(), "fc1")) stat.create("fc1", "FullyCoupled");
    stat.feature("fc1").set("linsolver", "dDef");
    stat.feature("fc1").set("damp", "0.03");
    stat.feature("fc1").set("maxiter", 500);
    return solution;
  }

  private static String evaluate(Model model, String branchName, String branchType,
      String solidMode, String coreMode, double dnExtraUm, boolean useCore0100,
      String segment, double target, String pressureSol, String solidInit,
      String solidSol, boolean tffLocal) {
    String safe = safeTag(branchName + "_" + segment);
    String bpressName = bpressExpr(useCore0100);
    String supportName = supportExpr(useCore0100);
    String pressureData = dataset(model, "dset576w5d_" + safe + "_pressure", pressureSol);
    String solidData = dataset(model, "dset576w5d_" + safe + "_solid", solidSol);
    double area = surface(model, pressureData, "w5d_" + safe + "_area", "IntSurface", "1", SWEPT);
    double contact = global(model, solidData, "w5d_" + safe + "_contact", "Fn_contact570");
    double support = surface(model, pressureData, "w5d_" + safe + "_support",
        "IntSurface", supportName, SWEPT);
    double rawMasked = surface(model, pressureData, "w5d_" + safe + "_rawMasked",
        "IntSurface", bpressName + "*p_pos576w5d", SWEPT);
    double rawSwept = surface(model, pressureData, "w5d_" + safe + "_rawSwept",
        "IntSurface", "p_pos576w5d", SWEPT);
    double maxP = surface(model, pressureData, "w5d_" + safe + "_maxP",
        "MaxSurface", "p_pos576w5d", SWEPT);
    double avgH = div(surface(model, pressureData, "w5d_" + safe + "_avgH",
        "IntSurface", "h_calc576w5d", SWEPT), area);
    double minH = surface(model, pressureData, "w5d_" + safe + "_minH",
        "MinSurface", "h_calc576w5d", SWEPT);
    double maxH = surface(model, pressureData, "w5d_" + safe + "_maxH",
        "MaxSurface", "h_calc576w5d", SWEPT);
    double minTheta = surface(model, pressureData, "w5d_" + safe + "_minTheta",
        "MinSurface", "tff.theta", SWEPT);
    double lowTheta02 = div(surface(model, pressureData, "w5d_" + safe + "_lowTheta02",
        "IntSurface", "flc2hs(0.2-tff.theta,0.02)", SWEPT), area);
    double areaBpress = surface(model, pressureData, "w5d_" + safe + "_areaBpress",
        "IntSurface", bpressName, SWEPT);
    double areaMcore = surface(model, pressureData, "w5d_" + safe + "_areaMcore",
        "IntSurface", useCore0100 ? "M_core576w5d_core0100" : "M_core573", SWEPT);
    double minGap = surface(model, solidData, "w5d_" + safe + "_minGap",
        "MinSurface", "geomgap_dst_cp_lid_cornea", PATCH);
    String tn = "if(isdefined(solid.Tn),max(solid.Tn,0[Pa]),0[Pa])";
    double maxTn = surface(model, solidData, "w5d_" + safe + "_maxTn",
        "MaxSurface", tn, PATCH);
    double patchArea = surface(model, solidData, "w5d_" + safe + "_patchArea",
        "IntSurface", "1", PATCH);
    double activeArea = surface(model, solidData, "w5d_" + safe + "_activeArea",
        "IntSurface", "flc2hs(" + tn + "-1[Pa],1[Pa])", PATCH);
    String geomGap = "geomgap_dst_cp_lid_cornea";
    String geomGapClip = "min(max(" + geomGap + ",-500[um]),500[um])";
    String srcGap = "if(isdefined(" + pairSourceGap + ")," + pairSourceGap + ",1[m])";
    String srcGapClip = "min(max(" + srcGap + ",-500[um]),500[um])";
    String dstGap = "if(isdefined(" + pairDestinationGap + ")," + pairDestinationGap + ",1[m])";
    String dstGapClip = "min(max(" + dstGap + ",-500[um]),500[um])";
    String mappedGap = DST2SRC + "(if(isdefined(" + pairDestinationGap
        + "),if(abs(" + pairDestinationGap + ")<1[mm],"
        + pairDestinationGap + ",0[m]),0[m]))";
    String mappedGapClip = "min(max(" + mappedGap + ",-500[um]),500[um])";
    double geomMin = surface(model, solidData, "w5d_" + safe + "_geomMin",
        "MinSurface", geomGap, PATCH);
    double geomAvg = avgSurface(model, solidData, "w5d_" + safe + "_geomAvg",
        geomGapClip, PATCH);
    double geomLt100 = avgSurface(model, solidData, "w5d_" + safe + "_geomLt100",
        "flc2hs(-100[um]-(" + geomGap + "),1[um])", PATCH);
    double srcMin = surface(model, solidData, "w5d_" + safe + "_srcMin",
        "MinSurface", srcGap, PAIR_SOURCE);
    double srcAvg = avgSurface(model, solidData, "w5d_" + safe + "_srcAvg",
        srcGapClip, PAIR_SOURCE);
    double srcLt100 = avgSurface(model, solidData, "w5d_" + safe + "_srcLt100",
        "flc2hs(-100[um]-(" + srcGap + "),1[um])", PAIR_SOURCE);
    double dstMin = surface(model, solidData, "w5d_" + safe + "_dstMin",
        "MinSurface", dstGap, PAIR_DESTINATION);
    double dstAvg = avgSurface(model, solidData, "w5d_" + safe + "_dstAvg",
        dstGapClip, PAIR_DESTINATION);
    double dstLt100 = avgSurface(model, solidData, "w5d_" + safe + "_dstLt100",
        "flc2hs(-100[um]-(" + dstGap + "),1[um])", PAIR_DESTINATION);
    double mappedMin = surface(model, solidData, "w5d_" + safe + "_mappedMin",
        "MinSurface", mappedGap, PAIR_SOURCE);
    double mappedAvg = avgSurface(model, solidData, "w5d_" + safe + "_mappedAvg",
        mappedGapClip, PAIR_SOURCE);
    double mappedLt100 = avgSurface(model, solidData, "w5d_" + safe + "_mappedLt100",
        "flc2hs(-100[um]-(" + mappedGap + "),1[um])", PAIR_SOURCE);
    double inContactSrc = avgSurface(model, solidData, "w5d_" + safe + "_inContactSrc",
        "if(isdefined(incontact_cp_lid_cornea),incontact_cp_lid_cornea,0)", PAIR_SOURCE);
    double sourceArea = surface(model, solidData, "w5d_" + safe + "_sourceArea",
        "IntSurface", "1", PAIR_SOURCE);
    double destinationArea = surface(model, solidData, "w5d_" + safe + "_destinationArea",
        "IntSurface", "1", PAIR_DESTINATION);
    double tnAreaGt100k = avgSurface(model, solidData, "w5d_" + safe + "_tnArea100k",
        "flc2hs(" + tn + "-0.1[MPa],1[kPa])", PATCH);
    double tnAreaGt500k = avgSurface(model, solidData, "w5d_" + safe + "_tnArea500k",
        "flc2hs(" + tn + "-0.5[MPa],1[kPa])", PATCH);
    double tnAreaGt1m = avgSurface(model, solidData, "w5d_" + safe + "_tnArea1m",
        "flc2hs(" + tn + "-1[MPa],1[kPa])", PATCH);
    double tnLoadGt100k = surface(model, solidData, "w5d_" + safe + "_tnLoad100k",
        "IntSurface", "if(" + tn + ">0.1[MPa]," + tn + ",0[Pa])", PATCH);
    double tnLoadGt500k = surface(model, solidData, "w5d_" + safe + "_tnLoad500k",
        "IntSurface", "if(" + tn + ">0.5[MPa]," + tn + ",0[Pa])", PATCH);
    double tnLoadGt1m = surface(model, solidData, "w5d_" + safe + "_tnLoad1m",
        "IntSurface", "if(" + tn + ">1[MPa]," + tn + ",0[Pa])", PATCH);
    double avgTnActive = div(surface(model, solidData, "w5d_" + safe + "_tnActiveLoad",
        "IntSurface", tn, PATCH), activeArea);
    double activeContactRatio = div(activeArea, patchArea);
    double tnLoadFracGt100k = div(tnLoadGt100k, contact);
    double total = contact + support;
    String status = classify(total, maxP, minTheta, lowTheta02, avgH, maxTn,
        activeContactRatio, tnAreaGt100k, tnLoadFracGt100k, avgTnActive,
        tffLocal);
    System.out.println("CSV_ROW="
        + branchName + ","
        + branchType + ","
        + solidMode + ","
        + coreMode + ","
        + csv(dnExtraUm) + ","
        + segment + ","
        + csv(target) + ","
        + csv(2.25 + dnExtraUm) + ","
        + pressureSol + ","
        + solidInit + ","
        + solidSol + ","
        + csv(contact) + ","
        + csv(rawSwept) + ","
        + csv(rawMasked) + ","
        + csv(support) + ","
        + csv(total) + ","
        + csv(maxP) + ","
        + csv(avgH) + ","
        + csv(minH) + ","
        + csv(maxH) + ","
        + csv(minTheta) + ","
        + csv(lowTheta02) + ","
        + csv(minGap) + ","
        + csv(maxTn) + ","
        + csv(activeContactRatio) + ","
        + csv(div(areaBpress, area)) + ","
        + csv(div(areaMcore, area)) + ","
        + Boolean.toString(tffLocal) + ","
        + csv(geomMin) + ","
        + csv(geomAvg) + ","
        + csv(geomLt100) + ","
        + csv(srcMin) + ","
        + csv(srcAvg) + ","
        + csv(srcLt100) + ","
        + csv(dstMin) + ","
        + csv(dstAvg) + ","
        + csv(dstLt100) + ","
        + csv(mappedMin) + ","
        + csv(mappedAvg) + ","
        + csv(mappedLt100) + ","
        + csv(inContactSrc) + ","
        + csv(sourceArea) + ","
        + csv(destinationArea) + ","
        + csv(tnAreaGt100k) + ","
        + csv(tnAreaGt500k) + ","
        + csv(tnAreaGt1m) + ","
        + csv(tnLoadFracGt100k) + ","
        + csv(div(tnLoadGt500k, contact)) + ","
        + csv(div(tnLoadGt1m, contact)) + ","
        + csv(avgTnActive) + ","
        + status);
    return status;
  }

  private static String runBranch(Model model, ModelNode comp, String branchName,
      String solidMode, String pressureInit, String solidInit, double dnExtraUm,
      boolean tffLocal, int indexBase) {
    String pressureCurrent = pressureInit;
    String solidCurrent = solidInit;
    String finalStatus = "PASS";
    for (int i = 0; i < TARGETS.length; i++) {
      String segment = SEGMENTS[i];
      String label = branchName + "_segment_" + segment;
      int index = indexBase + i;
      String pressureSol = buildTff(model, comp, label, pressureCurrent, index,
          STARTS[i], TARGETS[i]);
      System.out.println("RUN576w5d_TFF branch=" + branchName
          + " segment=" + segment
          + " start=" + csv(STARTS[i])
          + " target=" + csv(TARGETS[i])
          + " pressureInit=" + pressureCurrent
          + " geometrySolid=" + BASE_SOLID
          + " solution=" + pressureSol);
      model.sol(pressureSol).runAll();
      setStructuralLoad(comp, pressureSol, supportExpr(true));
      double scheduledDn = dnExtraUm;
      model.param().set("dn_extra576w4i", csv(scheduledDn) + "[um]");
      model.param().set("dn_extra576w5d", csv(scheduledDn) + "[um]");
      model.param().set("dn_comp_total576w4i",
          "(" + csv(2.25 + scheduledDn) + ")[um]");
      model.param().set("dn_comp_total576w5d",
          "(" + csv(2.25 + scheduledDn) + ")[um]");
      String solidSol = buildSolid(model, comp, label, solidCurrent, index);
      System.out.println("RUN576w5d_SOLID branch=" + branchName
          + " segment=" + segment
          + " solidInit=" + solidCurrent
          + " pressure=" + pressureSol
          + " support=" + supportExpr(true)
          + " dn_extra=" + csv(scheduledDn) + "[um]"
          + " solution=" + solidSol);
      model.sol(solidSol).runAll();
      String status = evaluate(model, branchName, "NORMAL_PLUS_CORE_0100_DN20_SPLIT_FORWARD_075",
          solidMode, "CORE_0100", scheduledDn, true, segment, TARGETS[i],
          pressureSol, solidCurrent, solidSol, tffLocal);
      dataset(model, "dset576w5d_" + safeTag(branchName + "_" + segment)
          + "_final_pressure", pressureSol);
      dataset(model, "dset576w5d_" + safeTag(branchName + "_" + segment)
          + "_final_solid", solidSol);
      if ("FAIL".equals(status)) {
        finalStatus = "FAIL";
        break;
      }
      finalStatus = status;
      pressureCurrent = pressureSol;
      solidCurrent = solidSol;
    }
    return finalStatus;
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      ModelNode comp = model.component("comp1");
      configureTff(model);
      model.param().set("alpha_pfb576w5d", "0.15");
      model.param().set("cap_pressure576w5d", "6.5[kPa]");
      model.param().set("dn_extra576w4i", csv(SCHEDULE_DN_UM) + "[um]");
      model.param().set("dn_extra576w5d", csv(SCHEDULE_DN_UM) + "[um]");
      model.param().set("dn_comp_total576w4i",
          "(" + csv(2.25 + SCHEDULE_DN_UM) + ")[um]");
      model.param().set("dn_comp_total576w5d",
          "(" + csv(2.25 + SCHEDULE_DN_UM) + ")[um]");
      boolean tffLocal = tffSelectionEqualsSwept(comp);
      System.out.println("INPUT_MODEL=" + INPUT);
      System.out.println("ALPHA_PFB576w5d=0.15");
      System.out.println("CAP_PRESSURE576w5d=6.5[kPa]");
      System.out.println("PRESSURE_STRATEGY576w5d=reset all branches from sol493");
      System.out.println("DN_SCAN576w5d=22,23,24[um] from 62.5% to 75%");
      System.out.println("DN_COMP_TOTAL_SCAN576w5d=24.25,25.25,26.25[um]");
      System.out.println("SEGMENTS576w5d=0.625->0.6875->0.750");
      System.out.println("CORE0100_THETA_PHYSICAL_DEG="
          + csv(thetaPhysicalDegrees(0.100)));
      System.out.println("TFF_SELECTION_EQUALS_SWEPT_INITIAL=" + tffLocal);
      System.out.println("CSV_HEADER=branch,branch_type,solid_mode,core_mode,dn_extra_um,segment,target,dn_comp_total_um,pressure_solution,solid_init,solid_solution,F_contact,F_film_raw_swept,F_film_raw_masked,F_film_support,F_total_support,MaxP_raw,AvgH,MinH,MaxH,MinTheta,LowThetaAreaRatio02,MinGap,MaxTn,active_contact_area_over_patch,BpressOverSwept,McoreOverSwept,TffSelectionLocal,GeomGapMin,GeomGapAvgClip,GeomGapLtMinus100Ratio,PairSourceGapMin,PairSourceGapAvgClip,PairSourceGapLtMinus100Ratio,PairDestinationGapMin,PairDestinationGapAvgClip,PairDestinationGapLtMinus100Ratio,Dst2SrcGapMin,Dst2SrcGapAvgClip,Dst2SrcGapLtMinus100Ratio,InContactSourceRatio,PairSourceArea,PairDestinationArea,TnAreaGt0p1MPa,TnAreaGt0p5MPa,TnAreaGt1MPa,TnLoadFracGt0p1MPa,TnLoadFracGt0p5MPa,TnLoadFracGt1MPa,AvgTnActive,status");

      Map<String, String> statuses = new LinkedHashMap<String, String>();
      int index = 97000;
      for (int modeIndex = 0; modeIndex < 2; modeIndex++) {
        String solidMode = modeIndex == 0 ? "RESET" : "REUSE";
        String pressureInit = RESET_PRESSURE_0625;
        String solidInit = modeIndex == 0 ? RESET_SOLID_0625 : REUSE_SOLID_0625;
        for (double dnExtra : DN_SCAN_UM) {
          String dnLabel = String.format(Locale.US, "DN%.1fUM", dnExtra)
              .replace(".", "P");
          String branchName = "PRESSURE_RESET_NORMAL_PLUS_CORE_0100_" + dnLabel + "_TO_075_" + solidMode;
          String status;
          try {
            status = runBranch(model, comp, branchName, solidMode,
                pressureInit, solidInit, dnExtra, tffLocal, index);
          } catch (Exception branchError) {
            status = "FAIL";
            System.out.println("BRANCH_EXCEPTION576w5d branch=" + branchName
                + " dn_extra=" + csv(dnExtra)
                + " message=" + branchError.getMessage());
          }
          statuses.put(branchName, status);
          index += 10;
        }
      }

      boolean anyPass = false;
      boolean anyMarginal = false;
      boolean anyFail = false;
      Set<String> passedModes = new HashSet<String>();
      for (String branchName : statuses.keySet()) {
        String status = statuses.get(branchName);
        System.out.println(branchName + "_STATUS=" + status);
        if ("PASS".equals(status)) anyPass = true;
        if ("PASS".equals(status) && branchName.endsWith("_RESET")) passedModes.add("RESET");
        if ("PASS".equals(status) && branchName.endsWith("_REUSE")) passedModes.add("REUSE");
        if ("MARGINAL".equals(status)) anyMarginal = true;
        if ("FAIL".equals(status)) anyFail = true;
      }

      String diagnosis;
      if (passedModes.contains("RESET") && passedModes.contains("REUSE")) {
        diagnosis = "075_PRESSURE_RESET_HIGH_DN_SCAN_HAS_PASS_BOTH";
      } else if (anyPass) {
        diagnosis = "075_PRESSURE_RESET_HIGH_DN_SCAN_HAS_PARTIAL_PASS";
      } else if (anyMarginal) {
        diagnosis = "075_PRESSURE_RESET_HIGH_DN_SCAN_MARGINAL";
      } else if (anyFail) {
        diagnosis = "075_PRESSURE_RESET_HIGH_DN_SCAN_FAILS";
      } else {
        diagnosis = "075_PRESSURE_RESET_HIGH_DN_SCAN_INCONCLUSIVE";
      }

      System.out.println("OVERALL_DIAGNOSIS=" + diagnosis);
      System.out.println("CHECKED_STATUS=DIAGNOSTIC_ONLY");
      System.out.println("w5d_BUILD_STATUS=PASS");
      model.label("Stage 576w5d 075 pressure reset DN scan " + diagnosis);
      String saveTarget = "075_PRESSURE_RESET_HIGH_DN_SCAN_HAS_PASS_BOTH".equals(diagnosis)
          ? CHECKED : RESULTS;
      model.save(saveTarget);
      System.out.println("SAVED_MPH=" + saveTarget);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}






















