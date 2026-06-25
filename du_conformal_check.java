import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Arrays;

public class du_conformal_check {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_conformal_spherical_rectangular_lid_wiper.mph");
    System.out.println("Rcor=" + model.param().get("Rcor"));
    System.out.println("Rlid_in=" + model.param().get("Rlid_in"));
    System.out.println("Rlid_out=" + model.param().get("Rlid_out"));
    System.out.println("lid_arc_length=" + model.param().get("lid_arc_length"));
    System.out.println("lid_arc_chord=" + model.param().get("lid_arc_chord"));
    System.out.println("lid_width=" + model.param().get("lid_width"));
    System.out.println("lid_width_chord=" + model.param().get("lid_width_chord"));
    System.out.println("inner=" + Arrays.toString(model.component("comp1").selection("sel_lid_wiper_inner_surface_dyn").entities(2)));
    System.out.println("load=" + Arrays.toString(model.component("comp1").selection("sel_lid_load_surface_dyn").entities(2)));
    System.out.println("pair source=" + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").source().entities()));
    System.out.println("pair dest=" + Arrays.toString(model.component("comp1").pair("cp_lid_cornea").destination().entities()));
  }
}
