import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576e_global_controller {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "576a_stage576_load_controller_closure_checked.mph");
      ModelNode comp = model.component("comp1");
      PhysicsFeature ge = comp.physics("ge_force_total111").feature("ge1");
      System.out.println("GE_ACTIVE=" + comp.physics("ge_force_total111").isActive());
      System.out.println("GE_TYPE=" + comp.physics("ge_force_total111").getType());
      System.out.println("GE1_TYPE=" + ge.getType());
      System.out.println("GE1_PROPERTIES=" + Arrays.toString(ge.properties()));
      for (String property : ge.properties()) {
        try {
          String[] value = ge.getStringArray(property);
          if (value.length > 0) System.out.println("PROP " + property + "=" + Arrays.toString(value));
        } catch (Exception ignored) {}
      }
      for (String variableTag : comp.variable().tags()) {
        for (String name : new String[] {"Fcontact111", "Wfilm555", "Ftotal555", "Ferr555", "dr_indent119"}) {
          try {
            String value = comp.variable(variableTag).get(name);
            if (value != null && !value.isEmpty()) {
              System.out.println("VAR tag=" + variableTag + " name=" + name + " value=" + value);
            }
          } catch (Exception ignored) {}
        }
      }
      System.out.println("DISP=" + Arrays.toString(comp.physics("solid").feature("disp_lid_time").getStringArray("U0")));
      System.out.println("PARAM_Q=" + model.param().get("q_scale574"));
      System.out.println("SOL201_VARS=" + Arrays.toString(model.sol("sol201").feature("v1").getStringArray("clist")));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
