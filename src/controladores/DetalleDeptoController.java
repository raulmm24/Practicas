package controladores;

import modelo.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class DetalleDeptoController {

    @FXML private Label lblTituloDepto;
    @FXML private VBox contenedorTrabajadores;
    @FXML private VBox contenedorObjetivos;

    private final SupervisorDAO dao = new SupervisorDAO();

    public void initData(String nombreDepartamento) {
        String deptoLimpio = nombreDepartamento.trim();
        lblTituloDepto.setText("Gestión: " + deptoLimpio);
        cargarDatosDesdeBD(deptoLimpio);
    }

    private void cargarDatosDesdeBD(String depto) {
        contenedorTrabajadores.getChildren().clear();
        contenedorObjetivos.getChildren().clear();

        try {
            // === TRABAJADORES ===
            List<TrabajadorSeleccion> listaT = dao.obtenerTrabajadoresPorDepartamento(depto);
            for (TrabajadorSeleccion ts : listaT) {
                // Pasamos el objeto completo 'ts' para usar todos sus datos
                contenedorTrabajadores.getChildren().add(crearTarjetaEmpleado(ts));
            }

            // === OBJETIVOS ===
            List<Objetivos> listaO = dao.obtenerObjetivosPorDepartamento(depto);
            for (Objetivos obj : listaO) {
                contenedorObjetivos.getChildren().add(crearTarjetaObjetivo(obj.getDescripcion(), obj.getEstado()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox crearTarjetaEmpleado(TrabajadorSeleccion emp) {
        HBox card = new HBox(15);
        card.getStyleClass().add("item-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(420);

        // Bloque de texto (Nombre y Observaciones)
        VBox info = new VBox(5);
        Label lblNombre = new Label(emp.getNombre());
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e293b;");

        Label lblNotaText = new Label(emp.getNota());
        lblNotaText.setWrapText(true);
        lblNotaText.setMaxWidth(280);
        lblNotaText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        info.getChildren().addAll(lblNombre, lblNotaText);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Badge de nota numérica
        Label lblNotaNum = new Label(String.format("%.1f", emp.getValoracion()));
        double nota = emp.getValoracion();
        String color = (nota < 5) ? "#ef4444" : (nota >= 8.5) ? "#10b981" : "#1e293b";
        String fondo = (nota < 5) ? "#fef2f2" : (nota >= 8.5) ? "#ecfdf5" : "#f1f5f9";

        lblNotaNum.setStyle("-fx-text-fill: " + color + "; -fx-background-color: " + fondo +
                "; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 10;");

        card.getChildren().addAll(info, spacer, lblNotaNum);
        return card;
    }

    private VBox crearTarjetaObjetivo(String desc, String estado) {
        VBox card = new VBox(8);
        card.getStyleClass().add("item-card");
        card.setPadding(new Insets(15));

        Label lblDesc = new Label(desc);
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");

        Label lblEstado = new Label(estado.toUpperCase());
        String estiloEstado = "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 20;";

        if (estado.equalsIgnoreCase("En curso")) {
            estiloEstado += "-fx-text-fill: #f59e0b; -fx-background-color: #fffbeb;";
        } else if (estado.equalsIgnoreCase("Completado")) {
            estiloEstado += "-fx-text-fill: #10b981; -fx-background-color: #ecfdf5;";
        } else {
            estiloEstado += "-fx-text-fill: #ef4444; -fx-background-color: #fef2f2;";
        }

        lblEstado.setStyle(estiloEstado);
        card.getChildren().addAll(lblDesc, lblEstado);
        return card;
    }

    @FXML
    private void volverAlHub(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vistas/SupervisorHub.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}