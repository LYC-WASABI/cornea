import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_bind_plot_selections {
  public static void main(String[] args) throws java.io.IOException {
    String path = "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_robust_contact_rounded_lid_disp_calibrated_0p03_results.mph";
    Model model = ModelUtil.load("Model", path);
    String[][] groups = {
      {"pg_cor_disp", "sel_cornea_anterior_surface"},
      {"pg_lid_disp", "sel_lid_contact_source_robust2"},
      {"pg_cor_mises", "sel_cornea_anterior_surface"},
      {"pg_lid_mises", "sel_lid_contact_source_robust2"}
    };
    for (String[] g : groups) {
      try {
        model.result(g[0]).selection().named(g[1]);
        System.out.println("group selection bound " + g[0] + " -> " + g[1]);
      } catch (Exception ex) {
        System.out.println("group selection failed " + g[0] + ": " + ex.getMessage());
      }
      try {
        model.result(g[0]).feature("surf1").selection().named(g[1]);
        System.out.println("surface selection bound " + g[0] + " -> " + g[1]);
      } catch (Exception ex) {
        System.out.println("surface selection failed " + g[0] + ": " + ex.getMessage());
      }
    }
    model.save(path);
  }
}
