import com.comsol.model.*;import com.comsol.model.util.*;import java.util.*;
public class probe_stage171_results{public static void main(String[]a)throws Exception{
ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model","stage171_film_pressure_sign_test_output_Model.mph");
m.result().dataset().create("dx171","Solution");m.result().dataset("dx171").set("solution","sol35");
m.result().numerical().create("ex171","EvalGlobal");m.result().numerical("ex171").set("data","dx171");
m.result().numerical("ex171").set("expr",new String[]{"pressure_sign171","Wfilm170","Fn_contact119"});
double[][]x=m.result().numerical("ex171").getReal();for(int j=0;j<x[0].length;j++)System.out.printf(Locale.US,
"sign=%.3g W=%.9g Fc=%.9g total=%.9g%n",x[0][j],x[1][j],x[2][j],x[1][j]+x[2][j]);
m.save("310_lid8mm_stage171_pressure_sign_test_results_Model.mph");ModelUtil.disconnect();}}
