import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class diagnose_stage576d_node9_pressure_spike {
  private static final String BASE = "576c_stage576_partitioned_constant_load_results.mph";
  private static final String OUT = "576d_stage576_node9_pressure_spike_diagnostic.mph";

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static String sci(double value) {
    return String.format(Locale.US, "%.12g", value);
  }

  private static double surface(Model model, String data, String tag, String type, String expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static double[] integrate(Model model, String data, String tag, String[] expr) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expr);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] values = new double[raw.length];
    for (int i = 0; i < raw.length; i++) values[i] = raw[i][0];
    return values;
  }

  private static void summarizeRegion(Model model, String data, String prefix, String name, String mask) {
    double[] v = integrate(model, data, prefix + "_" + name, new String[] {
      mask,
      "(" + mask + ")*X",
      "(" + mask + ")*Y",
      "(" + mask + ")*Z",
      "(" + mask + ")*theta_surface572",
      "(" + mask + ")*geomgap_dst_cp_lid_cornea",
      "(" + mask + ")*h_calc573",
      "(" + mask + ")*Bfilm573",
      "(" + mask + ")*B_low573",
      "(" + mask + ")*B_high573",
      "(" + mask + ")*Afilm573",
      "(" + mask + ")*M_core573",
      "(" + mask + ")*M_drain573",
      "(" + mask + ")*(tff.p-p_amb573)",
      "(" + mask + ")*p_load573",
      "(" + mask + ")*Qvent573",
      "(" + mask + ")*tff.theta"
    });
    double a = v[0];
    System.out.println("REGION name=" + name + " area=" + sci(a));
    if (!Double.isFinite(a) || Math.abs(a) < 1e-30) return;
    System.out.printf(Locale.US,
        "REGION_VALUES name=%s centroid=(%.12g,%.12g,%.12g) thetaSurface=%.12g gap=%.12g h=%.12g Bfilm=%.12g Blow=%.12g Bhigh=%.12g Afilm=%.12g Mcore=%.12g Mdrain=%.12g pressure=%.12g pload=%.12g Qvent=%.12g theta=%.12g%n",
        name, v[1]/a, v[2]/a, v[3]/a, v[4]/a, v[5]/a, v[6]/a, v[7]/a,
        v[8]/a, v[9]/a, v[10]/a, v[11]/a, v[12]/a, v[13]/a, v[14]/a,
        v[15]/a, v[16]/a);
  }

  private static void summarizeWeighted(Model model, String data, String prefix, String name, String weight) {
    double[] v = integrate(model, data, prefix + "_weighted_" + name, new String[] {
      weight,
      "(" + weight + ")*X",
      "(" + weight + ")*Y",
      "(" + weight + ")*Z",
      "(" + weight + ")*theta_surface572",
      "(" + weight + ")*g_pair_safe573",
      "(" + weight + ")*h_calc573",
      "(" + weight + ")*Bfilm573",
      "(" + weight + ")*B_low573",
      "(" + weight + ")*B_high573",
      "(" + weight + ")*Afilm573",
      "(" + weight + ")*M_core573",
      "(" + weight + ")*M_drain573",
      "(" + weight + ")*(tff.p-p_amb573)",
      "(" + weight + ")*p_load573",
      "(" + weight + ")*Qvent573",
      "(" + weight + ")*tff.theta"
    });
    double w = v[0];
    System.out.println("WEIGHTED name=" + name + " integral=" + sci(w));
    if (!Double.isFinite(w) || Math.abs(w) < 1e-30) return;
    System.out.printf(Locale.US,
        "WEIGHTED_VALUES name=%s centroid=(%.12g,%.12g,%.12g) thetaSurface=%.12g safeGap=%.12g h=%.12g Bfilm=%.12g Blow=%.12g Bhigh=%.12g Afilm=%.12g Mcore=%.12g Mdrain=%.12g pressure=%.12g pload=%.12g Qvent=%.12g theta=%.12g%n",
        name, v[1]/w, v[2]/w, v[3]/w, v[4]/w, v[5]/w, v[6]/w, v[7]/w,
        v[8]/w, v[9]/w, v[10]/w, v[11]/w, v[12]/w, v[13]/w, v[14]/w,
        v[15]/w, v[16]/w);
  }

  private static void diagnose(Model model, String name, String solution) {
    String data = "dset576d_" + name;
    removeDataset(model, data);
    model.result().dataset().create(data, "Solution");
    model.result().dataset(data).set("solution", solution);
    double[] whole = integrate(model, data, "int576d_" + name + "_whole",
        new String[] {"1", "p_load573", "M_core573", "M_drain573", "Qvent573"});
    double maxP = surface(model, data, "max576d_" + name + "_p", "MaxSurface", "tff.p-p_amb573");
    double maxLoad = surface(model, data, "max576d_" + name + "_pload", "MaxSurface", "p_load573");
    double minH = surface(model, data, "min576d_" + name + "_h", "MinSurface", "h_calc573");
    double maxH = surface(model, data, "max576d_" + name + "_h", "MaxSurface", "h_calc573");
    double minGap = surface(model, data, "min576d_" + name + "_g", "MinSurface", "geomgap_dst_cp_lid_cornea");
    double maxGap = surface(model, data, "max576d_" + name + "_g", "MaxSurface",
        "if(abs(geomgap_dst_cp_lid_cornea)<1[m],geomgap_dst_cp_lid_cornea,-1[m])");
    System.out.printf(Locale.US,
        "STATE name=%s sol=%s area=%.12g Ffilm=%.12g meanCore=%.12g meanDrain=%.12g intQvent=%.12g maxP=%.12g maxPload=%.12g hRange=[%.12g,%.12g] gapRange=[%.12g,%.12g]%n",
        name, solution, whole[0], whole[1], whole[2]/whole[0], whole[3]/whole[0], whole[4],
        maxP, maxLoad, minH, maxH, minGap, maxGap);
    String nearP = "if((tff.p-p_amb573)>" + sci(0.9*maxP) + "[Pa],1,0)";
    String nearLoad = "if(p_load573>" + sci(0.9*maxLoad) + "[Pa],1,0)";
    String coreEdge = "if(abs(M_core573-0.5)<0.4,1,0)";
    String drainEdge = "if(abs(M_drain573-0.5)<0.4,1,0)";
    summarizeRegion(model, data, "int576d_" + name, name + "_pressure90", nearP);
    summarizeRegion(model, data, "int576d_" + name, name + "_pload90", nearLoad);
    summarizeRegion(model, data, "int576d_" + name, name + "_core_transition", coreEdge);
    summarizeRegion(model, data, "int576d_" + name, name + "_drain_transition", drainEdge);
    summarizeWeighted(model, data, "int576d_" + name, name + "_positive_pressure", "max(tff.p-p_amb573,0[Pa])");
    summarizeWeighted(model, data, "int576d_" + name, name + "_positive_pload", "max(p_load573,0[Pa])");
  }

  private static void addPlot(Model model, String tag, String label, String data, String expr) {
    try { model.result().remove(tag); } catch (Exception ignored) {}
    model.result().create(tag, "PlotGroup3D");
    model.result(tag).label(label);
    model.result(tag).set("data", data);
    model.result(tag).feature().create("surf1", "Surface");
    model.result(tag).feature("surf1").set("expr", expr);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      System.out.println("BASE=" + BASE);
      diagnose(model, "node8", "sol298");
      diagnose(model, "node9", "sol358");
      diagnose(model, "node10", "sol458");
      addPlot(model, "pg576d_p", "Stage 576d node 9 pressure", "dset576d_node9", "tff.p-p_amb573");
      addPlot(model, "pg576d_h", "Stage 576d node 9 film thickness", "dset576d_node9", "h_calc573");
      addPlot(model, "pg576d_b", "Stage 576d node 9 film gate", "dset576d_node9", "Bfilm573");
      addPlot(model, "pg576d_core", "Stage 576d node 9 moving core", "dset576d_node9", "M_core573");
      addPlot(model, "pg576d_vent", "Stage 576d node 9 vent source", "dset576d_node9", "Qvent573");
      model.label("Stage 576d node 9 pressure-spike diagnostic");
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
