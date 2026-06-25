import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage573_cornea_dynamic_regions {
  private static double drainHalfDegrees = 3.89;

  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); }
    catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static double[] evaluate(
      Model model, String tag, String[] expressions) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "IntSurface");
    model.result().numerical(tag).set("data", "dset573_regions");
    model.result().numerical(tag)
        .selection().named("sel_film_swept571");
    model.result().numerical(tag).set("expr", expressions);
    double[][] raw = model.result().numerical(tag).getReal();
    double[] result = new double[raw.length];
    for (int i = 0; i < result.length; i++) result[i] = raw[i][0];
    return result;
  }

  private static String xMask() {
    return "0.25*(1+tanh((x+3.81[mm])/0.05[mm]))"
        + "*(1+tanh((3.81[mm]-x)/0.05[mm]))";
  }

  private static String angularMask(
      double centerDegrees, double halfWidthDegrees) {
    String center =
        String.format(Locale.US, "%.16g[deg]", centerDegrees);
    String halfWidth =
        String.format(Locale.US, "%.16g[deg]", halfWidthDegrees);
    return "0.25*(1+tanh((atan2(y,z)-(" + center + ")"
        + "+" + halfWidth + ")/0.05[deg]))"
        + "*(1+tanh(((" + center + ")+" + halfWidth
        + "-atan2(y,z))/0.05[deg]))";
  }

  private static String coreMask(double centerDegrees) {
    return "(" + xMask() + ")*("
        + angularMask(centerDegrees, 3.89) + ")";
  }

  private static String drainMask(double centerDegrees) {
    return "(" + xMask() + ")*("
        + angularMask(centerDegrees, drainHalfDegrees) + ")";
  }

  private static double[] evaluateAt(
      Model model, String tag, double centerDegrees) {
    String core = coreMask(centerDegrees);
    String drain = drainMask(centerDegrees);
    return evaluate(model, tag, new String[] {
        "1",
        core,
        drain,
        "max(1-(" + drain + "),0)",
        "(" + core + ")*x",
        "(" + core + ")*atan2(y,z)",
        "(" + drain + ")*x",
        "(" + drain + ")*atan2(y,z)"
    });
  }

  private static void requireFinite(
      String label, double[] values) {
    for (double value : values) {
      if (!Double.isFinite(value)) {
        throw new IllegalStateException(label + " contains " + value);
      }
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "572_stage572_dynamic_motion_mask_checked.mph");
      ModelNode comp = model.component("comp1");
      String destinationGap =
          comp.pair("cp_lid_cornea").gapName(true);
      drainHalfDegrees = 3.89
          + model.param().evaluate("0.25[mm]/Rcor")
          * 180.0 / Math.PI;

      model.param().set(
          "stage573_revision", "573",
          "Fixed corneal swept-domain dynamic JFO regions");
      model.param().set(
          "drain_buffer573", "0.25[mm]",
          "Initial drainage extension before and after lid footprint");
      model.param().set(
          "drain_angle573", "drain_buffer573/Rcor",
          "Angular drainage extension on the cornea");
      model.param().set(
          "h_break573", "0.05[um]",
          "Physical tear-film rupture threshold");
      model.param().set(
          "dh_break573", "0.005[um]",
          "Smooth rupture transition width");
      model.param().set(
          "h_background573", "3[um]",
          "Background tear-film thickness outside the active region");
      model.param().set(
          "h_num573", "0.005[um]",
          "Numerical positive thickness floor in wet regions");
      model.param().set(
          "eps_h_num573", "0.001[um]",
          "Numerical thickness-floor smoothing width");
      model.param().set(
          "g_pair_limit573", "0.1[mm]",
          "Largest destination gap accepted as contact-pair mapped");
      model.param().set(
          "kvent573", "1e-7[kg/(m^2*s*Pa)]",
          "Initial distributed pressure-relief coefficient");
      model.param().set(
          "kanchor573", "1e-7[kg/(m^2*s*Pa)]",
          "Numerical pressure anchor in fully cavitated open regions");
      model.param().set(
          "p_amb573", "pfilm_ambient520",
          "Ambient tear-film gauge pressure");

      String variables = "var_cornea_dynamic_regions573";
      try { comp.variable().remove(variables); }
      catch (Exception ignored) {}
      comp.variable().create(variables);
      comp.variable(variables).label(
          "Stage 573 fixed-cornea moving wet and drainage regions");
      comp.variable(variables).selection().named("sel_film_swept571");
      comp.variable(variables).set("M_core573", "M_lid572");
      comp.variable(variables).set(
          "M_drain_a573",
          "0.25*(1+tanh((theta_surface572-theta_lid_spatial572"
              + "+lid_mask_ahalf572+drain_angle573)"
              + "/lid_mask_epsa572))"
              + "*(1+tanh((theta_lid_spatial572+lid_mask_ahalf572"
              + "+drain_angle573-theta_surface572)"
              + "/lid_mask_epsa572))");
      comp.variable(variables).set(
          "M_drain573", "M_lid_x572*M_drain_a573");
      comp.variable(variables).set(
          "M_open573", "max(1-M_drain573,0)");
      comp.variable(variables).set(
          "g_pair_native573", destinationGap);
      comp.variable(variables).set(
          "g_pair_valid573",
          "if(isdefined(g_pair_native573),"
              + "if(abs(g_pair_native573)<g_pair_limit573,1,0),0)");
      comp.variable(variables).set(
          "g_pair_safe573",
          "if(isdefined(g_pair_native573),"
              + "if(abs(g_pair_native573)<g_pair_limit573,"
              + "g_pair_native573,h_background573),"
              + "h_background573)");
      comp.variable(variables).set(
          "Bfilm573",
          "g_pair_valid573*0.5*(1+tanh("
              + "(g_pair_safe573-h_break573)/dh_break573))");
      comp.variable(variables).set(
          "h_wet573",
          "h_num573+0.5*((g_pair_safe573-h_num573)"
              + "+sqrt((g_pair_safe573-h_num573)^2"
              + "+eps_h_num573^2))");
      comp.variable(variables).set(
          "Afilm573",
          "M_core573*Bfilm573"
              + "+max(M_drain573-M_core573,0)");
      comp.variable(variables).set(
          "h_calc573",
          "Afilm573*h_wet573"
              + "+(1-Afilm573)*h_background573");
      comp.variable(variables).set(
          "Qvent573",
          "-kvent573*(1-Afilm573)*(tff.p-p_amb573)");
      comp.variable(variables).set(
          "p_load573",
          "M_core573*Bfilm573*(tff.p-p_amb573)");
      comp.variable(variables).set(
          "p_open_diagnostic573",
          "(1-M_core573)*(tff.p-p_amb573)");
      comp.variable(variables).set(
          "p_drain_diagnostic573",
          "max(M_drain573-M_core573,0)*(tff.p-p_amb573)");
      comp.variable(variables).set(
          "p_ruptured_diagnostic573",
          "M_core573*(1-Bfilm573)*(tff.p-p_amb573)");

      String diagnostics = "var_cornea_dynamic_loads573";
      try { comp.variable().remove(diagnostics); }
      catch (Exception ignored) {}
      comp.variable().create(diagnostics);
      comp.variable(diagnostics).label(
          "Stage 573 fixed-cornea load diagnostics");
      comp.variable(diagnostics).set(
          "Fn_film573", "intop_film(p_load573)");
      comp.variable(diagnostics).set(
          "Fn_contact573", "Fn_contact570");
      comp.variable(diagnostics).set(
          "Fn_total573", "Fn_contact573+Fn_film573");

      comp.physics("tff").selection().named("sel_film_swept571");
      comp.physics("tff").feature("ffp1").set("hw1", "h_calc573");
      comp.physics("tff").feature("ffp1").set("hb1", "0");
      comp.physics("tff").feature("ffp1")
          .set("TangentialBaseVelocity", "Off");
      comp.physics("tff").feature("ffp1")
          .set("TangentialWallVelocity", "userdef");
      comp.physics("tff").feature("ffp1").set(
          "vw", new String[] {
            "0",
            "-M_core573*omega_lid_rot572*Z",
            "M_core573*omega_lid_rot572*Y"
          });
      comp.physics("tff").feature("init1")
          .set("pfilm", "p_amb573");
      comp.cpl("intop_film").selection().named("sel_film_swept571");

      String vent = "ms_vent573";
      try { comp.physics("tff").feature().remove(vent); }
      catch (Exception ignored) {}
      comp.physics("tff").create(vent, "MassSource", 2);
      comp.physics("tff").feature(vent).label(
          "Stage 573 distributed relief outside intact active film");
      comp.physics("tff").feature(vent)
          .selection().named("sel_film_swept571");
      comp.physics("tff").feature(vent).set("QudR", "Qvent573");

      String anchor = "wc_open_anchor573";
      try { comp.physics("tff").feature().remove(anchor); }
      catch (Exception ignored) {}
      comp.physics("tff").create(anchor, "WeakContribution", 2);
      comp.physics("tff").feature(anchor).label(
          "Stage 573 numerical open-region pressure anchor");
      comp.physics("tff").feature(anchor)
          .selection().named("sel_film_swept571");
      comp.physics("tff").feature(anchor).set(
          "weakExpression",
          "-kanchor573*(1-M_drain573)"
              + "*(pfilm-p_amb573)*test(pfilm)");

      model.label("Stage 573 fixed-cornea dynamic regions setup");
      model.save("573a_stage573_cornea_dynamic_regions_setup.mph");

      removeDataset(model, "dset573_regions");
      model.result().dataset().create(
          "dset573_regions", "Solution");
      model.result().dataset("dset573_regions")
          .set("solution", "sol93");

      double tSlide = model.param().evaluate("T_slide572");
      double[] start = evaluateAt(model, "eval573_start", 35.1);
      double[] quarter = evaluateAt(
          model, "eval573_quarter", 17.6);
      double[] middle = evaluateAt(
          model, "eval573_middle", 0.1);
      double[] threeQuarter = evaluateAt(
          model, "eval573_three_quarter", -17.4);
      double[] end = evaluateAt(
          model, "eval573_end", -34.9);
      requireFinite("start mask", start);
      requireFinite("quarter mask", quarter);
      requireFinite("middle mask", middle);
      requireFinite("three-quarter mask", threeQuarter);
      requireFinite("end mask", end);
      System.out.printf(Locale.US,
          "MASK_AREAS start=(%.12g,%.12g)"
              + " quarter=(%.12g,%.12g)"
              + " middle=(%.12g,%.12g)"
              + " three_quarter=(%.12g,%.12g)"
              + " end=(%.12g,%.12g)%n",
          start[1], start[2],
          quarter[1], quarter[2],
          middle[1], middle[2],
          threeQuarter[1], threeQuarter[2],
          end[1], end[2]);

      // sol93 is the retained central structural reference solution.
      // Other path positions require new structural solves in Stage 574.
      String core = coreMask(0.1);
      String drain = drainMask(0.1);
      String valid = "if(isdefined(" + destinationGap + "),"
          + "if(abs(" + destinationGap
          + ")<0.1[mm],1,0),0)";
      String safe = "if(isdefined(" + destinationGap + "),"
          + "if(abs(" + destinationGap
          + ")<0.1[mm]," + destinationGap
          + ",3[um]),3[um])";
      String broken = "0.5*(1+tanh(((" + safe
          + ")-0.05[um])/0.005[um]))";
      String wet = "0.005[um]+0.5*(((" + safe + ")-0.005[um])"
          + "+sqrt(((" + safe + ")-0.005[um])^2"
          + "+(0.001[um])^2))";
      String active = "(" + core + ")*(" + valid + ")*(" + broken + ")"
          + "+max((" + drain + ")-(" + core + "),0)";
      String thickness = "(" + active + ")*(" + wet + ")"
          + "+(1-(" + active + "))*3[um]";
      double[] gap = evaluate(model, "eval573_gap", new String[] {
          core,
          "(" + core + ")*(" + valid + ")",
          drain,
          "(" + drain + ")*(" + valid + ")",
          "(" + core + ")*(" + valid + ")*(" + broken + ")",
          "(" + core + ")*(1-(" + valid + ")*(" + broken + "))",
          "(" + core + ")*(" + safe + ")",
          "(" + core + ")*(" + thickness + ")"
      });
      requireFinite("destination gap", gap);
      double coreCoverage = gap[1] / gap[0];
      double drainCoverage = gap[3] / gap[2];
      double wetFraction = gap[4] / gap[0];
      double ruptureFraction = gap[5] / gap[0];
      double coreGapAverage = gap[6] / gap[0];
      double coreThicknessAverage = gap[7] / gap[0];
      System.out.printf(Locale.US,
          "GAP_CHECK core_coverage=%.12g drain_coverage=%.12g"
              + " wet_fraction=%.12g rupture_fraction=%.12g"
              + " core_gap_avg=%.12g core_hcalc_avg=%.12g%n",
          coreCoverage, drainCoverage, wetFraction, ruptureFraction,
          coreGapAverage, coreThicknessAverage);

      double[][] positions = new double[][] {
          start, quarter, middle, threeQuarter, end};
      double[] targetAngles = new double[] {
          35.1, 17.6, 0.1, -17.4, -34.9};
      double minCoreArea = Double.POSITIVE_INFINITY;
      double maxCoreArea = 0;
      for (int i = 0; i < positions.length; i++) {
        double[] position = positions[i];
        minCoreArea = Math.min(minCoreArea, position[1]);
        maxCoreArea = Math.max(maxCoreArea, position[1]);
        if (position[2] <= position[1]) {
          throw new IllegalStateException(
              "Drainage region does not exceed core area");
        }
        double coreX = position[4] / position[1];
        double coreAngleDegrees =
            position[5] / position[1] * 180.0 / Math.PI;
        if (Math.abs(coreX) > 0.1e-3) {
          throw new IllegalStateException(
              "Moving core transverse centroid is " + coreX);
        }
        if (Math.abs(coreAngleDegrees - targetAngles[i]) > 0.5) {
          throw new IllegalStateException(
              "Moving core angular centroid is " + coreAngleDegrees
                  + " degrees at position " + i);
        }
      }
      if (maxCoreArea / minCoreArea > 1.15) {
        throw new IllegalStateException(
            "Moving core area range exceeds 15 percent");
      }
      if (coreCoverage < 0.95) {
        throw new IllegalStateException(
            "Central-reference core destination-gap coverage is "
                + coreCoverage);
      }

      System.out.printf(Locale.US,
          "DESTINATION_GAP=%s%n"
              + "T_SLIDE=%.12g%n"
              + "START_CORE=%.12g START_DRAIN=%.12g%n"
              + "QUARTER_CORE=%.12g QUARTER_DRAIN=%.12g%n"
              + "MIDDLE_CORE=%.12g MIDDLE_DRAIN=%.12g%n"
              + "THREE_QUARTER_CORE=%.12g THREE_QUARTER_DRAIN=%.12g%n"
              + "END_CORE=%.12g END_DRAIN=%.12g%n"
              + "CORE_COVERAGE=%.12g DRAIN_COVERAGE=%.12g%n"
              + "WET_FRACTION=%.12g RUPTURE_FRACTION=%.12g%n"
              + "CORE_GAP_AVG=%.12g CORE_HCALC_AVG=%.12g%n",
          destinationGap, tSlide,
          start[1], start[2],
          quarter[1], quarter[2],
          middle[1], middle[2],
          threeQuarter[1], threeQuarter[2],
          end[1], end[2],
          coreCoverage, drainCoverage,
          wetFraction, ruptureFraction,
          coreGapAverage, coreThicknessAverage);

      model.label("Stage 573 fixed-cornea dynamic regions checked");
      model.save("573_stage573_cornea_dynamic_regions_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
