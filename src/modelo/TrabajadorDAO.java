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

        String sql = "SELECT * FROM trabajador";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        String.valueOf(rs.getInt("departamento")),
                        0.0,
                        "",
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

        String sql = "SELECT * FROM trabajador WHERE nombre LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_empleado"),                    // ← corregido
                        rs.getString("nombre"),
                        String.valueOf(rs.getInt("departamento")),   // ← si departamento es int
                        0.0,
                        "",
                        rs.getInt("id_supervisor")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 3. Actualizar valoración y nota
    public void actualizarValoracionYNota(int id, double valoracion, String nota) {
        if (conn == null) return;

        String sql = "UPDATE trabajador SET valoracion = ?, nota = ? WHERE id_empleado = ?"; // ← corregido

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, valoracion);
            ps.setString(2, nota);
            ps.setInt(3, id);
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
