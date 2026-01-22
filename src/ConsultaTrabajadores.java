import java.sql.*;

public class ConsultaTrabajadores {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/practicas";
    private static final String user = "root";
    private static final String password = "774411";

    private static final String consultaTrabajadores =
            "SELECT * FROM departamento";

    public static void main(String[] args) {

        Connection conexion = null;
        Statement sentencia = null;
        ResultSet result = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, user, password);
            sentencia = conexion.createStatement();
            result = sentencia.executeQuery(consultaTrabajadores);

            while (result.next()) {
                System.out.printf("%2d %-15s %s\n",
                        result.getInt("id_dpto"),
                        result.getString("nombre"),
                        result.getString("localizacion"));
            }

            System.out.println("\nTabla 'departamento' consultada correctamente");

        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            try { if (result != null) result.close(); } catch (SQLException ex) {}
            try { if (sentencia != null) sentencia.close(); } catch (SQLException ex) {}
            try { if (conexion != null) conexion.close(); } catch (SQLException ex) {}
        }
    }
}
