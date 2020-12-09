package com.svalero.domain;

public class Ciudad {
    private int idCiudad;
    private String nombreCiudad;
    private String comunidadAutonoma;

    public Ciudad(int idCiudad, String nombreCiudad, String comunidadAutonoma){
        this.idCiudad = idCiudad;
        this.nombreCiudad = nombreCiudad;
        this.comunidadAutonoma = comunidadAutonoma;
    }

    public Ciudad(){}

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

    public String getComunidadAutonoma() {
        return comunidadAutonoma;
    }

    public void setComunidadAutonoma(String comunidadAutonoma) {
        this.comunidadAutonoma = comunidadAutonoma;
    }

    @Override
    public String toString() {
        return getNombreCiudad();
    }

    public String showNombre(){
        return getNombreCiudad();
    }
}
