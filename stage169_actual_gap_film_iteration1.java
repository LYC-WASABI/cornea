import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage169_actual_gap_film_iteration1{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);
  Model m=ModelUtil.load("Model","290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
  String c="comp1",mv="var_mixed_lub",st="std_gapfilm169";
  m.param().set("phi_qs142","-35[deg]");m.param().set("t_replay","0.28[s]");
  m.param().set("h_gap_reg169","20[nm]");
  m.component(c).variable(mv).set("gap_actual169",
   "min(max(withsol('sol25',geomgap_dst_cp_lid_cornea,"
       +"setval(phi_qs142,-35[deg])),0),gap_cap_tear)");
  m.component(c).variable(mv).set("gap_pos169",
   "0.5*(gap_actual169+sqrt(gap_actual169^2+h_gap_reg169^2))");
  m.component(c).variable(mv).set("h_actual169",
   "max(h_min_tear,h0_tear+Rq_eq+gap_pos169)");
  m.component(c).physics("tff").feature("ffp1").set("hw1","h_actual169");
  m.component(c).physics("tff").feature("init1").set("pfilm","0[Pa]");
  try{m.study().remove(st);}catch(Exception e){}
  m.study().create(st);m.study(st).label("Stage 169 film from actual contact gap");
  m.study(st).create("stat","Stationary");
  m.study(st).feature("stat").set("activate",
   new String[]{"solid","off","tff","on","ge_force_total111","off"});
  String step=st+"/stat";for(String f:m.component(c).physics("tff").feature().tags())
   try{m.component(c).physics("tff").feature(f).set("StudyStep",step);}catch(Exception e){}
  String[]b=m.sol().tags();m.study(st).createAutoSequences("sol");String sol=newest(m,b);
  m.save("305_lid8mm_stage169_actual_gap_film_iter1_setup_Model.mph");
  System.out.println("RUN_STAGE169 "+sol);m.sol(sol).runAll();
  m.result().dataset().create("dset169","Solution");m.result().dataset("dset169").set("solution",sol);
  m.result().numerical().create("eval169","EvalGlobal");m.result().numerical("eval169").set("data","dset169");
  m.result().numerical("eval169").set("expr",new String[]{
   "intop_film(max(pfilm,0))","intop_film(pfilm)","intop_film(tau_film_wall)",
   "intop_film(h_actual169)/intop_film(1)"});
  double[][]x=m.result().numerical("eval169").getReal();System.out.printf(Locale.US,
   "Wpos=%.10g Wnet=%.10g Fshear=%.10g havg=%.10g%n",x[0][0],x[1][0],x[2][0],x[3][0]);
  m.save("306_lid8mm_stage169_actual_gap_film_iter1_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
