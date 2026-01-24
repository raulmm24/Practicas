package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {

    private static final String URL = "jdbc:mysql://localhost:3306/practicas";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "774411";

    public Connection conexionBBDD() {
        try {
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al conectar la base de datos.\n" + e);
            return null;
        }
    }

    public void cerrarConexion(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al cerrar la conexion con la base de datos.\n" + e);
        }
    }
}
