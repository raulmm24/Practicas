package modelo;

public class Usuario {

    private final int id;
    private final String username;
    private final String rol;
    private final Integer idTrabajador;

    public Usuario(int id, String username, String rol, Integer idTrabajador) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.idTrabajador = idTrabajador;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRol() { return rol; }
    public Integer getIdTrabajador() { return idTrabajador; }
}
