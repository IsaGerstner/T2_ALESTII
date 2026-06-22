package src;

// GUARDA AS INFORMAÇÕES DE UM VOO

public class EdgeVoos extends Edge {
  private long partida;     
  private long chegada;     
  private String cia;       
  private String numeroVoo; 
  private String aeronave;  

  public EdgeVoos(String v, String w, long partida, long chegada, String cia, String numeroVoo, String aeronave) {
    
    super(v, w, chegada - partida);
    this.partida = partida;
    this.chegada = chegada;
    this.cia = cia;
    this.numeroVoo = numeroVoo;
    this.aeronave = aeronave;
  }

  public long getPartida() {
     return partida;
  }

  public long getChegada() {
     return chegada; 
  }
  
  public String getCia() { 
    return cia; 
  }

  public String getNumeroVoo() { 
    return numeroVoo; 
  }

  public String getAeronave() { 
    return aeronave;
  }

  @Override
  public String toString() {
    return String.format("%s-%s [%s %s, %s] part=%d cheg=%d",
        getV(), getW(), cia, numeroVoo, aeronave, partida, chegada);
  }
}