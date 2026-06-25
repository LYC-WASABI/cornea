import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage570_pair_footprint {
  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); }
    catch (Exception ignored) {}
  }

  private static double scalar(Model model, String tag) {
    return model.result().numerical(tag).getReal()[0][0];
  }

  private static double extremum(
      Model model, String tag, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset569_pair_gap");
    model.result().numerical(tag).selection().named("sel_film_track");
    model.result().numerical(tag).set("expr", expression);
    return scalar(model, tag);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "566c_stage569_true_pair_gap_checked.mph");

      double xmin = extremum(
          model, "min570_x", "MinSurface",
          "if(pair_map_valid569>0.5,x,1[m])");
      double xmax = extremum(
          model, "max570_x", "MaxSurface",
          "if(pair_map_valid569>0.5,x,-1[m])");
      double amin = extremum(
          model, "min570_angle", "MinSurface",
          "if(pair_map_valid569>0.5,atan2(y,z),pi)");
      double amax = extremum(
          model, "max570_angle", "MaxSurface",
          "if(pair_map_valid569>0.5,atan2(y,z),-pi)");
      double ymin = extremum(
          model, "min570_y", "MinSurface",
          "if(pair_map_valid569>0.5,y,1[m])");
      double ymax = extremum(
          model, "max570_y", "MaxSurface",
          "if(pair_map_valid569>0.5,y,-1[m])");
      double zmin = extremum(
          model, "min570_z", "MinSurface",
          "if(pair_map_valid569>0.5,z,1[m])");
      double zmax = extremum(
          model, "max570_z", "MaxSurface",
          "if(pair_map_valid569>0.5,z,-1[m])");

      System.out.printf(Locale.US,
          "X_MIN=%.12g%nX_MAX=%.12g%n"
              + "ANGLE_MIN_RAD=%.12g%nANGLE_MAX_RAD=%.12g%n"
              + "ANGLE_MIN_DEG=%.12g%nANGLE_MAX_DEG=%.12g%n"
              + "Y_MIN=%.12g%nY_MAX=%.12g%n"
              + "Z_MIN=%.12g%nZ_MAX=%.12g%n",
          xmin, xmax, amin, amax,
          amin * 180.0 / Math.PI, amax * 180.0 / Math.PI,
          ymin, ymax, zmin, zmax);

      removeNumerical(model, "int570_footprint");
      model.result().numerical().create("int570_footprint", "IntSurface");
      model.result().numerical("int570_footprint")
          .set("data", "dset569_pair_gap");
      model.result().numerical("int570_footprint")
          .selection().named("sel_film_track");
      model.result().numerical("int570_footprint").set(
          "expr", new String[] {
            "pair_map_valid569",
            "if(pair_map_valid569>0.5"
                + "&&x>=" + xmin + "[m]&&x<=" + xmax + "[m]"
                + "&&atan2(y,z)>=" + amin
                + "&&atan2(y,z)<=" + amax + ",1,0)"
          });
      System.out.println("AREA_VALUES=" + Arrays.deepToString(
          model.result().numerical("int570_footprint").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
