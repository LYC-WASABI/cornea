import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage574_source_boundary_bounds {
  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load(
          "Model", "probe_stage574_select_inset_patch.mph");
      ModelNode comp = model.component("comp1");
      MeshSequence mesh = comp.mesh("mesh1");
      double[][] allCoordinates = mesh.getVertex();
      int[][] triangles = mesh.getElem("tri");
      int[] triangleEntities = mesh.getElemEntity("tri");
      int[] boundaries =
          comp.selection("sel_lid_source_full574").entities(2);
      for (int boundary : boundaries) {
        Set<Integer> vertices = new LinkedHashSet<>();
        for (int element = 0; element < triangleEntities.length; element++) {
          if (triangleEntities[element] != boundary) continue;
          for (int local = 0; local < triangles.length; local++) {
            vertices.add(triangles[local][element]);
          }
        }
        double[] minimum = {
          Double.POSITIVE_INFINITY,
          Double.POSITIVE_INFINITY,
          Double.POSITIVE_INFINITY
        };
        double[] maximum = {
          Double.NEGATIVE_INFINITY,
          Double.NEGATIVE_INFINITY,
          Double.NEGATIVE_INFINITY
        };
        double angleMinimum = Double.POSITIVE_INFINITY;
        double angleMaximum = Double.NEGATIVE_INFINITY;
        double radiusMinimum = Double.POSITIVE_INFINITY;
        double radiusMaximum = Double.NEGATIVE_INFINITY;
        int count = vertices.size();
        for (int direction = 0; direction < allCoordinates.length; direction++) {
          for (int vertex : vertices) {
            double coordinate = allCoordinates[direction][vertex];
            minimum[direction] = Math.min(minimum[direction], coordinate);
            maximum[direction] = Math.max(maximum[direction], coordinate);
          }
        }
        for (int vertex : vertices) {
          double x = allCoordinates[0][vertex];
          double y = allCoordinates[1][vertex];
          double z = allCoordinates[2][vertex];
          double angle = Math.atan2(y, z) * 180.0 / Math.PI;
          double radius = Math.sqrt(x * x + y * y + z * z);
          angleMinimum = Math.min(angleMinimum, angle);
          angleMaximum = Math.max(angleMaximum, angle);
          radiusMinimum = Math.min(radiusMinimum, radius);
          radiusMaximum = Math.max(radiusMaximum, radius);
        }
        System.out.println(
            "BND=" + boundary + " N=" + count
            + " MIN=" + Arrays.toString(minimum)
            + " MAX=" + Arrays.toString(maximum)
            + " ANGLE=[" + angleMinimum + "," + angleMaximum + "]"
            + " RADIUS=[" + radiusMinimum + "," + radiusMaximum + "]");
      }
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
