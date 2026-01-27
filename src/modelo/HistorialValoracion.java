package modelo;

import javafx.beans.property.*;

public class HistorialValoracion {

    private final StringProperty fecha;
    private final DoubleProperty valoracionAnterior;
    private final DoubleProperty valoracionNueva;
    private final StringProperty notaAnterior;
    private final StringProperty notaNueva;
    private final StringProperty supervisor;

    public HistorialValoracion(String fecha, double valoracionAnterior, double valoracionNueva,
                               String notaAnterior, String notaNueva, String supervisor) {

        this.fecha = new SimpleStringProperty(fecha);
        this.valoracionAnterior = new SimpleDoubleProperty(valoracionAnterior);
        this.valoracionNueva = new SimpleDoubleProperty(valoracionNueva);
        this.notaAnterior = new SimpleStringProperty(notaAnterior);
        this.notaNueva = new SimpleStringProperty(notaNueva);
        this.supervisor = new SimpleStringProperty(supervisor);
    }

    public StringProperty fechaProperty() { return fecha; }
    public DoubleProperty valoracionAnteriorProperty() { return valoracionAnterior; }
    public DoubleProperty valoracionNuevaProperty() { return valoracionNueva; }
    public StringProperty notaAnteriorProperty() { return notaAnterior; }
    public StringProperty notaNuevaProperty() { return notaNueva; }
    public StringProperty supervisorProperty() { return supervisor; }
}
