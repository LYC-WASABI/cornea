import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage573_source_true_gap {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static void createEdgeBox(
      ModelNode comp, String tag, String label,
      String xmin, String xmax, String ymin, String ymax) {
    removeSelection(comp, tag);
    comp.selection().create(tag, "Box");
    comp.selection(tag).label(label);
    comp.selection(tag).set("entitydim", "1");
    comp.selection(tag).set("inputent", "selections");
    comp.selection(tag).set("input", new String[] {"sel_lid_edges_all573"});
    comp.selection(tag).set("xmin", xmin);
    comp.selection(tag).set("xmax", xmax);
    comp.selection(tag).set("ymin", ymin);
    comp.selection(tag).set("ymax", ymax);
    comp.selection(tag).set("zmin", "0[mm]");
    comp.selection(tag).set("zmax", "20[mm]");
    comp.selection(tag).set("condition", "inside");
  }

  private static double lineIntegral(
      Model model, String tag, String expression, String selection) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntLine");
    model.result().numerical(tag).set("data", "dset573_edge_class");
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static int[] toArray(List<Integer> values) {
    int[] result = new int[values.size()];
    for (int i = 0; i < result.length; i++) result[i] = values.get(i);
    return result;
  }

  private static void createExplicitEdges(
      ModelNode comp, String tag, String label, List<Integer> edges) {
    removeSelection(comp, tag);
    comp.selection().create(tag, "Explicit");
    comp.selection(tag).label(label);
    comp.selection(tag).geom("geom1", 1);
    comp.selection(tag).set(toArray(edges));
  }

  private static void classifyEdges(Model model, ModelNode comp) {
    removeDataset(model, "dset573_edge_class");
    model.result().dataset().create("dset573_edge_class", "Solution");
    model.result().dataset("dset573_edge_class").set("solution", "sol93");
    List<Integer> leading = new ArrayList<>();
    List<Integer> trailing = new ArrayList<>();
    List<Integer> left = new ArrayList<>();
    List<Integer> right = new ArrayList<>();
    int counter = 0;
    for (int edge : comp.selection("sel_lid_edges_all573").entities(1)) {
      String selection = "sel573_edge_tmp";
      removeSelection(comp, selection);
      comp.selection().create(selection, "Explicit");
      comp.selection(selection).geom("geom1", 1);
      comp.selection(selection).set(new int[] {edge});
      String prefix = "edge573_" + (++counter) + "_";
      double length = lineIntegral(
          model, prefix + "len", "1", selection);
      double xAverage = lineIntegral(
          model, prefix + "x", "x", selection) / length;
      double angleAverage = lineIntegral(
          model, prefix + "a", "atan2(y,z)", selection) / length;
      if (length > 2.0) {
        if (angleAverage < 0) leading.add(edge);
        else trailing.add(edge);
      } else {
        if (xAverage < 0) left.add(edge);
        else right.add(edge);
      }
    }
    removeSelection(comp, "sel573_edge_tmp");
    createExplicitEdges(
        comp, "sel_lid_leading573",
        "Stage 573 leading edge at negative material angle", leading);
    createExplicitEdges(
        comp, "sel_lid_trailing573",
        "Stage 573 trailing edge at positive material angle", trailing);
    createExplicitEdges(
        comp, "sel_lid_side_left573",
        "Stage 573 left lateral drainage edge", left);
    createExplicitEdges(
        comp, "sel_lid_side_right573",
        "Stage 573 right lateral drainage edge", right);
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solution created");
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "572_stage572_dynamic_motion_mask_checked.mph");
      ModelNode comp = model.component("comp1");
      Pair pair = comp.pair("cp_lid_cornea");
      String gap = pair.gapName(false);

      // Stage 572 scoped the motion variables to the corneal swept surface.
      // The prescribed lid displacement also needs the scalar motion law.
      comp.variable("var_dynamic_motion572").selection().all();

      model.param().set("stage573_revision", "573");
      model.param().set(
          "g_pair_limit573", "0.1[mm]",
          "Maximum finite source-pair gap accepted as mapped");
      model.param().set(
          "h_residual573", "0.05[um]",
          "Residual numerical film thickness in contact");
      model.param().set(
          "eps_h573", "0.02[um]",
          "Smooth positive-gap regularization width");
      model.param().set(
          "h_unmapped_drain573", "50[um]",
          "Open drainage thickness only at unmapped edge points");

      removeSelection(comp, "sel_lid_film573");
      comp.selection().create("sel_lid_film573", "Explicit");
      comp.selection("sel_lid_film573").label(
          "Stage 573 lid-attached source-side film surface");
      comp.selection("sel_lid_film573").geom("geom1", 2);
      comp.selection("sel_lid_film573").set(pair.source().entities());

      removeSelection(comp, "sel_lid_edges_all573");
      comp.selection().create("sel_lid_edges_all573", "Adjacent");
      comp.selection("sel_lid_edges_all573").label(
          "Stage 573 exterior edges of lid film surface");
      comp.selection("sel_lid_edges_all573").set("entitydim", "2");
      comp.selection("sel_lid_edges_all573").set("outputdim", "1");
      comp.selection("sel_lid_edges_all573")
          .set("input", new String[] {"sel_lid_film573"});
      comp.selection("sel_lid_edges_all573").set("exterior", "on");
      comp.selection("sel_lid_edges_all573").set("interior", "off");

      classifyEdges(model, comp);

      String vars = "var_source_gap573";
      try { comp.variable().remove(vars); } catch (Exception ignored) {}
      comp.variable().create(vars);
      comp.variable(vars).label(
          "Stage 573 source-side true paired film gap");
      comp.variable(vars).selection().named("sel_lid_film573");
      comp.variable(vars).set("g_pair_raw573", gap);
      comp.variable(vars).set(
          "g_pair_valid573",
          "if(isdefined(g_pair_raw573),"
              + "if(g_pair_raw573<g_pair_limit573,1,0),0)");
      comp.variable(vars).set(
          "g_pair_penetration573",
          "if(g_pair_valid573>0.5,min(g_pair_raw573,0[m]),0[m])");
      comp.variable(vars).set(
          "g_pair_open573",
          "if(g_pair_valid573>0.5,max(g_pair_raw573,0[m]),"
              + "h_unmapped_drain573)");
      comp.variable(vars).set(
          "g_pair_safe573",
          "if(isdefined(g_pair_raw573),"
              + "if(g_pair_raw573<g_pair_limit573,"
              + "g_pair_raw573,h_unmapped_drain573),"
              + "h_unmapped_drain573)");
      comp.variable(vars).set(
          "h_true573",
          "h_residual573+0.5*((g_pair_safe573-h_residual573)"
              + "+sqrt((g_pair_safe573-h_residual573)^2+eps_h573^2))");

      String study = "std573_source_gap_compile";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 573 compile source gap at scratch start");
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
      model.study(study).feature("stat").set("initsol", "sol93");
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
      dependent.set("initsol", "sol93");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol93");
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

      model.label("Stage 573 lid film domain and source gap setup");
      model.save("573b_stage573_lid_film_domain_setup.mph");
      String[] dynamicU0 = comp.physics("solid").feature("disp_lid_time")
          .getStringArray("U0");
      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "Y*(cos(phi_qs142)-1)-Z*sin(phi_qs142)"
                + "-dr_indent570*(Y*cos(phi_qs142)-Z*sin(phi_qs142))"
                + "/sqrt(Y^2+Z^2)",
            "Y*sin(phi_qs142)+Z*(cos(phi_qs142)-1)"
                + "-dr_indent570*(Y*sin(phi_qs142)+Z*cos(phi_qs142))"
                + "/sqrt(Y^2+Z^2)"
          });
      comp.variable("var_dynamic_motion572").set(
          "tau572", "time_offset572");
      model.sol(solution).runAll();
      comp.physics("solid").feature("disp_lid_time").set("U0", dynamicU0);
      comp.variable("var_dynamic_motion572").set(
          "tau572", "t+time_offset572");

      removeDataset(model, "dset573_source");
      model.result().dataset().create("dset573_source", "Solution");
      model.result().dataset("dset573_source").set("solution", solution);
      removeNumerical(model, "eval573_source");
      model.result().numerical().create(
          "eval573_source", "IntSurface");
      model.result().numerical("eval573_source")
          .set("data", "dset573_source");
      model.result().numerical("eval573_source")
          .selection().named("sel_lid_film573");
      model.result().numerical("eval573_source").set(
          "expr", new String[] {
            "1", "g_pair_valid573",
            "if(g_pair_valid573>0.5&&g_pair_raw573<0[m],1,0)",
            "if(g_pair_valid573>0.5,g_pair_raw573,0[m])",
            "h_true573"
          });
      double[][] integrals =
          model.result().numerical("eval573_source").getReal();
      double area = integrals[0][0];
      double valid = integrals[1][0];
      double negative = integrals[2][0];

      removeNumerical(model, "min573_htrue");
      model.result().numerical().create("min573_htrue", "MinSurface");
      model.result().numerical("min573_htrue")
          .set("data", "dset573_source");
      model.result().numerical("min573_htrue")
          .selection().named("sel_lid_film573");
      model.result().numerical("min573_htrue").set("expr", "h_true573");
      removeNumerical(model, "max573_htrue");
      model.result().numerical().create("max573_htrue", "MaxSurface");
      model.result().numerical("max573_htrue")
          .set("data", "dset573_source");
      model.result().numerical("max573_htrue")
          .selection().named("sel_lid_film573");
      model.result().numerical("max573_htrue").set("expr", "h_true573");

      System.out.println("SOLUTION=" + solution);
      System.out.println("SOURCE_GAP=" + gap);
      System.out.println("FILM_SURFACE=" + Arrays.toString(
          comp.selection("sel_lid_film573").entities(2)));
      System.out.println("LEADING=" + Arrays.toString(
          comp.selection("sel_lid_leading573").entities(1)));
      System.out.println("TRAILING=" + Arrays.toString(
          comp.selection("sel_lid_trailing573").entities(1)));
      System.out.println("LEFT=" + Arrays.toString(
          comp.selection("sel_lid_side_left573").entities(1)));
      System.out.println("RIGHT=" + Arrays.toString(
          comp.selection("sel_lid_side_right573").entities(1)));
      System.out.printf(Locale.US,
          "AREA=%.12g%nVALID_FRACTION=%.12g%nNEGATIVE_FRACTION=%.12g%n"
              + "AVG_VALID_RAW_GAP=%.12g%nAVG_TRUE_GAP=%.12g%n",
          area, valid / area, negative / area,
          integrals[3][0] / valid, integrals[4][0] / area);
      System.out.println("MIN_TRUE_GAP=" + Arrays.deepToString(
          model.result().numerical("min573_htrue").getReal()));
      System.out.println("MAX_TRUE_GAP=" + Arrays.deepToString(
          model.result().numerical("max573_htrue").getReal()));

      if (valid / area < 0.99) {
        throw new IllegalStateException(
            "Source pair coverage below 99 percent");
      }
      for (String tag : new String[] {
          "sel_lid_leading573", "sel_lid_trailing573",
          "sel_lid_side_left573", "sel_lid_side_right573"
      }) {
        if (comp.selection(tag).entities(1).length < 1) {
          throw new IllegalStateException(tag + " is empty");
        }
      }

      model.label("Stage 573 source-side true gap checked");
      model.save("573_stage573_source_true_gap_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
