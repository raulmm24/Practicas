package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorTrabajadores {

    private static final String url = "jdbc:mysql://localhost:3306/practicas";
    private static final String user = "root";
    private static final String password = "774411";

    private static final String creaTrabajador =
            "CREATE TABLE IF NOT EXISTS trabajador (" +
                    "id_empleado INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "departamento INT, " +
                    "fecha_alta DATE NOT NULL" +
                    ");";

    private static final String insertaTrabajador =
            "INSERT INTO trabajador (nombre, departamento, fecha_alta) VALUES " +
                    "('Ana López', 1, '2024-01-10'), " +
                    "('Carlos Pérez', 2, '2024-02-15'), " +
                    "('María Gómez', 3, '2024-03-01'), " +
                    "('Javier Ruiz', 1, '2024-04-20'), " +
                    "('Lucía Fernández', 4, '2024-05-05');";

    public static void main(String[] args) {

        Connection conexion = null;
        Statement sentencia = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, user, password);
            sentencia = conexion.createStatement();

            sentencia.execute(creaTrabajador);
            System.out.println("Tabla 'trabajador' creada correctamente.");

            sentencia.execute(insertaTrabajador);
            System.out.println("Trabajadores insertados correctamente.");

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try { if (sentencia != null) sentencia.close(); } catch (SQLException e) {}
            try { if (conexion != null) conexion.close(); } catch (SQLException e) {}
        }
    }
}
