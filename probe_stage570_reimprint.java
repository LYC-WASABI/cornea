import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage570_reimprint {
  private static int[] entities(Model model, String tag, int dim) {
    return model.component("comp1").selection(tag).entities(dim);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "566c_stage569_true_pair_gap_checked.mph");
      String comp = "comp1";
      String geom = "geom1";

      model.param().set("film_overlap_xmin570", "-3.760[mm]");
      model.param().set("film_overlap_xmax570", "3.754[mm]");
      model.param().set("film_overlap_amin570", "-3.739[deg]");
      model.param().set("film_overlap_amax570", "3.938[deg]");
      model.param().set(
          "film_overlap_xcenter570",
          "(film_overlap_xmin570+film_overlap_xmax570)/2");
      model.param().set(
          "film_overlap_xhalf570",
          "(film_overlap_xmax570-film_overlap_xmin570)/2");
      model.param().set(
          "film_overlap_acenter570",
          "(film_overlap_amin570+film_overlap_amax570)/2");
      model.param().set(
          "film_overlap_ahalf570",
          "(film_overlap_amax570-film_overlap_amin570)/2");

      String x =
          "film_overlap_xcenter570-film_overlap_xhalf570"
              + "+2*film_overlap_xhalf570*s510";
      String angle =
          "film_overlap_acenter570-film_overlap_ahalf570"
              + "+2*film_overlap_ahalf570*t510";
      String radial = "sqrt(Rcor^2-(" + x + ")^2)";
      model.component(comp).geom(geom).feature("ps_film510").set(
          "coord", new String[] {
            x, radial + "*sin(" + angle + ")",
            radial + "*cos(" + angle + ")"
          });
      model.component(comp).geom(geom).feature("ps_film510").label(
          "Stage 570 eroded true opposing-lid footprint");
      model.component(comp).geom(geom).run();

      System.out.println("AUTO_TOOL=" + Arrays.toString(
          entities(model, "geom1_ps_film510_bnd", 2)));
      System.out.println("PAIRS="
          + Arrays.toString(model.component(comp).pair().tags()));
      for (String tag : model.component(comp).pair().tags()) {
        System.out.println("PAIR|" + tag + "|"
            + model.component(comp).pair(tag).label()
            + "|SRC=" + Arrays.toString(
                model.component(comp).pair(tag).source().entities())
            + "|DST=" + Arrays.toString(
                model.component(comp).pair(tag).destination().entities()));
      }
      System.out.println("OLD_FILM_TRACK=" + Arrays.toString(
          entities(model, "sel_film_track", 2)));
      System.out.println("CONTACT_DST=" + Arrays.toString(
          model.component(comp).pair("cp_lid_cornea")
              .destination().entities()));
      model.save("probe_stage570_reimprint.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
