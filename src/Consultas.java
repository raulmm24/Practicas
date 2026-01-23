import java.sql.*;

public class Consultas {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/practicas";
    private static final String user = "root";
    private static final String password = "774411";

    private static final String consulta =
            "SELECT t.id_empleado, t.nombre, d.nombre AS nombre_departamento, " +
                    "t.fecha_alta, v.valoracion, v.nota_trabajador " +
                    "FROM trabajador t " +
                    "JOIN departamento d ON t.departamento = d.id_dpto " +
                    "JOIN valoracion v ON t.id_empleado = v.id_trabajador";

    public static void main(String[] args) {

        Connection conexion = null;
        Statement sentencia = null;
        ResultSet result = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, user, password);
            sentencia = conexion.createStatement();
            result = sentencia.executeQuery(consulta);

            System.out.printf("%-5s %-18s %-18s %-12s %-8s %-60s%n",
                    "ID", "Nombre", "Departamento", "Fecha", "Nota", "Nota_Trabajador");

            System.out.println("---------------------------------------------------------------------------------------------------------------");

            while (result.next()) {
                System.out.printf("%-5d %-18s %-18s %-12s %-8.2f %-60s%n",
                        result.getInt("id_empleado"),
                        result.getString("nombre"),
                        result.getString("nombre_departamento"),
                        result.getDate("fecha_alta").toString(),
                        result.getDouble("valoracion"),
                        result.getString("nota_trabajador"));
                System.out.println("---------------------------------------------------------------------------------------------------------------");
            }


            System.out.println("\nTabla 'trabajador' consultada correctamente");

        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            try { if (result != null) result.close(); } catch (SQLException ex) {}
            try { if (sentencia != null) sentencia.close(); } catch (SQLException ex) {}
            try { if (conexion != null) conexion.close(); } catch (SQLException ex) {}
        }
    }
}
