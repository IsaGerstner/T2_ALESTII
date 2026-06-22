package src;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// guarda a lista de todos os voos que partem dele; guarda o mapa de aeroportos e calcula os 5 hubs e fecha algum hub

public class GrafoVoos {
  protected static final String NEWLINE = System.getProperty("line.separator");

  protected Map<String, List<EdgeVoos>> graph; // lista de voos
  protected Set<String> vertices;
  protected int totalVertices;
  protected int totalEdges;
  protected Map<String, Aeroporto> aeroportos; // mapa dos aeroportos

  public GrafoVoos() {
    graph = new HashMap<>();
    vertices = new HashSet<>();
    aeroportos = new HashMap<>();
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

public void addAeroporto(Aeroporto a) { 
    aeroportos.put(a.getIcao(), a); 
}

public Aeroporto getAeroporto(String icao) { 
    return aeroportos.get(icao); 
}

public Map<String, Aeroporto> getAeroportos() { 
    return aeroportos; 
}


// REVISAR - calcula hubs (5 aeroportos com mais conexões)
public List<String> calcularHubs(int n) {
    Map<String, Integer> grau = new HashMap<>();
    // percorre TODAS as arestas uma vez:
    for (String v : vertices)
        for (EdgeVoos e : getAdj(v)) {
            grau.merge(e.getV(), 1, Integer::sum); // +1 no grau de SAÍDA da origem
            grau.merge(e.getW(), 1, Integer::sum); // +1 no grau de ENTRADA do destino
        }
    // ordena por grau (maior primeiro), só nacionais, pega os n primeiros:
    return grau.entrySet().stream()
        .filter(en -> { Aeroporto a = getAeroporto(en.getKey()); return a != null && a.isNacional(); })
        .sorted((a, b) -> b.getValue() - a.getValue())
        .limit(n)
        .map(Map.Entry::getKey)
        .toList();
}

//REVISAR - fechar um hub
public void removerAeroporto(String icao) {
    vertices.remove(icao);
    graph.remove(icao);                              // tira os voos que SAEM dele
    for (List<EdgeVoos> lista : graph.values())
        lista.removeIf(e -> e.getW().equals(icao));  // tira os voos que CHEGAM nele
}

    
}
