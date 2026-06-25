import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574i_full_velocity {
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
          "Model", "574i_stage574_fixed_structure_constant_velocity_jfo_results.mph");
      ModelNode comp = model.component("comp1");
      model.param().set("lambda_v574", "1");
      try { comp.variable("var_dynamic_motion572").set("tau572", "time_offset572"); }
      catch (Exception ignored) {}
      System.out.println("time_offset572=" + model.param().evaluate("time_offset572"));
      double tSlide = model.param().evaluate("T_slide572");
      double thetaTotal = model.param().evaluate("theta_slide_total");
      double omegaMid = thetaTotal * 0.5 * Math.PI / tSlide;
      System.out.println("T_slide572=" + tSlide);
      System.out.println("theta_slide_total=" + thetaTotal);
      System.out.println("omega_mid_formula=" + omegaMid);
      System.out.println("omega_lid_rot572_rad_s_expr=" + comp.variable("var_dynamic_motion572").get("omega_lid_rot572"));

      String data = "dset574i_vel_probe";
      removeDataset(model, data);
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", "sol121");

      removeNumerical(model, "int574i_vel_probe");
      model.result().numerical().create("int574i_vel_probe", "IntSurface");
      model.result().numerical("int574i_vel_probe").set("data", data);
      model.result().numerical("int574i_vel_probe")
          .selection().named("sel_local_cornea_patch574");
      model.result().numerical("int574i_vel_probe").set("expr", new String[] {
        "1",
        "sqrt((lambda_v574*(theta_slide_total*0.5*pi/T_slide572)*Z)^2"
            + "+(lambda_v574*(theta_slide_total*0.5*pi/T_slide572)*Y)^2)",
        "abs(lambda_v574*(theta_slide_total*0.5*pi/T_slide572)*Z)",
        "abs(lambda_v574*(theta_slide_total*0.5*pi/T_slide572)*Y)"
      });
      double[][] ints = model.result().numerical("int574i_vel_probe").getReal();
      double area = ints[0][0];
      System.out.println("patch_area=" + area);
      System.out.println("mean_abs_vw=" + ints[1][0] / area);
      System.out.println("mean_abs_vwy=" + ints[2][0] / area);
      System.out.println("mean_abs_vwz=" + ints[3][0] / area);

      for (String type : new String[] {"MinSurface", "MaxSurface"}) {
        String tag = ("MinSurface".equals(type) ? "min" : "max") + "574i_vmag_probe";
        removeNumerical(model, tag);
        model.result().numerical().create(tag, type);
        model.result().numerical(tag).set("data", data);
        model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
        model.result().numerical(tag).set("expr",
            "sqrt((lambda_v574*(theta_slide_total*0.5*pi/T_slide572)*Z)^2"
                + "+(lambda_v574*(theta_slide_total*0.5*pi/T_slide572)*Y)^2)");
        System.out.println(tag + "=" + model.result().numerical(tag).getReal()[0][0]);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
