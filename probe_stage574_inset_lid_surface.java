import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_inset_lid_surface {
  private static int[] entities(Model model, String tag, int dimension) {
    return model.component("comp1").selection(tag).entities(dimension);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573_stage573_source_true_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      GeomSequence geom = comp.geom("geom1");

      model.param().set(
          "film_inset_long574", "0.15[mm]",
          "Inset from each long-direction lid edge");
      model.param().set(
          "film_inset_short574", "0.05[mm]",
          "Inset from each short-direction lid edge");
      model.param().set(
          "film_half_x574",
          "lid_cut_length/2-film_inset_long574");
      model.param().set(
          "film_half_theta574",
          "(lid_arc_width/2-film_inset_short574)/Rlid_in");

      try { geom.feature().remove("ps_lidfilm574"); }
      catch (Exception ignored) {}
      geom.create("ps_lidfilm574", "ParametricSurface");
      geom.feature("ps_lidfilm574").label(
          "Stage 574 inset lid-attached film partition tool");
      geom.feature("ps_lidfilm574").set("parname1", "s574");
      geom.feature("ps_lidfilm574").set("parmin1", "0");
      geom.feature("ps_lidfilm574").set("parmax1", "1");
      geom.feature("ps_lidfilm574").set("parname2", "t574");
      geom.feature("ps_lidfilm574").set("parmin2", "0");
      geom.feature("ps_lidfilm574").set("parmax2", "1");
      String x = "-film_half_x574+2*film_half_x574*s574";
      String angle =
          "-film_half_theta574+2*film_half_theta574*t574";
      String radial = "sqrt(Rlid_in^2-(" + x + ")^2)";
      geom.feature("ps_lidfilm574").set(
          "coord", new String[] {
            x,
            radial + "*sin(" + angle + ")",
            radial + "*cos(" + angle + ")"
          });
      geom.feature("ps_lidfilm574").set("rtol", "1e-5");
      geom.feature("ps_lidfilm574").set("maxknots", "40");
      geom.feature("ps_lidfilm574").set("selresult", "on");
      geom.feature("ps_lidfilm574").set("selresultshow", "bnd");
      String[] geometryTags = geom.feature().tags();
      int rotateIndex = -1;
      for (int i = 0; i < geometryTags.length; i++) {
        if ("rot_lid".equals(geometryTags[i])) {
          rotateIndex = i;
          break;
        }
      }
      if (rotateIndex < 0) {
        throw new IllegalStateException("rot_lid geometry feature not found");
      }
      geom.feature().move("ps_lidfilm574", rotateIndex);

      try { geom.feature().remove("pd_lidfilm574"); }
      catch (Exception ignored) {}
      geom.create("pd_lidfilm574", "PartitionDomains");
      geom.feature("pd_lidfilm574").label(
          "Stage 574 partition lid solid with inset film surface");
      geometryTags = geom.feature().tags();
      rotateIndex = -1;
      for (int i = 0; i < geometryTags.length; i++) {
        if ("rot_lid".equals(geometryTags[i])) {
          rotateIndex = i;
          break;
        }
      }
      geom.feature().move("pd_lidfilm574", rotateIndex);
      geom.feature("pd_lidfilm574").selection("domain")
          .set("int_lid", new int[] {1});
      geom.feature("pd_lidfilm574").set("partitionwith", "objects");
      geom.feature("pd_lidfilm574").selection("object")
          .set(new String[] {"ps_lidfilm574"});
      geom.feature("pd_lidfilm574").set("keepobject", "off");
      geom.feature("pd_lidfilm574").set("selresult", "on");
      geom.feature("pd_lidfilm574").set("selresultshow", "bnd");
      geom.feature("rot_lid").selection("input")
          .set(new String[] {"pd_lidfilm574"});
      geom.feature("fin").set("imprint", "off");

      model.label("Stage 574 inset lid film geometry probe");
      model.save("probe_stage574_inset_lid_surface_setup.mph");
      geom.run();

      System.out.println("TOOL=" + Arrays.toString(
          entities(model, "geom1_ps_lidfilm574_bnd", 2)));
      System.out.println("PAIRS=" + Arrays.toString(comp.pair().tags()));
      for (String tag : comp.pair().tags()) {
        Pair pair = comp.pair(tag);
        System.out.println(tag + " LABEL=" + pair.label());
        System.out.println("  SOURCE=" + Arrays.toString(
            pair.source().entities()));
        System.out.println("  DESTINATION=" + Arrays.toString(
            pair.destination().entities()));
      }
      System.out.println("CONTACT_SOURCE=" + Arrays.toString(
          comp.pair("cp_lid_cornea").source().entities()));
      System.out.println("CONTACT_DESTINATION=" + Arrays.toString(
          comp.pair("cp_lid_cornea").destination().entities()));
      System.out.println("LID_DOMAIN=" + Arrays.toString(
          comp.selection("sel_lid_dom").entities(3)));

      model.save("probe_stage574_inset_lid_surface.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
