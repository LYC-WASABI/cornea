import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_alpha0 {
  static void eval(Model model, String tag, String type, String expr) {
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dseta0");
    model.result().numerical(tag).selection().named("sel_film_track");
    model.result().numerical(tag).set("expr", expr);
    model.result().numerical(tag).set("outersolnum", 1);
    System.out.println(type + " " + expr + "="
        + Arrays.deepToString(model.result().numerical(tag).getReal()));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "stage555_coupled_gap_indentation_output_Model.mph");
      model.result().dataset().create("dseta0", "Solution");
      model.result().dataset("dseta0").set("solution", "sol61");
      int i = 0;
      for (String expr : new String[] {
          "h_gap_direct555", "h_raw555", "h_geom555",
          "dgap_n555", "dr_indent119"
      }) {
        try { eval(model, "mina" + (++i), "MinSurface", expr); }
        catch (Exception e) { System.out.println("MIN ERR " + e.getMessage()); }
        try { eval(model, "maxa" + (++i), "MaxSurface", expr); }
        catch (Exception e) { System.out.println("MAX ERR " + e.getMessage()); }
        try { eval(model, "avga" + (++i), "AvSurface", expr); }
        catch (Exception e) { System.out.println("AVG ERR " + e.getMessage()); }
      }
      model.result().numerical().create("globala0", "EvalGlobal");
      model.result().numerical("globala0").set("data", "dseta0");
      model.result().numerical("globala0").set("outersolnum", 1);
      model.result().numerical("globala0").set("expr", new String[] {
        "Wfilm555", "Fn_contact119", "Ftotal555", "dr_indent119"
      });
      System.out.println("GLOBAL=" + Arrays.deepToString(
          model.result().numerical("globala0").getReal()));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
