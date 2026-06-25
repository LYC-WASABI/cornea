public class probe_comsol_methodexec_heap {
  public static void main(String[] args) {
    System.out.printf("METHOD_EXEC_MAX_HEAP_GB=%.3f%n",
        Runtime.getRuntime().maxMemory()/1024.0/1024.0/1024.0);
  }
}
