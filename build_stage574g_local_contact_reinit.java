import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574g_local_contact_reinit {
  private static final String BASE = "574f_stage574_local_cornea_patch_structure_setup.mph";
  private static final String SETUP = "574g_stage574_local_contact_gap_setup.mph";
  private static final String PART1 = "574g_stage574_local_contact_gap_results_part1.mph";
  private static final String RESULTS = "574g_stage574_local_contact_gap_results.mph";
  private static final String CHECKED = "574g_stage574_local_contact_gap_checked.mph";
  private static final String REPORT = "574g_stage574_local_contact_gap_checked.md";
  private static final double[] PART1_Q =
      new double[] {0, 0.005, 0.01, 0.02, 0.03, 0.05, 0.075, 0.1};
  private static final double[] PART2_Q =
      new double[] {0.15, 0.2, 0.3, 0.5, 0.75, 1.0};

  private static final List<String> report = new ArrayList<>();

  private static void line(String text) {
    report.add(text);
    System.out.println(text);
  }

  private static void writeReport() {
    // COMSOL batch file-system security can block arbitrary Java file writes.
    // Keep the report on stdout; PowerShell writes the checked markdown after runs.
  }

  private static boolean has(String[] tags, String tag) {
    for (String value : tags) if (value.equals(tag)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) latest = tag;
    }
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static void removeStudy(Model model, String tag) {
    try { model.study().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static int[] edgeVertices(
      MeshSequence mesh, int edge, int[][] elements, int[] entities) {
    Set<Integer> vertices = new LinkedHashSet<>();
    for (int element = 0; element < entities.length; element++) {
      if (entities[element] != edge) continue;
      for (int local = 0; local < elements.length; local++) {
        vertices.add(elements[local][element]);
      }
    }
    int[] result = new int[vertices.size()];
    int index = 0;
    for (int vertex : vertices) result[index++] = vertex;
    return result;
  }

  private static void explicitEdges(
      ModelNode comp, String tag, String label, List<Integer> edges) {
    removeSelection(comp, tag);
    comp.selection().create(tag, "Explicit");
    comp.selection(tag).label(label);
    comp.selection(tag).geom("geom1", 1);
    int[] values = new int[edges.size()];
    for (int i = 0; i < values.length; i++) values[i] = edges.get(i);
    comp.selection(tag).set(values);
  }

  private static void classifyEdges(ModelNode comp) {
    MeshSequence mesh = comp.mesh("mesh1");
    double[][] coordinates = mesh.getVertex();
    int[][] elements = mesh.getElem("edg");
    int[] entities = mesh.getElemEntity("edg");
    int[] edges = comp.selection("sel_local_edges_all574").entities(1);
    List<Integer> left = new ArrayList<>();
    List<Integer> right = new ArrayList<>();
    List<Integer> leading = new ArrayList<>();
    List<Integer> trailing = new ArrayList<>();
    for (int edge : edges) {
      int[] vertices = edgeVertices(mesh, edge, elements, entities);
      if (vertices.length == 0) continue;
      double meanX = 0;
      double meanAngle = 0;
      for (int vertex : vertices) {
        meanX += coordinates[0][vertex];
        meanAngle += Math.atan2(coordinates[1][vertex], coordinates[2][vertex]);
      }
      meanX /= vertices.length;
      meanAngle /= vertices.length;
      if (Math.abs(meanX) > 3.0) {
        if (meanX < 0) left.add(edge);
        else right.add(edge);
      } else {
        if (meanAngle < -35.0 * Math.PI / 180.0) leading.add(edge);
        else trailing.add(edge);
      }
    }
    explicitEdges(comp, "sel_local_leading574",
        "Stage 574g local-patch leading drain", leading);
    explicitEdges(comp, "sel_local_trailing574",
        "Stage 574g local-patch trailing drain", trailing);
    explicitEdges(comp, "sel_local_left574",
        "Stage 574g local-patch left drain", left);
    explicitEdges(comp, "sel_local_right574",
        "Stage 574g local-patch right drain", right);
  }

  private static void rebindStartPatch(ModelNode comp) {
    removeSelection(comp, "sel_local_cornea_patch574");
    comp.selection().create("sel_local_cornea_patch574", "Explicit");
    comp.selection("sel_local_cornea_patch574").label(
        "Stage 574g corrected start-position local corneal contact patch");
    comp.selection("sel_local_cornea_patch574").geom("geom1", 2);
    comp.selection("sel_local_cornea_patch574").set(new int[] {10, 16});

    removeSelection(comp, "sel_local_edges_all574");
    comp.selection().create("sel_local_edges_all574", "Adjacent");
    comp.selection("sel_local_edges_all574").label(
        "Stage 574g exterior edges of corrected local patch");
    comp.selection("sel_local_edges_all574").set("entitydim", "2");
    comp.selection("sel_local_edges_all574").set("outputdim", "1");
    comp.selection("sel_local_edges_all574").set(
        "input", new String[] {"sel_local_cornea_patch574"});
    comp.selection("sel_local_edges_all574").set("exterior", "on");
    comp.selection("sel_local_edges_all574").set("interior", "off");
    comp.mesh("mesh1").run();
    classifyEdges(comp);
    try { comp.physics("tff").selection().named("sel_local_cornea_patch574"); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("ms_vent573").selection().named("sel_local_cornea_patch574"); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("wc_open_anchor573").selection().named("sel_local_cornea_patch574"); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("bdr_inlet520").selection().named("sel_local_leading574"); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("bdr_outlet520").selection().named("sel_local_trailing574"); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("bdr_left520").selection().named("sel_local_left574"); }
    catch (Exception ignored) {}
    try { comp.physics("tff").feature("bdr_right520").selection().named("sel_local_right574"); }
    catch (Exception ignored) {}
    try { comp.cpl("intop_film").selection().named("sel_local_cornea_patch574"); }
    catch (Exception ignored) {}
    try { comp.variable("var_cornea_dynamic_regions573").selection().named("sel_local_cornea_patch574"); }
    catch (Exception ignored) {}
  }

  private static String initialSolution(Model model) {
    if (has(model.sol().tags(), "sol94")) return "sol94";
    String[] tags = model.sol().tags();
    if (tags.length == 0) throw new IllegalStateException("No initial solution exists");
    return tags[tags.length - 1];
  }

  private static void deactivateFriction(ModelNode comp) {
    PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
    for (String childTag : contact.feature().tags()) {
      PhysicsFeature child = contact.feature(childTag);
      String type = child.getType();
      String label = child.label();
      if ("Friction".equals(type) || label.toLowerCase(Locale.ROOT).contains("fric")) {
        child.active(false);
        line("- disabled friction child `" + childTag + "` (`" + label + "`)");
      }
    }
  }

  private static void forceContactSettings(ModelNode comp) {
    PhysicsFeature contact = comp.physics("solid").feature("dcnt1");
    contact.set("pairSelection", "list");
    contact.set("pairs", new String[] {"cp_lid_cornea"});
    try { contact.set("useCutback", "1"); } catch (Exception ignored) {}
    deactivateFriction(comp);
  }

  private static void setIndentedDisplacement(ModelNode comp) {
    comp.physics("solid").feature("disp_lid_time").set(
        "U0", new String[] {
          "0",
          "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
          "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
        });
  }

  private static String buildStudy(
      Model model, String study, String label, String initSol) {
    removeStudy(model, study);
    model.study().create(study);
    model.study(study).label(label);
    model.study(study).create("stat", "Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity", "on");
    model.study(study).feature("stat").set(
        "activate", new String[] {
          "solid", "on", "ge_force_total111", "off", "tff", "off",
          "frame:spatial1", "on", "frame:material1", "on", "comp1", "on"
        });
    model.study(study).feature("stat").set("useinitsol", "on");
    model.study(study).feature("stat").set("initmethod", "sol");
    model.study(study).feature("stat").set("initsol", initSol);
    model.study(study).feature("stat").set("initsoluse", "current");

    String step = study + "/stat";
    ModelNode comp = model.component("comp1");
    for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
      try { comp.physics("solid").feature(tag).set("StudyStep", step); }
      catch (Exception ignored) {}
    }

    String[] before = model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String sol = newest(model, before);
    SolverFeature dependent = model.sol(sol).feature("v1");
    dependent.set("initmethod", "sol");
    dependent.set("initsol", initSol);
    dependent.set("solnum", "last");
    dependent.set("notsolmethod", "sol");
    dependent.set("notsol", initSol);
    dependent.set("notsolnum", "last");

    SolverFeature stationary = model.sol(sol).feature("s1");
    for (String tag : stationary.feature().tags()) {
      if (tag.startsWith("se")) {
        try { stationary.feature().remove(tag); } catch (Exception ignored) {}
      }
    }
    if (!has(stationary.feature().tags(), "fc1")) {
      stationary.create("fc1", "FullyCoupled");
    }
    stationary.feature("fc1").set("linsolver", "dDef");
    stationary.feature("fc1").set("damp", "0.05");
    stationary.feature("fc1").set("maxiter", 300);
    return sol;
  }

  private static double[][] globalEval(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double[][] patchEval(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal();
  }

  private static double surfaceValue(
      Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static double[] evaluate(Model model, String solution, String suffix) {
    String data = "dset574g_contact_" + suffix;
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", solution);

    double[][] global = globalEval(
        model, data, "eval574g_contact_global_" + suffix,
        new String[] {
          "q_scale574",
          "q_scale574*q_fixed574*1[mm]",
          "Fn_contact570"
        });
    double[][] patch = patchEval(
        model, data, "int574g_patch_gap_" + suffix,
        new String[] {
          "1",
          "if(isdefined(geomgap_dst_cp_lid_cornea),"
              + "if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)"
        });
    double minGap = surfaceValue(
        model, data, "min574g_patch_gap_" + suffix, "MinSurface",
        "geomgap_dst_cp_lid_cornea");
    double maxTn = surfaceValue(
        model, data, "max574g_patch_tn_" + suffix, "MaxSurface",
        "solid.Tn");

    double gapCoverage = patch[1][0] / patch[0][0];
    double coreGapCoverage = gapCoverage;
    double finalQ = global[0][global[0].length - 1];
    double finalContact = global[2][global[2].length - 1];
    line("");
    line("## Metrics " + suffix);
    line("");
    line("- global rows: `q_scale574`, `q_scale574*q_fixed574*1[mm]`, `Fn_contact570`");
    line("- global values: `" + Arrays.deepToString(global) + "`");
    line("- patch integral rows: `area`, `finite-gap area`");
    line("- patch values: `" + Arrays.deepToString(patch) + "`");
    line("- min geomgap_dst_cp_lid_cornea: `" + minGap + "`");
    line("- max solid.Tn on local patch: `" + maxTn + "`");
    line("- local gap coverage: `" + gapCoverage + "`");
    line("- core gap coverage: `" + coreGapCoverage + "`");
    line("- final q_scale574: `" + finalQ + "`");
    line("- final Fn_contact570: `" + finalContact + "`");
    return new double[] {gapCoverage, coreGapCoverage, finalQ, finalContact};
  }

  private static String runQPoint(
      Model model, double q, String initSol, String prefix, int index) {
    String study = prefix + "_" + index;
    model.param().set("q_scale574", String.format(Locale.US, "%.12g", q));
    String sol = buildStudy(
        model, study,
        "Stage 574g contact reinit q=" + String.format(Locale.US, "%.12g", q),
        initSol);
    line("- running q_scale574=`" + String.format(Locale.US, "%.12g", q)
        + "` with solution `" + sol + "` from `" + initSol + "`");
    model.sol(sol).runAll();
    return sol;
  }

  public static void main(String[] args) {
    Model model = null;
    try {
      report.add("# Stage 574g local contact-gap reinitialization");
      report.add("");
      ModelUtil.initStandalone(false);
      model = ModelUtil.load("Model", BASE);
      ModelNode comp = model.component("comp1");
      line("- loaded `" + BASE + "`");
      line("- pair source: `" + Arrays.toString(comp.pair("cp_lid_cornea").source().entities()) + "`");
      line("- pair destination: `" + Arrays.toString(comp.pair("cp_lid_cornea").destination().entities()) + "`");
      line("- destination gap: `" + comp.pair("cp_lid_cornea").gapName(true) + "`");

      rebindStartPatch(comp);
      line("- rebound `sel_local_cornea_patch574` to corrected start-contact boundaries `[10, 16]`");
      line("- corrected local patch: `" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)) + "`");
      forceContactSettings(comp);
      setIndentedDisplacement(comp);
      model.param().set("q_scale574", "0");
      model.label("Stage 574g local contact-gap setup");
      model.save(SETUP);
      line("- saved setup `" + SETUP + "`");

      String init = initialSolution(model);
      line("- initial solution: `" + init + "`");

      String current = init;
      double[] lastMetrics = null;
      line("- running part 1 q list `" + Arrays.toString(PART1_Q) + "`");
      for (int i = 0; i < PART1_Q.length; i++) {
        double q = PART1_Q[i];
        try {
          current = runQPoint(model, q, current, "std574g_soft", i);
          lastMetrics = evaluate(model, current, "part1_" + i);
          line("- q_scale574 `" + q + "` converged; Fn_contact570 final value `"
              + lastMetrics[3] + "`");
        } catch (Exception error) {
          model.save("574g_stage574_local_contact_gap_last_success.mph");
          throw new IllegalStateException(
              "Part 1 failed at q_scale574=" + q, error);
        }
      }
      model.save(PART1);
      line("- saved part 1 results `" + PART1 + "`");

      line("- running part 2 q list `" + Arrays.toString(PART2_Q) + "`");
      for (int i = 0; i < PART2_Q.length; i++) {
        double q = PART2_Q[i];
        try {
          current = runQPoint(model, q, current, "std574g_full", i);
          lastMetrics = evaluate(model, current, "part2_" + i);
          line("- q_scale574 `" + q + "` converged; Fn_contact570 final value `"
              + lastMetrics[3] + "`");
        } catch (Exception error) {
          model.save("574g_stage574_local_contact_gap_last_success.mph");
          throw new IllegalStateException(
              "Part 2 failed at q_scale574=" + q, error);
        }
      }
      model.save(RESULTS);
      line("- saved results `" + RESULTS + "`");

      boolean pass = true;
      if (lastMetrics == null || Math.abs(lastMetrics[2] - 1.0) > 1e-9) pass = false;
      if (lastMetrics == null || Math.abs(lastMetrics[3]) < 0.005 || Math.abs(lastMetrics[3]) > 0.08) pass = false;
      if (lastMetrics == null || lastMetrics[1] < 0.95) pass = false;

      line("");
      line("## Acceptance");
      line("");
      line("- q_scale574=1 reached: `" + (lastMetrics != null && Math.abs(lastMetrics[2] - 1.0) <= 1e-9) + "`");
      line("- contact force is 0.03 N order: `" + (lastMetrics != null && Math.abs(lastMetrics[3]) >= 0.005 && Math.abs(lastMetrics[3]) <= 0.08) + "`");
      line("- core gap coverage >= 0.95: `" + (lastMetrics != null && lastMetrics[1] >= 0.95) + "`");
      line("- checked status: `" + (pass ? "PASS" : "FAIL") + "`");
      if (!pass) {
        throw new IllegalStateException("Stage 574g acceptance failed; checked model not saved");
      }
      model.label("Stage 574g local contact-gap checked");
      model.save(CHECKED);
      line("- saved checked model `" + CHECKED + "`");
      ModelUtil.disconnect();
    } catch (Exception error) {
      line("");
      line("## Failure");
      line("");
      line("`" + error.toString().replace("`", "'") + "`");
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }

}
