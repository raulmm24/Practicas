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

    // ============================================================
    // 1. OBTENER OBJETIVOS POR DEPARTAMENTO (NORMALIZADO)
    // ============================================================
    public List<Objetivos> obtenerObjetivosPorDepartamento(String nombreDepartamento) {
        List<Objetivos> lista = new ArrayList<>();
        if (conn == null) return lista;

        String sql = """
            SELECT id, descripcion, departamento, progreso, estado, fecha_limite
            FROM objetivos
            WHERE LOWER(departamento) = LOWER(?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreDepartamento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Objetivos(
                            rs.getInt("id"),
                            rs.getString("descripcion"),
                            rs.getString("departamento"),
                            rs.getDouble("progreso"),
                            rs.getString("estado"),
                            rs.getDate("fecha_limite")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerObjetivos: " + e.getMessage());
        }
        return lista;
    }

    // ============================================================
    // 2. OBTENER TRABAJADORES POR DEPARTAMENTO (NORMALIZADO)
    // ============================================================
    public List<TrabajadorSeleccion> obtenerTrabajadoresPorDepartamento(String nombreDepartamento) {
        List<TrabajadorSeleccion> lista = new ArrayList<>();
        if (conn == null) return lista;

        // Consulta robusta que une trabajador, departamento y valoración
        String sql = """
        SELECT t.id_trabajador AS id, t.nombre, d.nombre AS departamento,
               IFNULL(v.valoracion, 0.0) AS valoracion,
               IFNULL(v.nota_trabajador, 'Sin observaciones') AS nota,
               IFNULL(t.id_supervisor, 0) AS supervisor
        FROM trabajador t
        JOIN departamento d ON t.departamento = d.id_dpto
        LEFT JOIN valoracion v ON t.id_trabajador = v.id_trabajador
        WHERE LOWER(d.nombre) = LOWER(?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreDepartamento.trim()); // Limpieza de espacios
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerTrabajadores: " + e.getMessage());
        }
        return lista;
    }

    // ============================================================
    // 3. ASIGNAR EQUIPO
    // ============================================================
    public void asignarEquipo(int idSupervisor, List<Integer> trabajadores, String nombreDepartamento) throws SQLException {
        if (conn == null || trabajadores == null || trabajadores.isEmpty()) return;

        String sql = """
            UPDATE trabajador
            SET id_supervisor = ?
            WHERE id_trabajador = ?
              AND departamento = (SELECT id_dpto FROM departamento WHERE LOWER(nombre) = LOWER(?) LIMIT 1)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer idTrabajador : trabajadores) {
                ps.setInt(1, idSupervisor);
                ps.setInt(2, idTrabajador);
                ps.setString(3, nombreDepartamento);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ============================================================
    // 4. LISTA DE DEPARTAMENTOS
    // ============================================================
    public List<String> obtenerDepartamentos() {
        List<String> lista = new ArrayList<>();
        if (conn == null) return lista;

        String sql = "SELECT nombre FROM departamento ORDER BY nombre ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerDepartamentos: " + e.getMessage());
        }
        return lista;
    }

    // ============================================================
    // 5. ACTUALIZAR VALORACIÓN
    // ============================================================
    public void actualizarValoracionYNota(int idTrabajador, double valoracion, String nota) {
        if (conn == null) return;

        String sql = """
            INSERT INTO valoracion (id_trabajador, valoracion, nota_trabajador)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                valoracion = VALUES(valoracion),
                nota_trabajador = VALUES(nota_trabajador)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            ps.setDouble(2, valoracion);
            ps.setString(3, nota);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en actualizarValoracionYNota: " + e.getMessage());
        }
    }

    // ============================================================
    // 6. INSERTAR TRABAJADOR
    // ============================================================
    public void insertarTrabajador(String nombre, String departamento, double valoracion, String nota) {
        if (conn == null) return;

        String sql = """
            INSERT INTO trabajador (nombre, departamento)
            VALUES (?, (SELECT id_dpto FROM departamento WHERE LOWER(nombre) = LOWER(?) LIMIT 1))
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, departamento);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    actualizarValoracionYNota(rs.getInt(1), valoracion, nota);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en insertarTrabajador: " + e.getMessage());
        }
    }

    // ============================================================
    // 7. ELIMINAR TRABAJADOR
    // ============================================================
    public void eliminarTrabajador(int id) {
        if (conn == null) return;

        try {
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=0");

            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM valoracion WHERE id_trabajador=?")) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM trabajador WHERE id_trabajador=?")) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }

            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=1");

        } catch (SQLException e) {
            System.err.println("Error en eliminarTrabajador: " + e.getMessage());
        }
    }
}
