package controladores;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Objetivos;
import modelo.ObjetivosDAO;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ObjetivosController implements Initializable {

    @FXML private VBox contenedorObjetivos;
    @FXML private ComboBox<String> comboDepartamento;
    @FXML private Button btnVolver;
    @FXML private Button btnNuevoObjetivo;

    private final ObjetivosDAO dao = new ObjetivosDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Departamentos
        comboDepartamento.getItems().addAll(
                "Programación", "Finanzas", "Marketing", "Informática", "Atención al cliente"
        );

        comboDepartamento.setOnAction(e -> filtrarPorDepartamento(comboDepartamento.getValue()));
        btnVolver.setOnAction(this::volverAlHub);
        btnNuevoObjetivo.setOnAction(e -> mostrarDialogoCrearMeta());
    }

    private void filtrarPorDepartamento(String depto) {
        contenedorObjetivos.getChildren().clear();
        List<Objetivos> objetivos = dao.obtenerObjetivosPorDepto(depto);

        double delay = 0;
        for (Objetivos obj : objetivos) {
            VBox card = crearTarjeta(obj);
            contenedorObjetivos.getChildren().add(card);
            animarEntrada(card, delay);
            delay += 0.1;
        }
    }

    private VBox crearTarjeta(Objetivos obj) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card-objetivo");

        // Título del Objetivo (Corregido para que sea visible)
        Label lblDesc = new Label(obj.getDescripcion());
        lblDesc.getStyleClass().add("label-titulo-meta");
        lblDesc.setWrapText(true); // Evita que se corte si es largo

        ProgressBar pb = new ProgressBar(obj.getProgreso());
        pb.setMaxWidth(Double.MAX_VALUE);
        if (obj.getProgreso() >= 1.0) pb.setStyle("-fx-accent: #22c55e;");

        HBox footer = new HBox(10);
        Label lblInfo = new Label("Estado: " + obj.getEstado() + " | Fin: " + obj.getFecha_limite());
        lblInfo.getStyleClass().add("label-info-footer");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEliminar = new Button("🗑");
        btnEliminar.getStyleClass().add("btn-delete");
        btnEliminar.setOnAction(e -> confirmarEliminacion(obj));

        footer.getChildren().addAll(lblInfo, spacer, btnEliminar);
        card.getChildren().addAll(lblDesc, pb, footer);

        return card;
    }

    private void volverAlHub(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vistas/SupervisorHub.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void confirmarEliminacion(Objetivos obj) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar");
        alert.setHeaderText("¿Borrar meta?");
        alert.setContentText(obj.getDescripcion());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (dao.eliminarObjetivo(obj.getId())) filtrarPorDepartamento(comboDepartamento.getValue());
        }
    }

    private void mostrarDialogoCrearMeta() {
        if (comboDepartamento.getValue() == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Meta");
        dialog.setHeaderText("Meta para " + comboDepartamento.getValue());
        dialog.setContentText("Descripción:");
        dialog.showAndWait().ifPresent(desc -> {
            if (!desc.trim().isEmpty()) {
                dao.insertarObjetivo(desc, comboDepartamento.getValue(), 0.0, "En curso", "2026-12-31");
                filtrarPorDepartamento(comboDepartamento.getValue());
            }
        });
    }

    private void animarEntrada(Node n, double d) {
        n.setOpacity(0);
        n.setTranslateY(15);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.4), n);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.4), n);
        tt.setToY(0);
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setDelay(Duration.seconds(d));
        pt.play();
    }
}