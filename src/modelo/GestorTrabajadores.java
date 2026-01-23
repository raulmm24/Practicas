package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorTrabajadores {

    private static final String url = "jdbc:mysql://localhost:3306/practicas";
    private static final String user = "root";
    private static final String password = "774411";

    private static final String creaValoracion =
            "CREATE TABLE valoracion ("
                    + "id_valoracion INT PRIMARY KEY AUTO_INCREMENT,"
                    + "valoracion DECIMAL(5,2) NOT NULL,"
                    + "nota_trabajador VARCHAR(255) NOT NULL,"
                    + "fecha DATE NOT NULL,"
                    + "id_trabajador INT NOT NULL,"
                    + "FOREIGN KEY (id_trabajador) REFERENCES trabajador(id_empleado)"
                    + ")";

    private static final String insertaValoracion =
            "INSERT INTO valoracion (valoracion, nota_trabajador, fecha, id_trabajador) VALUES "
                    + "(8.50, 'Muy buen desempeño general.', '2024-06-01', 1),"
                    + "(9.20, 'Excelente capacidad analítica.', '2024-06-03', 2),"
                    + "(7.30, 'Creativa pero debe mejorar la puntualidad.', '2024-06-05', 3),"
                    + "(8.90, 'Proactivo y con buena actitud.', '2024-06-07', 4)";

    public static void main(String[] args) {

        Connection conexion = null;
        Statement sentencia = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, user, password);
            sentencia = conexion.createStatement();

            sentencia.execute(creaValoracion);
            System.out.println("Tabla 'valoracion' creada correctamente.");

            sentencia.execute(insertaValoracion);
            System.out.println("Valoraciones insertadas correctamente.");

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try { if (sentencia != null) sentencia.close(); } catch (SQLException e) {}
            try { if (conexion != null) conexion.close(); } catch (SQLException e) {}
        }
    }
}