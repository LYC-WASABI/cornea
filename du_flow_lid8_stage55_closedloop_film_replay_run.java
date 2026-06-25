import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage55_closedloop_film_replay_run {
  private static void global(Model m,String tag,String expr,String unit){
    m.result().numerical().create(tag,"EvalGlobal");m.result().numerical(tag).set("data","dset_closedloop_film55");m.result().numerical(tag).set("expr",new String[]{expr});m.result().numerical(tag).set("unit",new String[]{unit});m.result().numerical(tag).setResult();
    double min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY;for(double v:m.result().numerical(tag).getReal()[0])if(Double.isFinite(v)){min=Math.min(min,v);max=Math.max(max,v);}System.out.printf(Locale.US,"%s min=%.12g[%s] max=%.12g[%s]%n",tag,min,unit,max,unit);
  }
  private static void plot3(Model m,String tag,String label,String expr,String unit){
    try{m.result().remove(tag);}catch(Exception ignored){}m.result().create(tag,"PlotGroup3D");m.result(tag).label(label);m.result(tag).set("data","dset_closedloop_film55");m.result(tag).create("surf1","Surface");m.result(tag).feature("surf1").set("expr",expr);m.result(tag).feature("surf1").set("unit",unit);
  }
  public static void main(String[] a)throws Exception{
    ModelUtil.initStandalone(true);Model m=ModelUtil.load("Model","D:\\COMSOL_Outputs\\models\\du\\flow\\136_lid8mm_stage54_structure_loadshare_feedback_results.mph");m.label("137_lid8mm_stage55_closedloop_gap_film_replay_results.mph");
    System.out.println("RUN_STAGE55_STUDY=std_tff_gap_qs45");m.study("std_tff_gap_qs45").run();
    m.result().dataset().create("dset_closedloop_film55","Solution");m.result().dataset("dset_closedloop_film55").set("solution","sol21");
    m.component("comp1").variable("var_mixed_lub").set("W_contact_budget55","max(F_total_target-W_film,0)");
    m.component("comp1").variable("var_mixed_lub").set("W_total_partitioned55","W_film+W_contact_budget55");
    m.component("comp1").variable("var_mixed_lub").set("F_friction_partitioned55","F_film_shear+0.02*W_contact_budget55");
    m.component("comp1").variable("var_mixed_lub").set("mu_app_partitioned55","F_friction_partitioned55/F_total_target");
    global(m,"eval55_Wfilm","W_film","N");global(m,"eval55_Wcontact_budget","max(F_total_target-W_film,0)","N");global(m,"eval55_Wtotal","W_film+max(F_total_target-W_film,0)","N");global(m,"eval55_Ffilm_shear","F_film_shear","N");global(m,"eval55_Ffriction","F_film_shear+0.02*max(F_total_target-W_film,0)","N");global(m,"eval55_mu","(F_film_shear+0.02*max(F_total_target-W_film,0))/F_total_target","1");
    plot3(m,"pg55_hfilm","Stage 55 closed-loop tear-film thickness","h_film_input","um");plot3(m,"pg55_gap","Stage 55 closed-loop dynamic positive pair gap","gap_replay_tear","um");plot3(m,"pg55_pfilm","Stage 55 closed-loop tear-film pressure","max(pfilm,0)","Pa");
    m.result().create("pg55_loadshare","PlotGroup1D");m.result("pg55_loadshare").label("Stage 55 closed-loop 0.03 N load sharing");m.result("pg55_loadshare").set("data","dset_closedloop_film55");m.result("pg55_loadshare").create("glob1","Global");m.result("pg55_loadshare").feature("glob1").set("expr",new String[]{"W_film","max(F_total_target-W_film,0)","W_film+max(F_total_target-W_film,0)"});
    m.result().create("pg55_friction","PlotGroup1D");m.result("pg55_friction").label("Stage 55 closed-loop friction force and coefficient");m.result("pg55_friction").set("data","dset_closedloop_film55");m.result("pg55_friction").create("glob1","Global");m.result("pg55_friction").feature("glob1").set("expr",new String[]{"F_film_shear","F_film_shear+0.02*max(F_total_target-W_film,0)","(F_film_shear+0.02*max(F_total_target-W_film,0))/F_total_target"});
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\137_lid8mm_stage55_closedloop_gap_film_replay_results.mph");System.out.println("SAVED_STAGE55=137_lid8mm_stage55_closedloop_gap_film_replay_results.mph");
  }
}
