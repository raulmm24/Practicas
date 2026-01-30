package controladores;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
        btnBuscar.getStyleClass().add("button-primary");
        btnVolver.getStyleClass().add("button-ghost");

        btnBuscar.setOnAction(e -> simularCargaYBuscar());
        btnVolver.setOnAction(e -> volver());
        txtBuscar.setOnAction(e -> simularCargaYBuscar());

        configurarEfectoHover(btnBuscar);
        configurarEfectoHover(btnVolver);
    }

    private void simularCargaYBuscar() {
        contenedorResultados.getChildren().clear();

        // Creamos un Spinner de carga
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.getStyleClass().add("progress-indicator");

        VBox loadingBox = new VBox(spinner, new Label("Consultando expedientes..."));
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(50));

        contenedorResultados.getChildren().add(loadingBox);

        // Pequeña pausa para que el usuario vea la animación (700ms)
        PauseTransition pause = new PauseTransition(Duration.millis(700));
        pause.setOnFinished(event -> buscarEmpleado());
        pause.play();
    }

    private void buscarEmpleado() {
        String filtro = txtBuscar.getText().trim();
        List<Trabajador> resultados = dao.obtenerTrabajadores(filtro);
        contenedorResultados.getChildren().clear();

        if (resultados == null || resultados.isEmpty()) {
            mostrarAlerta("No se encontraron registros.");
            return;
        }

        double delay = 0;
        for (Trabajador t : resultados) {
            VBox tarjeta = crearTarjetaGrafica(t);
            // Aplicamos MARGEN EXTERNO para que no choquen
            VBox.setMargin(tarjeta, new Insets(15, 50, 15, 50));

            contenedorResultados.getChildren().add(tarjeta);
            aplicarAnimacionEntrada(tarjeta, delay);
            delay += 0.15;
        }
    }

    private VBox crearTarjetaGrafica(Trabajador t) {
        VBox card = new VBox(20);
        card.getStyleClass().add("card-trabajador");

        // --- CABECERA (Nombre, Depto e ID) ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(5);
        Label lblNombre = new Label(t.getNombre().toUpperCase());
        lblNombre.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #1e293b;");
        Label lblDepto = new Label("📍 " + t.getDepartamento() + " • ID: #" + t.getId());
        lblDepto.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;");
        info.getChildren().addAll(lblNombre, lblDepto);

        header.getChildren().add(info);

        // --- VALORACIÓN VISUAL ---
        HBox filaVal = new HBox(15);
        filaVal.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label(String.format("%.1f ★", t.getValoracion()));
        badge.getStyleClass().add("badge-valoracion");

        ProgressBar pb = new ProgressBar(t.getValoracion() / 10.0);
        pb.setPrefWidth(250);

        filaVal.getChildren().addAll(badge, pb);

        // --- NOTA (Burbuja) ---
        VBox bloqueNota = new VBox(8);
        Label titNota = new Label("COMENTARIOS DEL SUPERVISOR");
        titNota.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: #94a3b8;");

        Label lblNota = new Label(t.getNota().isEmpty() ? "Sin observaciones registradas." : t.getNota());
        lblNota.getStyleClass().add("nota-text");
        lblNota.setWrapText(true);
        lblNota.setMaxWidth(600);

        bloqueNota.getChildren().addAll(titNota, lblNota);

        card.getChildren().addAll(header, new Separator(), filaVal, bloqueNota);

        // Efecto Hover
        card.setOnMouseEntered(e -> escalarNodo(card, 1.015));
        card.setOnMouseExited(e -> escalarNodo(card, 1.0));

        return card;
    }

    // --- MÉTODOS DE ANIMACIÓN Y UTILIDADES (Iguales que antes) ---
    private void aplicarAnimacionEntrada(Node nodo, double delay) {
        nodo.setOpacity(0); nodo.setTranslateY(30);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.6), nodo);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.6), nodo);
        tt.setToY(0);
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setDelay(Duration.seconds(delay));
        pt.play();
    }

    private void configurarEfectoHover(Button b) {
        b.setOnMouseEntered(e -> escalarNodo(b, 1.05));
        b.setOnMouseExited(e -> escalarNodo(b, 1.0));
    }

    private void escalarNodo(Node n, double f) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(f); st.setToY(f); st.play();
    }

    private void volver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vistas/Login.fxml"));
            Stage s = (Stage) btnVolver.getScene().getWindow();
            s.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void mostrarAlerta(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(m); a.showAndWait();
    }
}