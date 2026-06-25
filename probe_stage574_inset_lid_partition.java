import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_inset_lid_partition {
  private static void moveBefore(
      GeomSequence geometry, String feature, String before) {
    String[] tags = geometry.feature().tags();
    for (int i = 0; i < tags.length; i++) {
      if (before.equals(tags[i])) {
        geometry.feature().move(feature, Math.max(0, i - 1));
        return;
      }
    }
    throw new IllegalStateException("Missing geometry feature " + before);
  }

  private static void createQuickXPlane(
      GeomSequence geometry, String tag, String position) {
    geometry.create(tag, "WorkPlane");
    geometry.feature(tag).set("planetype", "quick");
    geometry.feature(tag).set("quickplane", "yz");
    geometry.feature(tag).set("quickx", position);
    moveBefore(geometry, tag, "rot_lid");
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
    moveBefore(geometry, tag, "rot_lid");
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
    throw new IllegalStateException("Missing geometry feature " + after);
  }

  private static void partition(
      GeomSequence geometry, String tag, String input,
      int domainCount, String workPlane) {
    geometry.create(tag, "PartitionDomains");
    int[] domains = new int[domainCount];
    for (int i = 0; i < domainCount; i++) domains[i] = i + 1;
    moveAfter(geometry, tag, workPlane);
    System.out.println("ORDER_" + tag + "="
        + Arrays.toString(geometry.feature().tags()));
    geometry.feature(tag).selection("domain").all(input);
    geometry.feature(tag).set("partitionwith", "workplane");
    geometry.feature(tag).set("workplane", workPlane);
    geometry.feature(tag).set("selresult", "on");
    geometry.feature(tag).set("selresultshow", "all");
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

      geometry.feature("int_lid").set("selresult", "on");
      geometry.feature("int_lid").set("selresultshow", "all");
      geometry.run("int_lid");
      int initialLidDomains = comp.selection(
          "geom1_int_lid_dom").entities(3).length;
      System.out.println("INITIAL_LID_DOMAINS=" + initialLidDomains);

      createQuickXPlane(
          geometry, "wp_lid_xneg574", "-film_half_x574");
      partition(
          geometry, "pd_lid_xneg574", "int_lid", initialLidDomains,
          "wp_lid_xneg574");
      geometry.run("pd_lid_xneg574");
      int domainsAfterXNegative = comp.selection(
          "geom1_pd_lid_xneg574_dom").entities(3).length;
      if (domainsAfterXNegative == 0) domainsAfterXNegative = 2;
      System.out.println(
          "DOMAINS_AFTER_XNEG=" + domainsAfterXNegative);
      createQuickXPlane(
          geometry, "wp_lid_xpos574", "film_half_x574");
      partition(
          geometry, "pd_lid_xpos574", "pd_lid_xneg574",
          domainsAfterXNegative,
          "wp_lid_xpos574");
      geometry.run("pd_lid_xpos574");
      int domainsAfterXPositive = comp.selection(
          "geom1_pd_lid_xpos574_dom").entities(3).length;
      if (domainsAfterXPositive == 0) domainsAfterXPositive = 3;
      System.out.println(
          "DOMAINS_AFTER_XPOS=" + domainsAfterXPositive);
      createAngularPlane(
          geometry, "wp_lid_aneg574",
          "cos(film_half_theta574)",
          "sin(film_half_theta574)");
      partition(
          geometry, "pd_lid_aneg574", "pd_lid_xpos574",
          domainsAfterXPositive,
          "wp_lid_aneg574");
      geometry.run("pd_lid_aneg574");
      int domainsAfterAngleNegative = comp.selection(
          "geom1_pd_lid_aneg574_dom").entities(3).length;
      if (domainsAfterAngleNegative == 0) {
        domainsAfterAngleNegative = 6;
      }
      System.out.println(
          "DOMAINS_AFTER_ANEG=" + domainsAfterAngleNegative);
      createAngularPlane(
          geometry, "wp_lid_apos574",
          "cos(film_half_theta574)",
          "-sin(film_half_theta574)");
      partition(
          geometry, "pd_lid_apos574", "pd_lid_aneg574",
          domainsAfterAngleNegative,
          "wp_lid_apos574");
      geometry.run("pd_lid_apos574");
      geometry.feature("rot_lid").selection("input")
          .set(new String[] {"pd_lid_apos574"});
      geometry.feature("fin").set("imprint", "off");

      model.label("Stage 574 inset lid domain partition probe");
      model.save("probe_stage574_inset_lid_partition_setup.mph");
      geometry.run();

      System.out.println("GEOMETRY="
          + geometry.feature("fin").getString("buildmessage"));
      System.out.println("PAIRS=" + Arrays.toString(comp.pair().tags()));
      for (String tag : comp.pair().tags()) {
        Pair pair = comp.pair(tag);
        System.out.println(tag + " LABEL=" + pair.label());
        System.out.println("  SOURCE="
            + Arrays.toString(pair.source().entities()));
        System.out.println("  DESTINATION="
            + Arrays.toString(pair.destination().entities()));
      }
      for (String tag : new String[] {
          "geom1_pd_lid_xneg574_bnd",
          "geom1_pd_lid_xpos574_bnd",
          "geom1_pd_lid_aneg574_bnd",
          "geom1_pd_lid_apos574_bnd"}) {
        try {
          System.out.println(tag + "=" + Arrays.toString(
              comp.selection(tag).entities(2)));
        } catch (Exception error) {
          System.out.println(tag + "=UNAVAILABLE " + error.getMessage());
        }
      }
      model.save("probe_stage574_inset_lid_partition.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
