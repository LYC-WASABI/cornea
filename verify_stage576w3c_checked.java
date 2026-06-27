import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.Locale;

public class verify_stage576w3c_checked {
  private static final String MODEL =
      "576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph";
  private static final String PRESSURE_SOL = "sol271";
  private static final String RELAXED_SOL = "sol272";
  private static final String SOLID_SOL = "sol273";
  private static final String SWEPT = "sel_film_swept571";
  private static final String PATCH = "sel_local_cornea_patch574";

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
      Model model, String data, String tag, String selection, String type, String expression) {
    removeNumerical(model, tag);
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", data);
    model.result().numerical(tag).selection().named(selection);
    model.result().numerical(tag).set("expr", expression);
    double[] values = model.result().numerical(tag).getReal()[0];
    return values[values.length - 1];
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model", MODEL);
      String pressureData = dataset(model, "verifyW3cPressure", PRESSURE_SOL);
      String relaxedData = dataset(model, "verifyW3cRelaxed", RELAXED_SOL);
      String solidData = dataset(model, "verifyW3cSolid", SOLID_SOL);

      double film = surface(
          model, pressureData, "verifyW3cFilm", SWEPT, "IntSurface", "max(p_load573,0[Pa])");
      double theta = surface(
          model, pressureData, "verifyW3cTheta", SWEPT, "MinSurface", "tff.theta");
      double maxPressure = surface(
          model, pressureData, "verifyW3cMaxPressure", SWEPT, "MaxSurface", "tff.p-p_amb573");
      double minHeight = surface(
          model, pressureData, "verifyW3cMinHeight", SWEPT, "MinSurface", "h_calc576w3c");
      double avgHeight = surface(
          model, pressureData, "verifyW3cIntHeight", SWEPT, "IntSurface", "h_calc576w3c")
          / surface(model, pressureData, "verifyW3cArea", SWEPT, "IntSurface", "1");
      double feedback = surface(model, relaxedData, "verifyW3cFeedback", PATCH,
          "IntSurface", "p_scale576w3c*rrel576w3c");
      double residual = surface(model, relaxedData, "verifyW3cResidual", PATCH,
          "IntSurface",
          "abs(p_scale576w3c*rrel576w3c-alpha_pfb576w3c*withsol('"
              + PRESSURE_SOL + "',max(p_load573,0[Pa])))");
      double contact = global(model, solidData, "verifyW3cContact", "Fn_contact570");
      double minGap = surface(model, solidData, "verifyW3cGap", PATCH,
          "MinSurface", "geomgap_dst_cp_lid_cornea");
      double drel = global(model, solidData, "verifyW3cDrel", "drel576w3c");
      double total = contact + film;
      double loadError = total - 0.03;

      boolean finite =
          Double.isFinite(contact) && Double.isFinite(film) && Double.isFinite(total) &&
          Double.isFinite(feedback) && Double.isFinite(residual) && Double.isFinite(theta) &&
          Double.isFinite(maxPressure) && Double.isFinite(minHeight) &&
          Double.isFinite(avgHeight) && Double.isFinite(minGap) && Double.isFinite(drel);
      boolean pass = finite
          && Math.abs(loadError) <= 0.003
          && residual < 0.002
          && theta >= -1e-8;

      System.out.printf(Locale.US, "VERIFY_MODEL=%s%n", MODEL);
      System.out.printf(Locale.US, "VERIFY_PRESSURE_SOL=%s%n", PRESSURE_SOL);
      System.out.printf(Locale.US, "VERIFY_RELAXED_SOL=%s%n", RELAXED_SOL);
      System.out.printf(Locale.US, "VERIFY_SOLID_SOL=%s%n", SOLID_SOL);
      System.out.printf(Locale.US, "FINAL_FCONTACT=%.12g%n", contact);
      System.out.printf(Locale.US, "FINAL_FFILM=%.12g%n", film);
      System.out.printf(Locale.US, "FINAL_FTOTAL=%.12g%n", total);
      System.out.printf(Locale.US, "FINAL_LOAD_ERROR=%.12g%n", loadError);
      System.out.printf(Locale.US, "FINAL_FFEEDBACK=%.12g%n", feedback);
      System.out.printf(Locale.US, "FINAL_FIELD_RESIDUAL=%.12g%n", residual);
      System.out.printf(Locale.US, "FINAL_DREL=%.12g%n", drel);
      System.out.printf(Locale.US, "FINAL_MIN_THETA=%.12g%n", theta);
      System.out.printf(Locale.US, "FINAL_MAX_PRESSURE=%.12g%n", maxPressure);
      System.out.printf(Locale.US, "FINAL_MIN_HEIGHT=%.12g%n", minHeight);
      System.out.printf(Locale.US, "FINAL_AVG_HEIGHT=%.12g%n", avgHeight);
      System.out.printf(Locale.US, "FINAL_MIN_GAP=%.12g%n", minGap);
      System.out.println("VERIFY_FINITE=" + finite);
      System.out.println("VERIFY_STATUS=" + (pass ? "PASS" : "FAIL"));

      ModelUtil.remove("Model");
      ModelUtil.disconnect();
      if (!pass) System.exit(2);
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
