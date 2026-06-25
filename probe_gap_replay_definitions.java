import com.comsol.model.*;import com.comsol.model.util.*;
public class probe_gap_replay_definitions{public static void main(String[]a)throws Exception{
ModelUtil.initStandalone(false);Model m=ModelUtil.load("Model","290_lid8mm_stage161_bounded_contact_predicted_friction_results_Model.mph");
for(String vt:m.component("comp1").variable().tags())for(String n:new String[]{"gap_smooth_replay_tear",
"gap_replay_tear","gap_raw_replay_tear","gap_smooth_tear","h_inside_lid","h_geom_limit","h_available"})
try{String x=m.component("comp1").variable(vt).get(n);if(x!=null&&!x.isEmpty())System.out.println(vt+" "+n+"="+x);}catch(Exception e){}
ModelUtil.disconnect();}}
