package modelo;

import java.sql.Date;

public class Objetivos {
    private final int id;
    private final String descripcion;
    private final String departamento;
    private final double progreso;
    private final String estado;
    private final Date fecha_limite;

    public Objetivos(int id, String descripcion, String departamento, double progreso, String estado, Date fecha_limite) {
        this.id = id;
        this.descripcion = descripcion;
        this.departamento = departamento;
        this.progreso = progreso;
        this.estado = estado;
        this.fecha_limite = fecha_limite;
    }

    // Getters
    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public String getDepartamento() { return departamento; }
    public double getProgreso() { return progreso; }
    public String getEstado() { return estado; }
    public Date getFecha_limite() { return fecha_limite; }
}