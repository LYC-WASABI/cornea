import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage53_film_load_feedback_converged {
  private static String last(Model m){String[] t=m.sol().tags();return t[t.length-1];}
  private static void global(Model m,String tag,String expr,String unit){
    m.result().numerical().create(tag,"EvalGlobal");m.result().numerical(tag).set("data","dset_tff_gap_feedback53");
    m.result().numerical(tag).set("expr",new String[]{expr});m.result().numerical(tag).set("unit",new String[]{unit});m.result().numerical(tag).setResult();
    double min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY;for(double v:m.result().numerical(tag).getReal()[0])if(Double.isFinite(v)){min=Math.min(min,v);max=Math.max(max,v);}
    System.out.printf(Locale.US,"%s min=%.12g[%s] max=%.12g[%s]%n",tag,min,unit,max,unit);
  }
  private static void plot3(Model m,String tag,String label,String expr,String unit){
    m.result().create(tag,"PlotGroup3D");m.result(tag).label(label);m.result(tag).set("data","dset_tff_gap_feedback53");
    m.result(tag).create("surf1","Surface");m.result(tag).feature("surf1").set("expr",expr);m.result(tag).feature("surf1").set("unit",unit);
  }
  public static void main(String[] a)throws Exception{
    ModelUtil.initStandalone(true);
    Model r=ModelUtil.load("Result","D:\\COMSOL_Outputs\\models\\du\\flow\\133_lid8mm_stage52_h3um_partitioned_loadshare_final_results.mph");
    double[][] f=r.result().numerical("eval52_Wfilm").getReal(),o=r.result().numerical("eval52_hsep").getReal();List<String[]> rows=new ArrayList<>();
    for(int i=0;i<f[0].length;i++){double sep=Math.max(0,Math.min(25,o[0][i]+20*(Math.sqrt(Math.max(1,f[0][i]/.03))-1)));rows.add(new String[]{String.format(Locale.US,"%.12g",.01*i),String.format(Locale.US,"%.12g",sep)});}
    ModelUtil.remove("Result");
    Model m=ModelUtil.load("Model","D:\\COMSOL_Outputs\\models\\du\\flow\\122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph");m.label("134_lid8mm_stage53_h3um_partitioned_loadshare_converged_results.mph");
    m.func().create("hsep53","Interpolation");m.func("hsep53").set("funcname","h_sep_feedback53");m.func("hsep53").set("table",rows.toArray(new String[0][0]));m.func("hsep53").set("argunit",new String[]{"s"});m.func("hsep53").set("fununit","um");m.func("hsep53").set("interp","piecewisecubic");m.func("hsep53").set("extrap","const");
    m.component("comp1").variable("var_mixed_lub").set("h_feedback_sep53","h_sep_feedback53(t_replay)");
    m.component("comp1").variable("var_mixed_lub").set("h_inside_lid","max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq+h_feedback_sep53)");
    m.component("comp1").variable("var_mixed_lub").set("h_film_input","h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");
    m.component("comp1").variable("var_mixed_lub").set("W_contact_budget53","max(F_total_target-W_film,0)");
    m.component("comp1").variable("var_mixed_lub").set("W_total_partitioned53","W_film+W_contact_budget53");
    m.component("comp1").variable("var_mixed_lub").set("F_friction_partitioned53","F_film_shear+0.02*W_contact_budget53");
    m.component("comp1").variable("var_mixed_lub").set("mu_app_partitioned53","F_friction_partitioned53/F_total_target");
    m.study("std_tff_gap_qs45").createAutoSequences("sol");String s=last(m);m.sol(s).runAll();m.result().dataset().create("dset_tff_gap_feedback53","Solution");m.result().dataset("dset_tff_gap_feedback53").set("solution",s);
    global(m,"eval53_Wfilm","W_film","N");global(m,"eval53_Wcontact_budget","W_contact_budget53","N");global(m,"eval53_Wtotal","W_total_partitioned53","N");global(m,"eval53_Ffilm_shear","F_film_shear","N");global(m,"eval53_Ffriction","F_friction_partitioned53","N");global(m,"eval53_mu","mu_app_partitioned53","1");global(m,"eval53_hsep","h_feedback_sep53","um");
    plot3(m,"pg53_hfilm","Stage 53 converged partitioned tear-film thickness","h_film_input","um");plot3(m,"pg53_gap","Stage 53 dynamic positive pair gap","gap_replay_tear","um");plot3(m,"pg53_pfilm","Stage 53 converged tear-film pressure","max(pfilm,0)","Pa");
    m.result().create("pg53_loadshare","PlotGroup1D");m.result("pg53_loadshare").label("Stage 53 converged 0.03 N partitioned load sharing");m.result("pg53_loadshare").set("data","dset_tff_gap_feedback53");m.result("pg53_loadshare").create("glob1","Global");m.result("pg53_loadshare").feature("glob1").set("expr",new String[]{"W_film","W_contact_budget53","W_total_partitioned53"});
    m.result().create("pg53_friction","PlotGroup1D");m.result("pg53_friction").label("Stage 53 friction force and apparent coefficient");m.result("pg53_friction").set("data","dset_tff_gap_feedback53");m.result("pg53_friction").create("glob1","Global");m.result("pg53_friction").feature("glob1").set("expr",new String[]{"F_film_shear","F_friction_partitioned53","mu_app_partitioned53"});
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\134_lid8mm_stage53_h3um_partitioned_loadshare_converged_results.mph");System.out.println("SAVED_STAGE53=134_lid8mm_stage53_h3um_partitioned_loadshare_converged_results.mph");
  }
}
