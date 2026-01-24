package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    private static final String SQL_BASE =
            "SELECT t.id_empleado, t.nombre, d.nombre AS departamento, " +
                    "v.valoracion, v.nota_trabajador " +
                    "FROM trabajador t " +
                    "JOIN departamento d ON t.departamento = d.id_dpto " +
                    "JOIN valoracion v ON t.id_empleado = v.id_trabajador ";

    // ============================================================
    // 1. OBTENER TODOS LOS TRABAJADORES
    // ============================================================
    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> lista = new ArrayList<>();

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(SQL_BASE);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota_trabajador")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ============================================================
    // 2. BÚSQUEDA MULTICRITERIO
    // ============================================================
    public List<Trabajador> buscarTrabajadores(String filtro) {
        List<Trabajador> lista = new ArrayList<>();

        String sqlBuscar = SQL_BASE +
                "WHERE t.nombre LIKE ? " +
                "OR d.nombre LIKE ? " +
                "OR v.valoracion LIKE ? " +
                "OR v.nota_trabajador LIKE ?";

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(sqlBuscar)) {

            String patron = "%" + filtro + "%";

            stmt.setString(1, patron);
            stmt.setString(2, patron);
            stmt.setString(3, patron);
            stmt.setString(4, patron);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota_trabajador")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ============================================================
    // 3. ACTUALIZAR VALORACIÓN Y NOTA
    // ============================================================
    public boolean actualizarValoracionYNota(int idTrabajador, double valoracion, String nota) {
        String sql = "UPDATE valoracion SET valoracion = ?, nota_trabajador = ? WHERE id_trabajador = ?";

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, valoracion);
            stmt.setString(2, nota);
            stmt.setInt(3, idTrabajador);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
