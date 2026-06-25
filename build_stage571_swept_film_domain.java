import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage571_swept_film_domain {
  private static void removeSelection(ModelNode comp, String tag) {
    try { comp.selection().remove(tag); } catch (Exception ignored) {}
  }

  private static void createUnion(
      ModelNode comp, String tag, String label,
      int entityDimension, String source) {
    removeSelection(comp, tag);
    comp.selection().create(tag, "Union");
    comp.selection(tag).label(label);
    comp.selection(tag).set("entitydim", Integer.toString(entityDimension));
    comp.selection(tag).set("input", new String[] {source});
  }

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
          "Model", "570b_stage570_dynamic_master_clean.mph");
      ModelNode comp = model.component("comp1");

      model.param().set(
          "stage571_revision", "571",
          "Fixed full-path swept film domain");
      model.param().set(
          "film_swept_half_width571", "film_track_half_width",
          "Half width of the complete dynamic film band");
      model.param().set(
          "film_swept_theta_max571", "film_track_theta_max",
          "Half angle including start and end drainage buffers");

      createUnion(
          comp, "sel_film_swept571",
          "Stage 571 fixed complete scratch swept film domain",
          2, "sel_film_track");
      createUnion(
          comp, "sel_film_inlet571",
          "Stage 571 swept-film inlet drain",
          1, "sel_film_inlet");
      createUnion(
          comp, "sel_film_outlet571",
          "Stage 571 swept-film outlet drain",
          1, "sel_film_outlet");
      createUnion(
          comp, "sel_film_side_left571",
          "Stage 571 swept-film left lateral drain",
          1, "sel_film_side_left");
      createUnion(
          comp, "sel_film_side_right571",
          "Stage 571 swept-film right lateral drain",
          1, "sel_film_side_right");

      comp.physics("tff").selection().named("sel_film_swept571");
      comp.physics("tff").feature("bdr_inlet520")
          .selection().named("sel_film_inlet571");
      comp.physics("tff").feature("bdr_outlet520")
          .selection().named("sel_film_outlet571");
      comp.physics("tff").feature("bdr_left520")
          .selection().named("sel_film_side_left571");
      comp.physics("tff").feature("bdr_right520")
          .selection().named("sel_film_side_right571");
      comp.cpl("intop_film").selection().named("sel_film_swept571");

      model.label("Stage 571 swept film domain setup");
      model.save("571a_stage571_swept_film_domain_setup.mph");

      int[] domain = comp.selection("sel_film_swept571").entities(2);
      int[] inlet = comp.selection("sel_film_inlet571").entities(1);
      int[] outlet = comp.selection("sel_film_outlet571").entities(1);
      int[] left = comp.selection("sel_film_side_left571").entities(1);
      int[] right = comp.selection("sel_film_side_right571").entities(1);
      if (domain.length < 1 || inlet.length < 1 || outlet.length < 1
          || left.length < 1 || right.length < 1) {
        throw new IllegalStateException("Incomplete swept-film selections");
      }

      removeDataset(model, "dset571_swept");
      model.result().dataset().create("dset571_swept", "Solution");
      model.result().dataset("dset571_swept")
          .set("solution", "sol93");
      removeNumerical(model, "eval571_swept");
      model.result().numerical().create(
          "eval571_swept", "IntSurface");
      model.result().numerical("eval571_swept")
          .set("data", "dset571_swept");
      model.result().numerical("eval571_swept")
          .selection().named("sel_film_swept571");
      model.result().numerical("eval571_swept").set(
          "expr", new String[] {"1"});
      double area =
          model.result().numerical("eval571_swept").getReal()[0][0];

      removeNumerical(model, "min571_angle");
      model.result().numerical().create("min571_angle", "MinSurface");
      model.result().numerical("min571_angle")
          .set("data", "dset571_swept");
      model.result().numerical("min571_angle")
          .selection().named("sel_film_swept571");
      model.result().numerical("min571_angle")
          .set("expr", "atan2(y,z)");
      removeNumerical(model, "max571_angle");
      model.result().numerical().create("max571_angle", "MaxSurface");
      model.result().numerical("max571_angle")
          .set("data", "dset571_swept");
      model.result().numerical("max571_angle")
          .selection().named("sel_film_swept571");
      model.result().numerical("max571_angle")
          .set("expr", "atan2(y,z)");
      double amin =
          model.result().numerical("min571_angle").getReal()[0][0];
      double amax =
          model.result().numerical("max571_angle").getReal()[0][0];

      System.out.println("SWEPT_DOMAIN=" + Arrays.toString(domain));
      System.out.println("INLET=" + Arrays.toString(inlet));
      System.out.println("OUTLET=" + Arrays.toString(outlet));
      System.out.println("LEFT=" + Arrays.toString(left));
      System.out.println("RIGHT=" + Arrays.toString(right));
      System.out.printf(Locale.US,
          "AREA=%.12g%nANGLE_MIN_DEG=%.12g%nANGLE_MAX_DEG=%.12g%n",
          area, amin * 180.0 / Math.PI, amax * 180.0 / Math.PI);
      System.out.println("TFF_SELECTION="
          + Arrays.toString(comp.physics("tff").selection().entities()));

      model.label("Stage 571 complete swept film domain checked");
      model.save("571_stage571_swept_film_domain_checked.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
