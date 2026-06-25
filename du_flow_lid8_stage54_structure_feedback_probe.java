import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage54_structure_feedback_probe {
  public static void main(String[] args)throws Exception{
    ModelUtil.initStandalone(true);
    Model m=ModelUtil.load("Model","D:\\COMSOL_Outputs\\models\\du\\flow\\111_lid8mm_stage40_final_dynamic_lubrication_postprocessing_results.mph");
    System.out.println("VARIABLES="+Arrays.toString(m.component("comp1").variable().tags()));
    for(String v:m.component("comp1").variable().tags()){
      System.out.println("VAR "+v+" names="+Arrays.toString(m.component("comp1").variable(v).varnames()));
    }
    System.out.println("SOLID_FEATURES="+Arrays.toString(m.component("comp1").physics("solid").feature().tags()));
    for(String f:m.component("comp1").physics("solid").feature().tags()){
      try{System.out.println("FEATURE "+f+" U0="+Arrays.toString(m.component("comp1").physics("solid").feature(f).getStringArray("U0")));}catch(Exception ignored){}
    }
    System.out.println("STUDIES="+Arrays.toString(m.study().tags()));
    System.out.println("DATASETS="+Arrays.toString(m.result().dataset().tags()));
    for(String n:new String[]{"slide_fraction_structure","phi_lid_structure","t_film_replay","pfilm_replay","W_film_replay","dr_force_reaction39"}){
      System.out.println(n+"="+m.component("comp1").variable("var_partitioned_local_pfilm").get(n));
    }
    try{System.out.println("LOAD_FperArea="+Arrays.toString(m.component("comp1").physics("solid").feature("load_partitioned_pfilm").getStringArray("FperArea")));}catch(Exception e){System.out.println("LOAD_PROBE="+e.getMessage());}
  }
}
