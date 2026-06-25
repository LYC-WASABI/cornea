import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage172_partitioned_iteration2{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model",
   "308_lid8mm_stage170_actual_gap_offset_scan_results_Model.mph");
  String c="comp1",mv="var_mixed_lub",pv="var_partitioned_local_pfilm",ge="ge_force_total111";
  m.param().set("h_offset170","0.8[um]");
  m.component(c).variable(pv).set("dr_indent119",
   "2*d_indent_bound154/pi*atan(pi*q_force_total111/(2*q_indent_scale154))");
  m.component(c).variable(pv).set("Fnfilm172","Wfilm170");
  m.component(c).variable(pv).set("Fntotal172","Fn_contact119+Fnfilm172");
  m.component(c).variable(pv).set("Fnerr172",
   "(Fntotal172-F_total_target)/F_total_target+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
  m.component(c).physics("solid").feature("load_partitioned_pfilm").set("FperArea",
   new String[]{"-pfilm170*nx","-pfilm170*ny","-pfilm170*nz"});
  m.component(c).physics(ge).feature("ge1").set("equation",1,1,"Fnerr172");
  String ss="std_struct172";try{m.study().remove(ss);}catch(Exception e){}
  m.study().create(ss);m.study(ss).label("Stage 172 partitioned structure iteration");
  m.study(ss).create("stat","Stationary");m.study(ss).feature("stat").set("geometricNonlinearity","on");
  m.study(ss).feature("stat").set("activate",new String[]{"solid","on","tff","off",ge,"on"});
  m.study(ss).feature("stat").set("useinitsol","on");m.study(ss).feature("stat").set("initmethod","sol");
  m.study(ss).feature("stat").set("initsol","sol34");m.study(ss).feature("stat").set("initsoluse","sol34");
  m.study(ss).feature("stat").set("initsolusesolnum",5);
  String step=ss+"/stat";for(String f:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
   m.component(c).physics("solid").feature(f).set("StudyStep",step);
  m.component(c).physics(ge).feature("ge1").set("StudyStep",step);
  String[]b=m.sol().tags();m.study(ss).createAutoSequences("sol");String ssol=newest(m,b);
  SolverFeature s1=m.sol(ssol).feature("s1");if(!Arrays.asList(s1.feature().tags()).contains("fc1"))s1.create("fc1","FullyCoupled");
  s1.feature("fc1").set("linsolver","dDef");s1.feature("fc1").set("maxiter",260);
  m.save("311_lid8mm_stage172_partition_iter2_setup_Model.mph");
  System.out.println("RUN_STRUCT172 "+ssol);m.sol(ssol).runAll();
  m.result().dataset().create("dset172s","Solution");m.result().dataset("dset172s").set("solution",ssol);
  m.result().numerical().create("eval172s","EvalGlobal");m.result().numerical("eval172s").set("data","dset172s");
  m.result().numerical("eval172s").set("expr",new String[]{"Fn_contact119","Fnfilm172","Fntotal172","dr_indent119"});
  double[][]sx=m.result().numerical("eval172s").getReal();System.out.printf(Locale.US,
   "STRUCT Fc=%.10g Ffilm=%.10g Ft=%.10g d=%.10g%n",sx[0][0],sx[1][0],sx[2][0],sx[3][0]);

  m.component(c).variable(mv).set("gap_actual172",
   "min(max(withsol('"+ssol+"',geomgap_dst_cp_lid_cornea),0),gap_cap_tear)");
  m.component(c).variable(mv).set("gap_pos172",
   "0.5*(gap_actual172+sqrt(gap_actual172^2+h_gap_reg169^2))");
  m.component(c).variable(mv).set("h_actual172",
   "max(h_min_tear,h0_tear+Rq_eq+gap_pos172)");
  m.component(c).physics("tff").feature("ffp1").set("hw1","h_actual172");
  String fs="std_film172";try{m.study().remove(fs);}catch(Exception e){}
  m.study().create(fs);m.study(fs).label("Stage 172 updated film from new structural gap");
  m.study(fs).create("stat","Stationary");m.study(fs).feature("stat").set("activate",
   new String[]{"solid","off","tff","on",ge,"off"});
  step=fs+"/stat";for(String f:m.component(c).physics("tff").feature().tags())
   try{m.component(c).physics("tff").feature(f).set("StudyStep",step);}catch(Exception e){}
  b=m.sol().tags();m.study(fs).createAutoSequences("sol");String fsol=newest(m,b);
  System.out.println("RUN_FILM172 "+fsol);m.sol(fsol).runAll();
  m.result().dataset().create("dset172f","Solution");m.result().dataset("dset172f").set("solution",fsol);
  m.result().numerical().create("eval172f","EvalGlobal");m.result().numerical("eval172f").set("data","dset172f");
  m.result().numerical("eval172f").set("expr",new String[]{"intop_film(max(pfilm,0))",
   "intop_film(tau_film_wall)","intop_film(h_actual172)/intop_film(1)"});
  double[][]fx=m.result().numerical("eval172f").getReal();System.out.printf(Locale.US,
   "FILM2 W=%.10g Fshear=%.10g havg=%.10g%n",fx[0][0],fx[1][0],fx[2][0]);
  m.save("312_lid8mm_stage172_partition_iter2_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
