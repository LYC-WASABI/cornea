import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage162_midpoint_film_pressure_calibration {
  private static String newest(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
      String c="comp1", mv="var_mixed_lub", study="std_pressure162";
      m.param().set("phi_qs142", "-35[deg]");
      m.param().set("t_replay", "0.28[s]");
      m.param().set("h_lift162", "0[um]",
          "Additional load-dependent separation for film pressure calibration");
      m.component(c).variable(mv).set("h_under_lid162",
          "max(h_min_tear,h0_tear+Rq_eq+h_lift162)");
      m.component(c).variable(mv).set("h_pressure162",
          "h_outside_track+lid_mask*(h_under_lid162-h_outside_track)");
      m.component(c).physics("tff").feature("ffp1").set("hw1","h_pressure162");
      m.component(c).physics("tff").feature("init1").set("pfilm","0[Pa]");
      try { m.study().remove(study); } catch(Exception ignore) {}
      m.study().create(study);
      m.study(study).label("Stage 162 midpoint film pressure calibration");
      m.study(study).create("param","Parametric");
      m.study(study).feature("param").set("pname",new String[]{"h_lift162"});
      m.study(study).feature("param").set("plistarr",
          new String[]{"0 1 2 3 5 8 12 20 30 50"});
      m.study(study).feature("param").set("punit",new String[]{"um"});
      m.study(study).create("stat","Stationary");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid","off","tff","on","ge_force_total111","off"});
      String step=study+"/stat";
      for(String f:m.component(c).physics("tff").feature().tags())
        try { m.component(c).physics("tff").feature(f).set("StudyStep",step); }
        catch(Exception ignore){}
      String[] before=m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol=newest(m,before);
      m.save("291_lid8mm_stage162_midpoint_pressure_calibration_setup_Model.mph");
      System.out.println("RUN_STAGE162 solver="+sol);
      m.sol(sol).runAll();
      m.result().dataset().create("dset162","Solution");
      m.result().dataset("dset162").set("solution",sol);
      m.result().numerical().create("eval162","EvalGlobal");
      m.result().numerical("eval162").set("data","dset162");
      m.result().numerical("eval162").set("expr",new String[]{
          "h_lift162","intop_film(max(pfilm,0))","intop_film(pfilm)",
          "intop_film(tau_film_wall)","h_under_lid162"});
      double[][] a=m.result().numerical("eval162").getReal();
      for(int j=0;j<a[0].length;j++)
        System.out.printf(Locale.US,
            "row=%d hlift=%.8g Wpos=%.8g Wnet=%.8g Fshear=%.8g hunder=%.8g%n",
            j,a[0][j],a[1][j],a[2][j],a[3][j],a[4][j]);
      m.save("292_lid8mm_stage162_midpoint_pressure_calibration_results_Model.mph");
      ModelUtil.disconnect();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
