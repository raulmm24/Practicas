package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    private final Connection conn;

    public TrabajadorDAO() {
        conn = new ConexionMySQL().conexionBBDD();
        if (conn == null) {
            System.err.println("ERROR: No se pudo conectar a la base de datos.");
        }
    }

    // Obtener todos los trabajadores con nombre de departamento
    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> lista = new ArrayList<>();

        if (conn == null) return lista;

        String sql =
                "SELECT t.id_empleado, t.nombre, d.nombre AS departamento, " +
                        "IFNULL(v.valoracion, 0) AS valoracion, " +
                        "IFNULL(v.nota_trabajador, '') AS nota " +
                        "FROM trabajador t " +
                        "JOIN departamento d ON t.id_departamento = d.id_departamento " +
                        "LEFT JOIN valoracion v ON t.id_empleado = v.id_trabajador";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Obtener trabajadores con filtro
    public List<Trabajador> obtenerTrabajadores(String filtro) {
        List<Trabajador> lista = new ArrayList<>();

        if (conn == null) return lista;

        String sql =
                "SELECT t.id_empleado, t.nombre, d.nombre AS departamento, " +
                        "IFNULL(v.valoracion, 0) AS valoracion, " +
                        "IFNULL(v.nota_trabajador, '') AS nota " +
                        "FROM trabajador t " +
                        "JOIN departamento d ON t.id_departamento = d.id_departamento " +
                        "LEFT JOIN valoracion v ON t.id_empleado = v.id_trabajador " +
                        "WHERE t.nombre LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ComboBox
    public List<String> obtenerTrabajadoresParaCombo() {
        List<String> lista = new ArrayList<>();

        if (conn == null) return lista;

        String sql = "SELECT id_empleado, nombre FROM trabajador ORDER BY nombre";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id_empleado") + " - " + rs.getString("nombre"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Actualizar valoración y nota
    public boolean actualizarValoracionYNota(int idTrabajador, double valoracion, String nota) {
        if (conn == null) return false;

        String sql = "REPLACE INTO valoracion (id_trabajador, valoracion, nota_trabajador) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            ps.setDouble(2, valoracion);
            ps.setString(3, nota);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insertar historial
    public void insertarHistorial(int idTrabajador, int idSupervisor,
                                  double valorAnterior, double valorNueva,
                                  String notaAnterior, String notaNueva) {

        if (conn == null) return;

        String sql =
                "INSERT INTO historial_valoracion " +
                        "(id_trabajador, id_supervisor, fecha, valoracion_anterior, valoracion_nueva, nota_anterior, nota_nueva) " +
                        "VALUES (?, ?, CURDATE(), ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            ps.setInt(2, idSupervisor);
            ps.setDouble(3, valorAnterior);
            ps.setDouble(4, valorNueva);
            ps.setString(5, notaAnterior);
            ps.setString(6, notaNueva);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
