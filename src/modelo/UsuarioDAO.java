package modelo;

import java.sql.*;

public class UsuarioDAO {

    private final Connection conn;

    public UsuarioDAO() {
        conn = new ConexionMySQL().conexionBBDD();
        if (conn == null) {
            System.err.println("ERROR: No se pudo conectar a la base de datos.");
        }
    }

    public Usuario login(String usuario, String password) {

        if (conn == null) return null;

        String sql = "SELECT * FROM usuario WHERE usuario = ? AND password = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getInt("rol")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
