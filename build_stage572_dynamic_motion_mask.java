import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage572_dynamic_motion_mask {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static double[] evaluateMask(
      Model model, String eval, double centerDegrees) {
    String center = String.format(Locale.US, "%.12g[deg]", centerDegrees);
    String mask =
        "(0.25*(1+tanh((x+3.81[mm])/0.05[mm]))"
            + "*(1+tanh((3.81[mm]-x)/0.05[mm])))"
            + "*(0.25*(1+tanh((atan2(y,z)-(" + center
            + ")+3.89[deg])/0.05[deg]))"
            + "*(1+tanh(((" + center
            + ")+3.89[deg]-atan2(y,z))/0.05[deg])))";
    model.result().numerical(eval).set("expr", new String[] {
        mask, "(" + mask + ")*x", "(" + mask + ")*atan2(y,z)"
    });
    double[][] values = model.result().numerical(eval).getReal();
    double area = values[0][0];
    return new double[] {
      area, values[1][0] / area, values[2][0] / area
    };
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "571_stage571_swept_film_domain_checked.mph");
      ModelNode comp = model.component("comp1");

      model.param().set("stage572_revision", "572");
      model.param().set(
          "T_pre572", "0.01[s]", "Initial stationary hold");
      model.param().set(
          "T_slide572", "L_slide/v_blink_avg",
          "Physical scratch time at the prescribed blink speed");
      model.param().set(
          "T_hold572", "0.01[s]", "Final stationary hold");
      model.param().set(
          "T_end572", "T_pre572+T_slide572+T_hold572");
      model.param().set(
          "time_offset572", "0[s]",
          "Zero in transient solves; used only for stationary mask checks");
      model.param().set(
          "lid_mask_xhalf572", "3.81[mm]",
          "Half width of the mapped rounded lid footprint");
      model.param().set(
          "lid_mask_ahalf572", "3.89[deg]",
          "Angular half length of the mapped lid footprint");
      model.param().set(
          "lid_mask_aoffset572", "0.10[deg]",
          "Small angular offset measured from the Stage 569 footprint");
      model.param().set(
          "lid_mask_epsx572", "0.05[mm]",
          "Smooth lateral footprint transition");
      model.param().set(
          "lid_mask_epsa572", "0.05[deg]",
          "Smooth angular footprint transition");

      String vars = "var_dynamic_motion572";
      try { comp.variable().remove(vars); } catch (Exception ignored) {}
      comp.variable().create(vars);
      comp.variable(vars).label(
          "Stage 572 full-path motion and moving lid footprint");
      comp.variable(vars).selection().named("sel_film_swept571");
      comp.variable(vars).set("tau572", "t+time_offset572");
      comp.variable(vars).set(
          "slide_fraction572",
          "if(tau572<T_pre572,0,"
              + "if(tau572<T_pre572+T_slide572,"
              + "0.5-0.5*cos(pi*(tau572-T_pre572)/T_slide572),1))");
      comp.variable(vars).set(
          "phi_lid_rot572", "theta_slide_total*slide_fraction572");
      comp.variable(vars).set(
          "theta_lid_physical572",
          "-35[deg]+70[deg]*slide_fraction572");
      comp.variable(vars).set(
          "theta_lid_spatial572",
          "-theta_lid_physical572+lid_mask_aoffset572");
      comp.variable(vars).set(
          "omega_lid_rot572",
          "if(tau572<T_pre572||tau572>T_pre572+T_slide572,"
              + "0[rad/s],theta_slide_total*0.5*pi/T_slide572"
              + "*sin(pi*(tau572-T_pre572)/T_slide572))");
      comp.variable(vars).set(
          "theta_surface572", "atan2(y,z)");
      comp.variable(vars).set(
          "M_lid_x572",
          "0.25*(1+tanh((x+lid_mask_xhalf572)/lid_mask_epsx572))"
              + "*(1+tanh((lid_mask_xhalf572-x)/lid_mask_epsx572))");
      comp.variable(vars).set(
          "M_lid_a572",
          "0.25*(1+tanh((theta_surface572-theta_lid_spatial572"
              + "+lid_mask_ahalf572)/lid_mask_epsa572))"
              + "*(1+tanh((theta_lid_spatial572+lid_mask_ahalf572"
              + "-theta_surface572)/lid_mask_epsa572))");
      comp.variable(vars).set(
          "M_lid572", "M_lid_x572*M_lid_a572");

      comp.variable("var_dynamic_lid_motion").set(
          "slide_fraction", "slide_fraction572");
      comp.variable("var_dynamic_lid_motion").set(
          "phi_lid_dyn", "phi_lid_rot572");
      comp.variable("var_dynamic_lid_motion").set(
          "theta_lid_physical", "theta_lid_physical572");
      comp.variable("var_partitioned_local_pfilm").set(
          "slide_fraction_structure", "slide_fraction572");
      comp.variable("var_partitioned_local_pfilm").set(
          "phi_lid_structure", "phi_lid_rot572");

      comp.physics("solid").feature("disp_lid_time").set(
          "U0", new String[] {
            "0",
            "Y*(cos(phi_lid_rot572)-1)-Z*sin(phi_lid_rot572)"
                + "-dr_indent570*(Y*cos(phi_lid_rot572)"
                + "-Z*sin(phi_lid_rot572))/sqrt(Y^2+Z^2)",
            "Y*sin(phi_lid_rot572)+Z*(cos(phi_lid_rot572)-1)"
                + "-dr_indent570*(Y*sin(phi_lid_rot572)"
                + "+Z*cos(phi_lid_rot572))/sqrt(Y^2+Z^2)"
          });

      model.label("Stage 572 dynamic motion mask setup");
      model.save("572a_stage572_dynamic_motion_mask_setup.mph");

      removeDataset(model, "dset572_mask");
      model.result().dataset().create("dset572_mask", "Solution");
      model.result().dataset("dset572_mask").set("solution", "sol93");
      String eval = "eval572_mask";
      removeNumerical(model, eval);
      model.result().numerical().create(eval, "IntSurface");
      model.result().numerical(eval).set("data", "dset572_mask");
      model.result().numerical(eval)
          .selection().named("sel_film_swept571");
      double tPre = model.param().evaluate("T_pre572");
      double tSlide = model.param().evaluate("T_slide572");
      double[] start = evaluateMask(model, eval, 35.1);
      double[] middle = evaluateMask(model, eval, 0.1);
      double[] end = evaluateMask(model, eval, -34.9);
      model.param().set("time_offset572", "0[s]");

      System.out.printf(Locale.US,
          "T_SLIDE=%.12g%n"
              + "START_AREA=%.12g START_X=%.12g START_ANGLE_DEG=%.12g%n"
              + "MID_AREA=%.12g MID_X=%.12g MID_ANGLE_DEG=%.12g%n"
              + "END_AREA=%.12g END_X=%.12g END_ANGLE_DEG=%.12g%n",
          tSlide,
          start[0], start[1], start[2] * 180.0 / Math.PI,
          middle[0], middle[1], middle[2] * 180.0 / Math.PI,
          end[0], end[1], end[2] * 180.0 / Math.PI);

      model.label("Stage 572 dynamic motion and mask checked");
      model.save("572_stage572_dynamic_motion_mask_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
