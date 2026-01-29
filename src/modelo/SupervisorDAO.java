package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupervisorDAO {

    private final Connection conn;

    public SupervisorDAO() {
        conn = new ConexionMySQL().conexionBBDD();
        if (conn == null) {
            System.err.println("ERROR: No se pudo conectar a la base de datos.");
        }
    }

    // 1. Obtener lista de nombres de departamentos
    public List<String> obtenerDepartamentos() {
        List<String> lista = new ArrayList<>();
        if (conn == null) return lista;

        String sql = "SELECT nombre FROM departamento ORDER BY nombre";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 2. Obtener trabajadores por nombre de departamento
    public List<TrabajadorSeleccion> obtenerTrabajadoresPorDepartamento(String nombreDepartamento) {
        List<TrabajadorSeleccion> lista = new ArrayList<>();
        if (conn == null) return lista;

        String sql =
                "SELECT t.id_empleado AS id, t.nombre, d.nombre AS departamento, " +
                        "IFNULL(v.valoracion, 0) AS valoracion, " +
                        "IFNULL(v.nota_trabajador, '') AS nota, " +
                        "IFNULL(t.id_supervisor, 0) AS supervisor " +
                        "FROM trabajador t " +
                        "JOIN departamento d ON t.departamento = d.id_dpto " +
                        "LEFT JOIN valoracion v ON t.id_empleado = v.id_trabajador " +
                        "WHERE d.nombre = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreDepartamento);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new TrabajadorSeleccion(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota"),
                        rs.getInt("supervisor")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    // 3. Asignar equipo a supervisor por nombre de departamento
    public void asignarEquipo(int idSupervisor, List<Integer> trabajadores, String nombreDepartamento) {
        if (conn == null) return;

        String sql = "UPDATE trabajador SET id_supervisor = ? WHERE id_empleado = ? AND departamento = " +
                "(SELECT id_dpto FROM departamento WHERE nombre = ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer idTrabajador : trabajadores) {
                ps.setInt(1, idSupervisor);
                ps.setInt(2, idTrabajador);
                ps.setString(3, nombreDepartamento);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Guardar historial de cambios
    public void guardarHistorial(int idTrabajador, int idSupervisor,
                                 double valorAnterior, double valorNueva,
                                 String notaAnterior, String notaNueva) {
        if (conn == null) return;

        String sql =
                "INSERT INTO historial_valoracion " +
                        "(id_trabajador, id_supervisor, fecha, valoracion_anterior, valoracion_nueva, nota_anterior, nota_nueva) " +
                        "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";

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

    // 5. Actualizar valoración y nota
    public void actualizarValoracionYNota(int idTrabajador, double valoracion, String nota) {
        if (conn == null) return;

        String sql = "REPLACE INTO valoracion (id_trabajador, valoracion, nota_trabajador) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            ps.setDouble(2, valoracion);
            ps.setString(3, nota);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
