import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558v_double_surface_mapping {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solver created");
  }

  private static void surface(
      Model model, String tag, String type, String expr) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset_map569");
    model.result().numerical(tag).selection().named("sel_film_track");
    model.result().numerical(tag).set("expr", expr);
    System.out.println(tag + "|" + expr + "|"
        + Arrays.deepToString(
            model.result().numerical(tag).getReal()));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558v_stage567_structure_balance_results.mph");
      ModelNode comp = model.component("comp1");

      try { comp.cpl().remove("genext_lid569p"); }
      catch (Exception ignored) {}
      comp.cpl().create("genext_lid569p", "GeneralExtrusion");
      var op = comp.cpl("genext_lid569p");
      op.set("opname", "genextlid569p");
      op.selection().named("sel_lid_contact_source_robust");
      op.set("srcframe", "material");
      op.set("usesrcmap", "on");
      op.set("srcmap", new String[] {"X", "atan2(Y,Z)", "0"});
      op.set("dstmap", new String[] {"X", "atan2(Y,Z)", "0"});
      op.set("manualsearchdist", "on");
      op.set("searchdist", "3[mm]");

      String vars = "var_gap569_probe";
      try { comp.variable().remove(vars); } catch (Exception ignored) {}
      comp.variable().create(vars);
      comp.variable(vars).selection().named("sel_film_track");
      comp.variable(vars).set("xl569p", "genextlid569p(X+u)");
      comp.variable(vars).set("yl569p", "genextlid569p(Y+v)");
      comp.variable(vars).set("zl569p", "genextlid569p(Z+w)");
      comp.variable(vars).set("xc569p", "X+u");
      comp.variable(vars).set("yc569p", "Y+v");
      comp.variable(vars).set("zc569p", "Z+w");
      comp.variable(vars).set("dx569p", "xl569p-xc569p");
      comp.variable(vars).set("dy569p", "yl569p-yc569p");
      comp.variable(vars).set("dz569p", "zl569p-zc569p");
      comp.variable(vars).set(
          "hproj569p", "dx569p*nx+dy569p*ny+dz569p*nz");
      comp.variable(vars).set(
          "hprojneg569p", "-hproj569p");
      comp.variable(vars).set(
          "hdist569p",
          "sqrt(dx569p^2+dy569p^2+dz569p^2)");
      comp.variable(vars).set(
          "mapvalid569p", "genextlid569p(1)");

      String study = "std_probe_gap569";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label("Stage 569 mapped-gap structure recompile");
      model.study(study).create("stat", "Stationary");
      model.study(study).feature("stat")
          .set("geometricNonlinearity", "on");
      model.study(study).feature("stat").set(
          "activate",
          new String[] {
            "solid", "on", "ge_force_total111", "on", "tff", "off",
            "frame:spatial1", "on", "frame:material1", "on",
            "comp1", "on"
          });
      model.study(study).feature("stat").set("useinitsol", "on");
      model.study(study).feature("stat").set("initmethod", "sol");
      model.study(study).feature("stat").set("initsol", "sol91");
      model.study(study).feature("stat").set("initsoluse", "current");
      String step = study + "/stat";
      for (String tag : new String[] {
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm"
      }) {
        comp.physics("solid").feature(tag).set("StudyStep", step);
      }
      comp.physics("ge_force_total111").feature("ge1")
          .set("StudyStep", step);
      String[] before = model.sol().tags();
      model.study(study).createAutoSequences("sol");
      String solution = newest(model, before);
      SolverFeature dependent = model.sol(solution).feature("v1");
      dependent.set("initmethod", "sol");
      dependent.set("initsol", "sol91");
      dependent.set("solnum", "last");
      dependent.set("notsolmethod", "sol");
      dependent.set("notsol", "sol91");
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
      model.save("probe_558v_double_surface_mapping_setup.mph");
      model.sol(solution).runAll();
      model.save("probe_558v_double_surface_mapping_results.mph");
      ModelUtil.remove("Model");
      model = ModelUtil.load(
          "Model", "probe_558v_double_surface_mapping_results.mph");

      try { model.result().dataset().remove("dset_map569"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset_map569", "Solution");
      model.result().dataset("dset_map569").set("solution", solution);

      int i = 0;
      for (String expr : new String[] {
          "mapvalid569p", "xl569p", "yl569p", "zl569p",
          "hproj569p", "hprojneg569p", "hdist569p",
          "geomgap_dst_cp_lid_cornea", "incontact_cp_lid_cornea",
          "solid.Tn"
      }) {
        try { surface(model, "min_map569_" + (++i), "MinSurface", expr); }
        catch (Exception error) {
          System.out.println("MIN_ERROR|" + expr + "|" + error.getMessage());
        }
        try { surface(model, "max_map569_" + (++i), "MaxSurface", expr); }
        catch (Exception error) {
          System.out.println("MAX_ERROR|" + expr + "|" + error.getMessage());
        }
        try { surface(model, "avg_map569_" + (++i), "AvSurface", expr); }
        catch (Exception error) {
          System.out.println("AVG_ERROR|" + expr + "|" + error.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
