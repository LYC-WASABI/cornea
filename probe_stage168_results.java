import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class probe_stage168_results{public static void main(String[]a)throws Exception{
ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model","stage168_extended_separation_balance_scan_output_Model.mph");
try{m.result().dataset().remove("dx168");}catch(Exception e){}m.result().dataset().create("dx168","Solution");m.result().dataset("dx168").set("solution","sol35");
m.result().numerical().create("ex168","EvalGlobal");m.result().numerical("ex168").set("data","dx168");
m.result().numerical("ex168").set("expr",new String[]{"h_ext168","dr_indent119","Fn_contact119"});
double[][]x=m.result().numerical("ex168").getReal();for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
"h=%.9g d=%.9g Fc=%.9g%n",x[0][j],x[1][j],x[2][j]);m.save("304_lid8mm_stage168_extended_balance_results_Model.mph");ModelUtil.disconnect();}}
