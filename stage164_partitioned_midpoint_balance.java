import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage164_partitioned_midpoint_balance {
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
          "292_lid8mm_stage162_midpoint_pressure_calibration_results_Model.mph");
      String c="comp1",pv="var_partitioned_local_pfilm";
      String ge="ge_force_total111",study="std_partition164";
      m.param().set("phi_qs142","-35[deg]");
      m.param().set("h_iter164","1[um]",
          "Current partitioned film-separation iterate");
      m.param().set("d_ref_mid163","0.00696[mm]",
          "Dry-contact midpoint indentation reference");
      m.component(c).variable(pv).set("pfilm_iter164",
          "withsol('sol32',max(pfilm,0),setval(h_lift162,h_iter164))");
      m.component(c).variable(pv).set("Wfilm_iter164",
          "withsol('sol32',intop_film(max(pfilm,0)),"
              +"setval(h_lift162,h_iter164))");
      m.component(c).variable(pv).set("Ftotal_iter164",
          "Fn_contact119+Wfilm_iter164");
      m.component(c).variable(pv).set("Ferr_iter164",
          "(Ftotal_iter164-F_total_target)/F_total_target"
              +"+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      m.component(c).variable(pv).set("h_implied164",
          "max(0[um],d_ref_mid163-dr_indent119)");
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea",new String[]{
              "-pfilm_iter164*nx","-pfilm_iter164*ny","-pfilm_iter164*nz"});
      m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
      m.component(c).physics("solid").feature("load_shear_lid73").active(false);
      m.component(c).physics(ge).feature("ge1").set("equation",1,1,"Ferr_iter164");
      try{m.study().remove(study);}catch(Exception ignore){}
      m.study().create(study);
      m.study(study).label("Stage 164 partitioned midpoint true load balance");
      m.study(study).create("stat","Stationary");
      m.study(study).feature("stat").set("geometricNonlinearity","on");
      m.study(study).feature("stat").set("activate",
          new String[]{"solid","on","tff","off",ge,"on"});
      m.study(study).feature("stat").set("useinitsol","on");
      m.study(study).feature("stat").set("initmethod","sol");
      m.study(study).feature("stat").set("initsol","sol25");
      m.study(study).feature("stat").set("initsoluse","sol25");
      m.study(study).feature("stat").set("initsolusesolnum",36);
      String step=study+"/stat";
      for(String tag:new String[]{"dcnt1","disp_lid_time",
          "load_partitioned_pfilm"})
        m.component(c).physics("solid").feature(tag).set("StudyStep",step);
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
      m.save("295_lid8mm_stage164_partitioned_midpoint_setup_Model.mph");
      System.out.println("RUN_STAGE164 solver="+sol);
      m.sol(sol).runAll();
      m.result().dataset().create("dset164","Solution");
      m.result().dataset("dset164").set("solution",sol);
      m.result().numerical().create("eval164","EvalGlobal");
      m.result().numerical("eval164").set("data","dset164");
      m.result().numerical("eval164").set("expr",new String[]{
          "Fn_contact119","Wfilm_iter164","Ftotal_iter164","Ferr_iter164",
          "dr_indent119","h_iter164","h_implied164"});
      double[][]a=m.result().numerical("eval164").getReal();
      for(int j=0;j<a[0].length;j++)
        System.out.printf(Locale.US,
            "Fc=%.10g Ffilm=%.10g Ft=%.10g err=%.10g d=%.10g hiter=%.10g himplied=%.10g%n",
            a[0][j],a[1][j],a[2][j],a[3][j],a[4][j],a[5][j],a[6][j]);
      m.save("296_lid8mm_stage164_partitioned_midpoint_results_Model.mph");
      ModelUtil.disconnect();
    }catch(Exception e){e.printStackTrace();System.exit(1);}
  }
}
