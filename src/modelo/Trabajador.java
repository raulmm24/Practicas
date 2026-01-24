package modelo;

public class Trabajador {

    private final int id;
    private final String nombre;
    private final String departamento;
    private final String fechaAlta;
    private final double valoracion;
    private final String nota;

    public Trabajador(int id, String nombre, String departamento, String fechaAlta, double valoracion, String nota) {
        this.id = id;
        this.nombre = nombre;
        this.departamento = departamento;
        this.fechaAlta = fechaAlta;
        this.valoracion = valoracion;
        this.nota = nota;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDepartamento() { return departamento; }
    public String getFechaAlta() { return fechaAlta; }
    public double getValoracion() { return valoracion; }
    public String getNota() { return nota; }
}
