import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage120_time_switches {
  private static final String IN =
      "204_lid8mm_stage120_delayed_motion_variable_indent_setup_Model.mph";

  private static boolean relevant(String s) {
    if (s == null) return false;
    String x = s.toLowerCase(Locale.ROOT);
    return x.contains("0.01") || x.contains("1e-2")
        || x.contains("t_pre") || x.contains("t_structure")
        || x.contains("replay") || x.contains("slide")
        || x.contains("speed") || x.contains("ramp")
        || x.contains("film") || x.contains("force_transition")
        || x.contains("if(t") || x.contains("step");
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model", IN);

    System.out.println("## PARAMETERS");
    for (String p : m.param().varnames()) {
      String value = "";
      String descr = "";
      try { value = m.param().get(p); } catch (Exception ignore) {}
      try { descr = m.param().descr(p); } catch (Exception ignore) {}
      if (relevant(p) || relevant(value) || relevant(descr)) {
        System.out.println("PARAM " + p + "=" + value + " ; " + descr);
      }
    }

    System.out.println("## VARIABLES");
    for (String tag : m.component("comp1").variable().tags()) {
      for (String name : m.component("comp1").variable(tag).varnames()) {
        String value = "";
        try { value = m.component("comp1").variable(tag).get(name); }
        catch (Exception ignore) {}
        if (relevant(tag) || relevant(name) || relevant(value)) {
          System.out.println("VAR " + tag + "." + name + "=" + value);
        }
      }
    }

    System.out.println("## FUNCTIONS");
    for (String tag : m.func().tags()) {
      System.out.println("FUNC " + tag + " label=" + m.func(tag).label()
          + " type=" + m.func(tag).getType());
      for (String p : m.func(tag).properties()) {
        try {
          String s = m.func(tag).getString(p);
          if (relevant(p) || relevant(s))
            System.out.println("FUNC " + tag + "." + p + "=" + s);
        } catch (Exception ignore) {}
        try {
          String s = Arrays.toString(m.func(tag).getStringArray(p));
          if (relevant(p) || relevant(s))
            System.out.println("FUNC " + tag + "." + p + "=" + s);
        } catch (Exception ignore) {}
      }
    }
    for (String tag : m.component("comp1").func().tags()) {
      System.out.println("CFUNC " + tag + " label="
          + m.component("comp1").func(tag).label()
          + " type=" + m.component("comp1").func(tag).getType());
      for (String p : m.component("comp1").func(tag).properties()) {
        try {
          String s = m.component("comp1").func(tag).getString(p);
          if (relevant(p) || relevant(s))
            System.out.println("CFUNC " + tag + "." + p + "=" + s);
        } catch (Exception ignore) {}
        try {
          String s = Arrays.toString(
              m.component("comp1").func(tag).getStringArray(p));
          if (relevant(p) || relevant(s))
            System.out.println("CFUNC " + tag + "." + p + "=" + s);
        } catch (Exception ignore) {}
      }
    }

    System.out.println("## SOLID FEATURES");
    for (String tag : m.component("comp1").physics("solid").feature().tags()) {
      for (String p : m.component("comp1").physics("solid").feature(tag).properties()) {
        try {
          String s = m.component("comp1").physics("solid").feature(tag).getString(p);
          if (relevant(p) || relevant(s))
            System.out.println("SOLID " + tag + "." + p + "=" + s);
        } catch (Exception ignore) {}
        try {
          String s = Arrays.toString(m.component("comp1").physics("solid")
              .feature(tag).getStringArray(p));
          if (relevant(p) || relevant(s))
            System.out.println("SOLID " + tag + "." + p + "=" + s);
        } catch (Exception ignore) {}
      }
      for (String child : m.component("comp1").physics("solid")
          .feature(tag).feature().tags()) {
        for (String p : m.component("comp1").physics("solid")
            .feature(tag).feature(child).properties()) {
          try {
            String s = m.component("comp1").physics("solid")
                .feature(tag).feature(child).getString(p);
            if (relevant(p) || relevant(s))
              System.out.println("SOLID " + tag + "/" + child + "." + p + "=" + s);
          } catch (Exception ignore) {}
        }
      }
    }

    System.out.println("## GLOBAL EQUATION FEATURES");
    for (String tag : m.component("comp1").physics("ge_force_total111").feature().tags()) {
      for (String p : m.component("comp1").physics("ge_force_total111")
          .feature(tag).properties()) {
        try {
          String s = m.component("comp1").physics("ge_force_total111")
              .feature(tag).getString(p);
          if (relevant(p) || relevant(s))
            System.out.println("GE " + tag + "." + p + "=" + s);
        } catch (Exception ignore) {}
      }
    }

    ModelUtil.disconnect();
  }
}
