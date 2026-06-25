import com.comsol.model.*;
import com.comsol.model.util.*;

public class finalize_stage550_9mm_track {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "552_stage550_five_position_results.mph");
      model.label("Stage 550 checked: corrected 9 mm film track");
      model.save("553_stage550_five_position_checked_9mm_track.mph");
      System.out.println("STAGE550_9MM_FINALIZE_PASS");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
