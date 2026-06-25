import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage170_actual_gap_offset_load_scan{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model",
   "306_lid8mm_stage169_actual_gap_film_iter1_results_Model.mph");
  String c="comp1",mv="var_mixed_lub",pv="var_partitioned_local_pfilm",ge="ge_force_total111";
  m.param().set("h_offset170","0[um]");m.param().set("d_mid_fixed170","0.0069567[mm]");
  m.component(c).variable(mv).set("h_actual170","max(h_min_tear,h_actual169+h_offset170)");
  m.component(c).physics("tff").feature("ffp1").set("hw1","h_actual170");
  String fs="std_film170";try{m.study().remove(fs);}catch(Exception e){}
  m.study().create(fs);m.study(fs).label("Stage 170 actual-gap film offset scan");
  m.study(fs).create("param","Parametric");m.study(fs).feature("param").set("pname",new String[]{"h_offset170"});
  m.study(fs).feature("param").set("plistarr",new String[]{"range(0,0.2,2.0)"});
  m.study(fs).feature("param").set("punit",new String[]{"um"});
  m.study(fs).create("stat","Stationary");m.study(fs).feature("stat").set("activate",
   new String[]{"solid","off","tff","on",ge,"off"});
  String step=fs+"/stat";for(String f:m.component(c).physics("tff").feature().tags())
   try{m.component(c).physics("tff").feature(f).set("StudyStep",step);}catch(Exception e){}
  String[]b=m.sol().tags();m.study(fs).createAutoSequences("sol");String fsol=newest(m,b);
  System.out.println("RUN_FILM170 "+fsol);m.sol(fsol).runAll();
  m.component(c).variable(pv).set("dr_indent119","d_mid_fixed170");
  m.component(c).variable(pv).set("pfilm170",
   "withsol('"+fsol+"',max(pfilm,0),setval(h_offset170,h_offset170))");
  m.component(c).variable(pv).set("Wfilm170",
   "withsol('"+fsol+"',intop_film(max(pfilm,0)),setval(h_offset170,h_offset170))");
  m.component(c).variable(pv).set("Ftotal170","Fn_contact119+Wfilm170");
  m.component(c).physics("solid").feature("load_partitioned_pfilm").set("FperArea",
   new String[]{"-pfilm170*nx","-pfilm170*ny","-pfilm170*nz"});
  m.component(c).physics("solid").feature("load_shear_cornea73").active(false);
  m.component(c).physics("solid").feature("load_shear_lid73").active(false);
  String ss="std_solid170";try{m.study().remove(ss);}catch(Exception e){}
  m.study().create(ss);m.study(ss).label("Stage 170 pressure-feedback total-load scan");
  m.study(ss).create("param","Parametric");m.study(ss).feature("param").set("pname",new String[]{"h_offset170"});
  m.study(ss).feature("param").set("plistarr",new String[]{"range(0,0.2,2.0)"});
  m.study(ss).feature("param").set("punit",new String[]{"um"});
  m.study(ss).create("stat","Stationary");m.study(ss).feature("stat").set("geometricNonlinearity","on");
  m.study(ss).feature("stat").set("activate",new String[]{"solid","on","tff","off",ge,"off"});
  m.study(ss).feature("stat").set("useinitsol","on");m.study(ss).feature("stat").set("initmethod","sol");
  m.study(ss).feature("stat").set("initsol","sol25");m.study(ss).feature("stat").set("initsoluse","sol25");
  m.study(ss).feature("stat").set("initsolusesolnum",36);
  step=ss+"/stat";for(String f:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
   m.component(c).physics("solid").feature(f).set("StudyStep",step);
  b=m.sol().tags();m.study(ss).createAutoSequences("sol");String ssol=newest(m,b);
  SolverFeature s1=m.sol(ssol).feature("s1");if(!Arrays.asList(s1.feature().tags()).contains("fc1"))s1.create("fc1","FullyCoupled");
  s1.feature("fc1").set("linsolver","dDef");s1.feature("fc1").set("maxiter",220);
  m.save("307_lid8mm_stage170_actual_gap_offset_scan_setup_Model.mph");
  System.out.println("RUN_SOLID170 "+ssol);m.sol(ssol).runAll();
  m.result().dataset().create("dset170","Solution");m.result().dataset("dset170").set("solution",ssol);
  m.result().numerical().create("eval170","EvalGlobal");m.result().numerical("eval170").set("data","dset170");
  m.result().numerical("eval170").set("expr",new String[]{"h_offset170","Wfilm170","Fn_contact119","Ftotal170"});
  double[][]x=m.result().numerical("eval170").getReal();for(int j=0;j<x[0].length;j++)
   System.out.printf(Locale.US,"hoff=%.9g W=%.9g Fc=%.9g Ft=%.9g%n",x[0][j],x[1][j],x[2][j],x[3][j]);
  m.save("308_lid8mm_stage170_actual_gap_offset_scan_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
