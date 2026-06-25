import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;
public class stage166_midpoint_fixedpoint_fine{
 static String newest(Model m,String[]b){Set<String>o=new HashSet<>(Arrays.asList(b));String z="";
  for(String s:m.sol().tags()){z=s;if(!o.contains(s))return s;}return z;}
 public static void main(String[]args){
  try{
   ModelUtil.initStandalone(false);
   Model m=ModelUtil.load("Model","stage165_partitioned_fixedpoint_scan_output_Model.mph");
   String c="comp1",mv="var_mixed_lub",pv="var_partitioned_local_pfilm",ge="ge_force_total111";
   m.param().set("h_fine166","1.0[um]");
   m.component(c).variable(mv).set("h_under166","max(h_min_tear,h0_tear+Rq_eq+h_fine166)");
   m.component(c).variable(mv).set("h_film166",
    "h_outside_track+lid_mask*(h_under166-h_outside_track)");
   m.component(c).physics("tff").feature("ffp1").set("hw1","h_film166");
   String fs="std_film166";try{m.study().remove(fs);}catch(Exception e){}
   m.study().create(fs);m.study(fs).label("Stage 166 fine film gap");
   m.study(fs).create("param","Parametric");
   m.study(fs).feature("param").set("pname",new String[]{"h_fine166"});
   m.study(fs).feature("param").set("plistarr",new String[]{"range(1.00,0.01,1.10)"});
   m.study(fs).feature("param").set("punit",new String[]{"um"});
   m.study(fs).create("stat","Stationary");
   m.study(fs).feature("stat").set("activate",new String[]{"solid","off","tff","on",ge,"off"});
   String step=fs+"/stat";for(String f:m.component(c).physics("tff").feature().tags())
    try{m.component(c).physics("tff").feature(f).set("StudyStep",step);}catch(Exception e){}
   String[]b=m.sol().tags();m.study(fs).createAutoSequences("sol");String fsol=newest(m,b);
   System.out.println("RUN_FILM166 "+fsol);m.sol(fsol).runAll();
   m.component(c).variable(pv).set("pfilm166",
    "withsol('"+fsol+"',max(pfilm,0),setval(h_fine166,h_fine166))");
   m.component(c).variable(pv).set("Wfilm166",
    "withsol('"+fsol+"',intop_film(max(pfilm,0)),setval(h_fine166,h_fine166))");
   m.component(c).variable(pv).set("Ftotal166","Fn_contact119+Wfilm166");
   m.component(c).variable(pv).set("Ferr166",
    "(Ftotal166-F_total_target)/F_total_target+q_barrier_scale154*(q_force_total111/q_indent_scale154)^5");
   m.component(c).variable(pv).set("himplied166","max(0[um],d_ref_mid165-dr_indent119)");
   m.component(c).variable(pv).set("hres166","himplied166-h_fine166");
   m.component(c).physics("solid").feature("load_partitioned_pfilm").set("FperArea",
    new String[]{"-pfilm166*nx","-pfilm166*ny","-pfilm166*nz"});
   m.component(c).physics(ge).feature("ge1").set("equation",1,1,"Ferr166");
   String ss="std_solid166";try{m.study().remove(ss);}catch(Exception e){}
   m.study().create(ss);m.study(ss).label("Stage 166 fine fixed point");
   m.study(ss).create("param","Parametric");
   m.study(ss).feature("param").set("pname",new String[]{"h_fine166"});
   m.study(ss).feature("param").set("plistarr",new String[]{"range(1.00,0.01,1.10)"});
   m.study(ss).feature("param").set("punit",new String[]{"um"});
   m.study(ss).create("stat","Stationary");
   m.study(ss).feature("stat").set("geometricNonlinearity","on");
   m.study(ss).feature("stat").set("activate",new String[]{"solid","on","tff","off",ge,"on"});
   m.study(ss).feature("stat").set("useinitsol","on");
   m.study(ss).feature("stat").set("initmethod","sol");
   m.study(ss).feature("stat").set("initsol","sol33");
   m.study(ss).feature("stat").set("initsoluse","sol33");
   m.study(ss).feature("stat").set("initsolusesolnum",2);
   step=ss+"/stat";for(String f:new String[]{"dcnt1","disp_lid_time","load_partitioned_pfilm"})
    m.component(c).physics("solid").feature(f).set("StudyStep",step);
   m.component(c).physics(ge).feature("ge1").set("StudyStep",step);
   b=m.sol().tags();m.study(ss).createAutoSequences("sol");String ssol=newest(m,b);
   SolverFeature s1=m.sol(ssol).feature("s1");try{s1.feature().remove("se1");}catch(Exception e){}
   try{s1.feature().remove("fc1");}catch(Exception e){}s1.create("fc1","FullyCoupled");
   s1.feature("fc1").set("linsolver","dDef");s1.feature("fc1").set("maxiter",260);
   m.save("299_lid8mm_stage166_fine_fixedpoint_setup_Model.mph");
   System.out.println("RUN_SOLID166 "+ssol);m.sol(ssol).runAll();
   m.result().dataset().create("dset166","Solution");m.result().dataset("dset166").set("solution",ssol);
   m.result().numerical().create("eval166","EvalGlobal");m.result().numerical("eval166").set("data","dset166");
   m.result().numerical("eval166").set("expr",new String[]{"h_fine166","Wfilm166","Fn_contact119",
    "Ftotal166","dr_indent119","himplied166","hres166"});
   double[][]x=m.result().numerical("eval166").getReal();
   for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
    "h=%.9g W=%.9g Fc=%.9g Ft=%.9g d=%.9g himp=%.9g res=%.9g%n",
    x[0][j],x[1][j],x[2][j],x[3][j],x[4][j],x[5][j],x[6][j]);
   m.save("300_lid8mm_stage166_fine_fixedpoint_results_Model.mph");ModelUtil.disconnect();
  }catch(Exception e){e.printStackTrace();System.exit(1);}
 }
}
