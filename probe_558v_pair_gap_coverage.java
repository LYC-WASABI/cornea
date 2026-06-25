import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_558v_pair_gap_coverage {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558v_stage567_structure_balance_results.mph");
      String gap = "geomgap_dst_cp_lid_cornea";
      try { model.result().dataset().remove("dset_cov569"); }
      catch (Exception ignored) {}
      model.result().dataset().create("dset_cov569", "Solution");
      model.result().dataset("dset_cov569").set("solution", "sol91");

      String eval = "eval_cov569";
      try { model.result().numerical().remove(eval); }
      catch (Exception ignored) {}
      model.result().numerical().create(eval, "IntSurface");
      model.result().numerical(eval).set("data", "dset_cov569");
      model.result().numerical(eval)
          .selection().named("sel_film_track");
      List<String> expr = new ArrayList<>();
      expr.add("1");
      for (String limit : new String[] {
          "0.05[mm]", "0.1[mm]", "0.25[mm]", "0.5[mm]",
          "1[mm]", "2[mm]", "5[mm]"
      }) {
        expr.add("if(" + gap + "<" + limit + ",1,0)");
        expr.add("if(" + gap + "<" + limit + "," + gap + ",0)");
      }
      model.result().numerical(eval)
          .set("expr", expr.toArray(new String[0]));
      double[][] values = model.result().numerical(eval).getReal();
      System.out.println("VALUES=" + Arrays.deepToString(values));
      double area = values[0][0];
      int row = 1;
      int i = 0;
      for (String limit : new String[] {
          "0.05mm", "0.1mm", "0.25mm", "0.5mm",
          "1mm", "2mm", "5mm"
      }) {
        double mappedArea = values[row++][0];
        double gapIntegral = values[row++][0];
        System.out.printf(Locale.US,
            "LIMIT=%s AREA_FRAC=%.9g AVG_GAP=%.9g%n",
            limit, mappedArea / area,
            mappedArea > 0 ? gapIntegral / mappedArea : Double.NaN);
        i++;
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
