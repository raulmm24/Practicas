package modelo;

import javafx.beans.property.*;

public class TrabajadorSeleccion extends Trabajador {

    private final BooleanProperty seleccionado = new SimpleBooleanProperty(false);

    private double valoracionOriginal;
    private String notaOriginal;

    private final StringProperty supervisor = new SimpleStringProperty();

    public TrabajadorSeleccion(int id, String nombre, String departamento,
                               double valoracion, String nota, Integer idSupervisor) {

        super(id, nombre, departamento, valoracion, nota, idSupervisor);

        this.valoracionOriginal = valoracion;
        this.notaOriginal = nota;
    }

    public TrabajadorSeleccion(int id, String nombre, String departamento,
                               double valoracion, String nota, Integer idSupervisor,
                               boolean seleccionado) {
        this(id, nombre, departamento, valoracion, nota, idSupervisor);
        this.seleccionado.set(seleccionado);
    }

    public StringProperty supervisorProperty() { return supervisor; }
    public String getSupervisor() { return supervisor.get(); }
    public void setSupervisor(String s) { supervisor.set(s); }

    public double getValoracionOriginal() { return valoracionOriginal; }
    public void setValoracionOriginal(double valoracionOriginal) { this.valoracionOriginal = valoracionOriginal; }

    public String getNotaOriginal() { return notaOriginal; }
    public void setNotaOriginal(String notaOriginal) { this.notaOriginal = notaOriginal; }

    public boolean isSeleccionado() { return seleccionado.get(); }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }
    public BooleanProperty seleccionadoProperty() { return seleccionado; }
}
