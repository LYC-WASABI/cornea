import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage569_true_pair_gap {
  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<>(Arrays.asList(before));
    for (String tag : model.sol().tags()) {
      if (!old.contains(tag)) return tag;
    }
    throw new IllegalStateException("No new solver created");
  }

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeTable(Model model, String tag) {
    try { model.result().table().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removePlot(Model model, String tag) {
    try { model.result().remove(tag); }
    catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558v_stage567_structure_balance_results.mph");
      String comp = "comp1";
      ModelNode component = model.component(comp);
      String gap = "geomgap_dst_cp_lid_cornea";

      model.param().set(
          "h_pair_limit569", "0.1[mm]",
          "Maximum finite contact-pair gap accepted as mapped");
      model.param().set(
          "h_residual569", "0.05[um]",
          "Residual film thickness for later JFO regularization");
      model.param().set(
          "eps_h569", "0.02[um]",
          "Smooth positive-gap regularization width");
      model.param().set(
          "h_invalid_marker569", "-100[um]",
          "Display marker for film-track regions without opposing lid");

      String vars = "var_true_pair_gap569";
      try { component.variable().remove(vars); }
      catch (Exception ignored) {}
      component.variable().create(vars);
      component.variable(vars).label(
          "Stage 569 true deformed lid-cornea pair gap");
      component.variable(vars)
          .selection().named("sel_film_track");
      component.variable(vars).set(
          "pair_gap_native569", gap);
      component.variable(vars).set(
          "pair_map_valid569",
          "if(pair_gap_native569<h_pair_limit569,1,0)");
      component.variable(vars).set(
          "h_pair_raw569",
          "if(pair_map_valid569>0.5,pair_gap_native569,0[m])");
      component.variable(vars).set(
          "h_pair_open569",
          "if(pair_map_valid569>0.5,max(pair_gap_native569,0[m]),0[m])");
      component.variable(vars).set(
          "h_pair_penetration569",
          "if(pair_map_valid569>0.5,min(pair_gap_native569,0[m]),0[m])");
      component.variable(vars).set(
          "h_pair_regular569",
          "if(pair_map_valid569>0.5,"
              + "h_residual569+0.5*((pair_gap_native569-h_residual569)"
              + "+sqrt((pair_gap_native569-h_residual569)^2"
              + "+eps_h569^2)),0[m])");
      component.variable(vars).set(
          "h_pair_display569",
          "if(pair_map_valid569>0.5,"
              + "pair_gap_native569,h_invalid_marker569)");
      component.variable(vars).set(
          "pair_contact_state569",
          "if(pair_map_valid569>0.5,incontact_cp_lid_cornea,0)");
      component.variable(vars).set(
          "pair_contact_pressure569",
          "if(pair_map_valid569>0.5,solid.Tn,0[Pa])");
      component.variable(vars).set(
          "h_proxy_error569",
          "if(pair_map_valid569>0.5,"
              + "h_pair_raw569-h_geom555,0[m])");

      String study = "std_stage569_gap_compile";
      try { model.study().remove(study); } catch (Exception ignored) {}
      model.study().create(study);
      model.study(study).label(
          "Stage 569 structure recompile with true pair gap");
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
        component.physics("solid").feature(tag).set("StudyStep", step);
      }
      component.physics("ge_force_total111").feature("ge1")
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

      model.label("Stage 569 true deformed pair-gap setup");
      model.save("566a_stage569_true_pair_gap_setup.mph");
      System.out.println("SETUP=566a_stage569_true_pair_gap_setup.mph");
      System.out.println("SOLUTION=" + solution);
      model.sol(solution).runAll();
      model.label("Stage 569 true pair-gap compiled structure result");
      model.save("566b_stage569_true_pair_gap_compiled.mph");

      ModelUtil.remove("Model");
      model = ModelUtil.load(
          "Model", "566b_stage569_true_pair_gap_compiled.mph");

      String dataset = "dset569_pair_gap";
      removeDataset(model, dataset);
      model.result().dataset().create(dataset, "Solution");
      model.result().dataset(dataset).label(
          "Stage 569 pair gap on 558v structure solution");
      model.result().dataset(dataset).set("solution", solution);

      String table = "tbl569_pair_gap";
      removeTable(model, table);
      model.result().table().create(table, "Table");
      model.result().table(table).label(
          "Stage 569 true pair-gap diagnostics");

      String integral = "eval569_pair_gap";
      removeNumerical(model, integral);
      model.result().numerical().create(integral, "IntSurface");
      model.result().numerical(integral).label(
          "Stage 569 pair coverage and gap integrals");
      model.result().numerical(integral).set("data", dataset);
      model.result().numerical(integral)
          .selection().named("sel_film_track");
      model.result().numerical(integral).set("expr", new String[] {
          "1",
          "pair_map_valid569",
          "pair_contact_state569",
          "if(pair_map_valid569>0.5,h_pair_raw569,0[m])",
          "if(pair_map_valid569>0.5,h_pair_regular569,0[m])",
          "if(pair_map_valid569>0.5,abs(h_proxy_error569),0[m])",
          "if(pair_map_valid569>0.5"
              + "&&h_pair_raw569<0[m],1,0)"
      });
      model.result().numerical(integral).set("table", table);
      model.result().numerical(integral).setResult();

      String min = "min569_pair_gap";
      removeNumerical(model, min);
      model.result().numerical().create(min, "MinSurface");
      model.result().numerical(min).label(
          "Stage 569 minimum valid raw pair gap");
      model.result().numerical(min).set("data", dataset);
      model.result().numerical(min)
          .selection().named("sel_film_track");
      model.result().numerical(min).set(
          "expr",
          "if(pair_map_valid569>0.5,h_pair_raw569,1[m])");
      model.result().numerical(min).set("unit", "um");

      String max = "max569_pair_gap";
      removeNumerical(model, max);
      model.result().numerical().create(max, "MaxSurface");
      model.result().numerical(max).label(
          "Stage 569 maximum valid raw pair gap");
      model.result().numerical(max).set("data", dataset);
      model.result().numerical(max)
          .selection().named("sel_film_track");
      model.result().numerical(max).set(
          "expr",
          "if(pair_map_valid569>0.5,h_pair_raw569,-1[m])");
      model.result().numerical(max).set("unit", "um");

      String minReg = "min569_regular_gap";
      removeNumerical(model, minReg);
      model.result().numerical().create(minReg, "MinSurface");
      model.result().numerical(minReg).label(
          "Stage 569 minimum regularized gap");
      model.result().numerical(minReg).set("data", dataset);
      model.result().numerical(minReg)
          .selection().named("sel_film_track");
      model.result().numerical(minReg).set(
          "expr",
          "if(pair_map_valid569>0.5,h_pair_regular569,1[m])");
      model.result().numerical(minReg).set("unit", "um");

      String maxTn = "max569_contact_pressure";
      removeNumerical(model, maxTn);
      model.result().numerical().create(maxTn, "MaxSurface");
      model.result().numerical(maxTn).label(
          "Stage 569 maximum contact pressure");
      model.result().numerical(maxTn).set("data", dataset);
      model.result().numerical(maxTn)
          .selection().named("sel_film_track");
      model.result().numerical(maxTn)
          .set("expr", "pair_contact_pressure569");
      model.result().numerical(maxTn).set("unit", "Pa");

      String contactGap = "avg569_contact_gap";
      removeNumerical(model, contactGap);
      model.result().numerical().create(contactGap, "IntSurface");
      model.result().numerical(contactGap).label(
          "Stage 569 gap integral in active contact");
      model.result().numerical(contactGap).set("data", dataset);
      model.result().numerical(contactGap)
          .selection().named("sel_film_track");
      model.result().numerical(contactGap).set("expr", new String[] {
          "pair_contact_state569",
          "if(pair_contact_state569>0.5,h_pair_raw569,0[m])"
      });

      String pgGap = "pg569_true_gap";
      removePlot(model, pgGap);
      model.result().create(pgGap, "PlotGroup3D");
      model.result(pgGap).label(
          "Stage 569 true pair gap (invalid region = -100 um)");
      model.result(pgGap).set("data", dataset);
      model.result(pgGap).create("surf1", "Surface");
      model.result(pgGap).feature("surf1")
          .set("expr", "h_pair_display569");
      model.result(pgGap).feature("surf1").set("unit", "um");

      String pgMask = "pg569_pair_mask";
      removePlot(model, pgMask);
      model.result().create(pgMask, "PlotGroup3D");
      model.result(pgMask).label(
          "Stage 569 opposing-lid coverage mask");
      model.result(pgMask).set("data", dataset);
      model.result(pgMask).create("surf1", "Surface");
      model.result(pgMask).feature("surf1")
          .set("expr", "pair_map_valid569");

      String pgCompare = "pg569_gap_error";
      removePlot(model, pgCompare);
      model.result().create(pgCompare, "PlotGroup3D");
      model.result(pgCompare).label(
          "Stage 569 true pair gap minus old proxy gap");
      model.result(pgCompare).set("data", dataset);
      model.result(pgCompare).create("surf1", "Surface");
      model.result(pgCompare).feature("surf1")
          .set("expr", "h_proxy_error569");
      model.result(pgCompare).feature("surf1").set("unit", "um");

      double[][] integrals =
          model.result().numerical(integral).getReal();
      double totalArea = integrals[0][0];
      double mappedArea = integrals[1][0];
      double contactArea = integrals[2][0];
      double avgRaw = integrals[3][0] / mappedArea;
      double avgReg = integrals[4][0] / mappedArea;
      double avgProxyError = integrals[5][0] / mappedArea;
      double penetrationArea = integrals[6][0];
      double[][] contactValues =
          model.result().numerical(contactGap).getReal();
      double activeArea = contactValues[0][0];
      double avgContactGap = activeArea > 0
          ? contactValues[1][0] / activeArea : Double.NaN;

      System.out.printf(Locale.US,
          "TOTAL_AREA=%.12g%nMAPPED_AREA=%.12g%n"
              + "MAPPED_FRACTION=%.12g%nCONTACT_AREA=%.12g%n"
              + "CONTACT_FRACTION_OF_MAPPED=%.12g%n"
              + "AVG_RAW_GAP=%.12g%nAVG_REG_GAP=%.12g%n"
              + "AVG_ABS_PROXY_ERROR=%.12g%n"
              + "PENETRATION_AREA=%.12g%n"
              + "AVG_CONTACT_GAP=%.12g%n",
          totalArea, mappedArea, mappedArea / totalArea,
          contactArea, contactArea / mappedArea,
          avgRaw, avgReg, avgProxyError,
          penetrationArea, avgContactGap);
      System.out.println("MIN_RAW_GAP=" + Arrays.deepToString(
          model.result().numerical(min).getReal()));
      System.out.println("MAX_RAW_GAP=" + Arrays.deepToString(
          model.result().numerical(max).getReal()));
      System.out.println("MIN_REG_GAP=" + Arrays.deepToString(
          model.result().numerical(minReg).getReal()));
      System.out.println("MAX_CONTACT_PRESSURE="
          + Arrays.deepToString(
              model.result().numerical(maxTn).getReal()));

      model.label("Stage 569 true deformed pair-gap checked");
      model.save("566c_stage569_true_pair_gap_checked.mph");
      System.out.println(
          "RESULTS=566c_stage569_true_pair_gap_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
