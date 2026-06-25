import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage50_film_load_feedback_iteration3_run {
  private static String last(Model m) { String[] t=m.sol().tags(); return t[t.length-1]; }
  private static void global(Model m,String tag,String expr,String unit) {
    m.result().numerical().create(tag,"EvalGlobal");
    m.result().numerical(tag).set("data","dset_tff_gap_feedback50");
    m.result().numerical(tag).set("expr",new String[]{expr});
    m.result().numerical(tag).set("unit",new String[]{unit});
    m.result().numerical(tag).setResult();
    double min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY;
    for(double v:m.result().numerical(tag).getReal()[0]) if(Double.isFinite(v)){min=Math.min(min,v);max=Math.max(max,v);}
    System.out.printf(java.util.Locale.US,"%s min=%.12g[%s] max=%.12g[%s]%n",tag,min,unit,max,unit);
  }
  public static void main(String[] args)throws Exception{
    ModelUtil.initStandalone(true);
    Model m=ModelUtil.load("Model","D:\\COMSOL_Outputs\\models\\du\\flow\\129_lid8mm_stage50_h3um_film_load_feedback_iteration3_setup.mph");
    m.label("130_lid8mm_stage50_h3um_film_load_feedback_iteration3_results.mph");
    m.study("std_tff_gap_qs45").createAutoSequences("sol"); String s=last(m); m.sol(s).runAll();
    m.result().dataset().create("dset_tff_gap_feedback50","Solution");m.result().dataset("dset_tff_gap_feedback50").set("solution",s);
    global(m,"eval50_Wfilm","W_film","N");
    global(m,"eval50_Ffilm_shear","F_film_shear","N");
    global(m,"eval50_mu_film","F_film_shear/F_total_target","1");
    global(m,"eval50_hsep","h_feedback_sep50","um");
    global(m,"eval50_Wcontact_budget","max(F_total_target-W_film,0)","N");
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\130_lid8mm_stage50_h3um_film_load_feedback_iteration3_results.mph");
    System.out.println("SAVED_STAGE50_RESULTS=130_lid8mm_stage50_h3um_film_load_feedback_iteration3_results.mph");
  }
}
