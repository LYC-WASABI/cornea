import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576q_transient_history {
  private static final String INPUT =
      "576q3_stage576_ramped_transient_edge_jfo_025_results.mph";
  private static final String SWEPT = "sel_film_swept571";

  private static double[] values(Model model, String data, String tag,
      String type, String expression, boolean surface) {
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    if (surface) model.result().numerical(tag).selection().named(SWEPT);
    model.result().numerical(tag).set("expr", expression);
    return model.result().numerical(tag).getReal()[0];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", INPUT);
      String data = "dset576qHistory";
      model.result().dataset().create(data, "Solution");
      model.result().dataset(data).set("solution", "sol184");
      double[] time = values(model,data,"eval576qTime","EvalGlobal","t",false);
      double[] film = values(model,data,"int576qHistoryFilm","IntSurface",
          "max(p_load573,0[Pa])",true);
      double[] maxP = values(model,data,"max576qHistoryP","MaxSurface",
          "tff.p-p_amb573",true);
      double[] minTheta = values(model,data,"min576qHistoryTheta","MinSurface",
          "tff.theta",true);
      int n = Math.min(Math.min(time.length,film.length),
          Math.min(maxP.length,minTheta.length));
      for (int i=0;i<n;i++) {
        if (i==0 || i==n-1 || i%10==0) {
          System.out.printf(Locale.US,
              "TRANSIENT_HISTORY index=%d t=%.12g Ffilm=%.12g MaxP=%.12g"
                  + " MinTheta=%.12g%n",
              i,time[i],film[i],maxP[i],minTheta[i]);
        }
      }
      if (n>1) {
        System.out.printf(Locale.US,
            "TRANSIENT_FINAL_SLOPE dFdt=%.12g dPdt=%.12g%n",
            (film[n-1]-film[n-2])/(time[n-1]-time[n-2]),
            (maxP[n-1]-maxP[n-2])/(time[n-1]-time[n-2]));
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
