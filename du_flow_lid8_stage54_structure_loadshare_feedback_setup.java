import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage54_structure_loadshare_feedback_setup {
  public static void main(String[] args)throws Exception{
    ModelUtil.initStandalone(true);
    Model m=ModelUtil.load("Model","D:\\COMSOL_Outputs\\models\\du\\flow\\134_lid8mm_stage53_h3um_partitioned_loadshare_converged_results.mph");
    m.label("135_lid8mm_stage54_structure_loadshare_feedback_setup.mph");
    m.param().set("scale_partitioned_pfilm","0.10","Continuation scale for local tear-film pressure feedback");
    m.param().set("K_contact_mixed54","0.8051323301[N/mm]","Local normal contact stiffness used for partitioned structural correction");
    double[][] wf=m.result().numerical("eval53_Wfilm").getReal();
    double[][] fs=m.result().numerical("eval53_Ffilm_shear").getReal();
    String[][] wfRows=new String[wf[0].length][2],fsRows=new String[fs[0].length][2];
    for(int i=0;i<wf[0].length;i++){wfRows[i]=new String[]{Double.toString(.01*i),Double.toString(wf[0][i])};fsRows[i]=new String[]{Double.toString(.01*i),Double.toString(fs[0][i])};}
    m.func().create("wf54","Interpolation");m.func("wf54").set("funcname","W_film_sched54");m.func("wf54").set("table",wfRows);m.func("wf54").set("argunit",new String[]{"s"});m.func("wf54").set("fununit","N");m.func("wf54").set("interp","piecewisecubic");m.func("wf54").set("extrap","const");
    m.func().create("fs54","Interpolation");m.func("fs54").set("funcname","F_shear_sched54");m.func("fs54").set("table",fsRows);m.func("fs54").set("argunit",new String[]{"s"});m.func("fs54").set("fununit","N");m.func("fs54").set("interp","piecewisecubic");m.func("fs54").set("extrap","const");
    String v="var_partitioned_local_pfilm";
    m.component("comp1").variable(v).set("t_film_replay_grid","min(0.53[s],max(0[s],0.01[s]*round(t_film_replay/0.01[s])))");
    m.component("comp1").variable(v).set("pfilm_replay53","withsol('sol21',max(pfilm,0),setval(t_replay,t_film_replay_grid))");
    m.component("comp1").variable(v).set("W_film_replay53","W_film_sched54(t_film_replay)");
    m.component("comp1").variable(v).set("F_film_shear_replay53","F_shear_sched54(t_film_replay)");
    m.component("comp1").variable(v).set("dr_force_mixed54","max(0[mm],dr_force_reaction39-W_film_replay53/K_contact_mixed54)");
    m.component("comp1").variable(v).set("W_contact_budget54","max(F_total_target-W_film_replay53,0)");
    m.component("comp1").variable(v).set("F_friction_budget54","F_film_shear_replay53+0.02*W_contact_budget54");
    m.component("comp1").variable(v).set("mu_app_budget54","F_friction_budget54/F_total_target");
    m.component("comp1").physics("solid").feature("disp_lid_time").set("U0",new String[]{
      "0",
      "Y*(cos(phi_lid_structure)-1)-Z*sin(phi_lid_structure)-dr_force_mixed54*(Y*cos(phi_lid_structure)-Z*sin(phi_lid_structure))/sqrt(Y^2+Z^2)",
      "Y*sin(phi_lid_structure)+Z*(cos(phi_lid_structure)-1)-dr_force_mixed54*(Y*sin(phi_lid_structure)+Z*cos(phi_lid_structure))/sqrt(Y^2+Z^2)"});
    m.component("comp1").physics("solid").feature("load_partitioned_pfilm").set("FperArea",new String[]{"-scale_partitioned_pfilm*pfilm_replay53*nx","-scale_partitioned_pfilm*pfilm_replay53*ny","-scale_partitioned_pfilm*pfilm_replay53*nz"});
    m.study("std_partitioned_local_pfilm").feature("time").set("tlist","range(0,dt_structure_out,T_structure_pre+T_structure_slide+T_structure_hold)");
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\135_lid8mm_stage54_structure_loadshare_feedback_setup.mph");
    System.out.println("SAVED_STAGE54_SETUP=135_lid8mm_stage54_structure_loadshare_feedback_setup.mph");
  }
}
