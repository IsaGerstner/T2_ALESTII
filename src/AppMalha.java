package src;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class AppMalha {

    static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static String hora(double minutos) {
        return LocalDateTime.ofEpochSecond((long) minutos * 60, 0, ZoneOffset.UTC).format(FMT);
    }
    static String duracao(long minutos) {
        return (minutos / 60) + "h" + String.format("%02d", minutos % 60) + "min";
    }
    static String nome(GrafoVoos g, String icao) {
        Aeroporto a = g.getAeroporto(icao);
        return a == null ? icao : a.getNome();
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        GrafoVoos g = LeitorDados.carregar("dados");
        System.out.println("Dados carregados: " + g.getTotalVerts() + " aeroportos, "
                           + g.getTotalEdges() + " voos.");

        List<String> hubsLista = g.calcularHubs(5);
        Set<String> hubs = new HashSet<>(hubsLista);
        System.out.println("\n5 principais hubs do país:");
        for (String h : hubsLista)
            System.out.println("  " + h + " - " + nome(g, h));

        System.out.print("\nAeroporto de ORIGEM (ICAO): ");
        String origem = sc.nextLine().trim().toUpperCase();
        System.out.print("Aeroporto de DESTINO (ICAO): ");
        String destino = sc.nextLine().trim().toUpperCase();
        System.out.print("Data e hora de partida (dd/MM/yyyy HH:mm): ");
        String dataStr = sc.nextLine().trim();

        if (g.getAeroporto(origem) == null || g.getAeroporto(destino) == null) {
            System.out.println("ERRO: origem ou destino não existe na base.");
            return;
        }
        long inicio;
        try {
            inicio = LeitorDados.paraMinutos(dataStr);
        } catch (Exception e) {
            System.out.println("ERRO: data inválida. Use o formato dd/MM/yyyy HH:mm.");
            return;
        }

        System.out.print("\nDeseja fechar um dos hubs? (s/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.print("Qual hub fechar " + hubsLista + "? ");
            String fechar = sc.nextLine().trim().toUpperCase();
            if (hubs.contains(fechar)) {
                g.removerAeroporto(fechar);
                hubs.remove(fechar);
                System.out.println(">> Hub " + fechar + " fechado.");
            } else {
                System.out.println(">> " + fechar + " não é um dos hubs; nada fechado.");
            }
        }

        DijkstraVoos dij = new DijkstraVoos(g, origem, inicio, hubs);

        
        System.out.println("\n===================== ROTA =====================");
        System.out.println("De " + nome(g, origem) + " para " + nome(g, destino));
        System.out.println("Partida desejada: " + hora(inicio));
        System.out.println("------------------------------------------------");

        if (!dij.hasPathTo(destino)) {
            System.out.println("Nenhuma rota disponível para esse destino.");
            return;
        }

        List<EdgeVoos> rota = new ArrayList<>();
        for (EdgeVoos e : dij.pathTo(destino)) rota.add(e);

        EdgeVoos anterior = null;
        for (EdgeVoos e : rota) {
            if (anterior != null) {
                long espera = e.getPartida() - anterior.getChegada();
                System.out.println("   (conexão em " + nome(g, e.getV())
                                   + ": espera de " + duracao(espera) + ")");
            }
            System.out.println(nome(g, e.getV()) + " [" + e.getV() + "]  " + hora(e.getPartida()));
            System.out.println("   voo " + e.getCia() + " " + e.getNumeroVoo() + " (" + e.getAeronave() + ")");
            System.out.println("-> " + nome(g, e.getW()) + " [" + e.getW() + "]  " + hora(e.getChegada()));
            anterior = e;
        }

        System.out.println("------------------------------------------------");
        long tempoVoo = 0;
        for (EdgeVoos e : rota)
            tempoVoo += (e.getChegada() - e.getPartida());
        System.out.println("Chegada final: " + hora(dij.distTo(destino)));
        System.out.println("Tempo total de voo: " + duracao(tempoVoo));
    }
}