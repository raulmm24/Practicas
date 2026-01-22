
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorTrabajadores {

    private static final String url = "jdbc:mysql://localhost:3306/practicas";
    private static final String user = "root";
    private static final String password = "774411";

    private static final String creaDepartamento =
            "CREATE TABLE departamento ("
                    + "id_dpto INT PRIMARY KEY AUTO_INCREMENT,"
                    + "nombre VARCHAR(100) NOT NULL,"
                    + "localizacion VARCHAR(100) NOT NULL"
                    + ")";

    private static final String creaTrabajador =
            "CREATE TABLE trabajador ("
                    + "id_empleado INT PRIMARY KEY AUTO_INCREMENT,"
                    +  "nombre VARCHAR(100) NOT NULL,"
                    +  "departamento INT,"
                    +  "fecha_alta DATE NOT NULL"
                    + ")";

    private static final String insertaDepartamento =
            "INSERT INTO departamento (nombre, localizacion) VALUES "
                    + "('Programación', 'Ecija'),"
                    + "('Finanzas', 'Guadaira'),"
                    + "('Marketing', 'Ecija'),"
                    + "('Informática', 'Guadaira')";

    public static void main(String[] args) {

        Connection conexion = null;
        Statement sentencia = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, user, password);
            sentencia = conexion.createStatement();
            sentencia.execute(creaDepartamento);
            System.out.println("Tabla 'departamento' creada correctamente");
            sentencia.execute(insertaDepartamento);
            System.out.println("Nuevos departamentos insertados correctamente");
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try { if (sentencia != null) sentencia.close(); } catch (SQLException e) {}
            try { if (conexion != null) conexion.close(); } catch (SQLException e) {}
        }
    }
}
