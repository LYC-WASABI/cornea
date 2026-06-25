import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;
public class probe_stage165_partial{
 public static void main(String[]a)throws Exception{
  ModelUtil.initStandalone(false);
  Model m=ModelUtil.load("Model","stage165_partitioned_fixedpoint_scan_output_Model.mph");
  m.result().dataset().create("dp165","Solution");
  m.result().dataset("dp165").set("solution","sol33");
  m.result().numerical().create("ep165","EvalGlobal");
  m.result().numerical("ep165").set("data","dp165");
  m.result().numerical("ep165").set("expr",new String[]{
   "h_iter165","Wfilm165","Fn_contact119","Ftotal165",
   "dr_indent119","h_implied165","h_fixedpoint_res165"});
  double[][]x=m.result().numerical("ep165").getReal();
  for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
   "h=%.9g W=%.9g Fc=%.9g Ft=%.9g d=%.9g himp=%.9g res=%.9g%n",
   x[0][j],x[1][j],x[2][j],x[3][j],x[4][j],x[5][j],x[6][j]);
  ModelUtil.disconnect();
 }
}
