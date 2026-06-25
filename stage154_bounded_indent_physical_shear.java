import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage154_bounded_indent_physical_shear {
  private static String newest(Model m, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String last = "";
    for (String s : m.sol().tags()) {
      last = s;
      if (!old.contains(s)) return s;
    }
    return last;
  }

  private static String angleList() {
    StringBuilder s = new StringBuilder();
    for (int deg = 0; deg >= -70; deg--) {
      if (s.length() > 0) s.append(" ");
      s.append(deg);
    }
    return s.toString();
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model m = ModelUtil.load("Model",
          "273_lid8mm_stage153_complete_qs_mixed_shear_results_Model.mph");
      String c = "comp1", v = "var_partitioned_local_pfilm";
      String ge = "ge_force_total111", study = "std_physical154";

      m.param().set("d_indent_bound154", "0.05[mm]",
          "Absolute bound for solved radial indentation");
      m.param().set("q_indent_scale154", "0.05",
          "Dimensionless scale preserving the previous indentation slope");
      m.param().set("q_barrier_scale154", "0.002",
          "Soft barrier preventing the load-control variable from escaping");
      m.component(c).variable(v).set("dr_indent119",
          "2*d_indent_bound154/pi*atan(pi*q_force_total111"
              + "/(2*q_indent_scale154))");
      m.component(c).variable(v).set("Fn_error154",
          "Fn_error119+q_barrier_scale154"
              + "*(q_force_total111/q_indent_scale154)^5");
      m.component(c).physics(ge).feature("ge1").set(
          "equation", 1, 1, "Fn_error154");

      m.component(c).variable(v).set("h_replay154",
          "withsol('sol21',h_film_input,setval(t_replay,t_film_replay_grid))");
      m.component(c).variable(v).set("lambda_replay154",
          "h_replay154/Rq_eq");
      m.component(c).variable(v).set("C_film_replay154",
          "if(h_replay154<=h_break_low,0,"
              + "if(h_replay154>=h_break_high,1,"
              + "0.5-0.5*cos(pi*(h_replay154-h_break_low)"
              + "/(h_break_high-h_break_low))))");
      m.component(c).variable(v).set("f_boundary_replay154",
          "1-C_film_replay154");
      m.component(c).variable(v).set("tau_film_replay154",
          "withsol('sol21',tau_film_wall,"
              + "setval(t_replay,t_film_replay_grid))");
      m.component(c).variable(v).set("tau_boundary_replay154",
          "mu_boundary_break90*max(if(isdefined(solid.Tn),solid.Tn,0),0)"
              + "*f_boundary_replay154");
      m.component(c).variable(v).set("tau_total_physical154",
          "tau_film_replay154+tau_boundary_replay154");
      m.component(c).variable(v).set("F_film_physical154",
          "withsol('sol21',F_film_shear,"
              + "setval(t_replay,t_film_replay_grid))");
      m.component(c).variable(v).set("F_boundary_physical154",
          "intop_film(tau_boundary_replay154)");
      m.component(c).variable(v).set("F_friction_physical154",
          "F_film_physical154+F_boundary_physical154");
      m.component(c).variable(v).set("mu_physical154",
          "F_friction_physical154/max(Fn_total119,1e-9[N])");
      m.component(c).variable(v).set("tau_lid_physical154",
          "F_friction_physical154/A_contact_nominal73");

      m.component(c).physics("solid").feature("load_shear_cornea73").active(true);
      m.component(c).physics("solid").feature("load_shear_lid73").active(true);
      m.component(c).physics("solid").feature("load_shear_cornea73").label(
          "Physical film plus local boundary shear on cornea");
      m.component(c).physics("solid").feature("load_shear_cornea73").set(
          "FperArea", new String[]{
              "0", "tau_total_physical154*ty_shear73",
              "tau_total_physical154*tz_shear73"
          });
      m.component(c).physics("solid").feature("load_shear_lid73").label(
          "Equal opposite physical mixed shear on lid wiper");
      m.component(c).physics("solid").feature("load_shear_lid73").set(
          "FperArea", new String[]{
              "0", "-tau_lid_physical154*ty_shear73",
              "-tau_lid_physical154*tz_shear73"
          });

      try { m.study().remove(study); } catch (Exception ignore) {}
      m.study().create(study);
      m.study(study).label(
          "Stage 154 bounded indentation and predicted mixed friction");
      m.study(study).create("param", "Parametric");
      m.study(study).feature("param").set("pname", new String[]{"phi_qs142"});
      m.study(study).feature("param").set("plistarr",
          new String[]{angleList()});
      m.study(study).feature("param").set("punit", new String[]{"deg"});
      m.study(study).create("stat", "Stationary");
      m.study(study).feature("stat").set("geometricNonlinearity", "on");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid", "on", "tff", "off", ge, "on"});
      String step = study + "/stat";
      for (String tag : new String[]{
          "dcnt1", "disp_lid_time", "load_partitioned_pfilm",
          "load_shear_cornea73", "load_shear_lid73"}) {
        m.component(c).physics("solid").feature(tag).set("StudyStep", step);
      }
      m.component(c).physics(ge).feature("ge1").set("StudyStep", step);

      String[] before = m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol = newest(m, before);
      SolverFeature s1 = m.sol(sol).feature("s1");
      try { s1.feature().remove("se1"); } catch (Exception ignore) {}
      try { s1.feature().remove("fc1"); } catch (Exception ignore) {}
      s1.create("fc1", "FullyCoupled");
      s1.feature("fc1").set("linsolver", "dDef");
      s1.feature("fc1").set("maxiter", 180);

      m.save("274_lid8mm_stage154_bounded_physical_shear_setup_Model.mph");
      System.out.println("RUN_STAGE154 solver=" + sol);
      m.sol(sol).runAll();

      try { m.result().dataset().remove("dset154"); } catch (Exception ignore) {}
      m.result().dataset().create("dset154", "Solution");
      m.result().dataset("dset154").set("solution", sol);
      try { m.result().numerical().remove("eval154"); } catch (Exception ignore) {}
      m.result().numerical().create("eval154", "EvalGlobal");
      m.result().numerical("eval154").set("data", "dset154");
      m.result().numerical("eval154").set("expr", new String[]{
          "phi_qs142", "t_film_replay", "Fn_contact119", "Fn_film119",
          "Fn_total119", "Fn_error119", "dr_indent119",
          "F_film_physical154", "F_boundary_physical154",
          "F_friction_physical154", "mu_physical154"
      });
      double[][] a = m.result().numerical("eval154").getReal();
      String[] n = {"phi", "time", "Fcontact", "FfilmN", "FtotalN",
          "err", "indent", "FfilmT", "FboundaryT", "Ffriction", "mu"};
      for (int j = 0; j < a[0].length; j++) {
        StringBuilder b = new StringBuilder("row=" + j);
        for (int i = 0; i < a.length; i++)
          b.append(" ").append(n[i]).append("=")
              .append(String.format(Locale.US, "%.9g", a[i][j]));
        System.out.println(b);
      }
      m.save("275_lid8mm_stage154_bounded_physical_shear_results_Model.mph");
      ModelUtil.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
