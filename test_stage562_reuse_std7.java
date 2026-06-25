import com.comsol.model.*;
import com.comsol.model.util.*;

public class test_stage562_reuse_std7 {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558i_stage561_JFO_diagnostics_results.mph");
      model.param().set("delta_h562", "0[um]");
      model.component("comp1").variable().create("var562test");
      model.component("comp1").variable("var562test")
          .selection().named("sel_film_track");
      model.component("comp1").variable("var562test")
          .set("h_film562", "h_geom555+delta_h562");
      model.component("comp1").physics("tff").feature("ffp1")
          .set("hw1", "h_film562");
      try { model.study("std7").feature().remove("param562test"); }
      catch (Exception ignored) {}
      model.study("std7").create("param562test", "Parametric");
      model.study("std7").feature("param562test")
          .set("pname", new String[] {"delta_h562"});
      model.study("std7").feature("param562test")
          .set("plistarr", new String[] {"0"});
      model.study("std7").feature("param562test")
          .set("punit", new String[] {"um"});
      model.study("std7").feature().move("param562test", 0);
      model.study("std7").run();
      System.out.println("RUN_OK");
      model.save("test_stage562_reuse_std7_results.mph");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
