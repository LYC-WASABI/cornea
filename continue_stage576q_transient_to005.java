import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class continue_stage576q_transient_to005 {
  private static final String INPUT =
      "576q_stage576_transient_edge_jfo_025_results.mph";
  private static final String OUTPUT =
      "576q2_stage576_transient_edge_jfo_025_to005_results.mph";
  private static final String SWEPT = "sel_film_swept571";

  private static boolean has(String[] values, String value) {
    for (String candidate : values) if (candidate.equals(value)) return true;
    return false;
  }

  private static String newest(Model model, String[] before) {
    Set<String> old = new HashSet<String>(Arrays.asList(before));
    String latest = null;
    for (String tag : model.sol().tags()) if (!old.contains(tag)) latest = tag;
    if (latest == null) throw new IllegalStateException("No new solution created");
    return latest;
  }

  private static double last(Model model, String data, String tag,
      String type, String expression) {
    model.result().numerical().create(tag,type);
    model.result().numerical(tag).set("data",data);
    model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr",expression);
    double[] values=model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model=ModelUtil.load("Model",INPUT);
      String study="std576q2_transient_025";
      model.study().create(study);
      model.study(study).label("Stage 576q2 transient JFO continuation to 5 ms");
      model.study(study).create("time","Transient");
      model.study(study).feature("time").set(
          "tlist","range(1e-3[s],2e-5[s],5e-3[s])");
      model.study(study).feature("time").set("activate",new String[]{
        "solid","off","ge_force_total111","off","tff","on",
        "frame:spatial1","on","frame:material1","on","comp1","on"
      });
      model.study(study).feature("time").set("useinitsol","on");
      model.study(study).feature("time").set("initmethod","sol");
      model.study(study).feature("time").set("initsol","sol184");
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
      dependent.set("initsol","sol184");
      dependent.set("solnum","last");
      dependent.set("notsolmethod","sol");
      dependent.set("notsol","sol149");
      dependent.set("notsolnum","last");
      SolverFeature time=model.sol(solution).feature("t1");
      time.set("tlist","range(1e-3[s],2e-5[s],5e-3[s])");
      try{time.set("maxorder",2);}catch(Exception ignored){}
      try{time.set("initialstepbdf","1e-8[s]");}catch(Exception ignored){}
      try{time.set("maxstepconstraintbdf","const");}catch(Exception ignored){}
      try{time.set("maxstepbdf","2e-5[s]");}catch(Exception ignored){}
      if(!has(time.feature().tags(),"fc1"))time.create("fc1","FullyCoupled");
      time.feature("fc1").set("linsolver","dDef");
      time.feature("fc1").set("damp","0.5");
      time.feature("fc1").set("maxiter",120);
      System.out.println("TRANSIENT_CONTINUE_START="+solution);
      model.sol(solution).runAll();
      String data="dset576q2";
      model.result().dataset().create(data,"Solution");
      model.result().dataset(data).set("solution",solution);
      double film=last(model,data,"int576q2Film","IntSurface",
          "max(p_load573,0[Pa])");
      double maxP=last(model,data,"max576q2P","MaxSurface",
          "tff.p-p_amb573");
      double minTheta=last(model,data,"min576q2Theta","MinSurface","tff.theta");
      System.out.printf(Locale.US,
          "TRANSIENT_CONTINUE_RESULT Ffilm=%.12g MaxP=%.12g MinTheta=%.12g%n",
          film,maxP,minTheta);
      model.label("Stage 576q2 transient edge JFO to 5 ms results");
      model.save(OUTPUT);
      System.out.println("TRANSIENT_CONTINUE_STATUS=COMPLETE");
      ModelUtil.disconnect();
    }catch(Exception error){
      error.printStackTrace();
      try{ModelUtil.disconnect();}catch(Exception ignored){}
      System.exit(1);
    }
  }
}
