package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    private static final String SQL =
            "SELECT t.id_empleado, t.nombre, d.nombre AS departamento, " +
                    "t.fecha_alta, v.valoracion, v.nota_trabajador " +
                    "FROM trabajador t " +
                    "JOIN departamento d ON t.departamento = d.id_dpto " +
                    "JOIN valoracion v ON t.id_empleado = v.id_trabajador";

    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> lista = new ArrayList<>();

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Trabajador(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getString("fecha_alta"),
                        rs.getDouble("valoracion"),
                        rs.getString("nota_trabajador")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
