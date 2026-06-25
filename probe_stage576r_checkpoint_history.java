import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576r_checkpoint_history {
  private static final String INPUT=
      "576r_stage576_first_quarter_piecewise_jfo_checkpoint.mph";
  private static final String SWEPT="sel_film_swept571";

  private static double last(Model model,String data,String tag,String type,
      String expression,boolean surface){
    try{model.result().numerical().remove(tag);}catch(Exception ignored){}
    model.result().numerical().create(tag,type);
    model.result().numerical(tag).set("data",data);
    if(surface)model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr",expression);
    double[] values=model.result().numerical(tag).getReal()[0];
    return values[values.length-1];
  }

  public static void main(String[] args){
    try{
      ModelUtil.initStandalone(false);
      Model model=ModelUtil.load("Model",INPUT);
      String[] pressure={"sol184","sol185","sol186"};
      String[] solid={"sol145","sol146","sol147"};
      double[] fraction={0.05,0.10,0.15};
      for(int i=0;i<pressure.length;i++){
        model.param().set("t_position576p2",String.format(Locale.US,
            "T_pre572+%.12g*T_slide572",fraction[i]));
        String dp="dset576rProbeP";
        String ds="dset576rProbeS";
        try{model.result().dataset().remove(dp);}catch(Exception ignored){}
        try{model.result().dataset().remove(ds);}catch(Exception ignored){}
        model.result().dataset().create(dp,"Solution");
        model.result().dataset(dp).set("solution",pressure[i]);
        model.result().dataset().create(ds,"Solution");
        model.result().dataset(ds).set("solution",solid[i]);
        double film=last(model,dp,"int576rProbeFilm","IntSurface",
            "max(p_load573,0[Pa])",true);
        double maxP=last(model,dp,"max576rProbeP","MaxSurface",
            "tff.p-p_amb573",true);
        double minTheta=last(model,dp,"min576rProbeTheta","MinSurface",
            "tff.theta",true);
        double contact=last(model,ds,"eval576rProbeContact","EvalGlobal",
            "Fn_contact570",false);
        System.out.printf(Locale.US,
            "CHECKPOINT_RESULT fraction=%.3f Fcontact=%.12g Ffilm=%.12g"
                +" Ftotal=%.12g MaxP=%.12g MinTheta=%.12g%n",
            fraction[i],contact,film,contact+film,maxP,minTheta);
      }
      ModelUtil.disconnect();
    }catch(Exception error){
      error.printStackTrace();
      try{ModelUtil.disconnect();}catch(Exception ignored){}
      System.exit(1);
    }
  }
}
