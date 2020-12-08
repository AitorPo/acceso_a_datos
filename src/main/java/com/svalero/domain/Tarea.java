package com.svalero.domain;


public class Tarea {
    private int idTarea;
    private int idParque;
    private String descripcion;

    public Tarea(int idTarea, int idParque, String descripcion){
        this.idTarea = idTarea;
        this.idParque = idParque;
        this.descripcion = descripcion;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

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
}
