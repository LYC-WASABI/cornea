import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage510_local_film_track {
  static final String BASE =
      "503_stage500_baseline_checked.mph";
  static final String INPUT =
      "510_stage510_local_film_track_input.mph";
  static final String SETUP =
      "511_stage510_local_film_track_setup.mph";
  static final String RESULTS =
      "512_stage510_local_film_track_results.mph";
  static final String CHECKED =
      "513_stage510_local_film_track_checked.mph";

  static void removeSelection(Model model, String tag) {
    try { model.component("comp1").selection().remove(tag); }
    catch (Exception ignored) {}
  }

  static void createEdgeBox(
      Model model, String tag, String label, String source,
      String xmin, String xmax, String ymin, String ymax,
      String zmin, String zmax) {
    removeSelection(model, tag);
    model.component("comp1").selection().create(tag, "Box");
    model.component("comp1").selection(tag).label(label);
    model.component("comp1").selection(tag).set("entitydim", "1");
    model.component("comp1").selection(tag).set("inputent", "selections");
    model.component("comp1").selection(tag).set(
        "input", new String[] {source});
    model.component("comp1").selection(tag).set("xmin", xmin);
    model.component("comp1").selection(tag).set("xmax", xmax);
    model.component("comp1").selection(tag).set("ymin", ymin);
    model.component("comp1").selection(tag).set("ymax", ymax);
    model.component("comp1").selection(tag).set("zmin", zmin);
    model.component("comp1").selection(tag).set("zmax", zmax);
    model.component("comp1").selection(tag).set("condition", "inside");
  }

  static int[] entities(Model model, String tag, int dimension) {
    return model.component("comp1").selection(tag).entities(dimension);
  }

  static void requireAtLeast(
      Model model, String tag, int dimension, int expected) {
    int[] ids = entities(model, tag, dimension);
    System.out.println(tag + "=" + Arrays.toString(ids));
    if (ids.length < expected) {
      throw new IllegalStateException(
          tag + " expected at least " + expected
              + " entities, got " + ids.length);
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", BASE);
      String comp = "comp1";
      String geom = "geom1";

      if (Math.abs(model.param().evaluate("stage500_revision") - 500) > 0.1) {
        throw new IllegalStateException(
            "Stage 510 parent is not the checked Stage 500 baseline");
      }
      if (!Arrays.asList(model.result().numerical().tags())
          .contains("eval500_audit")) {
        throw new IllegalStateException(
            "Stage 500 audit node is missing from Stage 510 parent");
      }
      model.save(INPUT);

      model.param().set(
          "film_track_lateral_buffer", "0.5[mm]",
          "Stage 510 lateral drainage margin beyond the 8 mm lid");
      model.param().set(
          "film_track_half_width",
          "s_lid/2+film_track_lateral_buffer",
          "Stage 510 half width: half lid length plus lateral buffer");
      model.param().set(
          "film_track_end_buffer", "0.75[mm]",
          "Stage 510 arc-length buffer beyond each motion endpoint");
      model.param().set(
          "film_track_theta_motion", "35[deg]",
          "Stage 510 half motion angle");
      model.param().set(
          "film_track_theta_max",
          "film_track_theta_motion+film_track_end_buffer/Rcor",
          "Stage 510 half angle including end buffer");

      try {
        model.component(comp).geom(geom).feature().remove("ps_film510");
      } catch (Exception ignored) {}
      model.component(comp).geom(geom).create(
          "ps_film510", "ParametricSurface");
      model.component(comp).geom(geom).feature("ps_film510").label(
          "Stage 510 local spherical rectangular film track");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "parname1", "s510");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "parmin1", "0");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "parmax1", "1");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "parname2", "t510");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "parmin2", "0");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "parmax2", "1");
      String x =
          "-film_track_half_width+2*film_track_half_width*s510";
      String theta =
          "-film_track_theta_max+2*film_track_theta_max*t510";
      String radial = "sqrt(Rcor^2-(" + x + ")^2)";
      model.component(comp).geom(geom).feature("ps_film510").set(
          "coord", new String[] {
            x, radial + "*sin(" + theta + ")",
            radial + "*cos(" + theta + ")"
          });
      model.component(comp).geom(geom).feature("ps_film510").set(
          "rtol", "1e-5");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "maxknots", "40");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "selresult", "on");
      model.component(comp).geom(geom).feature("ps_film510").set(
          "selresultshow", "bnd");
      model.component(comp).geom(geom).feature("fin").set("imprint", "on");

      model.label("Stage 510 local film track setup");
      model.save(SETUP);

      model.component(comp).geom(geom).run();
      String autoBoundary = "geom1_ps_film510_bnd";
      System.out.println("AUTO BOUNDARY=" + Arrays.toString(
          entities(model, autoBoundary, 2)));

      removeSelection(model, "sel_film_surface_tool");
      model.component(comp).selection().create(
          "sel_film_surface_tool", "Explicit");
      model.component(comp).selection("sel_film_surface_tool").label(
          "Stage 510 parametric film-track imprint tool");
      model.component(comp).selection("sel_film_surface_tool").geom(geom, 2);
      model.component(comp).selection("sel_film_surface_tool").set(
          entities(model, autoBoundary, 2));

      removeSelection(model, "sel_film_track");
      model.component(comp).selection().create("sel_film_track", "Explicit");
      model.component(comp).selection("sel_film_track").label(
          "Stage 510 imprinted cornea-side local film track");
      model.component(comp).selection("sel_film_track").geom(geom, 2);
      model.component(comp).selection("sel_film_track").set(
          model.component(comp).pair("ap1").source().entities());

      removeSelection(model, "sel_film_edges_all");
      model.component(comp).selection().create(
          "sel_film_edges_all", "Adjacent");
      model.component(comp).selection("sel_film_edges_all").label(
          "Stage 510 all four local film-track edges");
      model.component(comp).selection("sel_film_edges_all").set(
          "entitydim", "2");
      model.component(comp).selection("sel_film_edges_all").set(
          "outputdim", "1");
      model.component(comp).selection("sel_film_edges_all").set(
          "input", new String[] {"sel_film_track"});
      model.component(comp).selection("sel_film_edges_all").set(
          "exterior", "on");
      model.component(comp).selection("sel_film_edges_all").set(
          "interior", "off");

      createEdgeBox(
          model, "sel_film_inlet", "Stage 510 film inlet edge",
          "sel_film_edges_all",
          "-film_track_half_width-0.1[mm]",
          "film_track_half_width+0.1[mm]",
          "-Rcor*sin(film_track_theta_max)-0.12[mm]",
          "-sqrt(Rcor^2-film_track_half_width^2)"
              + "*sin(film_track_theta_max)+0.12[mm]",
          "sqrt(Rcor^2-film_track_half_width^2)"
              + "*cos(film_track_theta_max)-0.12[mm]",
          "Rcor*cos(film_track_theta_max)+0.12[mm]");
      createEdgeBox(
          model, "sel_film_outlet", "Stage 510 film outlet edge",
          "sel_film_edges_all",
          "-film_track_half_width-0.1[mm]",
          "film_track_half_width+0.1[mm]",
          "sqrt(Rcor^2-film_track_half_width^2)"
              + "*sin(film_track_theta_max)-0.12[mm]",
          "Rcor*sin(film_track_theta_max)+0.12[mm]",
          "sqrt(Rcor^2-film_track_half_width^2)"
              + "*cos(film_track_theta_max)-0.12[mm]",
          "Rcor*cos(film_track_theta_max)+0.12[mm]");
      createEdgeBox(
          model, "sel_film_side_left", "Stage 510 left drainage edge",
          "sel_film_edges_all",
          "-film_track_half_width-0.06[mm]",
          "-film_track_half_width+0.06[mm]",
          "-Rcor*sin(film_track_theta_max)-0.1[mm]",
          "Rcor*sin(film_track_theta_max)+0.1[mm]",
          "sqrt(Rcor^2-film_track_half_width^2)"
              + "*cos(film_track_theta_max)-0.2[mm]",
          "sqrt(Rcor^2-film_track_half_width^2)+0.1[mm]");
      createEdgeBox(
          model, "sel_film_side_right", "Stage 510 right drainage edge",
          "sel_film_edges_all",
          "film_track_half_width-0.06[mm]",
          "film_track_half_width+0.06[mm]",
          "-Rcor*sin(film_track_theta_max)-0.1[mm]",
          "Rcor*sin(film_track_theta_max)+0.1[mm]",
          "sqrt(Rcor^2-film_track_half_width^2)"
              + "*cos(film_track_theta_max)-0.2[mm]",
          "sqrt(Rcor^2-film_track_half_width^2)+0.1[mm]");

      removeSelection(model, "sel_cornea_contact_target");
      model.component(comp).selection().create(
          "sel_cornea_contact_target", "Explicit");
      model.component(comp).selection("sel_cornea_contact_target").label(
          "Stage 510 cornea anterior contact target");
      model.component(comp).selection("sel_cornea_contact_target").geom(
          geom, 2);
      model.component(comp).selection("sel_cornea_contact_target").set(
          entities(model, "sel_cornea_anterior_surface", 2));

      model.component(comp).mesh("mesh1").run();

      model.label("Stage 510 local film track geometry and mesh");
      model.save(RESULTS);

      requireAtLeast(model, "sel_film_track", 2, 1);
      requireAtLeast(model, "sel_film_surface_tool", 2, 1);
      requireAtLeast(model, "sel_film_edges_all", 1, 4);
      requireAtLeast(model, "sel_film_inlet", 1, 1);
      requireAtLeast(model, "sel_film_outlet", 1, 1);
      requireAtLeast(model, "sel_film_side_left", 1, 1);
      requireAtLeast(model, "sel_film_side_right", 1, 1);
      if (!Arrays.asList(model.component(comp).pair().tags())
          .contains("cp_lid_cornea")) {
        throw new IllegalStateException("Existing contact pair is missing");
      }
      int[] solidDomains =
          model.component(comp).physics("solid").selection().entities();
      System.out.println("SOLID DOMAINS=" + Arrays.toString(solidDomains));
      if (solidDomains.length != 2) {
        throw new IllegalStateException(
            "Solid Mechanics domain selection changed unexpectedly");
      }
      System.out.println("CORNEA TARGET=" + Arrays.toString(
          entities(model, "sel_cornea_contact_target", 2)));
      System.out.println("CONTACT PAIRS="
          + Arrays.toString(model.component(comp).pair().tags()));
      System.out.println("PARENT_STAGE="
          + model.param().evaluate("stage500_revision"));
      System.out.println("PARENT_AUDIT_PRESENT="
          + Arrays.asList(model.result().numerical().tags())
              .contains("eval500_audit"));
      System.out.println("FILM THETA MAX DEG="
          + model.param().evaluate("film_track_theta_max") * 180.0 / Math.PI);
      System.out.println("STAGE510 CHECK=PASS");

      model.label("Stage 510 local film track checked");
      model.save(CHECKED);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
