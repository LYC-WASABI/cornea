import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_partial_progress {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "stage555_coupled_gap_indentation_output_Model.mph");
      model.result().dataset().create("dsp555", "Solution");
      model.result().dataset("dsp555").set("solution", "sol61");
      model.result().numerical().create("evp555", "EvalGlobal");
      model.result().numerical("evp555").set("data", "dsp555");
      model.result().numerical("evp555").set("expr", new String[] {
        "alpha_gap555", "Wfilm555", "Fn_contact119", "Ftotal555",
        "dr_indent119", "intop_film(h_geom555)/intop_film(1)"
      });
      double[][] x = model.result().numerical("evp555").getReal();
      for (int j = 0; j < x[0].length; j++) {
        System.out.printf(Locale.US,
            "a=%.8g Wf=%.10g Fc=%.10g Ft=%.10g d=%.10g h=%.10g%n",
            x[0][j], x[1][j], x[2][j], x[3][j], x[4][j], x[5][j]);
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
