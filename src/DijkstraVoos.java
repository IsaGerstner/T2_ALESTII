package src;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

// ACHA A MELHOR ROTA

public class DijkstraVoos {

    private Map<String, Double> distTo;
    private Map<String, EdgeVoos> edgeTo;
    private IndexMinHeap<String, Double> pq;

public DijkstraVoos(GrafoVoos g, String s, long horarioInicial, Set<String> hubs) {
    distTo = new HashMap<>(); // anota o horário mais cedo de chegada
    edgeTo = new HashMap<>(); // truque pra reconstruir a rota no fim — partindo do destino, ela volta de voo em voo até a origem.
    pq = new IndexMinHeap<>();

    for (String v : g.getVerts())
        distTo.put(v, Double.POSITIVE_INFINITY);
    distTo.put(s, (double) horarioInicial);   // origem começa no horário escolhido, não em zero
    dijkstra(g, s, horarioInicial, hubs);
}

private void dijkstra(GrafoVoos g, String s, long horarioInicial, Set<String> hubs) { // esperas em hub são de 60 min, sem ser hub 45 min
    pq.insert(s, distTo.get(s));
    while (!pq.isEmpty()) {
        String v = pq.delMin();
        for (EdgeVoos e : g.getAdj(v)) {
            double prontoEm = distTo.get(v) + deltaT(v, s, hubs); // deltaT responde "quantos minutos de espera neste aeroporto?"
            if (e.getPartida() >= prontoEm)
                relax(e);
        }
    }
}

private int deltaT(String aeroporto, String origem, Set<String> hubs) {
    if (aeroporto.equals(origem)) {
        return 0; // quando começa a viagem tempo = 0
    }  
    if (hubs.contains(aeroporto)) {
        return 60;  // se for hub espera 60 min
    }
    return 45;     // se não espera 45 min                            
}


    private void relax(EdgeVoos e) {
        String v = e.getV();
        String w = e.getW();
        double dist = e.getChegada(); 
        
        if(distTo.get(w) > dist) {
            distTo.put(w, dist);
            edgeTo.put(w, e);
            if(pq.contains(w))
                pq.decreaseValue(w, dist);
            else
                pq.insert(w, dist);
        }
    }

    public double distTo(String v) {
        return distTo.get(v);
    }

    public boolean hasPathTo(String v) {
        return edgeTo.get(v) != null;
    }

    public Iterable<EdgeVoos> pathTo(String v) {
        LinkedList<EdgeVoos> path = new LinkedList<>();
        EdgeVoos e = edgeTo.get(v);
        // Enquanto não chegar na primeira aresta...
        while(e != null) {
            // Adiciona no início, pois o caminho é
            // percorrido ao contrário (do fim para o início)
            path.addFirst(e);
            // A próxima aresta é aquela que vem de V (início desta aresta)
            // (lembrando: estamos percorrendo ao CONTRÁRIO)
            e = edgeTo.get(e.getV());
        }
        return path;
    }

}




    

