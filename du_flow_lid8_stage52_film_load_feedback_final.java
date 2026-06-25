import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage52_film_load_feedback_final {
  private static String last(Model m){String[] t=m.sol().tags();return t[t.length-1];}
  private static void global(Model m,String tag,String expr,String unit){
    m.result().numerical().create(tag,"EvalGlobal");m.result().numerical(tag).set("data","dset_tff_gap_feedback52");
    m.result().numerical(tag).set("expr",new String[]{expr});m.result().numerical(tag).set("unit",new String[]{unit});m.result().numerical(tag).setResult();
    double min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY;for(double v:m.result().numerical(tag).getReal()[0])if(Double.isFinite(v)){min=Math.min(min,v);max=Math.max(max,v);}
    System.out.printf(Locale.US,"%s min=%.12g[%s] max=%.12g[%s]%n",tag,min,unit,max,unit);
  }
  private static void surfacePlot(Model m,String tag,String label,String expr,String unit){
    m.result().create(tag,"PlotGroup3D");m.result(tag).label(label);m.result(tag).set("data","dset_tff_gap_feedback52");
    m.result(tag).create("surf1","Surface");m.result(tag).feature("surf1").set("expr",expr);m.result(tag).feature("surf1").set("unit",unit);
  }
  public static void main(String[] args)throws Exception{
    ModelUtil.initStandalone(true);
    Model r=ModelUtil.load("Result","D:\\COMSOL_Outputs\\models\\du\\flow\\132_lid8mm_stage51_h3um_partitioned_loadshare_results.mph");
    double[][] film=r.result().numerical("eval51_Wfilm").getReal(),old=r.result().numerical("eval51_hsep").getReal();List<String[]> rows=new ArrayList<>();
    for(int i=0;i<film[0].length;i++){double ratio=Math.max(1,film[0][i]/0.03);double sep=Math.max(0,Math.min(25,old[0][i]+10*(Math.sqrt(ratio)-1)));
      rows.add(new String[]{String.format(Locale.US,"%.12g",.01*i),String.format(Locale.US,"%.12g",sep)});}
    ModelUtil.remove("Result");
    Model m=ModelUtil.load("Model","D:\\COMSOL_Outputs\\models\\du\\flow\\122_lid8mm_stage46_h3um_gap_rectangular_footprint_setup.mph");
    m.label("133_lid8mm_stage52_h3um_partitioned_loadshare_final_results.mph");
    m.func().create("hsep52","Interpolation");m.func("hsep52").set("funcname","h_sep_feedback52");m.func("hsep52").set("table",rows.toArray(new String[0][0]));
    m.func("hsep52").set("argunit",new String[]{"s"});m.func("hsep52").set("fununit","um");m.func("hsep52").set("interp","piecewisecubic");m.func("hsep52").set("extrap","const");
    m.component("comp1").variable("var_mixed_lub").set("h_feedback_sep52","h_sep_feedback52(t_replay)");
    m.component("comp1").variable("var_mixed_lub").set("h_inside_lid","max(h_min_tear,h0_tear+gap_replay_tear+Rq_eq+h_feedback_sep52)");
    m.component("comp1").variable("var_mixed_lub").set("h_film_input","h_inside_lid+(1-lid_mask)*(h_outside_track-h_inside_lid)");
    m.component("comp1").variable("var_mixed_lub").set("W_contact_budget52","max(F_total_target-W_film,0)");
    m.component("comp1").variable("var_mixed_lub").set("W_total_partitioned52","W_film+W_contact_budget52");
    m.component("comp1").variable("var_mixed_lub").set("F_friction_partitioned52","F_film_shear+0.02*W_contact_budget52");
    m.component("comp1").variable("var_mixed_lub").set("mu_app_partitioned52","F_friction_partitioned52/F_total_target");
    m.study("std_tff_gap_qs45").createAutoSequences("sol");String s=last(m);m.sol(s).runAll();
    m.result().dataset().create("dset_tff_gap_feedback52","Solution");m.result().dataset("dset_tff_gap_feedback52").set("solution",s);
    global(m,"eval52_Wfilm","W_film","N");global(m,"eval52_Wcontact_budget","W_contact_budget52","N");global(m,"eval52_Wtotal","W_total_partitioned52","N");
    global(m,"eval52_Ffilm_shear","F_film_shear","N");global(m,"eval52_Ffriction","F_friction_partitioned52","N");global(m,"eval52_mu","mu_app_partitioned52","1");global(m,"eval52_hsep","h_feedback_sep52","um");
    surfacePlot(m,"pg52_hfilm","Stage 52 tear-film thickness with partitioned feedback","h_film_input","um");
    surfacePlot(m,"pg52_gap","Stage 52 dynamic positive pair gap","gap_replay_tear","um");
    surfacePlot(m,"pg52_pfilm","Stage 52 tear-film pressure","max(pfilm,0)","Pa");
    m.result().create("pg52_loadshare","PlotGroup1D");m.result("pg52_loadshare").label("Stage 52 total 0.03 N partitioned load sharing");m.result("pg52_loadshare").set("data","dset_tff_gap_feedback52");m.result("pg52_loadshare").create("glob1","Global");m.result("pg52_loadshare").feature("glob1").set("expr",new String[]{"W_film","W_contact_budget52","W_total_partitioned52"});
    m.result().create("pg52_friction","PlotGroup1D");m.result("pg52_friction").label("Stage 52 friction force and apparent coefficient");m.result("pg52_friction").set("data","dset_tff_gap_feedback52");m.result("pg52_friction").create("glob1","Global");m.result("pg52_friction").feature("glob1").set("expr",new String[]{"F_film_shear","F_friction_partitioned52","mu_app_partitioned52"});
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\133_lid8mm_stage52_h3um_partitioned_loadshare_final_results.mph");
    System.out.println("SAVED_STAGE52_FINAL=133_lid8mm_stage52_h3um_partitioned_loadshare_final_results.mph");
  }
}
