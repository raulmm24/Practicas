package modelo;

import javafx.beans.property.*;
import java.sql.Date;

public class Objetivos {
    // Definición de Properties para vinculación con TableView
    private final IntegerProperty id;
    private final StringProperty descripcion;
    private final StringProperty departamento;
    private final DoubleProperty progreso;
    private final StringProperty estado;
    private final ObjectProperty<Date> fecha_limite;

    // Constructor completo para usar con la Base de Datos
    public Objetivos(int id, String descripcion, String departamento, double progreso, String estado, Date fecha_limite) {
        this.id = new SimpleIntegerProperty(id);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.departamento = new SimpleStringProperty(departamento);
        this.progreso = new SimpleDoubleProperty(progreso);
        this.estado = new SimpleStringProperty(estado);
        this.fecha_limite = new SimpleObjectProperty<>(fecha_limite);
    }

    // Constructor simplificado (basado en tu primera estructura de Objetivo)
    public Objetivos(String descripcion, String estado) {
        this(0, descripcion, "", 0.0, estado, null);
    }

    // Métodos Property (Obligatorios para colMeta.setCellValueFactory)
    public IntegerProperty idProperty() { return id; }
    public StringProperty descripcionProperty() { return descripcion; }
    public StringProperty departamentoProperty() { return departamento; }
    public DoubleProperty progresoProperty() { return progreso; }
    public StringProperty estadoProperty() { return estado; }
    public ObjectProperty<Date> fechaLimiteProperty() { return fecha_limite; }

    // Getters estándar (Para lógica interna o DAOs)
    public int getId() { return id.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public String getDepartamento() { return departamento.get(); }
    public double getProgreso() { return progreso.get(); }
    public String getEstado() { return estado.get(); }
    public Date getFecha_limite() { return fecha_limite.get(); }
}