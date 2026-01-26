package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    // ============================================================
    // CONSULTA BASE (LEFT JOIN para que no desaparezca nadie)
    // ============================================================
    private static final String SQL_BASE =
            "SELECT t.id_empleado, t.nombre, " +
                    "IFNULL(d.nombre, 'Sin departamento') AS departamento, " +
                    "IFNULL(v.valoracion, 0) AS valoracion, " +
                    "IFNULL(v.nota_trabajador, '') AS nota_trabajador " +
                    "FROM trabajador t " +
                    "LEFT JOIN departamento d ON t.departamento = d.id_dpto " +
                    "LEFT JOIN valoracion v ON t.id_empleado = v.id_trabajador ";

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

        } catch (SQLException e) {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ============================================================
    // 3. ACTUALIZAR VALORACIÓN Y NOTA (con inserción automática)
    // ============================================================
    public boolean actualizarValoracionYNota(int idTrabajador, double valoracion, String nota) {

        String sqlUpdate = "UPDATE valoracion SET valoracion = ?, nota_trabajador = ? WHERE id_trabajador = ?";
        String sqlInsert = "INSERT INTO valoracion (id_trabajador, valoracion, nota_trabajador) VALUES (?, ?, ?)";

        try (Connection conn = new ConexionMySQL().conexionBBDD()) {

            // Intentar actualizar primero
            PreparedStatement stmt = conn.prepareStatement(sqlUpdate);
            stmt.setDouble(1, valoracion);
            stmt.setString(2, nota);
            stmt.setInt(3, idTrabajador);

            int filas = stmt.executeUpdate();

            // Si no existía la valoración, insertarla
            if (filas == 0) {
                PreparedStatement insert = conn.prepareStatement(sqlInsert);
                insert.setInt(1, idTrabajador);
                insert.setDouble(2, valoracion);
                insert.setString(3, nota);
                insert.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
