package controladores;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TrabajadorController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar, btnVolver;
    @FXML private VBox contenedorResultados;
    @FXML private ScrollPane scrollResultados;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Asignar estilos modernos
        btnBuscar.getStyleClass().add("button-primary");
        btnVolver.getStyleClass().add("button-ghost");

        // Eventos con efectos de click
        btnBuscar.setOnAction(e -> {
            animarClick(btnBuscar);
            buscarEmpleado();
        });

        btnVolver.setOnAction(e -> {
            animarClick(btnVolver);
            volver();
        });

        txtBuscar.setOnAction(e -> buscarEmpleado());

        // Aplicar efectos hover avanzados
        configurarEfectoHover(btnBuscar);
        configurarEfectoHover(btnVolver);
    }

    private void buscarEmpleado() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            mostrarAlerta("Por favor, ingrese un nombre para consultar.");
            return;
        }

        List<Trabajador> resultados = dao.obtenerTrabajadores(filtro);
        contenedorResultados.getChildren().clear();

        if (resultados == null || resultados.isEmpty()) {
            Label sinResultados = new Label("No se encontró ningún registro.");
            sinResultados.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic;");
            contenedorResultados.getChildren().add(sinResultados);
            return;
        }

        double delay = 0;
        for (Trabajador t : resultados) {
            VBox tarjeta = crearTarjetaGrafica(t);
            contenedorResultados.getChildren().add(tarjeta);
            aplicarAnimacionEntrada(tarjeta, delay);
            delay += 0.1;
        }
    }

    private VBox crearTarjetaGrafica(Trabajador t) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card-trabajador");

        // --- CABECERA (Nombre e ID) ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox infoPersonal = new VBox(2);
        Label lblNombre = new Label(t.getNombre().toUpperCase());
        lblNombre.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #1e293b;");

        Label lblDpto = new Label("Departamento de " + t.getDepartamento());
        lblDpto.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 600;");
        infoPersonal.getChildren().addAll(lblNombre, lblDpto);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblId = new Label("#" + t.getId());
        lblId.setStyle("-fx-font-size: 14px; -fx-text-fill: #cbd5e1; -fx-font-weight: bold;");

        header.getChildren().addAll(infoPersonal, spacer, lblId);

        // --- CUERPO (Valoración destacada) ---
        HBox filaValoracion = new HBox(10);
        filaValoracion.setAlignment(Pos.CENTER_LEFT);

        Label txtPuntaje = new Label("Puntaje de Desempeño:");
        txtPuntaje.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");

        Label badgeVal = new Label(t.getValoracion() + " ★");
        badgeVal.getStyleClass().add("badge-valoracion");

        filaValoracion.getChildren().addAll(txtPuntaje, badgeVal);

        // --- NOTA (Bloque visual separado) ---
        VBox bloqueNota = new VBox(5);
        Label titNota = new Label("Observaciones del Supervisor:");
        titNota.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: #94a3b8; -fx-padding: 0 0 0 5;");

        Label lblNota = new Label(t.getNota().isEmpty() ? "Sin observaciones registradas." : t.getNota());
        lblNota.getStyleClass().add("nota-text");
        lblNota.setWrapText(true);
        lblNota.setMinWidth(450); // Asegura que la burbuja tenga buen tamaño

        bloqueNota.getChildren().addAll(titNota, lblNota);

        // Añadir todo a la card
        card.getChildren().addAll(header, new Separator(), filaValoracion, bloqueNota);

        // Efecto Hover
        card.setOnMouseEntered(e -> escalarNodo(card, 1.02));
        card.setOnMouseExited(e -> escalarNodo(card, 1.0));

        return card;
    }

    // --- SISTEMA DE ANIMACIONES ---

    private void animarClick(Button boton) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), boton);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(0.92); st.setToY(0.92);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void configurarEfectoHover(Button boton) {
        boton.setOnMouseEntered(e -> escalarNodo(boton, 1.05));
        boton.setOnMouseExited(e -> escalarNodo(boton, 1.0));
    }

    private void aplicarAnimacionEntrada(Node nodo, double delaySeconds) {
        nodo.setOpacity(0);
        nodo.setTranslateY(20);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), nodo);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), nodo);
        tt.setToY(0);
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setDelay(Duration.seconds(delaySeconds));
        pt.play();
    }

    private void escalarNodo(Node nodo, double factor) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), nodo);
        st.setToX(factor);
        st.setToY(factor);
        st.play();
    }

    private void volver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vistas/Login.fxml"));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            mostrarAlerta("Error al regresar.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}