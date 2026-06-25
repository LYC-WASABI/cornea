import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage174_stable_true_balance_midpoint{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model",
   "314_lid8mm_stage173_gap_relax_scan_results_Model.mph");
  String c="comp1",mv="var_mixed_lub",pv="var_partitioned_local_pfilm",ge="ge_force_total111",st="std_balance174";
  m.param().set("alpha_gap173","0.012");
  m.component(c).variable(pv).set("pfilm174",
   "withsol('sol37',max(pfilm,0),setval(alpha_gap173,alpha_gap173))");
  m.component(c).variable(pv).set("Wfilm174",
   "withsol('sol37',intop_film(max(pfilm,0)),setval(alpha_gap173,alpha_gap173))");
  m.component(c).variable(pv).set("dr_indent119",
   "2*d_indent_bound154/pi*atan(pi*q_force_total111/(2*q_indent_scale154))");
  m.component(c).variable(pv).set("Ftotal174","Fn_contact119+Wfilm174");
  m.component(c).variable(pv).set("Ferr174",
   "(Ftotal174-F_total_target)/F_total_target+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
  m.component(c).physics("solid").feature("load_partitioned_pfilm").set("FperArea",
   new String[]{"-pfilm174*nx","-pfilm174*ny","-pfilm174*nz"});
  m.component(c).physics(ge).feature("ge1").set("equation",1,1,"Ferr174");
  try{m.study().remove(st);}catch(Exception e){}m.study().create(st);
  m.study(st).label("Stage 174 stable midpoint true load balance");
  m.study(st).create("stat","Stationary");m.study(st).feature("stat").set("geometricNonlinearity","on");
  m.study(st).feature("stat").set("activate",new String[]{"solid","on","tff","off",ge,"on"});
  m.study(st).feature("stat").set("useinitsol","on");m.study(st).feature("stat").set("initmethod","sol");
  m.study(st).feature("stat").set("initsol","sol35");m.study(st).feature("stat").set("initsoluse","sol35");
  m.study(st).feature("stat").set("initsolusesolnum","last");
  String step=st+"/stat";for(String f:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
   m.component(c).physics("solid").feature(f).set("StudyStep",step);
  m.component(c).physics(ge).feature("ge1").set("StudyStep",step);
  String[]b=m.sol().tags();m.study(st).createAutoSequences("sol");String sol=newest(m,b);
  SolverFeature s1=m.sol(sol).feature("s1");if(!Arrays.asList(s1.feature().tags()).contains("fc1"))s1.create("fc1","FullyCoupled");
  s1.feature("fc1").set("linsolver","dDef");s1.feature("fc1").set("maxiter",280);
  m.save("315_lid8mm_stage174_stable_true_balance_setup_Model.mph");
  System.out.println("RUN_STAGE174 "+sol);m.sol(sol).runAll();
  m.component(c).variable(mv).set("gap_final174",
   "min(max(withsol('"+sol+"',geomgap_dst_cp_lid_cornea),0),gap_cap_tear)");
  m.result().dataset().create("dset174","Solution");m.result().dataset("dset174").set("solution",sol);
  m.result().numerical().create("eval174","EvalGlobal");m.result().numerical("eval174").set("data","dset174");
  m.result().numerical("eval174").set("expr",new String[]{"Fn_contact119","Wfilm174","Ftotal174",
   "dr_indent119","withsol('sol37',intop_film(tau_film_wall),setval(alpha_gap173,alpha_gap173))"});
  double[][]x=m.result().numerical("eval174").getReal();System.out.printf(Locale.US,
   "Fc=%.10g Wfilm=%.10g Ft=%.10g d=%.10g FshearFilm=%.10g muFilm=%.10g%n",
   x[0][0],x[1][0],x[2][0],x[3][0],x[4][0],x[4][0]/x[2][0]);
  m.save("316_lid8mm_stage174_stable_true_balance_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
