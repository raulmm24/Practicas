package controladores;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.ConexionMySQL;
import modelo.Sesion;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button loginButton;
    @FXML private VBox cardRoot;
    @FXML private ImageView logoImage;

    @FXML
    private void initialize() {
        // Cargar el logo desde la carpeta de recursos
        cargarLogo();
        // Ejecutar la animación de entrada suave
        animacionEntradaCard();
    }

    private void cargarLogo() {
        try {
            // Se asume que el logo está en la carpeta resources/estilo/
            URL logoUrl = getClass().getResource("/vistas/imagenes/logoTelmark.png");
            if (logoUrl != null) {
                logoImage.setImage(new Image(logoUrl.toExternalForm()));
            } else {
                System.err.println("Advertencia: No se encontró el archivo logo.png en /estilo/");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen del logo: " + e.getMessage());
        }
    }

    private void animacionEntradaCard() {
        cardRoot.setOpacity(0);
        cardRoot.setTranslateY(40);

        FadeTransition fade = new FadeTransition(Duration.millis(800), cardRoot);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), cardRoot);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();
    }

    @FXML
    private void login(javafx.event.ActionEvent event) {
        String user = txtUsuario.getText().trim();
        String pass = txtPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarError("Por favor, rellene todos los campos.");
            return;
        }

        try (Connection con = new ConexionMySQL().conexionBBDD()) {
            if (con == null) {
                mostrarError("No se pudo establecer conexión con la base de datos.");
                return;
            }

            String sql = "SELECT rol, id_trabajador FROM usuario WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("rol").toLowerCase();
                int idTrabajador = rs.getInt("id_trabajador");

                // GUARDAR EN SESIÓN
                Sesion.setIdUsuarioLogueado(idTrabajador);

                // Animación de salida antes de cambiar de escena
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), cardRoot);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    try {
                        switch (rol) {
                            case "coordinador" -> cambiarEscena("/vistas/HistorialValoracion.fxml", event);
                            case "supervisor"  -> cambiarEscena("/vistas/SupervisorHub.fxml", event);
                            case "trabajador"  -> cambiarEscena("/vistas/Trabajador.fxml", event);
                            default -> mostrarError("Acceso denegado: Rol no reconocido.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        mostrarError("Error al cargar la siguiente vista.");
                    }
                });
                fadeOut.play();
            } else {
                mostrarError("Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error crítico: " + e.getMessage());
        }
    }

    private void cambiarEscena(String ruta, javafx.event.ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Acceso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}