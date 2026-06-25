import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage555_jfo_only_setup {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558a_stage555_JFO_only_study_alpha0p964_setup.mph");
      String comp = "comp1";
      String study = "std_jfo_init555";
      String solution = "sol82";
      String equation = model.component(comp).physics("tff")
          .prop("EquationType").getString("EquationType");
      String transition = model.component(comp).physics("tff")
          .prop("EquationType").getString("sftransition");
      String[] active =
          model.study(study).feature("stat").getStringArray("activate");
      SolverFeature variables = model.sol(solution).feature("v1");

      System.out.println("alpha_gap555="
          + model.param().get("alpha_gap555"));
      System.out.println("EquationType=" + equation);
      System.out.println("sftransition=" + transition);
      System.out.println("StudyLabel=" + model.study(study).label());
      System.out.println("Activate=" + Arrays.toString(active));
      System.out.println("initmethod="
          + variables.getString("initmethod"));
      System.out.println("initsol=" + variables.getString("initsol"));
      System.out.println("solnum=" + variables.getString("solnum"));
      System.out.println("manualsolnum="
          + Arrays.toString(variables.getStringArray("manualsolnum")));
      System.out.println("rho_c=" + model.component(comp).physics("tff")
          .feature("ffp1").getString("rho_c"));
      System.out.println("beta=" + model.component(comp).physics("tff")
          .feature("ffp1").getString("beta"));
      for (String boundary : new String[] {
          "bdr_inlet520", "bdr_outlet520",
          "bdr_left520", "bdr_right520"
      }) {
        System.out.println(boundary + "=" + Arrays.toString(
            model.component(comp).physics("tff").feature(boundary)
                .selection().entities()));
      }

      if (!"0.964".equals(model.param().get("alpha_gap555"))) {
        throw new IllegalStateException("alpha_gap555 is not 0.964");
      }
      if (!"ReynoldsEquationWithCavitation".equals(equation)) {
        throw new IllegalStateException("JFO equation is not enabled");
      }
      if (!"sol81".equals(variables.getString("initsol"))) {
        throw new IllegalStateException("Initial solution is not sol81");
      }
      if (!"manual".equals(variables.getString("solnum"))
          || !"3".equals(variables.getStringArray("manualsolnum")[0])) {
        throw new IllegalStateException(
            "Initial parameter solution is not sol81 number 3");
      }
      System.out.println("VALIDATION=PASS");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
