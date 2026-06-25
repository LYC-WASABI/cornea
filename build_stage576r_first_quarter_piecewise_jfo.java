import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class build_stage576r_first_quarter_piecewise_jfo {
  private static final String INPUT =
      "576p2r_stage576_moving_structure_sparse_jfo_results.mph";
  private static final String CHECKPOINT =
      "576r_stage576_first_quarter_piecewise_jfo_checkpoint.mph";
  private static final String RESULTS =
      "576r_stage576_first_quarter_piecewise_jfo_results.mph";
  private static final String SWEPT = "sel_film_swept571";

  private static boolean has(String[] values,String value){
    for(String candidate:values)if(candidate.equals(value))return true;
    return false;
  }

  private static String newest(Model model,String[] before){
    Set<String> old=new HashSet<String>(Arrays.asList(before));
    String latest=null;
    for(String tag:model.sol().tags())if(!old.contains(tag))latest=tag;
    if(latest==null)throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static void configure(Model model){
    ModelNode comp=model.component("comp1");
    int[] edges=comp.physics("tff").feature("bdr1").selection().entities();
    comp.physics("tff").feature("bdr_inlet520").active(true);
    comp.physics("tff").feature("bdr_inlet520").selection().set(edges);
    comp.physics("tff").feature("bdr_outlet520").active(false);
    comp.physics("tff").feature("bdr_left520").active(false);
    comp.physics("tff").feature("bdr_right520").active(false);
    comp.physics("tff").feature("wc_open_anchor573").active(false);
    PhysicsFeature ffp=comp.physics("tff").feature("ffp1");
    ffp.set("hw1","h_calc573");
    ffp.set("vw",new String[]{
      "0",
      "-lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Z",
      "lambda_v574*M_drain573*Bfilm573*omega_lid_rot572*Y"
    });
  }

  private static String buildSegment(Model model,int index,String pressure,
      String solid){
    String study="std576r_segment_"+index;
    model.study().create(study);
    model.study(study).label("Stage 576r piecewise JFO segment "+index);
    model.study(study).create("time","Transient");
    String tlist="range(t0_576r,dt_576r/2,t1_576r)";
    model.study(study).feature("time").set("tlist",tlist);
    model.study(study).feature("time").set("activate",new String[]{
      "solid","off","ge_force_total111","off","tff","on",
      "frame:spatial1","on","frame:material1","on","comp1","on"
    });
    model.study(study).feature("time").set("useinitsol","on");
    model.study(study).feature("time").set("initmethod","sol");
    model.study(study).feature("time").set("initsol",pressure);
    model.study(study).feature("time").set("initsoluse","current");
    model.study(study).feature("time").set("initsolusesolnum","last");
    String step=study+"/time";
    ModelNode comp=model.component("comp1");
    for(String feature:comp.physics("tff").feature().tags()){
      try{comp.physics("tff").feature(feature).set("StudyStep",step);}
      catch(Exception ignored){}
    }
    String[] before=model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution=newest(model,before);
    SolverFeature dependent=model.sol(solution).feature("v1");
    dependent.set("initmethod","sol");
    dependent.set("initsol",pressure);
    dependent.set("solnum","last");
    dependent.set("notsolmethod","sol");
    dependent.set("notsol",solid);
    dependent.set("notsolnum","last");
    SolverFeature time=model.sol(solution).feature("t1");
    time.set("tlist",tlist);
    try{time.set("maxorder",2);}catch(Exception ignored){}
    try{time.set("initialstepbdf","dt_576r/200");}catch(Exception ignored){}
    try{time.set("maxstepconstraintbdf","const");}catch(Exception ignored){}
    try{time.set("maxstepbdf","dt_576r/20");}catch(Exception ignored){}
    if(!has(time.feature().tags(),"fc1"))time.create("fc1","FullyCoupled");
    time.feature("fc1").set("linsolver","dDef");
    time.feature("fc1").set("damp","0.5");
    time.feature("fc1").set("maxiter",120);
    return solution;
  }

  private static double last(Model model,String data,String tag,String type,
      String expression){
    try{model.result().numerical().remove(tag);}catch(Exception ignored){}
    model.result().numerical().create(tag,type);
    model.result().numerical(tag).set("data",data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr",expression);
    double[] values=model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }

  private static void evaluate(Model model,String solution,double fraction){
    String data="dset576r";
    try{model.result().dataset().remove(data);}catch(Exception ignored){}
    model.result().dataset().create(data,"Solution");
    model.result().dataset(data).set("solution",solution);
    double film=last(model,data,"int576rFilm","IntSurface",
        "max(p_load573,0[Pa])");
    double positive=last(model,data,"int576rPositive","IntSurface",
        "max(tff.p-p_amb573,0[Pa])");
    double yload=last(model,data,"int576rY","IntSurface",
        "max(tff.p-p_amb573,0[Pa])*Y");
    double maxP=last(model,data,"max576rP","MaxSurface",
        "tff.p-p_amb573");
    double minTheta=last(model,data,"min576rTheta","MinSurface","tff.theta");
    System.out.printf(Locale.US,
        "PIECEWISE_RESULT fraction=%.3f Ffilm=%.12g MaxP=%.12g"
            + " MinTheta=%.12g pressureY=%.12g solution=%s%n",
        fraction,film,maxP,minTheta,
        Math.abs(positive)>1e-30?yload/positive:Double.NaN,solution);
  }

  public static void main(String[] args){
    try{
      ModelUtil.initStandalone(false);
      Model model=ModelUtil.load("Model",INPUT);
      configure(model);
      double[] fractions=new double[]{0.05,0.10,0.15,0.20,0.25};
      String[] solids=new String[]{"sol145","sol146","sol147","sol148","sol149"};
      String pressure="sol142";
      double previous=0.0;
      for(int i=0;i<fractions.length;i++){
        double fraction=fractions[i];
        model.param().set("t_position576p2",String.format(Locale.US,
            "T_pre572+%.12g*T_slide572",fraction));
        model.param().set("t0_576r",String.format(Locale.US,
            "T_pre572+%.12g*T_slide572",previous));
        model.param().set("t1_576r",String.format(Locale.US,
            "T_pre572+%.12g*T_slide572",fraction));
        model.param().set("dt_576r",String.format(Locale.US,
            "%.12g*T_slide572",fraction-previous));
        String solution=buildSegment(model,i,pressure,solids[i]);
        System.out.println("PIECEWISE_START index="+i+" fraction="+fraction
            +" pressureInit="+pressure+" solid="+solids[i]
            +" solution="+solution);
        model.sol(solution).runAll();
        pressure=solution;
        previous=fraction;
        evaluate(model,solution,fraction);
        model.save(CHECKPOINT);
      }
      model.label("Stage 576r first-quarter piecewise transient JFO results");
      model.save(RESULTS);
      System.out.println("PIECEWISE_STATUS=COMPLETE");
      ModelUtil.disconnect();
    }catch(Exception error){
      error.printStackTrace();
      try{ModelUtil.disconnect();}catch(Exception ignored){}
      System.exit(1);
    }
  }
}
