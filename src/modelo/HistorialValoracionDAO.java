package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialValoracionDAO {

    private final Connection conn;

    public HistorialValoracionDAO() {
        conn = new ConexionMySQL().conexionBBDD();
        if (conn == null) {
            System.err.println("ERROR: No se pudo conectar a la base de datos.");
        }
    }

    public List<HistorialValoracion> obtenerHistorialPorTrabajador(int idTrabajador) {
        List<HistorialValoracion> lista = new ArrayList<>();

        if (conn == null) return lista;

        String sql = "SELECT fecha, valoracion_anterior, valoracion_nueva, nota_anterior, nota_nueva " +
                "FROM historial_valoracion WHERE id_trabajador = ? ORDER BY fecha DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new HistorialValoracion(
                        rs.getDate("fecha").toString(),
                        rs.getDouble("valoracion_anterior"),
                        rs.getDouble("valoracion_nueva"),
                        rs.getString("nota_anterior"),
                        rs.getString("nota_nueva")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
