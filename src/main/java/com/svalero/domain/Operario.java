package com.svalero.domain;

public class Operario {
    private int idOperario;
    private String nombre;
    private String password;

    public Operario(String nombre, String password){
        this.nombre = nombre;
        this.password = password;
    }

    public Operario(){}

    public int getIdOperario() {
        return idOperario;
    }

    public void setIdOperario(int idOperario) {
        this.idOperario = idOperario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return getIdOperario() + " " + getNombre() + " " + getPassword();
    }
}
