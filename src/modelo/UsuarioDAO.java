package modelo;

import java.sql.*;

public class UsuarioDAO {

    public Usuario validarLogin(String username, String password) {

        String sql = "SELECT * FROM usuario WHERE username = ? AND password = ?";

        try (Connection conn = new ConexionMySQL().conexionBBDD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("rol"),
                        (Integer) rs.getObject("id_trabajador")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
