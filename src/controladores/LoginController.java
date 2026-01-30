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
        configurarHoverLevitacion(); // Nueva animación de levitación integrada
    }

    private void cargarLogo() {
        try {
            Image logo = new Image(getClass().getResource("/vistas/imagenes/logoTelmark.png").toExternalForm());
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo, verifica la ruta.");
        }
    }

    // --- ANIMACIONES VISUALES ---

    private void animacionEntradaCard() {
        cardRoot.setOpacity(0);
        cardRoot.setTranslateY(40);

        FadeTransition fade = new FadeTransition(Duration.millis(800), cardRoot);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), cardRoot);
        slide.setToY(0);

        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();
    }

    /**
     * EFECTO PREMIUM: La tarjeta levita suavemente y aumenta su escala al pasar el mouse.
     */
    private void configurarHoverLevitacion() {
        hoverTransition = new TranslateTransition(Duration.millis(300), cardRoot);
        ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(300), cardRoot);

        cardRoot.setOnMouseEntered(e -> {
            // Detenemos animaciones previas para evitar saltos
            hoverTransition.stop();
            scaleTrans.stop();

            hoverTransition.setToY(-10); // Sube 10px
            scaleTrans.setToX(1.015);    // Crece un 1.5%
            scaleTrans.setToY(1.015);

            hoverTransition.play();
            scaleTrans.play();

            // Refuerzo de sombra dinámico
            cardRoot.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 45, 0, 0, 25);");
        });

        cardRoot.setOnMouseExited(e -> {
            hoverTransition.stop();
            scaleTrans.stop();

            hoverTransition.setToY(0);   // Vuelve a su sitio
            scaleTrans.setToX(1.0);
            scaleTrans.setToY(1.0);

            hoverTransition.play();
            scaleTrans.play();

            // Volver al estilo del archivo CSS
            cardRoot.setStyle("");
        });
    }

    private void configurarAnimacionPulseBoton() {
        pulseAnimation = new ScaleTransition(Duration.millis(600), loginButton);
        pulseAnimation.setToX(1.05);
        pulseAnimation.setToY(1.05);
        pulseAnimation.setCycleCount(Animation.INDEFINITE);
        pulseAnimation.setAutoReverse(true);

        loginButton.setOnMouseEntered(e -> pulseAnimation.play());
        loginButton.setOnMouseExited(e -> {
            pulseAnimation.stop();
            loginButton.setScaleX(1.0);
            loginButton.setScaleY(1.0);
        });
    }

    private void animacionErrorShake() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), cardRoot);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> cardRoot.setTranslateX(0));
        tt.play();
    }

    // --- LÓGICA DE NEGOCIO ---

    @FXML
    private void login(javafx.event.ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            animacionErrorShake();
            return;
        }

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

                // Salida elegante: desvanecimiento
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), cardRoot);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    try {
                        switch (rol) {
                            case "coordinador" -> cargarVista("/vistas/HistorialValoracion.fxml", event);
                            case "supervisor"  -> cargarVistaSupervisor("/vistas/SupervisorEquipo.fxml", event, idTrabajador);
                            case "trabajador"  -> cargarVista("/vistas/Trabajador.fxml", event);
                            default -> mostrarError("Rol no reconocido.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                fadeOut.play();

            } else {
                animacionErrorShake();
                mostrarError("Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al conectar con la base de datos.");
        }
    }

    private void cargarVista(String ruta, javafx.event.ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(ruta));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void cargarVistaSupervisor(String ruta, javafx.event.ActionEvent event, int idSupervisor) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        Parent root = loader.load();

        SupervisorEquipoController controller = loader.getController();
        controller.setIdSupervisor(idSupervisor);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Acceso Denegado");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void crearFondoDinamico() {
        // Esto crea un círculo de luz naranja muy suave detrás de la tarjeta
        Circle orangeGlow = new Circle(200, Color.web("#f97316", 0.15));
        orangeGlow.setEffect(new BoxBlur(100, 100, 3));

        // Lo añadimos al StackPane (root) en la posición 0 para que esté al fondo
        ((StackPane)cardRoot.getParent()).getChildren().add(0, orangeGlow);

        // Animación de movimiento lento
        TranslateTransition tt = new TranslateTransition(Duration.seconds(10), orangeGlow);
        tt.setFromX(-200);
        tt.setToX(200);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.play();
    }
}