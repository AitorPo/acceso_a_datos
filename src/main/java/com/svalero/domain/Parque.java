package com.svalero.domain;

public class Parque {
    public static Parque recoveredParque;
    private int idParque;
    private int idCiudad;
    private String nombreParque;

    public Parque(String nombreParque, int idCiudad){
        this.idCiudad = idCiudad;
        this.nombreParque = nombreParque;
    }
    public Parque(){}

    public int getIdParque() {
        return idParque;
    }

    public void setIdParque(int idParque) {
        this.idParque = idParque;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombreParque() {
        return nombreParque;
    }

    public void setNombreParque(String nombreParque) {
        this.nombreParque = nombreParque;
    }

    @Override
    public String toString() {
        return getNombreParque();
    }
}
