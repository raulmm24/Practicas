package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {

    // Obtener nombres de trabajadores para el ComboBox
    public List<String> obtenerNombresTrabajadores() {
        List<String> lista = new ArrayList<>();

        try (Connection con = new ConexionMySQL().conexionBBDD()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT nombre FROM trabajador ORDER BY nombre"
            );

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Obtener historial por nombre de trabajador
    public List<HistorialValoracion> obtenerHistorialPorTrabajador(String nombreTrabajador) {

        List<HistorialValoracion> lista = new ArrayList<>();

        try (Connection con = new ConexionMySQL().conexionBBDD()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT h.fecha, h.valoracion_anterior, h.valoracion_nueva, " +
                            "h.nota_anterior, h.nota_nueva, t2.nombre AS supervisor " +
                            "FROM historial_valoracion h " +
                            "JOIN trabajador t ON h.id_trabajador = t.id_empleado " +
                            "JOIN trabajador t2 ON h.id_supervisor = t2.id_empleado " +
                            "WHERE t.nombre = ? " +
                            "ORDER BY h.fecha DESC"
            );

            ps.setString(1, nombreTrabajador);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new HistorialValoracion(
                        rs.getString("fecha"),
                        rs.getDouble("valoracion_anterior"),
                        rs.getDouble("valoracion_nueva"),
                        rs.getString("nota_anterior"),
                        rs.getString("nota_nueva"),
                        rs.getString("supervisor")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Guardar historial (ya lo usas desde SupervisorDAO)
    public void guardarHistorial(int idTrabajador, int idSupervisor,
                                 double valoracionAnterior, double valoracionNueva,
                                 String notaAnterior, String notaNueva) {

        try (Connection con = new ConexionMySQL().conexionBBDD()) {

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO historial_valoracion " +
                            "(id_trabajador, id_supervisor, valoracion_anterior, valoracion_nueva, nota_anterior, nota_nueva) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            );

            ps.setInt(1, idTrabajador);
            ps.setInt(2, idSupervisor);
            ps.setDouble(3, valoracionAnterior);
            ps.setDouble(4, valoracionNueva);
            ps.setString(5, notaAnterior);
            ps.setString(6, notaNueva);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
