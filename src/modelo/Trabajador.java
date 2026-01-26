package modelo;

public class Trabajador {

    private final int id;
    private final String nombre;
    private final String departamento;
    private double valoracion;
    private String nota;
    private Integer idSupervisor; // puede ser null

    public Trabajador(int id, String nombre, String departamento,
                      double valoracion, String nota) {
        this.id = id;
        this.nombre = nombre;
        this.departamento = departamento;
        this.valoracion = valoracion;
        this.nota = nota;
    }

    public Trabajador(int id, String nombre, String departamento,
                      double valoracion, String nota, Integer idSupervisor) {
        this(id, nombre, departamento, valoracion, nota);
        this.idSupervisor = idSupervisor;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDepartamento() { return departamento; }
    public double getValoracion() { return valoracion; }
    public String getNota() { return nota; }
    public Integer getIdSupervisor() { return idSupervisor; }

    public void setValoracion(double valoracion) { this.valoracion = valoracion; }
    public void setNota(String nota) { this.nota = nota; }
    public void setIdSupervisor(Integer idSupervisor) { this.idSupervisor = idSupervisor; }
}
