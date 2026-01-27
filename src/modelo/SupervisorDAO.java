package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupervisorDAO {

    public List<String> obtenerDepartamentos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM departamento ORDER BY nombre";

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Integer obtenerIdDepartamentoPorNombre(String nombreDpto) {
        String sql = "SELECT id_dpto FROM departamento WHERE nombre = ?";

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreDpto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_dpto");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<TrabajadorSeleccion> obtenerTrabajadoresPorDepartamento(int idDepartamento, int idSupervisor) {
        List<TrabajadorSeleccion> lista = new ArrayList<>();

        String sql =
                "SELECT t.id_empleado, t.nombre, d.nombre AS departamento, " +
                        "IFNULL(v.valoracion, 0) AS valoracion, " +
                        "IFNULL(v.nota_trabajador, '') AS nota_trabajador, " +
                        "t.id_supervisor " +
                        "FROM trabajador t " +
                        "LEFT JOIN departamento d ON t.departamento = d.id_dpto " +
                        "LEFT JOIN valoracion v ON t.id_empleado = v.id_trabajador " +
                        "WHERE t.departamento = ?";

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDepartamento);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TrabajadorSeleccion t = new TrabajadorSeleccion(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota_trabajador"),
                        (Integer) rs.getObject("id_supervisor")
                );

                if (t.getIdSupervisor() != null && t.getIdSupervisor() == idSupervisor) {
                    t.setSeleccionado(true);
                }

                lista.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void asignarEquipo(int idSupervisor, List<Integer> idsTrabajadoresSeleccionados, int idDepartamento) {

        String limpiarSQL = "UPDATE trabajador SET id_supervisor = NULL WHERE departamento = ? AND id_supervisor = ?";
        String asignarSQL = "UPDATE trabajador SET id_supervisor = ? WHERE id_empleado = ?";

        try (Connection conn = new ConexionMySQL().conexionBBDD()) {

            try (PreparedStatement limpiar = conn.prepareStatement(limpiarSQL)) {
                limpiar.setInt(1, idDepartamento);
                limpiar.setInt(2, idSupervisor);
                limpiar.executeUpdate();
            }

            try (PreparedStatement asignar = conn.prepareStatement(asignarSQL)) {
                for (int idTrabajador : idsTrabajadoresSeleccionados) {
                    asignar.setInt(1, idSupervisor);
                    asignar.setInt(2, idTrabajador);
                    asignar.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarValoracionYNota(int idTrabajador, double valoracion, String nota) {
        try (Connection con = new ConexionMySQL().conexionBBDD()) {

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE trabajador SET valoracion = ?, nota = ? WHERE id_empleado = ?"
            );
            ps.setDouble(1, valoracion);
            ps.setString(2, nota);
            ps.setInt(3, idTrabajador);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
