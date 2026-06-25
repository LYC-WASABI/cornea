import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage576p2_mesh_connectivity {
  private static final int[] SURFACES = new int[] {6, 7, 10, 15, 16, 18};

  private static int[] parent;
  private static int find(int x) {
    return parent[x] == x ? x : (parent[x] = find(parent[x]));
  }
  private static void join(int a, int b) {
    a = find(a); b = find(b); if (a != b) parent[b] = a;
  }

  private static boolean selected(int entity) {
    for (int value : SURFACES) if (value == entity) return true;
    return false;
  }

  private static String key(double[][] xyz, int node) {
    return String.format(Locale.US, "%.10e|%.10e|%.10e",
        xyz[0][node], xyz[1][node], xyz[2][node]);
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      Model model = ModelUtil.load("Model",
          "576p2r_stage576_moving_structure_sparse_jfo_results.mph");
      MeshSequence mesh = model.component("comp1").mesh("mesh1");
      double[][] xyz = mesh.getVertex();
      int[][] tri = mesh.getElem("tri");
      int[] entity = mesh.getElemEntity("tri");
      parent = new int[xyz[0].length];
      for (int i = 0; i < parent.length; i++) parent[i] = i;
      Set<Integer> used = new HashSet<Integer>();
      Map<Integer,Set<Integer>> nodeEntities = new HashMap<Integer,Set<Integer>>();
      for (int i = 0; i < entity.length; i++) {
        if (!selected(entity[i])) continue;
        int a=tri[0][i], b=tri[1][i], c=tri[2][i];
        join(a,b); join(b,c);
        for (int node : new int[] {a,b,c}) {
          used.add(node);
          nodeEntities.computeIfAbsent(node, k -> new TreeSet<Integer>())
              .add(entity[i]);
        }
      }
      Map<Integer,Set<Integer>> componentEntities =
          new HashMap<Integer,Set<Integer>>();
      Map<Integer,Integer> componentNodes = new HashMap<Integer,Integer>();
      for (int node : used) {
        int root = find(node);
        componentNodes.put(root, componentNodes.getOrDefault(root,0)+1);
        componentEntities.computeIfAbsent(root,k->new TreeSet<Integer>())
            .addAll(nodeEntities.get(node));
      }
      System.out.println("MESH_COMPONENT_COUNT=" + componentEntities.size());
      for (int root : new TreeSet<Integer>(componentEntities.keySet())) {
        System.out.println("MESH_COMPONENT root=" + root + " nodes="
            + componentNodes.get(root) + " entities=" + componentEntities.get(root));
      }
      int sharedNodes=0;
      for (Map.Entry<Integer,Set<Integer>> entry : nodeEntities.entrySet()) {
        if (entry.getValue().size()>1) sharedNodes++;
      }
      System.out.println("SHARED_NODE_COUNT=" + sharedNodes);

      Map<String,Set<Integer>> coordinateComponents =
          new HashMap<String,Set<Integer>>();
      Map<String,List<Integer>> coordinateNodes =
          new HashMap<String,List<Integer>>();
      for (int node : used) {
        String key = key(xyz,node);
        coordinateComponents.computeIfAbsent(key,k->new TreeSet<Integer>())
            .add(find(node));
        coordinateNodes.computeIfAbsent(key,k->new ArrayList<Integer>()).add(node);
      }
      int duplicateKeys=0;
      for (String coordinate : coordinateComponents.keySet()) {
        if (coordinateComponents.get(coordinate).size()>1) {
          duplicateKeys++;
          if (duplicateKeys<=30) {
            System.out.println("DUPLICATE_COORD components="
                + coordinateComponents.get(coordinate) + " nodes="
                + coordinateNodes.get(coordinate) + " xyz=" + coordinate);
          }
        }
      }
      System.out.println("DUPLICATE_COORD_COUNT=" + duplicateKeys);
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      try { ModelUtil.disconnect(); } catch (Exception ignored) {}
      System.exit(1);
    }
  }
}
