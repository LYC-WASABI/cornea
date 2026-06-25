import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class validate_stage520_local_tff {
  static void require(boolean condition, String message) {
    if (!condition) throw new IllegalStateException(message);
  }

  static void expectSingle(
      Model model, String feature, String selection) {
    int[] actual = model.component("comp1").physics("tff")
        .feature(feature).selection().entities();
    int[] expected =
        model.component("comp1").selection(selection).entities(1);
    System.out.println(feature + "=" + Arrays.toString(actual));
    require(actual.length >= 1 && Arrays.equals(actual, expected),
        feature + " selection mismatch");
    require("ZeroPressure".equals(model.component("comp1").physics("tff")
        .feature(feature).getString("BorderCondition")),
        feature + " is not ZeroPressure");
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model model = ModelUtil.load(
        "Model", "523_stage520_local_tff_drainage_checked.mph");
    require(Math.abs(model.param().evaluate("stage500_revision") - 500)
        < 0.1, "Stage 500 ancestry missing");
    require(Math.abs(model.param().evaluate("stage520_revision") - 520)
        < 0.1, "Stage 520 metadata missing");
    int[] track =
        model.component("comp1").selection("sel_film_track").entities(2);
    int[] root =
        model.component("comp1").physics("tff").selection().entities();
    int[] ffp = model.component("comp1").physics("tff").feature("ffp1")
        .selection().entities();
    int[] init = model.component("comp1").physics("tff").feature("init1")
        .selection().entities();
    int[] intop =
        model.component("comp1").cpl("intop_film").selection().entities();
    System.out.println("track=" + Arrays.toString(track));
    System.out.println("tff=" + Arrays.toString(root));
    System.out.println("ffp1=" + Arrays.toString(ffp));
    System.out.println("init1=" + Arrays.toString(init));
    System.out.println("intop_film=" + Arrays.toString(intop));
    require(track.length >= 1, "track is empty");
    require(Arrays.equals(root, track), "TFF root is not local");
    require(Arrays.equals(ffp, track), "ffp1 is not local");
    require(Arrays.equals(init, track), "init1 is not local");
    require(Arrays.equals(intop, track), "intop_film is not local");
    expectSingle(model, "bdr_inlet520", "sel_film_inlet");
    expectSingle(model, "bdr_outlet520", "sel_film_outlet");
    expectSingle(model, "bdr_left520", "sel_film_side_left");
    expectSingle(model, "bdr_right520", "sel_film_side_right");
    if (Arrays.asList(model.component("comp1").physics("tff")
        .feature().tags()).contains("bdr1")) {
      int[] defaultBorder = model.component("comp1").physics("tff")
          .feature("bdr1").selection().entities();
      System.out.println("default_bdr1=" + Arrays.toString(defaultBorder));
      require(defaultBorder.length == 0,
          "default bdr1 still acts on geometric edges");
    }
    require("list".equals(model.component("comp1").physics("tff")
        .feature("dcont1").getString("pairSelection")),
        "dcont1 is not explicit");
    require(model.component("comp1").physics("tff").feature("dcont1")
        .getStringArray("pairs").length == 0,
        "dcont1 still contains a pair");
    require(Arrays.asList(model.component("comp1").pair().tags())
        .containsAll(Arrays.asList("cp_lid_cornea", "ap1")),
        "required pairs missing");
    System.out.println("TFF FEATURES=" + Arrays.toString(
        model.component("comp1").physics("tff").feature().tags()));
    System.out.println("STAGE520_RELOAD_VALIDATION=PASS");
    ModelUtil.disconnect();
  }
}
