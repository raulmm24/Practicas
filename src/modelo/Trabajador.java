package modelo;

import javafx.beans.property.*;

public class Trabajador {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty departamento;
    private final DoubleProperty valoracion;
    private final StringProperty nota;
    private final ObjectProperty<Integer> idSupervisor;

    public Trabajador(int id, String nombre, String departamento,
                      double valoracion, String nota) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.departamento = new SimpleStringProperty(departamento);
        this.valoracion = new SimpleDoubleProperty(valoracion);
        this.nota = new SimpleStringProperty(nota);
        this.idSupervisor = new SimpleObjectProperty<>(null);
    }

    public Trabajador(int id, String nombre, String departamento,
                      double valoracion, String nota, Integer idSupervisor) {
        this(id, nombre, departamento, valoracion, nota);
        this.idSupervisor.set(idSupervisor);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getNombre() { return nombre.get(); }
    public String getDepartamento() { return departamento.get(); }
    public double getValoracion() { return valoracion.get(); }
    public String getNota() { return nota.get(); }
    public Integer getIdSupervisor() { return idSupervisor.get(); }

    // Setters
    public void setValoracion(double valoracion) { this.valoracion.set(valoracion); }
    public void setNota(String nota) { this.nota.set(nota); }
    public void setIdSupervisor(Integer idSupervisor) { this.idSupervisor.set(idSupervisor); }

    // Properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty departamentoProperty() { return departamento; }
    public DoubleProperty valoracionProperty() { return valoracion; }
    public StringProperty notaProperty() { return nota; }
    public ObjectProperty<Integer> idSupervisorProperty() { return idSupervisor; }
}
