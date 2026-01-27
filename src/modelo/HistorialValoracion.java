package modelo;

import javafx.beans.property.*;

public class HistorialValoracion {

    private final StringProperty fecha;
    private final DoubleProperty valoracionAnterior;
    private final DoubleProperty valoracionNueva;
    private final StringProperty notaAnterior;
    private final StringProperty notaNueva;

    public HistorialValoracion(String fecha, double valAnt, double valNueva,
                               String notaAnt, String notaNueva) {

        this.fecha = new SimpleStringProperty(fecha);
        this.valoracionAnterior = new SimpleDoubleProperty(valAnt);
        this.valoracionNueva = new SimpleDoubleProperty(valNueva);
        this.notaAnterior = new SimpleStringProperty(notaAnt);
        this.notaNueva = new SimpleStringProperty(notaNueva);
    }

    public StringProperty fechaProperty() { return fecha; }
    public DoubleProperty valoracionAnteriorProperty() { return valoracionAnterior; }
    public DoubleProperty valoracionNuevaProperty() { return valoracionNueva; }
    public StringProperty notaAnteriorProperty() { return notaAnterior; }
    public StringProperty notaNuevaProperty() { return notaNueva; }
}
