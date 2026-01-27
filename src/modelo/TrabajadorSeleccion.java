package modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TrabajadorSeleccion extends Trabajador {

    private final BooleanProperty seleccionado = new SimpleBooleanProperty(false);

    private final double valoracionOriginal;
    private final String notaOriginal;

    public TrabajadorSeleccion(int id, String nombre, String departamento,
                               double valoracion, String nota, Integer idSupervisor) {

        super(id, nombre, departamento, valoracion, nota, idSupervisor);

        // Guardamos los valores originales para el historial
        this.valoracionOriginal = valoracion;
        this.notaOriginal = nota;
    }

    // Getters para el historial
    public double getValoracionOriginal() {
        return valoracionOriginal;
    }

    public String getNotaOriginal() {
        return notaOriginal;
    }

    // Checkbox
    public boolean isSeleccionado() { return seleccionado.get(); }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }
    public BooleanProperty seleccionadoProperty() { return seleccionado; }
}
