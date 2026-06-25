import com.comsol.model.*;
import com.comsol.model.util.*;
import java.io.*;
import java.util.*;

public class audit_558u_model {
  private static PrintWriter out;

  private static void line(String key, Object value) {
    out.println(key + "|" + String.valueOf(value));
  }

  private static void feature(ModelNode comp, String physics, String tag) {
    try {
      var f = comp.physics(physics).feature(tag);
      line("FEATURE", physics + "|" + tag + "|" + f.getType() + "|"
          + f.label() + "|active=" + f.isActive());
      try { line("FEATURE_NAMED", tag + "|" + f.selection().named()); }
      catch (Exception ignored) {}
      try {
        line("FEATURE_ENTITIES", tag + "|"
            + Arrays.toString(f.selection().entities(2)));
      } catch (Exception ignored) {}
      for (String prop : new String[] {
          "FperArea", "forceType", "p0", "mu_fric", "equation", "name",
          "initialValueU", "StudyStep", "WallVelocity", "U", "h"
      }) {
        try {
          String[] values = f.getStringArray(prop);
          if (values.length > 0) {
            line("FEATURE_PROP", tag + "|" + prop + "|"
                + Arrays.toString(values));
          }
        } catch (Exception ignored) {}
      }
    } catch (Exception error) {
      line("FEATURE_ERROR", physics + "|" + tag + "|" + error.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "558u_stage567_structure_balance_setup.mph");
      out = new PrintWriter(System.err, true);
      ModelNode comp = model.component("comp1");

      line("MODEL_LABEL", model.label());
      for (String name : new String[] {
          "F_total_target", "Ftotal_target", "delta_h562", "delta_h565",
          "delta_h566", "gamma_p565", "scale_pfilm555",
          "eps_q_regular555", "q_ref555"
      }) {
        try { line("PARAM", name + "|" + model.param().get(name)); }
        catch (Exception ignored) {}
      }

      for (String tag : comp.selection().tags()) {
        try {
          if (tag.contains("film") || tag.contains("lid")
              || tag.contains("cornea") || tag.contains("contact")) {
            line("SELECTION", tag + "|" + comp.selection(tag).label() + "|"
                + Arrays.toString(comp.selection(tag).entities()));
          }
        } catch (Exception ignored) {}
      }

      for (String tag : comp.variable().tags()) {
        for (String name : new String[] {
            "p_feedback567", "Wfilm567", "Ftotal567", "Ferr567",
            "Fn_contact119", "dr_indent119", "q_force_total111",
            "h_geom555", "p_old565", "p_new565", "p_feedback565"
        }) {
          try {
            String value = comp.variable(tag).get(name);
            if (value != null && !value.isEmpty()) {
              line("VAR", tag + "|" + name + "|" + value);
            }
          } catch (Exception ignored) {}
        }
      }

      feature(comp, "solid", "load_partitioned_pfilm");
      feature(comp, "solid", "press_iop");
      feature(comp, "solid", "press_iop1");
      feature(comp, "solid", "fix1");
      feature(comp, "solid", "dcnt1");
      for (String tag : comp.physics("solid").feature().tags()) {
        try {
          var f = comp.physics("solid").feature(tag);
          line("SOLID_TREE", tag + "|" + f.getType() + "|" + f.label()
              + "|active=" + f.isActive());
          try { line("SOLID_NAMED", tag + "|" + f.selection().named()); }
          catch (Exception ignored) {}
          for (String prop : f.properties()) {
            try {
              String[] values = f.getStringArray(prop);
              String joined = Arrays.toString(values);
              if (joined.contains("dr_indent119")
                  || joined.contains("q_force_total111")
                  || joined.contains("spring")
                  || joined.contains("Spring")
                  || joined.contains("fixed")
                  || joined.contains("Fixed")) {
                line("SOLID_RELEVANT_PROP",
                    tag + "|" + prop + "|" + joined);
              }
            } catch (Exception ignored) {}
          }
        } catch (Exception ignored) {}
      }
      try {
        for (String child : comp.physics("solid").feature("dcnt1")
            .feature().tags()) {
          feature(comp, "solid", "dcnt1");
          var cf = comp.physics("solid").feature("dcnt1").feature(child);
          line("CONTACT_CHILD", child + "|" + cf.getType() + "|"
              + cf.label() + "|active=" + cf.isActive());
          for (String prop : new String[] {"mu_fric", "method", "penalty"}) {
            try {
              line("CONTACT_PROP", child + "|" + prop + "|"
                  + Arrays.toString(cf.getStringArray(prop)));
            } catch (Exception ignored) {}
          }
        }
      } catch (Exception ignored) {}
      feature(comp, "ge_force_total111", "ge1");

      try {
        line("TFF_LABEL", comp.physics("tff").label());
        for (String tag : comp.physics("tff").feature().tags()) {
          var f = comp.physics("tff").feature(tag);
          line("TFF_FEATURE", tag + "|" + f.getType() + "|"
              + f.label() + "|active=" + f.isActive());
          try { line("TFF_NAMED", tag + "|" + f.selection().named()); }
          catch (Exception ignored) {}
          if (tag.equals("ffp1") || tag.equals("init1")) {
            for (String prop : f.properties()) {
              try {
                String[] values = f.getStringArray(prop);
                if (values.length > 0) {
                  line("TFF_PROP", tag + "|" + prop + "|"
                      + Arrays.toString(values));
                }
              } catch (Exception ignored) {}
            }
          }
        }
        try {
          line("TFF_EQUATION_TYPE",
              Arrays.toString(comp.physics("tff").prop("EquationType")
                  .getStringArray("EquationType")));
        } catch (Exception ignored) {}
      } catch (Exception error) {
        line("TFF_ERROR", error.getMessage());
      }

      for (String studyTag : model.study().tags()) {
        String label = model.study(studyTag).label();
        if (label.contains("566") || label.contains("567")) {
          line("STUDY", studyTag + "|" + label);
          for (String ft : model.study(studyTag).feature().tags()) {
            StudyFeature sf = model.study(studyTag).feature(ft);
            line("STUDY_FEATURE", ft + "|" + sf.getType() + "|" + sf.label());
            try {
              line("ACTIVATE", ft + "|"
                  + Arrays.toString(sf.getStringArray("activate")));
            } catch (Exception ignored) {}
            try { line("INITSOL", ft + "|" + sf.getString("initsol")); }
            catch (Exception ignored) {}
          }
        }
      }

      for (String solTag : model.sol().tags()) {
        try {
          String study = model.sol(solTag).study();
          String label = study.isEmpty() ? "" : model.study(study).label();
          if (label.contains("566") || label.contains("567")
              || solTag.equals("sol90") || solTag.equals("sol91")) {
            line("SOL", solTag + "|study=" + study + "|label=" + label
                + "|empty=" + model.sol(solTag).isEmpty() + "|pvals="
                + Arrays.toString(model.sol(solTag).getPVals()));
          }
        } catch (Exception error) {
          line("SOL_ERROR", solTag + "|" + error.getMessage());
        }
      }

      for (String sol : new String[] {"sol90", "sol91"}) {
        try {
          String dataset = "audit_dset_" + sol;
          String eval = "audit_eval_" + sol;
          try { model.result().dataset().remove(dataset); }
          catch (Exception ignored) {}
          try { model.result().numerical().remove(eval); }
          catch (Exception ignored) {}
          model.result().dataset().create(dataset, "Solution");
          model.result().dataset(dataset).set("solution", sol);
          model.result().numerical().create(eval, "EvalGlobal");
          model.result().numerical(eval).set("data", dataset);
          model.result().numerical(eval).set("expr", new String[] {
              "Wfilm567", "Fn_contact119", "Ftotal567", "Ferr567",
              "dr_indent119", "q_force_total111"
          });
          line("EVAL", sol + "|"
              + Arrays.deepToString(
                  model.result().numerical(eval).getReal()));
        } catch (Exception error) {
          line("EVAL_ERROR", sol + "|" + error.getMessage());
        }
      }

      out.flush();
      ModelUtil.disconnect();
    } catch (Exception error) {
      if (out != null) {
        error.printStackTrace(out);
        out.flush();
      } else {
        error.printStackTrace();
      }
      System.exit(1);
    }
  }
}
