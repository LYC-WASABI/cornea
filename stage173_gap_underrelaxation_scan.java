import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class stage173_gap_underrelaxation_scan{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){try{
  ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model",
   "312_lid8mm_stage172_partition_iter2_results_Model.mph");
  String c="comp1",mv="var_mixed_lub",st="std_relax173";
  m.param().set("alpha_gap173","0");
  m.component(c).variable(mv).set("h_prev173","h_actual169+h_offset170");
  m.component(c).variable(mv).set("h_relaxed173",
   "max(h_min_tear,(1-alpha_gap173)*h_prev173+alpha_gap173*h_actual172)");
  m.component(c).physics("tff").feature("ffp1").set("hw1","h_relaxed173");
  try{m.study().remove(st);}catch(Exception e){}m.study().create(st);
  m.study(st).label("Stage 173 gap under-relaxation scan");
  m.study(st).create("param","Parametric");m.study(st).feature("param").set("pname",new String[]{"alpha_gap173"});
  m.study(st).feature("param").set("plistarr",new String[]{"0 0.002 0.005 0.01 0.02 0.05 0.1"});
  m.study(st).feature("param").set("punit",new String[]{"1"});
  m.study(st).create("stat","Stationary");m.study(st).feature("stat").set("activate",
   new String[]{"solid","off","tff","on","ge_force_total111","off"});
  String step=st+"/stat";for(String f:m.component(c).physics("tff").feature().tags())
   try{m.component(c).physics("tff").feature(f).set("StudyStep",step);}catch(Exception e){}
  String[]b=m.sol().tags();m.study(st).createAutoSequences("sol");String sol=newest(m,b);
  m.save("313_lid8mm_stage173_gap_relax_scan_setup_Model.mph");
  System.out.println("RUN_STAGE173 "+sol);m.sol(sol).runAll();
  m.result().dataset().create("dset173","Solution");m.result().dataset("dset173").set("solution",sol);
  m.result().numerical().create("eval173","EvalGlobal");m.result().numerical("eval173").set("data","dset173");
  m.result().numerical("eval173").set("expr",new String[]{"alpha_gap173","intop_film(max(pfilm,0))",
   "intop_film(tau_film_wall)","intop_film(h_relaxed173)/intop_film(1)"});
  double[][]x=m.result().numerical("eval173").getReal();for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
   "alpha=%.6g W=%.9g Fshear=%.9g havg=%.9g%n",x[0][j],x[1][j],x[2][j],x[3][j]);
  m.save("314_lid8mm_stage173_gap_relax_scan_results_Model.mph");ModelUtil.disconnect();
 }catch(Exception e){e.printStackTrace();System.exit(1);}}
}
