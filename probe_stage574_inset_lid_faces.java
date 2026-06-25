import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_inset_lid_faces {
  private static void moveBefore(
      GeomSequence geometry, String feature, String before) {
    String[] tags = geometry.feature().tags();
    for (int i = 0; i < tags.length; i++) {
      if (before.equals(tags[i])) {
        geometry.feature().move(feature, Math.max(0, i - 1));
        return;
      }
    }
    throw new IllegalStateException("Missing feature " + before);
  }

  private static void moveAfter(
      GeomSequence geometry, String feature, String after) {
    String[] tags = geometry.feature().tags();
    for (int i = 0; i < tags.length; i++) {
      if (after.equals(tags[i])) {
        geometry.feature().move(feature, i + 1);
        return;
      }
    }
    throw new IllegalStateException("Missing feature " + after);
  }

  private static void createQuickXPlane(
      GeomSequence geometry, String tag, String position) {
    geometry.create(tag, "WorkPlane");
    geometry.feature(tag).set("planetype", "quick");
    geometry.feature(tag).set("quickplane", "yz");
    geometry.feature(tag).set("quickx", position);
  }

  private static void createAngularPlane(
      GeomSequence geometry, String tag, String normalY, String normalZ) {
    geometry.create(tag, "WorkPlane");
    geometry.feature(tag).set("planetype", "general");
    geometry.feature(tag).set(
        "normalvector", new String[] {"0", normalY, normalZ});
    geometry.feature(tag).set("normalpoint", "coord");
    geometry.feature(tag).set(
        "normalcoord", new String[] {"0", "0", "0"});
  }

  private static void partitionFaces(
      GeomSequence geometry, String tag, String input, String workPlane) {
    moveAfter(geometry, workPlane, input);
    geometry.create(tag, "PartitionFaces");
    moveAfter(geometry, tag, workPlane);
    System.out.println("ORDER_BEFORE_RUN_" + tag + "="
        + Arrays.toString(geometry.feature().tags()));
    geometry.run(input);
    System.out.println("ORDER_" + tag + "="
        + Arrays.toString(geometry.feature().tags()));
    geometry.feature(tag).selection("face").all(input);
    geometry.feature(tag).set("partitionwith", "workplane");
    geometry.feature(tag).set("workplane", workPlane);
    geometry.feature(tag).set("selresult", "on");
    geometry.feature(tag).set("selresultshow", "all");
    geometry.run(tag);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "573_stage573_source_true_gap_checked.mph");
      ModelNode comp = model.component("comp1");
      GeomSequence geometry = comp.geom("geom1");

      model.param().set("film_inset_long574", "0.15[mm]");
      model.param().set("film_inset_short574", "0.05[mm]");
      model.param().set(
          "film_half_x574",
          "lid_cut_length/2-film_inset_long574");
      model.param().set(
          "film_half_theta574",
          "(lid_arc_width/2-film_inset_short574)/Rlid_in");

      createQuickXPlane(
          geometry, "wpf_lid_xneg574", "-film_half_x574");
      partitionFaces(
          geometry, "pf_lid_xneg574", "int_lid",
          "wpf_lid_xneg574");
      createQuickXPlane(
          geometry, "wpf_lid_xpos574", "film_half_x574");
      partitionFaces(
          geometry, "pf_lid_xpos574", "pf_lid_xneg574",
          "wpf_lid_xpos574");
      createAngularPlane(
          geometry, "wpf_lid_aneg574",
          "cos(film_half_theta574)",
          "sin(film_half_theta574)");
      partitionFaces(
          geometry, "pf_lid_aneg574", "pf_lid_xpos574",
          "wpf_lid_aneg574");
      createAngularPlane(
          geometry, "wpf_lid_apos574",
          "cos(film_half_theta574)",
          "-sin(film_half_theta574)");
      partitionFaces(
          geometry, "pf_lid_apos574", "pf_lid_aneg574",
          "wpf_lid_apos574");

      geometry.feature("rot_lid").selection("input")
          .set(new String[] {"pf_lid_apos574"});
      geometry.feature("fin").set("imprint", "off");
      model.label("Stage 574 inset lid face partition probe");
      model.save("probe_stage574_inset_lid_faces_setup.mph");
      geometry.run();

      System.out.println("GEOMETRY="
          + geometry.feature("fin").getString("buildmessage"));
      Pair contact = comp.pair("cp_lid_cornea");
      System.out.println("CONTACT_SOURCE=" + Arrays.toString(
          contact.source().entities()));
      System.out.println("CONTACT_DESTINATION=" + Arrays.toString(
          contact.destination().entities()));
      System.out.println("PAIRS=" + Arrays.toString(comp.pair().tags()));
      model.save("probe_stage574_inset_lid_faces.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
