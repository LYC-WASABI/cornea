import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576e2_window_results {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", "576e2_stage576_staggered_controller_window_results.mph");
      int outside = 0;
      boolean stable = true;
      double fContact = 0.0255926141640;
      double q = -9.0;
      String[] numericalTags = model.result().numerical().tags();
      for (int i = 1; i <= 11; i++) {
        String tffData = "dset576e2_tff_" + i;
        String solidData = "dset576e2_solid_" + i;
        double[] filmValues = model.result().numerical("int576e2_film_" + i).getReal()[0];
        double fFilm = filmValues[filmValues.length-1];
        double[] thetaValues = model.result().numerical("min576e2_theta_" + i).getReal()[0];
        double minTheta = thetaValues[thetaValues.length-1];
        double[] pressureValues = model.result().numerical("max576e2_p_" + i).getReal()[0];
        double maxP = pressureValues[pressureValues.length-1];
        boolean hasSolidUpdate = false;
        for (String tag : numericalTags) if (tag.equals("eval576e2_contact_" + i)) hasSolidUpdate = true;
        if (hasSolidUpdate) {
          double[] contactValues = model.result().numerical("eval576e2_contact_" + i).getReal()[0];
          fContact = contactValues[contactValues.length-1];
          String eval = "probe576e2_" + i;
          model.result().numerical().create(eval, "EvalGlobal");
          model.result().numerical(eval).set("data", solidData);
          model.result().numerical(eval).set("expr", new String[] {"q_scale574"});
          q = model.result().numerical(eval).getReal()[0][0];
        }
        String evalT = "probe576e2_t_" + i;
        model.result().numerical().create(evalT, "EvalGlobal");
        model.result().numerical(evalT).set("data", tffData);
        model.result().numerical(evalT).set("expr", new String[] {"t"});
        double[] times = model.result().numerical(evalT).getReal()[0];
        double time = times[times.length-1];
        double total = fContact + fFilm;
        if (total < 0.025 || total > 0.035) outside++;
        stable = stable && Double.isFinite(total) && Double.isFinite(maxP) && minTheta >= -1e-8;
        System.out.printf(Locale.US,
            "ROW step=%d time=%.12g q=%.12g Fcontact=%.12g Ffilm=%.12g Ftotal=%.12g error=%.12g MaxP=%.12g MinTheta=%.12g%n",
            i, time, q, fContact, fFilm, total, total-0.03, maxP, minTheta);
      }
      System.out.println("OUTSIDE=" + outside);
      System.out.println("STABLE=" + stable);
      System.out.println("STATUS=" + (stable && outside <= 3 ? "PASS" : "FAIL"));
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
