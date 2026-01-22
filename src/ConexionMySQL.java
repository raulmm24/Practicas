import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String BBDD = "jdbc:mysql://localhost:3306/?user=root";
    private static final String DB_NAME = "practicas";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "774411";

    public static void main(String [] args) {}

    public Connection conexionBBDD() {
        Connection conexion = null;
        try {
            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(BBDD + DB_NAME, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Se ha producido un error en Driver.\n" + e);
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al conectar la base de datos.\n" + e);
        }
        return conexion;
    }

    public void cerrarConexion(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al cerrar la conexion con la base de datos." + e);
        }
    }
}