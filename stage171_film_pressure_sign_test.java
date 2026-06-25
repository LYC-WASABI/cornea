import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage171_film_pressure_sign_test{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model",
   "308_lid8mm_stage170_actual_gap_offset_scan_results_Model.mph");
  String c="comp1",pv="var_partitioned_local_pfilm",ge="ge_force_total111",st="std_sign171";
  m.param().set("pressure_sign171","-1");m.param().set("h_offset170","0[um]");
  m.component(c).physics("solid").feature("load_partitioned_pfilm").set("FperArea",
   new String[]{"pressure_sign171*pfilm170*nx","pressure_sign171*pfilm170*ny",
    "pressure_sign171*pfilm170*nz"});
  try{m.study().remove(st);}catch(Exception e){}m.study().create(st);m.study(st).label("Stage 171 pressure sign test");
  m.study(st).create("param","Parametric");m.study(st).feature("param").set("pname",new String[]{"pressure_sign171"});
  m.study(st).feature("param").set("plistarr",new String[]{"-1 0 1"});m.study(st).feature("param").set("punit",new String[]{"1"});
  m.study(st).create("stat","Stationary");m.study(st).feature("stat").set("geometricNonlinearity","on");
  m.study(st).feature("stat").set("activate",new String[]{"solid","on","tff","off",ge,"off"});
  m.study(st).feature("stat").set("useinitsol","on");m.study(st).feature("stat").set("initmethod","sol");
  m.study(st).feature("stat").set("initsol","sol25");m.study(st).feature("stat").set("initsoluse","sol25");
  m.study(st).feature("stat").set("initsolusesolnum",36);
  String step=st+"/stat";for(String f:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
   m.component(c).physics("solid").feature(f).set("StudyStep",step);
  String[]b=m.sol().tags();m.study(st).createAutoSequences("sol");String sol=newest(m,b);
  SolverFeature s1=m.sol(sol).feature("s1");if(!Arrays.asList(s1.feature().tags()).contains("fc1"))s1.create("fc1","FullyCoupled");
  s1.feature("fc1").set("linsolver","dDef");s1.feature("fc1").set("maxiter",220);
  m.save("309_lid8mm_stage171_pressure_sign_test_setup_Model.mph");System.out.println("RUN_STAGE171 "+sol);
  m.sol(sol).runAll();m.result().dataset().create("dset171","Solution");m.result().dataset("dset171").set("solution",sol);
  m.result().numerical().create("eval171","EvalGlobal");m.result().numerical("eval171").set("data","dset171");
  m.result().numerical("eval171").set("expr",new String[]{"pressure_sign171","Wfilm170","Fn_contact119","Ftotal170"});
  double[][]x=m.result().numerical("eval171").getReal();for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
   "sign=%.3g W=%.9g Fc=%.9g rawTotal=%.9g%n",x[0][j],x[1][j],x[2][j],x[1][j]+x[2][j]);
  m.save("310_lid8mm_stage171_pressure_sign_test_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
