package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LeitorDados {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static long paraMinutos(String dataHora) {
        LocalDateTime dt = LocalDateTime.parse(dataHora.trim(), FMT);
        return dt.toEpochSecond(ZoneOffset.UTC) / 60L;
    }

    
    public static GrafoVoos carregar(String pastaDados) throws IOException {
        GrafoVoos g = new GrafoVoos();
        carregarAerodromos(Path.of(pastaDados, "aerodromos.csv"), g);
        carregarVoos(Path.of(pastaDados, "voos_mar2026.csv"), g);
        return g;
    }

    private static void carregarAerodromos(Path arquivo, GrafoVoos g) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            br.readLine(); 
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] c = linha.split(";", -1);  
                if (c.length < 9) continue;
                String icao = tirarBom(c[0]).trim();
                String nome = c[2].trim();
                String pais = c[5].trim();
                double lat = paraDouble(c[7]);
                double lon = paraDouble(c[8]);
                g.addAeroporto(new Aeroporto(icao, nome, pais, lat, lon));
            }
        }
    }

    private static void carregarVoos(Path arquivo, GrafoVoos g) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            br.readLine(); 
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                List<String> c = parseCsv(linha);
                if (c.size() < 11) continue;
               
                long chegada = paraMinutos(c.get(1));
                long partida = paraMinutos(c.get(2));
                String numeroVoo = c.get(5).trim();
                String cia       = c.get(7).trim();
                String aeronave  = c.get(8).trim();
                String destino   = c.get(9).trim();
                String origem    = c.get(10).trim();
                if (origem.isEmpty() || destino.isEmpty()) continue;
                g.addEdge(origem, destino, partida, chegada, cia, numeroVoo, aeronave);
            }
        }
    }

    private static String tirarBom(String s) {
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') return s.substring(1);
        return s;
    }

    private static double paraDouble(String s) {
        s = s.trim().replace(',', '.');
        if (s.isEmpty()) return 0.0;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private static List<String> parseCsv(String linha) {
        List<String> campos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean dentroAspas = false;
        for (int i = 0; i < linha.length(); i++) {
            char ch = linha.charAt(i);
            if (ch == '"') {
                if (dentroAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    atual.append('"'); i++;
                } else {
                    dentroAspas = !dentroAspas;
                }
            } else if (ch == ',' && !dentroAspas) {
                campos.add(atual.toString()); atual.setLength(0);
            } else {
                atual.append(ch);
            }
        }
        campos.add(atual.toString());
        return campos;
    }
}
