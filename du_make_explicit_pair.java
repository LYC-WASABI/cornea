import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_make_explicit_pair {
  private static final String OUT = "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\";

  private static boolean hasPhysicsFeature(Model model, String tag) {
    for (String t : model.component("comp1").physics("solid").feature().tags()) {
      if (t.equals(tag)) return true;
    }
    return false;
  }

  private static boolean hasPair(Model model, String tag) {
    for (String t : model.component("comp1").pair().tags()) {
      if (t.equals(tag)) return true;
    }
    return false;
  }

  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", OUT + "du_cornea_lid_first_version_final.mph");
    model.modelPath("D:\\\\COMSOL_Outputs\\\\models\\\\du");

    model.component("comp1").selection().create("sel_lid_wiper_inner_surface", "Explicit");
    model.component("comp1").selection("sel_lid_wiper_inner_surface").label("Source: lid wiper inner surface");
    model.component("comp1").selection("sel_lid_wiper_inner_surface").geom("geom1", 2);
    model.component("comp1").selection("sel_lid_wiper_inner_surface").set(new int[]{15, 16});

    model.component("comp1").selection().create("sel_cornea_anterior_surface", "Explicit");
    model.component("comp1").selection("sel_cornea_anterior_surface").label("Destination: cornea anterior surface");
    model.component("comp1").selection("sel_cornea_anterior_surface").geom("geom1", 2);
    model.component("comp1").selection("sel_cornea_anterior_surface").set(new int[]{1, 2, 3, 6, 9});
    model.save(OUT + "12_explicit_contact_boundary_selections.mph");

    if (hasPhysicsFeature(model, "gcnt1")) {
      model.component("comp1").physics("solid").feature().remove("gcnt1");
    }
    // dgcnt1 is a COMSOL-maintained assembly contact manager and cannot be removed or disabled.
    // The user-added general contact gcnt1 is removed; the active pair contact is dcnt1 -> cp_lid_cornea.

    if (hasPair(model, "cp_lid_cornea")) {
      model.component("comp1").pair().remove("cp_lid_cornea");
    }
    model.component("comp1").pair().create("cp_lid_cornea", "Contact");
    model.component("comp1").pair("cp_lid_cornea").label("Explicit contact pair: lid wiper inner surface to cornea anterior surface");
    model.component("comp1").pair("cp_lid_cornea").source().named("sel_lid_wiper_inner_surface");
    model.component("comp1").pair("cp_lid_cornea").destination().named("sel_cornea_anterior_surface");

    model.component("comp1").physics("solid").feature("dcnt1").label("Pair contact: lid wiper to cornea");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairSelection", "list");
    model.component("comp1").physics("solid").feature("dcnt1").set("pairs", new String[]{"cp_lid_cornea"});
    model.save(OUT + "13_explicit_lid_cornea_contact_pair.mph");

    boolean hasFriction = false;
    for (String child : model.component("comp1").physics("solid").feature("dcnt1").feature().tags()) {
      if (child.equals("fric1")) hasFriction = true;
    }
    if (!hasFriction) {
      model.component("comp1").physics("solid").feature("dcnt1").feature().create("fric1", "Friction");
    }
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1").label("Coulomb friction, mu = 0.1");
    model.component("comp1").physics("solid").feature("dcnt1").feature("fric1").set("mu_fric", "mu_friction");
    model.save(OUT + "14_pair_contact_friction_mu_0p1.mph");

    model.save(OUT + "du_cornea_lid_explicit_pair_final.mph");
  }
}
