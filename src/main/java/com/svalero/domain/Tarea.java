package com.svalero.domain;


public class Tarea {
    private int idTarea;
    private int idParque;
    private String nombre;
    private String descripcion;

    public Tarea(int idParque, String nombre, String descripcion){
        this.idParque = idParque;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Tarea(){}

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdParque() {
        return idParque;
    }

    public void setIdParque(int idParque) {
        this.idParque = idParque;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return getIdTarea() + " " + getIdParque() + " " + getNombre() + " " + getDescripcion();
    }
}
