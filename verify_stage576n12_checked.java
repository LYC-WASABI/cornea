import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Locale;

public class verify_stage576n12_checked {
  private static void removeDataset(Model model, String tag) {
    try { model.result().dataset().remove(tag); } catch (Exception ignored) {}
  }

  private static void removeNumerical(Model model, String tag) {
    try { model.result().numerical().remove(tag); } catch (Exception ignored) {}
  }

  private static String dataset(Model model, String tag, String solution) {
    removeDataset(model, tag);
    model.result().dataset().create(tag, "Solution");
    model.result().dataset(tag).set("solution", solution);
    return tag;
  }

  private static double global(Model model, String data, String tag, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  private static double surface(
      Model model, String data, String tag, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named("sel_local_cornea_patch574");
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576n12_stage576_full_dynamic_recursive_checked.mph");
      String pressureData = dataset(model, "verifyPressure", "sol4984");
      String relaxedData = dataset(model, "verifyRelaxed", "sol4985");
      String solidData = dataset(model, "verifySolid", "sol4986");

      double film = surface(model, pressureData, "verifyFilm", "IntSurface", "p_load573");
      double theta = surface(model, pressureData, "verifyTheta", "MinSurface", "tff.theta");
      double maxPressure = surface(
          model, pressureData, "verifyMaxPressure", "MaxSurface", "tff.p-p_amb573");
      double feedback = surface(
          model, relaxedData, "verifyFeedback", "IntSurface", "p_scale576m*rrel576m");
      double residual = surface(model, relaxedData, "verifyResidual", "IntSurface",
          "abs(p_scale576m*rrel576m-alpha_pfb576m*withsol('sol4984',p_load573))");
      double contact = global(model, solidData, "verifyContact", "Fn_contact570");
      double minGap = surface(model, solidData, "verifyGap", "MinSurface",
          "geomgap_dst_cp_lid_cornea");

      System.out.printf(Locale.US, "FINAL_FCONTACT=%.12g%n", contact);
      System.out.printf(Locale.US, "FINAL_FFILM=%.12g%n", film);
      System.out.printf(Locale.US, "FINAL_FTOTAL=%.12g%n", contact + film);
      System.out.printf(Locale.US, "FINAL_FFEEDBACK=%.12g%n", feedback);
      System.out.printf(Locale.US, "FINAL_FIELD_RESIDUAL=%.12g%n", residual);
      System.out.printf(Locale.US, "FINAL_MIN_THETA=%.12g%n", theta);
      System.out.printf(Locale.US, "FINAL_MAX_PRESSURE=%.12g%n", maxPressure);
      System.out.printf(Locale.US, "FINAL_MIN_GAP=%.12g%n", minGap);
      System.out.println("VERIFY_FINITE=" +
          (Double.isFinite(contact) && Double.isFinite(film) &&
           Double.isFinite(feedback) && Double.isFinite(residual) &&
           Double.isFinite(theta) && Double.isFinite(maxPressure) &&
           Double.isFinite(minGap)));

      ModelUtil.remove("Model");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
