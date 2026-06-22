package src;

//GUARDA AS INFORMAÇÕES DE UM AEROPORTO

public class Aeroporto {
  private String icao, nome, pais; //ICAO = código do aeroporto
  private double latitude, longitude;

  public Aeroporto(String icao, String nome, String pais, double latitude, double longitude) {
    this.icao = icao; 
    this.nome = nome; 
    this.pais = pais;
    this.latitude = latitude; 
    this.longitude = longitude;
  }

  public String getIcao() { 
    return icao; 
  }

  public String getNome() {
    return nome; 
  }

  public String getPais() { 
    return pais; 
  }

  public double getLatitude() { 
    return latitude;   
  }

  public double getLongitude() { 
    return longitude; 
  }

  public boolean isNacional() { 
    return "BRASIL".equals(pais); 
  }

}
