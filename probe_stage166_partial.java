import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class probe_stage166_partial{public static void main(String[]a)throws Exception{
ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model","stage166_midpoint_fixedpoint_fine_output_Model.mph");
m.result().dataset().create("dp166","Solution");m.result().dataset("dp166").set("solution","sol35");
m.result().numerical().create("ep166","EvalGlobal");m.result().numerical("ep166").set("data","dp166");
m.result().numerical("ep166").set("expr",new String[]{"h_fine166","Wfilm166","Fn_contact119","Ftotal166","dr_indent119","himplied166","hres166"});
double[][]x=m.result().numerical("ep166").getReal();for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
"h=%.9g W=%.9g Fc=%.9g Ft=%.9g d=%.9g himp=%.9g res=%.9g%n",x[0][j],x[1][j],x[2][j],x[3][j],x[4][j],x[5][j],x[6][j]);
ModelUtil.disconnect();}}
