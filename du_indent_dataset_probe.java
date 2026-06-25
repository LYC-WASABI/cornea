import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_indent_dataset_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\du_cornea_lid_rounded_lid_geometric_indentation_calibration_results.mph");
    System.out.println("Studies:");
    for (String tag : model.study().tags()) {
      System.out.println(tag + " : " + model.study(tag).label());
    }
    System.out.println("Solutions:");
    for (String tag : model.sol().tags()) {
      System.out.println(tag + " : " + model.sol(tag).label());
    }
    System.out.println("Datasets:");
    for (String tag : model.result().dataset().tags()) {
      System.out.println(tag + " : " + model.result().dataset(tag).label() + " type=" + model.result().dataset(tag).getType());
      try { System.out.println("  sol=" + model.result().dataset(tag).getString("solution")); } catch (Exception ignored) {}
      try { System.out.println("  data=" + model.result().dataset(tag).getString("data")); } catch (Exception ignored) {}
    }
    System.out.println("Numericals:");
    for (String tag : model.result().numerical().tags()) {
      System.out.println(tag + " : " + model.result().numerical(tag).label() + " type=" + model.result().numerical(tag).getType());
      try { System.out.println("  data=" + model.result().numerical(tag).getString("data")); } catch (Exception ignored) {}
    }
  }
}
