package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.ConexionMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    @FXML
    private void login(ActionEvent event) {
        try (Connection con = new ConexionMySQL().conexionBBDD()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT rol, id_trabajador FROM usuario WHERE username = ? AND password = ?"
            );
            ps.setString(1, txtUsuario.getText());
            ps.setString(2, txtPassword.getText());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String rol = rs.getString("rol");
                int idTrabajador = rs.getInt("id_trabajador");

                switch (rol.toLowerCase()) {

                    case "coordinador":
                        cargarVista("/vistas/HistorialValoraciones.fxml", event);
                        break;

                    case "supervisor":
                        cargarVistaSupervisor("/vistas/SupervisorEquipo.fxml", event, idTrabajador);
                        break;

                    case "trabajador":
                        cargarVista("/vistas/Trabajador.fxml", event);
                        break;

                    default:
                        mostrarError("Rol no reconocido en el sistema.");
                }

            } else {
                mostrarError("Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al conectar con la base de datos.");
        }
    }

    private void cargarVista(String ruta, ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(ruta));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void cargarVistaSupervisor(String ruta, ActionEvent event, int idSupervisor) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();

        SupervisorEquipoController controller = loader.getController();
        controller.setIdSupervisor(idSupervisor);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de acceso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
