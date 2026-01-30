package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    private final Connection conn;

    public TrabajadorDAO() {
        conn = new ConexionMySQL().conexionBBDD();
    }

    // 1. Obtener todos los trabajadores
    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> lista = new ArrayList<>();

        if (conn == null) return lista;

        String sql =
                "SELECT t.id_trabajador, t.nombre, d.nombre AS departamento, " +
                        "IFNULL(v.valoracion, 0) AS valoracion, " +
                        "IFNULL(v.nota_trabajador, '') AS nota, " +
                        "t.id_supervisor " +
                        "FROM trabajador t " +
                        "JOIN departamento d ON t.departamento = d.id_dpto " +
                        "LEFT JOIN valoracion v ON t.id_trabajador = v.id_trabajador";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_trabajador"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota"),
                        rs.getInt("id_supervisor")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 2. Buscar trabajadores por nombre
    public List<Trabajador> obtenerTrabajadores(String filtro) {
        List<Trabajador> lista = new ArrayList<>();

        if (conn == null) return lista;

        String sql =
                "SELECT t.id_trabajador, t.nombre, d.nombre AS departamento, " +
                        "IFNULL(v.valoracion, 0) AS valoracion, " +
                        "IFNULL(v.nota_trabajador, '') AS nota, " +
                        "t.id_supervisor " +
                        "FROM trabajador t " +
                        "JOIN departamento d ON t.departamento = d.id_dpto " +
                        "LEFT JOIN valoracion v ON t.id_trabajador = v.id_trabajador " +
                        "WHERE t.nombre LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_trabajador"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota"),
                        rs.getInt("id_supervisor")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 3. Actualizar valoración y nota
    public void actualizarValoracionYNota(int idTrabajador, double valoracion, String nota) {
        if (conn == null) return;

        String sql =
                "REPLACE INTO valoracion (id_trabajador, valoracion, nota_trabajador) " +
                        "VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            ps.setDouble(2, valoracion);
            ps.setString(3, nota);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Insertar historial de valoración
    public void insertarHistorial(int idTrabajador, int idSupervisor,
                                  double valorAnterior, double valorNuevo,
                                  String notaAnterior, String notaNueva) {
        if (conn == null) return;

        String sql = "INSERT INTO historial_valoracion " +
                "(id_trabajador, id_supervisor, fecha, valoracion_anterior, valoracion_nueva, nota_anterior, nota_nueva) " +
                "VALUES (?, ?, NOW(), ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            ps.setInt(2, idSupervisor);
            ps.setDouble(3, valorAnterior);
            ps.setDouble(4, valorNuevo);
            ps.setString(5, notaAnterior);
            ps.setString(6, notaNueva);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
