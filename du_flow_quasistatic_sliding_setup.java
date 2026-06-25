import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_quasistatic_sliding_setup {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\03_dynamic_sliding_from_preload_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\04_quasistatic_dynamic_sliding_setup.mph";

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", IN);
    model.label("04_quasistatic_dynamic_sliding_setup.mph");
    model.component("comp1").physics("solid").prop("StructuralTransientBehavior")
        .set("StructuralTransientBehavior", "Quasistatic");
    model.component("comp1").variable("var_dynamic_lid_motion").set("slide_fraction",
        "if(t<T_pre,0,if(t<T_pre+T_slide,0.5-0.5*cos(pi*(t-T_pre)/T_slide),1))");
    model.component("comp1").variable("var_dynamic_lid_motion").set("phi_lid_dyn",
        "theta_slide_total*slide_fraction");
    model.component("comp1").variable("var_dynamic_lid_motion").set("theta_lid_physical",
        "-35[deg]+70[deg]*slide_fraction");
    model.save(OUT);
    System.out.println("Saved: " + OUT);
  }
}
