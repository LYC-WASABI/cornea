import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage51_film_load_feedback_iteration4 {
  private static void global(Model m,String tag,String expr,String unit) {
    m.result().numerical().create(tag,"EvalGlobal");
    m.result().numerical(tag).set("data","dset_tff_gap_feedback51");
    m.result().numerical(tag).set("expr",new String[]{expr});
    m.result().numerical(tag).set("unit",new String[]{unit});
    m.result().numerical(tag).setResult();
    double min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY;
    for(double v:m.result().numerical(tag).getReal()[0]) if(Double.isFinite(v)){min=Math.min(min,v);max=Math.max(max,v);}
    System.out.printf(Locale.US,"%s min=%.12g[%s] max=%.12g[%s]%n",tag,min,unit,max,unit);
  }
  private static String last(Model m){String[] t=m.sol().tags();return t[t.length-1];}
  public static void main(String[] args)throws Exception{
    ModelUtil.initStandalone(true);
    Model result=ModelUtil.load("Result","D:\\COMSOL_Outputs\\models\\du\\flow\\130_lid8mm_stage50_h3um_film_load_feedback_iteration3_results.mph");
    double[][] film=result.result().numerical("eval50_Wfilm").getReal();
    double[][] oldSep=result.result().numerical("eval50_hsep").getReal();
    List<String[]> rows=new ArrayList<>();
    for(int i=0;i<film[0].length;i++){
      double ratio=Math.max(1.0,film[0][i]/0.03);
      double sep=Math.max(0,Math.min(25,oldSep[0][i]+20.0*(Math.sqrt(ratio)-1)));
      rows.add(new String[]{String.format(Locale.US,"%.12g",0.01*i),String.format(Locale.US,"%.12g",sep)});
    }
    ModelUtil.remove("Result");
    Model m=ModelUtil.load("Model","D:\\COMSOL_Outputs\\models\\du\\flow\\122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph");
    m.label("132_lid8mm_stage51_h3um_partitioned_loadshare_results.mph");
    m.func().create("hsep51","Interpolation");
    m.func("hsep51").set("funcname","h_sep_feedback51");
    m.func("hsep51").set("table",rows.toArray(new String[0][0]));
    m.func("hsep51").set("argunit",new String[]{"s"});m.func("hsep51").set("fununit","um");
    m.func("hsep51").set("interp","piecewisecubic");m.func("hsep51").set("extrap","const");
    m.component("comp1").variable("var_mixed_lub").set("h_feedback_sep51","h_sep_feedback51(t_replay)");
    m.component("comp1").variable("var_mixed_lub").set("h_inside_lid","max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq+h_feedback_sep51)");
    m.component("comp1").variable("var_mixed_lub").set("h_film_input","h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");
    m.component("comp1").variable("var_mixed_lub").set("W_contact_budget51","max(F_total_target-W_film,0)");
    m.component("comp1").variable("var_mixed_lub").set("W_total_partitioned51","W_film+W_contact_budget51");
    m.component("comp1").variable("var_mixed_lub").set("F_friction_partitioned51","F_film_shear+0.02*W_contact_budget51");
    m.component("comp1").variable("var_mixed_lub").set("mu_app_partitioned51","F_friction_partitioned51/F_total_target");
    m.study("std_tff_gap_qs45").createAutoSequences("sol");String s=last(m);m.sol(s).runAll();
    m.result().dataset().create("dset_tff_gap_feedback51","Solution");m.result().dataset("dset_tff_gap_feedback51").set("solution",s);
    global(m,"eval51_Wfilm","W_film","N");
    global(m,"eval51_Wcontact_budget","W_contact_budget51","N");
    global(m,"eval51_Wtotal","W_total_partitioned51","N");
    global(m,"eval51_Ffilm_shear","F_film_shear","N");
    global(m,"eval51_Ffriction","F_friction_partitioned51","N");
    global(m,"eval51_mu","mu_app_partitioned51","1");
    global(m,"eval51_hsep","h_feedback_sep51","um");
    m.result().create("pg51_loadshare","PlotGroup1D");m.result("pg51_loadshare").label("Stage 51 partitioned normal-load sharing");
    m.result("pg51_loadshare").set("data","dset_tff_gap_feedback51");m.result("pg51_loadshare").create("glob1","Global");
    m.result("pg51_loadshare").feature("glob1").set("expr",new String[]{"W_film","W_contact_budget51","W_total_partitioned51"});
    m.result().create("pg51_friction","PlotGroup1D");m.result("pg51_friction").label("Stage 51 partitioned friction force and apparent coefficient");
    m.result("pg51_friction").set("data","dset_tff_gap_feedback51");m.result("pg51_friction").create("glob1","Global");
    m.result("pg51_friction").feature("glob1").set("expr",new String[]{"F_film_shear","F_friction_partitioned51","mu_app_partitioned51"});
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\132_lid8mm_stage51_h3um_partitioned_loadshare_results.mph");
    System.out.println("SAVED_STAGE51_RESULTS=132_lid8mm_stage51_h3um_partitioned_loadshare_results.mph");
  }
}
