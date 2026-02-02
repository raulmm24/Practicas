package modelo;

public class Sesion {
    private static int idUsuarioLogueado;

    public static void setIdUsuarioLogueado(int id) { idUsuarioLogueado = id; }
    public static int getIdUsuarioLogueado() { return idUsuarioLogueado; }
}