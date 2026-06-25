import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class probe_stage170_results{public static void main(String[]a)throws Exception{
ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model","stage170_actual_gap_offset_load_scan_output_Model.mph");
try{m.result().dataset().remove("dx170");}catch(Exception e){}m.result().dataset().create("dx170","Solution");m.result().dataset("dx170").set("solution","sol34");
m.result().numerical().create("ex170","EvalGlobal");m.result().numerical("ex170").set("data","dx170");
m.result().numerical("ex170").set("expr",new String[]{"h_offset170","Wfilm170","Fn_contact119","Ftotal170"});
double[][]x=m.result().numerical("ex170").getReal();for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
"hoff=%.9g W=%.9g Fc=%.9g Ft=%.9g%n",x[0][j],x[1][j],x[2][j],x[3][j]);
m.save("308_lid8mm_stage170_actual_gap_offset_scan_results_Model.mph");ModelUtil.disconnect();}}
