package modelo;

public class HistorialValoracion {

    private final String fecha;
    private final double valoracionAnterior;
    private final double valoracionNueva;
    private final String notaAnterior;
    private final String notaNueva;

    public HistorialValoracion(String fecha, double valoracionAnterior, double valoracionNueva,
                               String notaAnterior, String notaNueva) {
        this.fecha = fecha;
        this.valoracionAnterior = valoracionAnterior;
        this.valoracionNueva = valoracionNueva;
        this.notaAnterior = notaAnterior;
        this.notaNueva = notaNueva;
    }

    public String getFecha() {
        return fecha;
    }

    public double getValoracionAnterior() {
        return valoracionAnterior;
    }

    public double getValoracionNueva() {
        return valoracionNueva;
    }

    public String getNotaAnterior() {
        return notaAnterior;
    }

    public String getNotaNueva() {
        return notaNueva;
    }
}
