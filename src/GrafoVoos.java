package src;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GrafoVoos {
  protected static final String NEWLINE = System.getProperty("line.separator");

  protected Map<String, List<EdgeVoos>> graph;
  protected Set<String> vertices;
  protected int totalVertices;
  protected int totalEdges;

  public GrafoVoos() {
    graph = new HashMap<>();
    vertices = new HashSet<>();
    totalVertices = totalEdges = 0;
  }

  public void addEdge(String v, String w, long partida, long chegada, String cia, String numeroVoo, String aeronave) {
    EdgeVoos e = new EdgeVoos(v, w, partida, chegada, cia, numeroVoo, aeronave);
    addToList(v, e);
    if (!vertices.contains(v)) {
      vertices.add(v);
      totalVertices++;
    }
    if (!vertices.contains(w)) {
      vertices.add(w);
      totalVertices++;
    }
    totalEdges += 1;
  }

   public Iterable<EdgeVoos> getAdj(String v) {
    List<EdgeVoos> res = graph.get(v);
    if (res == null)
      res = new LinkedList<>();
    return res;
  }

  public int getTotalVerts() {
    return totalVertices;
  }

  public int getTotalEdges() {
    return totalEdges;
  }

  public Set<String> getVerts() {
    return vertices;
  }

  public Iterable<EdgeVoos> getEdges() {
    Set<EdgeVoos> ed = new HashSet<>();
    for (String v : getVerts().stream().sorted().toList()) {
      for (EdgeVoos e : getAdj(v)) {
        if (!ed.contains(e)) {
          ed.add(e);
        }
      }
    }
    return ed;
  }

  public String toDot() {
    StringBuilder sb = new StringBuilder();
    sb.append("graph {" + NEWLINE);
    sb.append("rankdir = LR;" + NEWLINE);
    sb.append("node [shape = circle];" + NEWLINE);
    for (EdgeVoos e : getEdges())
      sb.append(
          String.format("%s -> %s [label=\"%.2f / %.2f\" %s]", e.getV(), e.getW(), e.getCia(), e.getNumeroVoo(), e.getAeronave(), e.getChegada(), e.getPartida()) + NEWLINE);
    sb.append("}" + NEWLINE);
    return sb.toString();
  }

  
  protected List<EdgeVoos> addToList(String v, EdgeVoos e) {
    List<EdgeVoos> list = graph.get(v);
    if (list == null)
      list = new LinkedList<>();
    list.add(e);
    graph.put(v, list);
    return list;
  }

    
}
