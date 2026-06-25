import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage574_midpoint_cornea_patch_geometry {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); }
    catch (Exception ignored) {}
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573_stage573_cornea_dynamic_regions_checked.mph");
      ModelNode comp = model.component("comp1");
      GeomSequence geom = comp.geom("geom1");

      model.param().set(
          "patch_center574", "35.10[deg]",
          "Reference start-position lid projection for local JFO gate");
      model.param().set(
          "patch_xhalf574", "lid_mask_xhalf572",
          "Local patch half width");
      model.param().set(
          "patch_ahalf574",
          "lid_mask_ahalf572+drain_buffer573/Rcor",
          "Lid angular half length plus drainage buffer");

      try { geom.feature().remove("ps_local574"); }
      catch (Exception ignored) {}
      geom.create("ps_local574", "ParametricSurface");
      geom.feature("ps_local574").label(
          "Stage 574 start-position local corneal imprint patch");
      geom.feature("ps_local574").set("parname1", "s574");
      geom.feature("ps_local574").set("parmin1", "0");
      geom.feature("ps_local574").set("parmax1", "1");
      geom.feature("ps_local574").set("parname2", "a574");
      geom.feature("ps_local574").set("parmin2", "0");
      geom.feature("ps_local574").set("parmax2", "1");
      String x = "-patch_xhalf574+2*patch_xhalf574*s574";
      String angle =
          "patch_center574-patch_ahalf574+2*patch_ahalf574*a574";
      String radial = "sqrt(Rcor^2-(" + x + ")^2)";
      geom.feature("ps_local574").set("coord", new String[] {
          x,
          radial + "*sin(" + angle + ")",
          radial + "*cos(" + angle + ")"
      });
      geom.feature("ps_local574").set("rtol", "1e-6");
      geom.feature("ps_local574").set("maxknots", "60");
      geom.feature("ps_local574").set("selresult", "on");
      geom.feature("ps_local574").set("selresultshow", "bnd");
      geom.feature("fin").set("imprint", "on");

      model.label("Stage 574 start-position local corneal patch setup");
      model.save("574d_stage574_local_cornea_patch_setup.mph");
      geom.run();

      String auto = "geom1_ps_local574_bnd";
      System.out.println("AUTO_PATCH="
          + Arrays.toString(comp.selection(auto).entities(2)));
      System.out.println("CONTACT_DESTINATION="
          + Arrays.toString(
              comp.pair("cp_lid_cornea").destination().entities()));
      System.out.println("SWEPT_AFTER="
          + Arrays.toString(
              comp.selection("sel_film_swept571").entities(2)));

      removeSelection(comp, "sel_patch_tool574");
      comp.selection().create("sel_patch_tool574", "Explicit");
      comp.selection("sel_patch_tool574").label(
          "Stage 574 start-position parametric imprint tool");
      comp.selection("sel_patch_tool574").geom("geom1", 2);
      comp.selection("sel_patch_tool574").set(
          comp.selection(auto).entities(2));

      removeSelection(comp, "sel_patch_candidate574");
      comp.selection().create("sel_patch_candidate574", "Box");
      comp.selection("sel_patch_candidate574").label(
          "Stage 574 start-position local corneal patch candidates");
      comp.selection("sel_patch_candidate574").set("entitydim", "2");
      comp.selection("sel_patch_candidate574").set(
          "inputent", "selections");
      comp.selection("sel_patch_candidate574").set(
          "input", new String[] {"sel_cornea_contact_target"});
      comp.selection("sel_patch_candidate574").set(
          "xmin", "-patch_xhalf574-0.02[mm]");
      comp.selection("sel_patch_candidate574").set(
          "xmax", "patch_xhalf574+0.02[mm]");
      comp.selection("sel_patch_candidate574").set("ymin", "3[mm]");
      comp.selection("sel_patch_candidate574").set("ymax", "7[mm]");
      comp.selection("sel_patch_candidate574").set(
          "zmin", "5[mm]");
      comp.selection("sel_patch_candidate574").set(
          "zmax", "Rcor+0.1[mm]");
      comp.selection("sel_patch_candidate574").set("condition", "inside");

      comp.mesh("mesh1").run();
      System.out.println("PATCH_CANDIDATE="
          + Arrays.toString(
              comp.selection("sel_patch_candidate574").entities(2)));
      model.label("Stage 574 start-position local corneal patch geometry");
      model.save("574e_stage574_local_cornea_patch_geometry.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
