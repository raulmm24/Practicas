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
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.ConexionMySQL;

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

    private ScaleTransition pulseAnimation;
    private TranslateTransition hoverTransition;

    @FXML
    private void initialize() {
        cargarLogo();
        animacionEntradaCard();
        configurarAnimacionPulseBoton();
        configurarHoverLevitacion();
    }

    private void cargarLogo() {
        try {
            URL urlLogo = getClass().getResource("/vistas/imagenes/logoTelmark.png");
            if (urlLogo != null) {
                logoImage.setImage(new Image(urlLogo.toExternalForm()));
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo.");
        }
    }

    // --- ANIMACIONES ---

    private void animacionEntradaCard() {
        cardRoot.setOpacity(0);
        cardRoot.setTranslateY(40);
        FadeTransition fade = new FadeTransition(Duration.millis(800), cardRoot);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(800), cardRoot);
        slide.setToY(0);
        new ParallelTransition(fade, slide).play();
    }

    private void configurarHoverLevitacion() {
        hoverTransition = new TranslateTransition(Duration.millis(300), cardRoot);
        ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(300), cardRoot);

        cardRoot.setOnMouseEntered(e -> {
            hoverTransition.stop(); scaleTrans.stop();
            hoverTransition.setToY(-10);
            scaleTrans.setToX(1.015); scaleTrans.setToY(1.015);
            hoverTransition.play(); scaleTrans.play();
            cardRoot.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 45, 0, 0, 25);");
        });

        cardRoot.setOnMouseExited(e -> {
            hoverTransition.stop(); scaleTrans.stop();
            hoverTransition.setToY(0);
            scaleTrans.setToX(1.0); scaleTrans.setToY(1.0);
            hoverTransition.play(); scaleTrans.play();
            cardRoot.setStyle("");
        });
    }

    private void configurarAnimacionPulseBoton() {
        pulseAnimation = new ScaleTransition(Duration.millis(600), loginButton);
        pulseAnimation.setToX(1.05); pulseAnimation.setToY(1.05);
        pulseAnimation.setCycleCount(Animation.INDEFINITE);
        pulseAnimation.setAutoReverse(true);
        loginButton.setOnMouseEntered(e -> pulseAnimation.play());
        loginButton.setOnMouseExited(e -> { pulseAnimation.stop(); loginButton.setScaleX(1); loginButton.setScaleY(1); });
    }

    private void animacionErrorShake() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), cardRoot);
        tt.setByX(10); tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.setOnFinished(e -> cardRoot.setTranslateX(0));
        tt.play();
    }

    // --- LÓGICA DE ACCESO ---

    @FXML
    private void login(javafx.event.ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) { animacionErrorShake(); return; }

        try (Connection con = new ConexionMySQL().conexionBBDD()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT rol, id_trabajador FROM usuario WHERE username = ? AND password = ?"
            );
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("rol").toLowerCase();
                int idTrabajador = rs.getInt("id_trabajador");

                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), cardRoot);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    try {
                        switch (rol) {
                            case "coordinador" -> cambiarEscena("/vistas/HistorialValoracion.fxml", event);
                            case "supervisor"  -> cambiarEscena("/vistas/SupervisorHub.fxml", event); // Redirige al HUB
                            case "trabajador"  -> cambiarEscena("/vistas/Trabajador.fxml", event);
                            default -> mostrarError("Rol no reconocido.");
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
                fadeOut.play();
            } else {
                animacionErrorShake();
                mostrarError("Credenciales incorrectas.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error de conexión.");
        }
    }

    /**
     * MÉTODO DE CARGA BLINDADO
     */
    private void cambiarEscena(String ruta, javafx.event.ActionEvent event) throws Exception {
        URL url = getClass().getResource(ruta);

        if (url == null) {
            System.err.println("CRÍTICO: No se encontró el archivo FXML en la ruta: " + ruta);
            System.err.println("Verifica que el archivo esté en: src/main/resources" + ruta);
            return;
        }

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}