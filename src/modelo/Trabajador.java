package modelo;

import javafx.beans.property.*;

public class Trabajador {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty departamento;
    private final DoubleProperty valoracion;
    private final StringProperty nota;
    private final IntegerProperty idSupervisor;

    // Constructor completo
    public Trabajador(int id, String nombre, String departamento,
                      double valoracion, String nota, Integer idSupervisor) {

        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.departamento = new SimpleStringProperty(departamento);
        this.valoracion = new SimpleDoubleProperty(valoracion);
        this.nota = new SimpleStringProperty(nota);
        // Manejo de nulos para el ID del supervisor
        this.idSupervisor = new SimpleIntegerProperty(idSupervisor == null ? 0 : idSupervisor);
    }

    // --- GETTERS (Importantes para que JavaFX lea los datos) ---
    public int getId() { return id.get(); }
    public String getNombre() { return nombre.get(); }
    public String getDepartamento() { return departamento.get(); }
    public double getValoracion() { return valoracion.get(); }
    public String getNota() { return nota.get(); }
    public int getIdSupervisor() { return idSupervisor.get(); }

    // --- SETTERS ---
    public void setId(int id) { this.id.set(id); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setDepartamento(String departamento) { this.departamento.set(departamento); }
    public void setValoracion(double valoracion) { this.valoracion.set(valoracion); }
    public void setNota(String nota) { this.nota.set(nota); }
    public void setIdSupervisor(int idSupervisor) { this.idSupervisor.set(idSupervisor); }

    // --- PROPERTIES (Necesarios para el CellValueFactory de la TableView) ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty departamentoProperty() { return departamento; }
    public DoubleProperty valoracionProperty() { return valoracion; }
    public StringProperty notaProperty() { return nota; }
    public IntegerProperty idSupervisorProperty() { return idSupervisor; }

    @Override
    public String toString() {
        return nombre.get();
    }
}