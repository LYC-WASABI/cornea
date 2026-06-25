import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574_inset_source_film {
  private static void removeSelection(ModelNode component, String tag) {
    try { component.selection().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }

  private static void moveAfter(
      GeomSequence geometry, String feature, String after) {
    String[] tags = geometry.feature().tags();
    for (int i = 0; i < tags.length; i++) {
      if (after.equals(tags[i])) {
        geometry.feature().move(feature, i + 1);
        return;
      }
    }
    throw new IllegalStateException("Missing geometry feature " + after);
  }

  private static void createXPartition(
      GeomSequence geometry, String planeTag, String partitionTag,
      String input, String xPosition) {
    geometry.create(planeTag, "WorkPlane");
    geometry.feature(planeTag).set("planetype", "quick");
    geometry.feature(planeTag).set("quickplane", "yz");
    geometry.feature(planeTag).set("quickx", xPosition);
    moveAfter(geometry, planeTag, input);

    geometry.create(partitionTag, "PartitionFaces");
    moveAfter(geometry, partitionTag, planeTag);
    geometry.run(input);
    geometry.feature(partitionTag).selection("face").all(input);
    geometry.feature(partitionTag).set("partitionwith", "workplane");
    geometry.feature(partitionTag).set("workplane", planeTag);
    geometry.feature(partitionTag).set("selresult", "on");
    geometry.feature(partitionTag).set("selresultshow", "all");
    geometry.run(partitionTag);
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

  private static void createExplicitEdges(
      ModelNode component, String tag, String label, List<Integer> edges) {
    removeSelection(component, tag);
    component.selection().create(tag, "Explicit");
    component.selection(tag).label(label);
    component.selection(tag).geom("geom1", 1);
    int[] values = new int[edges.size()];
    for (int i = 0; i < values.length; i++) values[i] = edges.get(i);
    component.selection(tag).set(values);
  }

  private static void classifyFilmEdges(ModelNode component) {
    MeshSequence mesh = component.mesh("mesh1");
    double[][] coordinates = mesh.getVertex();
    int[][] elements = mesh.getElem("edg");
    int[] entities = mesh.getElemEntity("edg");
    int[] edges = component.selection("sel_lid_edges_all574").entities(1);

    List<Integer> left = new ArrayList<>();
    List<Integer> right = new ArrayList<>();
    List<Integer> angularEdges = new ArrayList<>();
    Map<Integer, Double> meanAngles = new HashMap<>();
    for (int edge : edges) {
      int[] vertices = edgeVertices(mesh, edge, elements, entities);
      double meanX = 0;
      double meanAngle = 0;
      for (int vertex : vertices) {
        double x = coordinates[0][vertex];
        double y = coordinates[1][vertex];
        double z = coordinates[2][vertex];
        meanX += x;
        meanAngle += Math.atan2(y, z);
      }
      meanX /= vertices.length;
      meanAngle /= vertices.length;
      if (Math.abs(meanX) > 3.0) {
        if (meanX < 0) left.add(edge);
        else right.add(edge);
      } else {
        angularEdges.add(edge);
        meanAngles.put(edge, meanAngle);
      }
    }
    double splitAngle = 0;
    for (int edge : angularEdges) splitAngle += meanAngles.get(edge);
    splitAngle /= angularEdges.size();
    List<Integer> leading = new ArrayList<>();
    List<Integer> trailing = new ArrayList<>();
    for (int edge : angularEdges) {
      if (meanAngles.get(edge) < splitAngle) leading.add(edge);
      else trailing.add(edge);
    }
    createExplicitEdges(
        component, "sel_lid_leading574",
        "Stage 574 physical leading drainage edge", leading);
    createExplicitEdges(
        component, "sel_lid_trailing574",
        "Stage 574 physical trailing drainage edge", trailing);
    createExplicitEdges(
        component, "sel_lid_side_left574",
        "Stage 574 inset left drainage edge", left);
    createExplicitEdges(
        component, "sel_lid_side_right574",
        "Stage 574 inset right drainage edge", right);
  }

  private static double surfaceValue(
      Model model, String tag, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", "dset574_gap");
    model.result().numerical(tag).selection().named("sel_lid_film574");
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573_stage573_source_true_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      GeomSequence geometry = comp.geom("geom1");

      model.param().set(
          "film_inset_side574", "0.15[mm]",
          "Side inset that removes pair-edge TFF degrees of freedom");
      model.param().set(
          "film_half_x574",
          "lid_cut_length/2-film_inset_side574");
      model.param().set("phi_probe574", "0[deg]");

      createXPartition(
          geometry, "wpf_lid_xneg574", "pf_lid_xneg574",
          "int_lid", "-film_half_x574");
      createXPartition(
          geometry, "wpf_lid_xpos574", "pf_lid_xpos574",
          "pf_lid_xneg574", "film_half_x574");
      geometry.feature("rot_lid").selection("input")
          .set(new String[] {"pf_lid_xpos574"});
      geometry.run();

      Pair contact = comp.pair("cp_lid_cornea");
      removeSelection(comp, "sel_lid_source_full574");
      comp.selection().create("sel_lid_source_full574", "Explicit");
      comp.selection("sel_lid_source_full574").label(
          "Stage 574 complete lid contact source after face partition");
      comp.selection("sel_lid_source_full574").geom("geom1", 2);
      comp.selection("sel_lid_source_full574").set(
          contact.source().entities());

      removeSelection(comp, "sel_lid_film574");
      comp.selection().create("sel_lid_film574", "Box");
      comp.selection("sel_lid_film574").label(
          "Stage 574 laterally inset lid-attached source film");
      comp.selection("sel_lid_film574").set("entitydim", "2");
      comp.selection("sel_lid_film574").set("inputent", "selections");
      comp.selection("sel_lid_film574").set(
          "input", new String[] {"sel_lid_source_full574"});
      comp.selection("sel_lid_film574").set(
          "xmin", "-film_half_x574-0.005[mm]");
      comp.selection("sel_lid_film574").set(
          "xmax", "film_half_x574+0.005[mm]");
      comp.selection("sel_lid_film574").set("ymin", "-10[mm]");
      comp.selection("sel_lid_film574").set("ymax", "10[mm]");
      comp.selection("sel_lid_film574").set("zmin", "-10[mm]");
      comp.selection("sel_lid_film574").set("zmax", "10[mm]");
      comp.selection("sel_lid_film574").set("condition", "inside");

      removeSelection(comp, "sel_lid_edges_all574");
      comp.selection().create("sel_lid_edges_all574", "Adjacent");
      comp.selection("sel_lid_edges_all574").label(
          "Stage 574 exterior edges of inset lid film");
      comp.selection("sel_lid_edges_all574").set("entitydim", "2");
      comp.selection("sel_lid_edges_all574").set("outputdim", "1");
      comp.selection("sel_lid_edges_all574").set(
          "input", new String[] {"sel_lid_film574"});
      comp.selection("sel_lid_edges_all574").set("exterior", "on");
      comp.selection("sel_lid_edges_all574").set("interior", "off");

      comp.variable("var_source_gap573").selection()
          .named("sel_lid_film574");
      comp.mesh("mesh1").run();
      classifyFilmEdges(comp);

      String[] dynamicU0 = comp.physics("solid").feature("disp_lid_time")
          .getStringArray("U0");
      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "Y*(cos(phi_probe574)-1)-Z*sin(phi_probe574)"
                + "-dr_indent570*(Y*cos(phi_probe574)"
                + "-Z*sin(phi_probe574))/sqrt(Y^2+Z^2)",
            "Y*sin(phi_probe574)+Z*(cos(phi_probe574)-1)"
                + "-dr_indent570*(Y*sin(phi_probe574)"
                + "+Z*cos(phi_probe574))/sqrt(Y^2+Z^2)"
          });

      String study = "std574_inset_gap";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate", new String[] {
            "solid", "on", "ge_force_total111", "on", "tff", "off",
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

      model.label("Stage 574 inset source film setup");
      model.save("574a_stage574_inset_source_film_setup.mph");
      model.sol(solution).runAll();
      comp.physics("solid").feature("disp_lid_time").set("U0", dynamicU0);

      removeDataset(model, "dset574_gap");
      model.result().dataset().create("dset574_gap", "Solution");
      model.result().dataset("dset574_gap").set("solution", solution);
      double area = surfaceValue(model, "int574_area", "1");
      double validArea = surfaceValue(
          model, "int574_valid", "g_pair_valid573");
      double negativeArea = surfaceValue(
          model, "int574_negative", "g_pair_raw573<0[m]");
      double averageGap = surfaceValue(
          model, "int574_gap", "g_pair_raw573") / area;
      System.out.println("SOLUTION=" + solution);
      System.out.println("FULL_SOURCE=" + Arrays.toString(
          comp.selection("sel_lid_source_full574").entities(2)));
      System.out.println("FILM_SOURCE=" + Arrays.toString(
          comp.selection("sel_lid_film574").entities(2)));
      System.out.println("FILM_EDGES=" + Arrays.toString(
          comp.selection("sel_lid_edges_all574").entities(1)));
      System.out.println("LEADING=" + Arrays.toString(
          comp.selection("sel_lid_leading574").entities(1)));
      System.out.println("TRAILING=" + Arrays.toString(
          comp.selection("sel_lid_trailing574").entities(1)));
      System.out.println("SIDE_LEFT=" + Arrays.toString(
          comp.selection("sel_lid_side_left574").entities(1)));
      System.out.println("SIDE_RIGHT=" + Arrays.toString(
          comp.selection("sel_lid_side_right574").entities(1)));
      System.out.println("AREA=" + area);
      System.out.println("VALID_FRACTION=" + validArea / area);
      System.out.println("NEGATIVE_FRACTION=" + negativeArea / area);
      System.out.println("AVERAGE_RAW_GAP=" + averageGap);

      model.label("Stage 574 inset source film and finite gap checked");
      model.save("574b_stage574_inset_source_gap_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
