import com.comsol.model.*;
import com.comsol.model.util.*;

public class export_stage62_ppt_images {
  static final String OUT = "C:\\Users\\l1363\\Documents\\复现\\outputs\\manual-20260602-lidflow\\presentations\\stage62-results-ppt\\assets\\";

  static void export(Model m, String exportTag, String plotTag, String fileName) {
    try { m.result().export().remove(exportTag); } catch (Exception ignored) {}
    m.result().export().create(exportTag, "Image");
    m.result().export(exportTag).set("plotgroup", plotTag);
    m.result().export(exportTag).set("pngfilename", OUT + fileName);
    m.result().export(exportTag).set("width", "1400");
    m.result().export(exportTag).set("height", "800");
    m.result().export(exportTag).run();
    System.out.println("EXPORTED=" + fileName + " FROM=" + plotTag);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model", "D:\\COMSOL_Outputs\\models\\du\\flow\\148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph");
    export(m, "img_ppt_model", "pg_solid_disp", "01_model_displacement.png");
    export(m, "img_ppt_contact", "pg_final_contact_pressure", "02_contact_pressure.png");
    export(m, "img_ppt_hfilm", "pg62_hfilm", "03_film_thickness.png");
    export(m, "img_ppt_pfilm", "pg62_pfilm", "04_film_pressure.png");
    export(m, "img_ppt_load", "pg62_loadshare", "05_load_sharing.png");
    export(m, "img_ppt_friction", "pg62_friction", "06_friction.png");
    export(m, "img_ppt_gap", "pg62_gap", "07_gap.png");
    System.out.println("DONE");
  }
}
