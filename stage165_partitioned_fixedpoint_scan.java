import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class stage165_partitioned_fixedpoint_scan {
  private static String newest(Model m,String[] before){
    Set<String> old=new HashSet<String>(Arrays.asList(before));
    String last="";
    for(String s:m.sol().tags()){last=s;if(!old.contains(s))return s;}
    return last;
  }
  public static void main(String[]args){
    try{
      ModelUtil.initStandalone(false);
      Model m=ModelUtil.load("Model",
          "290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
      String c="comp1",mv="var_mixed_lub",pv="var_partitioned_local_pfilm";
      String ge="ge_force_total111";
      m.param().set("phi_qs142","-35[deg]");
      m.param().set("t_replay","0.28[s]");
      m.param().set("h_iter165","0.9[um]");
      m.param().set("d_ref_mid165","0.00696[mm]");
      m.component(c).variable(mv).set("h_under165",
          "max(h_min_tear,h0_tear+Rq_eq+h_iter165)");
      m.component(c).variable(mv).set("h_film165",
          "h_outside_track+lid_mask*(h_under165-h_outside_track)");
      m.component(c).physics("tff").feature("ffp1").set("hw1","h_film165");
      m.component(c).physics("tff").feature("init1").set("pfilm","0[Pa]");

      String filmStudy="std_film165";
      try{m.study().remove(filmStudy);}catch(Exception ignore){}
      m.study().create(filmStudy);
      m.study(filmStudy).label("Stage 165 fine film-gap scan");
      m.study(filmStudy).create("param","Parametric");
      m.study(filmStudy).feature("param").set("pname",new String[]{"h_iter165"});
      m.study(filmStudy).feature("param").set("plistarr",
          new String[]{"0.9 1.0 1.1 1.2 1.3 1.4 1.5 1.6 1.7 1.8 1.9 2.0"});
      m.study(filmStudy).feature("param").set("punit",new String[]{"um"});
      m.study(filmStudy).create("stat","Stationary");
      m.study(filmStudy).feature("stat").set("activate",
          new String[]{"solid","off","tff","on",ge,"off"});
      String fstep=filmStudy+"/stat";
      for(String tag:m.component(c).physics("tff").feature().tags())
        try{m.component(c).physics("tff").feature(tag).set("StudyStep",fstep);}
        catch(Exception ignore){}
      String[]before=m.sol().tags();
      m.study(filmStudy).createAutoSequences("sol");
      String filmSol=newest(m,before);
      System.out.println("RUN_FILM165 "+filmSol);
      m.sol(filmSol).runAll();

      m.component(c).variable(pv).set("pfilm165",
          "withsol('"+filmSol+"',max(pfilm,0),setval(h_iter165,h_iter165))");
      m.component(c).variable(pv).set("Wfilm165",
          "withsol('"+filmSol+"',intop_film(max(pfilm,0)),"
              +"setval(h_iter165,h_iter165))");
      m.component(c).variable(pv).set("Ftotal165",
          "Fn_contact119+Wfilm165");
      m.component(c).variable(pv).set("Ferr165",
          "(Ftotal165-F_total_target)/F_total_target"
              +"+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
      m.component(c).variable(pv).set("h_implied165",
          "max(0[um],d_ref_mid165-dr_indent119)");
      m.component(c).variable(pv).set("h_fixedpoint_res165",
          "h_implied165-h_iter165");
      m.component(c).physics("solid").feature("load_partitioned_pfilm").set(
          "FperArea",new String[]{"-pfilm165*nx","-pfilm165*ny","-pfilm165*nz"});
      m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
      m.component(c).physics("solid").feature("load_shear_lid73").active(false);
      m.component(c).physics(ge).feature("ge1").set("equation",1,1,"Ferr165");

      String solidStudy="std_solid165";
      try{m.study().remove(solidStudy);}catch(Exception ignore){}
      m.study().create(solidStudy);
      m.study(solidStudy).label("Stage 165 structural fixed-point scan");
      m.study(solidStudy).create("param","Parametric");
      m.study(solidStudy).feature("param").set("pname",new String[]{"h_iter165"});
      m.study(solidStudy).feature("param").set("plistarr",
          new String[]{"0.9 1.0 1.1 1.2 1.3 1.4 1.5 1.6 1.7 1.8 1.9 2.0"});
      m.study(solidStudy).feature("param").set("punit",new String[]{"um"});
      m.study(solidStudy).create("stat","Stationary");
      m.study(solidStudy).feature("stat").set("geometricNonlinearity","on");
      m.study(solidStudy).feature("stat").set("activate",
          new String[]{"solid","on","tff","off",ge,"on"});
      m.study(solidStudy).feature("stat").set("useinitsol","on");
      m.study(solidStudy).feature("stat").set("initmethod","sol");
      m.study(solidStudy).feature("stat").set("initsol","sol25");
      m.study(solidStudy).feature("stat").set("initsoluse","sol25");
      m.study(solidStudy).feature("stat").set("initsolusesolnum",36);
      String sstep=solidStudy+"/stat";
      for(String tag:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
        m.component(c).physics("solid").feature(tag).set("StudyStep",sstep);
      m.component(c).physics(ge).feature("ge1").set("StudyStep",sstep);
      before=m.sol().tags();
      m.study(solidStudy).createAutoSequences("sol");
      String solidSol=newest(m,before);
      SolverFeature s1=m.sol(solidSol).feature("s1");
      try{s1.feature().remove("se1");}catch(Exception ignore){}
      try{s1.feature().remove("fc1");}catch(Exception ignore){}
      s1.create("fc1","FullyCoupled");s1.feature("fc1").set("linsolver","dDef");
      s1.feature("fc1").set("maxiter",240);
      m.save("297_lid8mm_stage165_fixedpoint_scan_setup_Model.mph");
      System.out.println("RUN_SOLID165 "+solidSol);
      m.sol(solidSol).runAll();
      m.result().dataset().create("dset165","Solution");
      m.result().dataset("dset165").set("solution",solidSol);
      m.result().numerical().create("eval165","EvalGlobal");
      m.result().numerical("eval165").set("data","dset165");
      m.result().numerical("eval165").set("expr",new String[]{
          "h_iter165","Wfilm165","Fn_contact119","Ftotal165",
          "dr_indent119","h_implied165","h_fixedpoint_res165"});
      double[][]a=m.result().numerical("eval165").getReal();
      for(int j=0;j<a[0].length;j++)
        System.out.printf(Locale.US,
            "h=%.8g W=%.8g Fc=%.8g Ft=%.8g d=%.8g himp=%.8g res=%.8g%n",
            a[0][j],a[1][j],a[2][j],a[3][j],a[4][j],a[5][j],a[6][j]);
      m.save("298_lid8mm_stage165_fixedpoint_scan_results_Model.mph");
      ModelUtil.disconnect();
    }catch(Exception e){e.printStackTrace();System.exit(1);}
  }
}
