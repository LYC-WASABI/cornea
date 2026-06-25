import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class probe_stage173_results{public static void main(String[]a)throws Exception{ModelUtil.initStandalone(false);
Model m=ModelUtil.load("Model","stage173_gap_underrelaxation_scan_output_Model.mph");m.result().dataset().create("dx173","Solution");
m.result().dataset("dx173").set("solution","sol37");m.result().numerical().create("ex173","EvalGlobal");
m.result().numerical("ex173").set("data","dx173");m.result().numerical("ex173").set("expr",new String[]{"alpha_gap173",
"intop_film(max(pfilm,0))","intop_film(tau_film_wall)","intop_film(h_relaxed173)/intop_film(1)"});
double[][]x=m.result().numerical("ex173").getReal();for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
"alpha=%.6g W=%.9g Fshear=%.9g havg=%.9g%n",x[0][j],x[1][j],x[2][j],x[3][j]);
m.save("314_lid8mm_stage173_gap_relax_scan_results_Model.mph");ModelUtil.disconnect();}}
