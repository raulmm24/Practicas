package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetivosDAO {

    public List<Objetivos> obtenerObjetivosPorDepto(String depto) {
        List<Objetivos> lista = new ArrayList<>();
        String sql = "SELECT * FROM objetivos WHERE departamento = ?";
        try (Connection con = new ConexionMySQL().conexionBBDD();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, depto);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean insertarObjetivo(String desc, String depto, double progreso, String estado, String fecha) {
        String sql = "INSERT INTO objetivos (descripcion, departamento, progreso, estado, fecha_limite) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = new ConexionMySQL().conexionBBDD();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, desc);
            ps.setString(2, depto);
            ps.setDouble(3, progreso);
            ps.setString(4, estado);
            ps.setString(5, fecha);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarObjetivo(int id) {
        String sql = "DELETE FROM objetivos WHERE id = ?";
        try (Connection con = new ConexionMySQL().conexionBBDD();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}