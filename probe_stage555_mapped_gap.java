import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_mapped_gap {
  static void eval(Model model, String tag, String type, String expr) {
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset540s");
    model.result().numerical(tag).selection().named("sel_film_track");
    model.result().numerical(tag).set("expr", expr);
    System.out.println(type + " " + expr + "="
        + Arrays.deepToString(model.result().numerical(tag).getReal()));
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "553_stage550_five_position_checked_9mm_track.mph");
      model.param().set("t_replay", "0.28[s]");
      model.param().set("phi_qs142", "-35[deg]");
      model.component("comp1").cpl().create("genext_lid555",
          "GeneralExtrusion");
      var op = model.component("comp1").cpl("genext_lid555");
      op.set("opname", "genext555");
      op.selection().named("sel_lid_contact_source_robust");
      op.set("srcframe", "material");
      op.set("usesrcmap", "on");
      op.set("srcmap", new String[] {"X", "atan2(Y,Z)", "0"});
      op.set("dstmap", new String[] {"X", "atan2(Y,Z)", "0"});
      op.set("dstattachdim", "2");
      op.set("manualsearchdist", "on");
      op.set("searchdist", "2[mm]");
      System.out.println("CPL TAGS="
          + Arrays.toString(model.component("comp1").cpl().tags()));
      System.out.println("OPNAME=" + op.getString("opname"));
      model.component("comp1").variable().create("var_gap555probe");
      model.component("comp1").variable("var_gap555probe")
          .selection().named("sel_film_track");
      model.component("comp1").variable("var_gap555probe").set(
          "duN555",
          "(genext555(u)-u)*nx"
              + "+(genext555(v)-v)*ny"
              + "+(genext555(w)-w)*nz");
      model.component("comp1").variable("var_gap555probe").set(
          "hgeom555probe",
          "h0_tear+duN555");
      model.save("probe_stage555_mapped_gap_setup.mph");
      ModelUtil.remove("Model");
      model = ModelUtil.load(
          "Model", "probe_stage555_mapped_gap_setup.mph");
      int i = 0;
      for (String expr : new String[] {
          "genext555(u)", "genext555(v)",
          "genext555(w)", "duN555", "hgeom555probe",
          "if(lid_mask>0.5,hgeom555probe,h0_tear)"
      }) {
        try { eval(model, "minp" + (++i), "MinSurface", expr); }
        catch (Exception error) {
          System.out.println("MIN ERROR " + expr + " " + error.getMessage());
        }
        try { eval(model, "maxp" + (++i), "MaxSurface", expr); }
        catch (Exception error) {
          System.out.println("MAX ERROR " + expr + " " + error.getMessage());
        }
        try { eval(model, "avgp" + (++i), "AvSurface", expr); }
        catch (Exception error) {
          System.out.println("AVG ERROR " + expr + " " + error.getMessage());
        }
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
