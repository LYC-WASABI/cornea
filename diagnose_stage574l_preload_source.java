import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class diagnose_stage574l_preload_source {
  private static final String OUT = "574l_diag_preload_source.mph";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static double[] global(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < raw.length; i++) values[i] = raw[i][0];
    return values;
  }

  private static double[] intSelection(Model model, String data, String tag, String selection, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < raw.length; i++) values[i] = raw[i][0];
    return values;
  }

  private static double[] intBoundary(Model model, String data, String tag, int boundary, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().set(new int[] {boundary});
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < raw.length; i++) values[i] = raw[i][0];
    return values;
  }

  private static String sci(double value) {
    return String.format(Locale.US, "%.12g", value);
  }

  private static void printContactSummary(Model model, String label, String sol) {
    String data = "dset_preload_" + label.replaceAll("[^A-Za-z0-9]", "");
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", sol);
    System.out.println("");
    System.out.println("## " + label + " solution=" + sol);
    double[] g = global(model, data, "eval_preload_" + label.replaceAll("[^A-Za-z0-9]", ""),
        new String[] {"q_scale574", "q_scale574*q_fixed574*1[mm]", "Fn_contact570"});
    System.out.println("GLOBAL q=" + sci(g[0]) + " disp=" + sci(g[1]) + " Fn_contact570=" + sci(g[2]));

    String[] expr = new String[] {
      "1",
      "if(isdefined(solid.Tn),solid.Tn,0)",
      "if(isdefined(solid.Tn),solid.Tn*X,0)",
      "if(isdefined(solid.Tn),solid.Tn*Y,0)",
      "if(isdefined(solid.Tn),solid.Tn*Z,0)",
      "if(isdefined(solid.Tn),if(solid.Tn>1[Pa],1,0),0)",
      "if(isdefined(geomgap_dst_cp_lid_cornea),if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)"
    };
    try {
      double[] patch = intSelection(model, data, "int_preload_patch_" + label.replaceAll("[^A-Za-z0-9]", ""),
          "sel_local_cornea_patch574", expr);
      double load = patch[1];
      System.out.println("LOCAL_PATCH area=" + sci(patch[0]) + " TnInt=" + sci(load)
          + " activeArea=" + sci(patch[5]) + " finiteGapArea=" + sci(patch[6]));
      if (Math.abs(load) > 1e-30) {
        System.out.println("LOCAL_PATCH Tn centroid=(" + sci(patch[2] / load) + ","
            + sci(patch[3] / load) + "," + sci(patch[4] / load) + ")");
      }
    } catch (Exception error) {
      System.out.println("LOCAL_PATCH_ERROR=" + error.getMessage());
    }
  }

  private static void printBoundaryBreakdown(Model model, String label, String sol, int[] boundaries) {
    String data = "dset_preload_bd_" + label.replaceAll("[^A-Za-z0-9]", "");
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", sol);
    System.out.println("");
    System.out.println("BOUNDARY_BREAKDOWN " + label + " " + sol);
    for (int b : boundaries) {
      try {
        double[] v = intBoundary(model, data, "int_preload_b" + b + "_" + label.replaceAll("[^A-Za-z0-9]", ""),
            b, new String[] {
              "1",
              "if(isdefined(solid.Tn),solid.Tn,0)",
              "if(isdefined(solid.Tn),if(solid.Tn>1[Pa],1,0),0)",
              "if(isdefined(geomgap_dst_cp_lid_cornea),if(abs(geomgap_dst_cp_lid_cornea)<1[m],1,0),0)"
            });
        if (Math.abs(v[1]) > 1e-9 || v[2] > 0) {
          System.out.println("B=" + b + " area=" + sci(v[0]) + " TnInt=" + sci(v[1])
              + " activeArea=" + sci(v[2]) + " finiteGapArea=" + sci(v[3]));
        }
      } catch (Exception error) {
        System.out.println("B=" + b + " ERROR=" + error.getMessage());
      }
    }
  }

  private static void printFeatureInventory(Model model) {
    ModelNode comp = model.component("comp1");
    System.out.println("");
    System.out.println("## Solid feature inventory");
    for (String tag : comp.physics("solid").feature().tags()) {
      try {
        PhysicsFeature f = comp.physics("solid").feature(tag);
        System.out.println("FEATURE " + tag + " type=" + f.getType() + " label=" + f.label());
        try {
          System.out.println("  selection=" + Arrays.toString(f.selection().entities(2)));
        } catch (Exception ignored) {}
        for (String p : new String[] {"U0", "FperArea", "LoadType", "constraintType", "prescribedPosition"}) {
          try {
            System.out.println("  " + p + "=" + Arrays.toString(f.getStringArray(p)));
          } catch (Exception ignored) {}
          try {
            System.out.println("  " + p + "=" + f.getString(p));
          } catch (Exception ignored) {}
        }
      } catch (Exception error) {
        System.out.println("FEATURE " + tag + " ERROR=" + error.getMessage());
      }
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "574l_stage574_structure_release_scan_results.mph");
      ModelNode comp = model.component("comp1");
      System.out.println("# Stage 574l preload source diagnostic");
      System.out.println("PAIR_SOURCE=" + Arrays.toString(comp.pair("cp_lid_cornea").source().entities()));
      System.out.println("PAIR_DEST=" + Arrays.toString(comp.pair("cp_lid_cornea").destination().entities()));
      System.out.println("LOCAL_PATCH=" + Arrays.toString(comp.selection("sel_local_cornea_patch574").entities(2)));
      printFeatureInventory(model);
      printContactSummary(model, "574l_q0", "sol130");
      printContactSummary(model, "574l_qminus04", "sol140");
      int[] candidates = new int[] {10, 16, 11, 12, 19, 20};
      printBoundaryBreakdown(model, "574l_q0", "sol130", candidates);
      printBoundaryBreakdown(model, "574l_qminus04", "sol140", candidates);
      model.save(OUT);
      System.out.println("SAVED=" + OUT);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
