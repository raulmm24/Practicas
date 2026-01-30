package controladores;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;

public class SupervisorHubController {

    @FXML private VBox cardEquipo;
    @FXML private VBox cardObjetivos;

    @FXML
    private void initialize() {
        configurarHover(cardEquipo);
        configurarHover(cardObjetivos);
    }

    private void configurarHover(VBox card) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        card.setOnMouseEntered(e -> {
            st.stop();
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        card.setOnMouseExited(e -> {
            st.stop();
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    @FXML
    private void irAGestionEquipo(MouseEvent event) throws Exception {
        cambiarEscena("/vistas/SupervisorEquipo.fxml", (Node) event.getSource());
    }

    @FXML
    private void irAObjetivos(MouseEvent event) throws Exception {
        cambiarEscena("/vistas/Objetivos.fxml", (Node) event.getSource());
    }

    @FXML
    private void cerrarSesion(ActionEvent event) throws Exception {
        cambiarEscena("/vistas/Login.fxml", (Node) event.getSource());
    }

    private void cambiarEscena(String ruta, Node nodoOrigen) throws Exception {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró la vista: " + ruta);
            return;
        }
        Parent root = FXMLLoader.load(url);
        Stage stage = (Stage) nodoOrigen.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }
}