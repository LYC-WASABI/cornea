import com.comsol.model.*;
import com.comsol.model.physics.*;
import com.comsol.model.util.*;
import java.util.*;

public class resume_stage576r_fine_015_to025 {
  private static final String INPUT=
      "576r_stage576_first_quarter_piecewise_jfo_checkpoint.mph";
  private static final String CHECKPOINT=
      "576rf_stage576_fine_015_to025_checkpoint.mph";
  private static final String RESULTS=
      "576rf_stage576_fine_015_to025_results.mph";
  private static final String SWEPT="sel_film_swept571";

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
  private static void fullyCoupled(Model model,String solution,double damping,
      int maxIterations){
    SolverFeature node=model.sol(solution).feature("s1");
    for(String feature:node.feature().tags())if(feature.startsWith("se")){
      try{node.feature().remove(feature);}catch(Exception ignored){}
    }
    if(!has(node.feature().tags(),"fc1"))node.create("fc1","FullyCoupled");
    node.feature("fc1").set("linsolver","dDef");
    node.feature("fc1").set("damp",damping);
    node.feature("fc1").set("maxiter",maxIterations);
  }

  private static String buildSolid(Model model,String previous,int index){
    String study="std576rf_solid_"+index;
    model.study().create(study);
    model.study(study).label("Stage 576rf fine moving contact "+index);
    model.study(study).create("stat","Stationary");
    model.study(study).feature("stat").set("geometricNonlinearity","on");
    model.study(study).feature("stat").set("activate",new String[]{
      "solid","on","ge_force_total111","off","tff","off",
      "frame:spatial1","on","frame:material1","on","comp1","on"
    });
    model.study(study).feature("stat").set("useinitsol","on");
    model.study(study).feature("stat").set("initmethod","sol");
    model.study(study).feature("stat").set("initsol",previous);
    model.study(study).feature("stat").set("initsoluse","current");
    String step=study+"/stat";
    ModelNode comp=model.component("comp1");
    for(String feature:new String[]{"dcnt1","disp_lid_time"}){
      try{comp.physics("solid").feature(feature).set("StudyStep",step);}
      catch(Exception ignored){}
    }
    String[] before=model.sol().tags();
    model.study(study).createAutoSequences("sol");
    String solution=newest(model,before);
    SolverFeature dependent=model.sol(solution).feature("v1");
    dependent.set("initmethod","sol");
    dependent.set("initsol",previous);
    dependent.set("solnum","last");
    dependent.set("notsolmethod","sol");
    dependent.set("notsol",previous);
    dependent.set("notsolnum","last");
    fullyCoupled(model,solution,0.03,1200);
    return solution;
  }

  private static String buildJfo(Model model,String pressure,String solid,
      int index){
    String study="std576rf_jfo_"+index;
    model.study().create(study);
    model.study(study).label("Stage 576rf fine JFO "+index);
    model.study(study).create("time","Transient");
    String tlist="range(t0_576rf,dt_576rf/2,t1_576rf)";
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
    try{time.set("initialstepbdf","dt_576rf/500");}catch(Exception ignored){}
    try{time.set("maxstepconstraintbdf","const");}catch(Exception ignored){}
    try{time.set("maxstepbdf","dt_576rf/25");}catch(Exception ignored){}
    if(!has(time.feature().tags(),"fc1"))time.create("fc1","FullyCoupled");
    time.feature("fc1").set("linsolver","dDef");
    time.feature("fc1").set("damp","0.5");
    time.feature("fc1").set("maxiter",150);
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
  private static double contact(Model model,String solution){
    String data="dset576rfSolid";
    try{model.result().dataset().remove(data);}catch(Exception ignored){}
    model.result().dataset().create(data,"Solution");
    model.result().dataset(data).set("solution",solution);
    String tag="eval576rfContact";
    try{model.result().numerical().remove(tag);}catch(Exception ignored){}
    model.result().numerical().create(tag,"EvalGlobal");
    model.result().numerical(tag).set("data",data);
    model.result().numerical(tag).set("expr","Fn_contact570");
    double[] values=model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }
  private static void evaluate(Model model,String solid,String pressure,
      double fraction){
    String data="dset576rfPressure";
    try{model.result().dataset().remove(data);}catch(Exception ignored){}
    model.result().dataset().create(data,"Solution");
    model.result().dataset(data).set("solution",pressure);
    double film=last(model,data,"int576rfFilm","IntSurface",
        "max(p_load573,0[Pa])");
    double maxP=last(model,data,"max576rfP","MaxSurface",
        "tff.p-p_amb573");
    double minTheta=last(model,data,"min576rfTheta","MinSurface","tff.theta");
    System.out.printf(Locale.US,
        "FINE_RESULT fraction=%.4f Fcontact=%.12g Ffilm=%.12g MaxP=%.12g"
            +" MinTheta=%.12g solid=%s pressure=%s%n",
        fraction,contact(model,solid),film,maxP,minTheta,solid,pressure);
  }

  public static void main(String[] args){
    try{
      ModelUtil.initStandalone(false);
      Model model=ModelUtil.load("Model",INPUT);
      String previousSolid="sol147";
      String previousPressure="sol186";
      double previousFraction=0.15;
      for(int index=0;index<8;index++){
        double fraction=0.1625+0.0125*index;
        model.param().set("t_position576p2",String.format(Locale.US,
            "T_pre572+%.12g*T_slide572",fraction));
        model.param().set("t0_576rf",String.format(Locale.US,
            "T_pre572+%.12g*T_slide572",previousFraction));
        model.param().set("t1_576rf",String.format(Locale.US,
            "T_pre572+%.12g*T_slide572",fraction));
        model.param().set("dt_576rf",String.format(Locale.US,
            "%.12g*T_slide572",fraction-previousFraction));
        String solid=buildSolid(model,previousSolid,index);
        System.out.println("FINE_SOLID_START index="+index+" fraction="
            +fraction+" solution="+solid);
        model.sol(solid).runAll();
        String pressure=buildJfo(model,previousPressure,solid,index);
        System.out.println("FINE_JFO_START index="+index+" fraction="
            +fraction+" solution="+pressure);
        model.sol(pressure).runAll();
        evaluate(model,solid,pressure,fraction);
        previousSolid=solid;
        previousPressure=pressure;
        previousFraction=fraction;
        model.save(CHECKPOINT);
      }
      model.label("Stage 576rf fine 15 to 25 percent results");
      model.save(RESULTS);
      System.out.println("FINE_STATUS=COMPLETE");
      ModelUtil.disconnect();
    }catch(Exception error){
      error.printStackTrace();
      try{ModelUtil.disconnect();}catch(Exception ignored){}
      System.exit(1);
    }
  }
}
