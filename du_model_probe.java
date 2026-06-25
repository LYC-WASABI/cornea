import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_model_probe {
  public static void main(String[] args) throws java.io.IOException {
    Model model = ModelUtil.load("Model", "D:\\\\COMSOL_Outputs\\\\models\\\\du\\\\du_cornea_lid_first_version_final.mph");
    System.out.println("components:");
    for (String tag : model.component().tags()) {
      System.out.println("component " + tag);
    }
    System.out.println("pairs:");
    for (String tag : model.component("comp1").pair().tags()) {
      System.out.println(tag + " : " + model.component("comp1").pair(tag).label());
    }
    System.out.println("physics:");
    for (String tag : model.component("comp1").physics().tags()) {
      System.out.println(tag + " : " + model.component("comp1").physics(tag).label());
      for (String ftag : model.component("comp1").physics(tag).feature().tags()) {
        System.out.println("  feature " + ftag + " : " + model.component("comp1").physics(tag).feature(ftag).label());
      }
    }
  }
}
