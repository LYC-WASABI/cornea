import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage163_midpoint_true_load_balance {
  private static String newest(Model m,String[] before){
    Set<String> old=new HashSet<String>(Arrays.asList(before));
    String last="";
    for(String s:m.sol().tags()){last=s;if(!old.contains(s))return s;}
    return last;
  }
  public static void main(String[] args){
    try{
      ModelUtil.initStandalone(false);
      Model m=ModelUtil.load("Model",
          "290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
      String c="comp1",pv="var_partitioned_local_pfilm",mv="var_mixed_lub";
      String ge="ge_force_total111",study="std_truebalance163";
      m.param().set("phi_qs142","-35[deg]");
      m.param().set("t_replay","0.28[s]");
      m.param().set("d_ref_mid163","0.00696[mm]",
          "Dry-contact midpoint indentation reference");
      m.param().set("h_lift_reg163","0.02[um]");
      m.param().set("scale_true_pfilm163","0",
          "Continuation scale for true film-pressure feedback");
      m.component(c).variable(pv).set("h_lift_live163",
          "0.5*((d_ref_mid163-dr_indent119)"
              + "+sqrt((d_ref_mid163-dr_indent119)^2+h_lift_reg163^2))");
      m.component(c).variable(mv).set("h_under_live163",
          "max(h_min_tear,h0_tear+Rq_eq+h_lift_live163)");
      m.component(c).variable(mv).set("h_live163",
          "h_outside_track+lid_mask*(h_under_live163-h_outside_track)");
      m.component(c).variable(pv).set("Fn_film_live163",
          "scale_true_pfilm163*intop_film(max(pfilm,0))");
      m.component(c).variable(pv).set("Fn_total_live163",
          "Fn_contact119+Fn_film_live163");
      m.component(c).variable(pv).set("Fn_error_live163",
          "(Fn_total_live163-F_total_target)/F_total_target"
              + "+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      m.component(c).physics("tff").feature("ffp1").set("hw1","h_live163");
      m.component(c).physics("tff").feature("init1").set("pfilm","0[Pa]");
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea",new String[]{
              "-scale_true_pfilm163*max(pfilm,0)*nx",
              "-scale_true_pfilm163*max(pfilm,0)*ny",
              "-scale_true_pfilm163*max(pfilm,0)*nz"});
      m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
      m.component(c).physics("solid").feature("load_shear_lid73").active(false);
      m.component(c).physics(ge).feature("ge1").set(
          "equation",1,1,"Fn_error_live163");
      try{m.study().remove(study);}catch(Exception ignore){}
      m.study().create(study);
      m.study(study).label("Stage 163 midpoint true film-contact load balance");
      m.study(study).create("param","Parametric");
      m.study(study).feature("param").set("pname",
          new String[]{"scale_true_pfilm163"});
      m.study(study).feature("param").set("plistarr",
          new String[]{"0 0.02 0.05 0.1 0.2 0.35 0.5 0.7 0.85 1"});
      m.study(study).feature("param").set("punit",new String[]{"1"});
      m.study(study).create("stat","Stationary");
      m.study(study).feature("stat").set("geometricNonlinearity","on");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid","on","tff","on",ge,"on"});
      m.study(study).feature("stat").set("useinitsol","on");
      m.study(study).feature("stat").set("initmethod","sol");
      m.study(study).feature("stat").set("initsol","sol25");
      m.study(study).feature("stat").set("initsoluse","sol25");
      m.study(study).feature("stat").set("initsolusesolnum",36);
      String step=study+"/stat";
      for(String tag:new String[]{"dcnt1","disp_lid_time",
          "load_partitioned_pfilm"})
        m.component(c).physics("solid").feature(tag).set("StudyStep",step);
      for(String tag:m.component(c).physics("tff").feature().tags())
        try{m.component(c).physics("tff").feature(tag).set("StudyStep",step);}
        catch(Exception ignore){}
      m.component(c).physics(ge).feature("ge1").set("StudyStep",step);
      String[] before=m.sol().tags();
      m.study(study).createAutoSequences("sol");
      String sol=newest(m,before);
      SolverFeature s1=m.sol(sol).feature("s1");
      try{s1.feature().remove("se1");}catch(Exception ignore){}
      try{s1.feature().remove("fc1");}catch(Exception ignore){}
      s1.create("fc1","FullyCoupled");
      s1.feature("fc1").set("linsolver","dDef");
      s1.feature("fc1").set("maxiter",220);
      m.save("293_lid8mm_stage163_midpoint_true_balance_setup_Model.mph");
      System.out.println("RUN_STAGE163 solver="+sol);
      m.sol(sol).runAll();
      m.result().dataset().create("dset163","Solution");
      m.result().dataset("dset163").set("solution",sol);
      m.result().numerical().create("eval163","EvalGlobal");
      m.result().numerical("eval163").set("data","dset163");
      m.result().numerical("eval163").set("expr",new String[]{
          "scale_true_pfilm163","Fn_contact119","Fn_film_live163","Fn_total_live163",
          "Fn_error_live163","dr_indent119","h_lift_live163",
          "h_under_live163","intop_film(tau_film_wall)"});
      double[][]a=m.result().numerical("eval163").getReal();
      for(int j=0;j<a[0].length;j++)
        System.out.printf(Locale.US,
            "scale=%.10g Fc=%.10g Ffilm=%.10g Ftotal=%.10g err=%.10g d=%.10g hlift=%.10g hunder=%.10g Fshear=%.10g%n",
            a[0][j],a[1][j],a[2][j],a[3][j],a[4][j],a[5][j],a[6][j],a[7][j],a[8][j]);
      m.save("294_lid8mm_stage163_midpoint_true_balance_results_Model.mph");
      ModelUtil.disconnect();
    }catch(Exception e){e.printStackTrace();System.exit(1);}
  }
}
