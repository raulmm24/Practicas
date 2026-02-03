package modelo;

import javafx.beans.property.*;

public class TrabajadorSeleccion extends Trabajador {

    private final BooleanProperty seleccionado = new SimpleBooleanProperty(false);
    private double valoracionOriginal;
    private String notaOriginal;
    private final StringProperty supervisor = new SimpleStringProperty();

    public TrabajadorSeleccion(int id, String nombre, String departamento,
                               double valoracion, String nota, Integer idSupervisor) {
        // Llamada al constructor de la clase padre (Trabajador)
        super(id, nombre, departamento, valoracion, nota, idSupervisor);

        this.valoracionOriginal = valoracion;
        this.notaOriginal = (nota == null) ? "" : nota; // Evita nulos en la interfaz
    }

    // Getters y Setters necesarios para la vinculación de datos
    public StringProperty supervisorProperty() { return supervisor; }
    public String getSupervisor() { return supervisor.get(); }
    public void setSupervisor(String s) { supervisor.set(s); }
    public boolean isSeleccionado() { return seleccionado.get(); }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }
    public BooleanProperty seleccionadoProperty() { return seleccionado; }
}