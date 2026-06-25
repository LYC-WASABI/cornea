import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage168_extended_separation_balance_scan{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);
  Model m=ModelUtil.load("Model","302_lid8mm_stage167_kinematic_balance_results_Model.mph");
  String c="comp1",pv="var_partitioned_local_pfilm",ge="ge_force_total111",st="std_ext168";
  m.param().set("h_ext168","2[um]");
  m.component(c).variable(pv).set("dr_indent119","d_ref_mid167-h_ext168");
  m.component(c).variable(pv).set("pfilm_ext168","0[Pa]");
  m.component(c).variable(pv).set("Wfilm_ext168","0[N]");
  m.component(c).variable(pv).set("Ftotal_ext168","Fn_contact119");
  m.component(c).physics("solid").feature("load_partitioned_pfilm").set("FperArea",
   new String[]{"0","0","0"});
  try{m.study().remove(st);}catch(Exception e){}
  m.study().create(st);m.study(st).label("Stage 168 extended separation balance");
  m.study(st).create("param","Parametric");m.study(st).feature("param").set("pname",new String[]{"h_ext168"});
  m.study(st).feature("param").set("plistarr",new String[]{"2 3 4 5 6 8 10 12 15 20 25 30"});
  m.study(st).feature("param").set("punit",new String[]{"um"});
  m.study(st).create("stat","Stationary");m.study(st).feature("stat").set("geometricNonlinearity","on");
  m.study(st).feature("stat").set("activate",new String[]{"solid","on","tff","off",ge,"off"});
  m.study(st).feature("stat").set("useinitsol","on");m.study(st).feature("stat").set("initmethod","sol");
  m.study(st).feature("stat").set("initsol","sol34");m.study(st).feature("stat").set("initsoluse","sol34");
  m.study(st).feature("stat").set("initsolusesolnum",12);
  String step=st+"/stat";for(String f:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
   m.component(c).physics("solid").feature(f).set("StudyStep",step);
  String[]b=m.sol().tags();m.study(st).createAutoSequences("sol");String sol=newest(m,b);
  SolverFeature s1=m.sol(sol).feature("s1");if(!Arrays.asList(s1.feature().tags()).contains("fc1"))s1.create("fc1","FullyCoupled");
  s1.feature("fc1").set("linsolver","dDef");s1.feature("fc1").set("maxiter",220);
  m.save("303_lid8mm_stage168_extended_balance_setup_Model.mph");
  System.out.println("RUN_STAGE168 "+sol);m.sol(sol).runAll();
  m.result().dataset().create("dset168","Solution");m.result().dataset("dset168").set("solution",sol);
  m.result().numerical().create("eval168","EvalGlobal");m.result().numerical("eval168").set("data","dset168");
  m.result().numerical("eval168").set("expr",new String[]{"h_ext168","dr_indent119","Fn_contact119"});
  double[][]x=m.result().numerical("eval168").getReal();for(int j=0;j<x[0].length;j++)
   System.out.printf(Locale.US,"h=%.9g d=%.9g Fc=%.9g%n",x[0][j],x[1][j],x[2][j]);
  m.save("304_lid8mm_stage168_extended_balance_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
