package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Usuario;
import modelo.UsuarioDAO;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    private final UsuarioDAO dao = new UsuarioDAO();

    @FXML
    public void login() {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        Usuario u = dao.validarLogin(user, pass);

        if (u == null) {
            System.out.println("Usuario o contraseña incorrectos.");
            return;
        }

        cargarPantallaPorRol(u);
    }

    private void cargarPantallaPorRol(Usuario u) {
        try {
            String archivoFXML = switch (u.getRol()) {
                case "Coordinador" -> "/vistas/Coordinador.fxml";
                case "Supervisor" -> "/vistas/SupervisorEquipo.fxml";
                case "Trabajador" -> "/vistas/Trabajador.fxml";
                default -> null;
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(archivoFXML));
            Parent root = loader.load();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

