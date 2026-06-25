import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage167_kinematically_consistent_balance_scan{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);
  Model m=ModelUtil.load("Model","stage165_partitioned_fixedpoint_scan_output_Model.mph");
  String c="comp1",pv="var_partitioned_local_pfilm",ge="ge_force_total111",study="std_kin167";
  m.param().set("d_ref_mid167","0.00696[mm]");
  m.component(c).variable(pv).set("dr_indent119","d_ref_mid167-h_iter165");
  m.component(c).variable(pv).set("pfilm_kin167",
   "withsol('sol32',max(pfilm,0),setval(h_iter165,h_iter165))");
  m.component(c).variable(pv).set("Wfilm_kin167",
   "withsol('sol32',intop_film(max(pfilm,0)),setval(h_iter165,h_iter165))");
  m.component(c).variable(pv).set("Ftotal_kin167","Fn_contact119+Wfilm_kin167");
  m.component(c).variable(pv).set("Ferr_kin167","Ftotal_kin167-F_total_target");
  m.component(c).physics("solid").feature("load_partitioned_pfilm").set("FperArea",
   new String[]{"-pfilm_kin167*nx","-pfilm_kin167*ny","-pfilm_kin167*nz"});
  m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
  m.component(c).physics("solid").feature("load_shear_lid73").active(false);
  try{m.study().remove(study);}catch(Exception e){}
  m.study().create(study);m.study(study).label("Stage 167 kinematically consistent load scan");
  m.study(study).create("param","Parametric");
  m.study(study).feature("param").set("pname",new String[]{"h_iter165"});
  m.study(study).feature("param").set("plistarr",
   new String[]{"0.9 1.0 1.1 1.2 1.3 1.4 1.5 1.6 1.7 1.8 1.9 2.0"});
  m.study(study).feature("param").set("punit",new String[]{"um"});
  m.study(study).create("stat","Stationary");
  m.study(study).feature("stat").set("geometricNonlinearity","on");
  m.study(study).feature("stat").set("activate",new String[]{"solid","on","tff","off",ge,"off"});
  m.study(study).feature("stat").set("useinitsol","on");
  m.study(study).feature("stat").set("initmethod","sol");
  m.study(study).feature("stat").set("initsol","sol25");
  m.study(study).feature("stat").set("initsoluse","sol25");
  m.study(study).feature("stat").set("initsolusesolnum",36);
  String step=study+"/stat";for(String f:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
   m.component(c).physics("solid").feature(f).set("StudyStep",step);
  String[]b=m.sol().tags();m.study(study).createAutoSequences("sol");String sol=newest(m,b);
  SolverFeature s1=m.sol(sol).feature("s1");try{s1.feature().remove("se1");}catch(Exception e){}
  if(!Arrays.asList(s1.feature().tags()).contains("fc1"))s1.create("fc1","FullyCoupled");
  s1.feature("fc1").set("linsolver","dDef");s1.feature("fc1").set("maxiter",220);
  m.save("301_lid8mm_stage167_kinematic_balance_setup_Model.mph");
  System.out.println("RUN_STAGE167 "+sol);m.sol(sol).runAll();
  m.result().dataset().create("dset167","Solution");m.result().dataset("dset167").set("solution",sol);
  m.result().numerical().create("eval167","EvalGlobal");m.result().numerical("eval167").set("data","dset167");
  m.result().numerical("eval167").set("expr",new String[]{"h_iter165","dr_indent119",
   "Wfilm_kin167","Fn_contact119","Ftotal_kin167","Ferr_kin167"});
  double[][]x=m.result().numerical("eval167").getReal();for(int j=0;j<x[0].length;j++)
   System.out.printf(Locale.US,"h=%.9g d=%.9g W=%.9g Fc=%.9g Ft=%.9g Ferr=%.9g%n",
    x[0][j],x[1][j],x[2][j],x[3][j],x[4][j],x[5][j]);
  m.save("302_lid8mm_stage167_kinematic_balance_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
