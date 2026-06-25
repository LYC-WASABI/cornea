import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574_midpoint_local_patch_structure {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); }
    catch (Exception ignored) {}
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
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
    int[] edges =
        comp.selection("sel_local_edges_all574").entities(1);
    List<Integer> left = new ArrayList<>();
    List<Integer> right = new ArrayList<>();
    List<Integer> leading = new ArrayList<>();
    List<Integer> trailing = new ArrayList<>();
    for (int edge : edges) {
      int[] vertices = edgeVertices(mesh, edge, elements, entities);
      double meanX = 0;
      double meanAngle = 0;
      for (int vertex : vertices) {
        meanX += coordinates[0][vertex];
        meanAngle += Math.atan2(
            coordinates[1][vertex], coordinates[2][vertex]);
      }
      meanX /= vertices.length;
      meanAngle /= vertices.length;
      if (Math.abs(meanX) > 3.0) {
        if (meanX < 0) left.add(edge);
        else right.add(edge);
      } else {
        if (meanAngle < modelAngleCenter()) leading.add(edge);
        else trailing.add(edge);
      }
    }
    explicitEdges(comp, "sel_local_leading574",
        "Stage 574 local-patch leading drain", leading);
    explicitEdges(comp, "sel_local_trailing574",
        "Stage 574 local-patch trailing drain", trailing);
    explicitEdges(comp, "sel_local_left574",
        "Stage 574 local-patch left drain", left);
    explicitEdges(comp, "sel_local_right574",
        "Stage 574 local-patch right drain", right);
  }

  private static double modelAngleCenter() {
    return 35.10 * Math.PI / 180.0;
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "574e_stage574_local_cornea_patch_geometry.mph");
      ModelNode comp = model.component("comp1");
      model.param().set(
          "q_fixed574", "0.0017756348994417612",
          "Verified Stage 570 indentation used for fixed-structure JFO");
      model.param().set(
          "q_scale574", "1",
          "Continuation factor for rebuilding contact on the imprinted mesh");
      model.param().set(
          "time_offset572", "0[s]",
          "Freeze Stage 572 moving masks at the start position");

      removeSelection(comp, "sel_local_cornea_patch574");
      comp.selection().create("sel_local_cornea_patch574", "Union");
      comp.selection("sel_local_cornea_patch574").label(
          "Stage 574 true local corneal JFO patch");
      comp.selection("sel_local_cornea_patch574").set("entitydim", "2");
      comp.selection("sel_local_cornea_patch574").set(
          "input", new String[] {"sel_patch_candidate574"});

      removeSelection(comp, "sel_local_edges_all574");
      comp.selection().create("sel_local_edges_all574", "Adjacent");
      comp.selection("sel_local_edges_all574").label(
          "Stage 574 exterior edges of local corneal JFO patch");
      comp.selection("sel_local_edges_all574").set("entitydim", "2");
      comp.selection("sel_local_edges_all574").set("outputdim", "1");
      comp.selection("sel_local_edges_all574").set(
          "input", new String[] {"sel_local_cornea_patch574"});
      comp.selection("sel_local_edges_all574").set("exterior", "on");
      comp.selection("sel_local_edges_all574").set("interior", "off");

      int[] verifiedSource =
          comp.pair("cp_lid_cornea").source().entities();
      removeSelection(comp, "sel_lid_contact_source574");
      comp.selection().create("sel_lid_contact_source574", "Explicit");
      comp.selection("sel_lid_contact_source574").label(
          "Stage 574 frozen valid lid contact source");
      comp.selection("sel_lid_contact_source574").geom("geom1", 2);
      comp.selection("sel_lid_contact_source574").set(verifiedSource);
      comp.mesh("mesh1").run();
      classifyEdges(comp);

      comp.physics("tff").selection().named("sel_local_cornea_patch574");
      comp.physics("tff").feature("ms_vent573")
          .selection().named("sel_local_cornea_patch574");
      comp.physics("tff").feature("wc_open_anchor573")
          .selection().named("sel_local_cornea_patch574");
      comp.physics("tff").feature("bdr_inlet520")
          .selection().named("sel_local_leading574");
      comp.physics("tff").feature("bdr_outlet520")
          .selection().named("sel_local_trailing574");
      comp.physics("tff").feature("bdr_left520")
          .selection().named("sel_local_left574");
      comp.physics("tff").feature("bdr_right520")
          .selection().named("sel_local_right574");
      comp.cpl("intop_film")
          .selection().named("sel_local_cornea_patch574");
      comp.variable("var_cornea_dynamic_regions573")
          .selection().named("sel_local_cornea_patch574");

      String[] dynamicU0 =
          comp.physics("solid").feature("disp_lid_time")
              .getStringArray("U0");
      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "-q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2)",
            "-q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)"
          });

      model.param().set("q_scale574", "0");
      String preloadStudy = "std574_local_preload";
      try { model.study().remove(preloadStudy); }
      catch (Exception ignored) {}
      comp.physics("solid").feature("dcnt1")
          .set("pairSelection", "list");
      comp.physics("solid").feature("dcnt1")
          .set("pairs", new String[] {});
      model.study().create(preloadStudy);
      model.study(preloadStudy).label(
          "Stage 574 imprinted-mesh preload without contact");
      model.study(preloadStudy).create("stat", "Stationary");
      model.study(preloadStudy).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(preloadStudy).feature("stat").set(
          "activate", new String[] {
            "solid", "on", "ge_force_total111", "off", "tff", "off",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(preloadStudy).feature("stat").set("useinitsol", "on");
      model.study(preloadStudy).feature("stat").set("initmethod", "sol");
      model.study(preloadStudy).feature("stat").set("initsol", "sol93");
      model.study(preloadStudy).feature("stat")
          .set("initsoluse", "current");
      String[] beforePreload = model.sol().tags();
      model.study(preloadStudy).createAutoSequences("sol");
      String preloadSolution = newest(model, beforePreload);
      model.sol(preloadSolution).feature("v1").set("initmethod", "sol");
      model.sol(preloadSolution).feature("v1").set("initsol", "sol93");
      model.sol(preloadSolution).feature("v1").set("solnum", "last");
      model.sol(preloadSolution).feature("v1").set("notsolmethod", "sol");
      model.sol(preloadSolution).feature("v1").set("notsol", "sol93");
      model.sol(preloadSolution).feature("v1").set("notsolnum", "last");
      SolverFeature preloadStationary =
          model.sol(preloadSolution).feature("s1");
      for (String tag : preloadStationary.feature().tags()) {
        if (tag.startsWith("se")) {
          try { preloadStationary.feature().remove(tag); }
          catch (Exception ignored) {}
        }
      }
      if (!Arrays.asList(preloadStationary.feature().tags())
          .contains("fc1")) {
        preloadStationary.create("fc1", "FullyCoupled");
      }
      preloadStationary.feature("fc1").set("linsolver", "dDef");
      preloadStationary.feature("fc1").set("damp", "0.1");
      preloadStationary.feature("fc1").set("maxiter", 300);
      model.sol(preloadSolution).runAll();
      comp.physics("solid").feature("dcnt1")
          .set("pairs", new String[] {"cp_lid_cornea"});

      String study = "std574_local_structure";
      try { model.study().remove(study); }
      catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 574 start-position local constant-load structure");
      model.study(study).create("param", "Parametric");
      model.study(study).feature("param").set(
          "pname", new String[] {"q_scale574"});
      model.study(study).feature("param").set(
          "plistarr", new String[] {"range(0,0.1,1)"});
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate", new String[] {
            "solid", "on", "ge_force_total111", "off", "tff", "off",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set(
          "initsol", preloadSolution);
      model.study(study).feature("stat").set("initsoluse", "current");
      String step = study + "/stat";
      for (String tag : new String[] {"dcnt1", "disp_lid_time"}) {
        comp.physics("solid").feature(tag).set("StudyStep", step);
      }
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", preloadSolution);
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", preloadSolution);
      dependent.set("notsolnum", "last");
      SolverFeature stationary = model.sol(solution).feature("s1");
      for (String tag : stationary.feature().tags()) {
        if (tag.startsWith("se")) {
          try { stationary.feature().remove(tag); }
          catch (Exception ignored) {}
        }
      }
      if (!Arrays.asList(stationary.feature().tags()).contains("fc1")) {
        stationary.create("fc1", "FullyCoupled");
      }
      stationary.feature("fc1").set("linsolver", "dDef");
      stationary.feature("fc1").set("damp", "0.1");
      stationary.feature("fc1").set("maxiter", 300);

      model.label("Stage 574 local corneal patch structure setup");
      model.save("574f_stage574_local_cornea_patch_structure_setup.mph");
      model.sol(solution).runAll();
      comp.physics("solid").feature("disp_lid_time").set("U0", dynamicU0);

      removeDataset(model, "dset574_local_structure");
      model.result().dataset().create(
          "dset574_local_structure", "Solution");
      model.result().dataset("dset574_local_structure")
          .set("solution", solution);
      removeNumerical(model, "eval574_local_structure");
      model.result().numerical().create(
          "eval574_local_structure", "EvalGlobal");
      model.result().numerical("eval574_local_structure")
          .set("data", "dset574_local_structure");
      model.result().numerical("eval574_local_structure").set(
          "expr", new String[] {
            "Fn_contact570", "q_fixed574*1[mm]",
            "q_force_total111"
          });
      double[][] balance =
          model.result().numerical("eval574_local_structure").getReal();
      removeNumerical(model, "int574_local_gap");
      model.result().numerical().create(
          "int574_local_gap", "IntSurface");
      model.result().numerical("int574_local_gap")
          .set("data", "dset574_local_structure");
      model.result().numerical("int574_local_gap")
          .selection().named("sel_local_cornea_patch574");
      model.result().numerical("int574_local_gap").set(
          "expr", new String[] {
            "1",
            "if(isdefined(geomgap_dst_cp_lid_cornea),"
                + "if(abs(geomgap_dst_cp_lid_cornea)<0.1[mm],1,0),0)",
            "M_core573",
            "M_core573*if(isdefined(geomgap_dst_cp_lid_cornea),"
                + "if(abs(geomgap_dst_cp_lid_cornea)<0.1[mm],1,0),0)"
          });
      double[][] gapCheck =
          model.result().numerical("int574_local_gap").getReal();
      double gapCoverage = gapCheck[1][0] / gapCheck[0][0];
      double coreGapCoverage = gapCheck[3][0] / gapCheck[2][0];
      System.out.println("LOCAL_PATCH="
          + Arrays.toString(
              comp.selection("sel_local_cornea_patch574").entities(2)));
      System.out.println("LOCAL_EDGES="
          + Arrays.toString(
              comp.selection("sel_local_edges_all574").entities(1)));
      System.out.println("LEADING="
          + Arrays.toString(
              comp.selection("sel_local_leading574").entities(1)));
      System.out.println("TRAILING="
          + Arrays.toString(
              comp.selection("sel_local_trailing574").entities(1)));
      System.out.println("LEFT="
          + Arrays.toString(
              comp.selection("sel_local_left574").entities(1)));
      System.out.println("RIGHT="
          + Arrays.toString(
              comp.selection("sel_local_right574").entities(1)));
      System.out.println("STRUCTURE_SOLUTION=" + solution);
      System.out.println("LOAD_BALANCE=" + Arrays.deepToString(balance));
      System.out.println("LOCAL_GAP_COVERAGE=" + gapCoverage);
      System.out.println("CORE_GAP_COVERAGE=" + coreGapCoverage);
      if (coreGapCoverage < 0.95) {
        throw new IllegalStateException(
            "Core contact-pair gap coverage is " + coreGapCoverage);
      }

      model.label("Stage 574 local corneal patch structure checked");
      model.save("574g_stage574_local_cornea_patch_structure_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
