package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.HistorialValoracion;
import modelo.HistorialValoracionDAO;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

import java.util.*;

public class HistorialValoracionController {

    @FXML private ComboBox<Trabajador> comboTrabajadores;
    @FXML private Button btnBuscar;
    @FXML private Button btnVolver;

    @FXML private LineChart<String, Number> graficoEvolucion;
    @FXML private PieChart graficoDonut;

    @FXML private ComboBox<String> comboFechas;

    @FXML private Label lblAlerta;

    private final HistorialValoracionDAO historialDAO = new HistorialValoracionDAO();
    private final TrabajadorDAO trabajadorDAO = new TrabajadorDAO();

    @FXML
    public void initialize() {

        comboTrabajadores.getItems().setAll(trabajadorDAO.obtenerTrabajadores());

        comboTrabajadores.setOnAction(e -> cargarSeleccionado());
        btnBuscar.setOnAction(e -> cargarSeleccionado());

        btnVolver.setOnAction(e -> volverAlLogin());
    }

    private void cargarSeleccionado() {
        Trabajador t = comboTrabajadores.getValue();
        if (t != null) cargarHistorial(t.getId());
    }

    private void volverAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarHistorial(int idTrabajador) {
        List<HistorialValoracion> historial = historialDAO.obtenerHistorialPorTrabajador(idTrabajador);

        actualizarGraficoEvolucion(historial);
        actualizarGraficoDonut(historial);
        cargarFechasEnCombo(historial);

        lblAlerta.setText(generarAlerta(historial));
    }

    private void actualizarGraficoEvolucion(List<HistorialValoracion> historial) {
        graficoEvolucion.getData().clear();
        graficoEvolucion.setLegendVisible(false);
        graficoEvolucion.getXAxis().setTickLabelsVisible(false);
        graficoEvolucion.getXAxis().setOpacity(0);
        graficoEvolucion.getYAxis().setTickLabelsVisible(false);
        graficoEvolucion.getYAxis().setOpacity(0);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("");

        for (HistorialValoracion h : historial) {
            String fecha = h.getFecha().split(" ")[0];
            serie.getData().add(new XYChart.Data<>(fecha, h.getValoracionNueva()));
        }

        graficoEvolucion.getData().add(serie);
    }

    private void actualizarGraficoDonut(List<HistorialValoracion> historial) {
        graficoDonut.getData().clear();
        graficoDonut.setLegendVisible(false);

        int mejoras = 0, bajadas = 0, neutros = 0;

        for (int i = 1; i < historial.size(); i++) {
            double antes = historial.get(i).getValoracionNueva();
            double despues = historial.get(i - 1).getValoracionNueva();

            if (despues > antes) mejoras++;
            else if (despues < antes) bajadas++;
            else neutros++;
        }

        graficoDonut.getData().add(new PieChart.Data("", mejoras));
        graficoDonut.getData().add(new PieChart.Data("", bajadas));
        graficoDonut.getData().add(new PieChart.Data("", neutros));
    }

    private void cargarFechasEnCombo(List<HistorialValoracion> historial) {
        comboFechas.getItems().clear();

        Set<String> fechas = new LinkedHashSet<>();
        for (HistorialValoracion h : historial) {
            fechas.add(h.getFecha().split(" ")[0]);
        }

        comboFechas.getItems().addAll(fechas);

        comboFechas.setOnAction(e -> {
            String fechaSeleccionada = comboFechas.getValue();
            if (fechaSeleccionada == null) return;

            for (HistorialValoracion h : historial) {
                if (h.getFecha().startsWith(fechaSeleccionada)) {
                    mostrarPopupDetalle(h);
                    break;
                }
            }
        });
    }

    private void mostrarPopupDetalle(HistorialValoracion h) {
        double antes = h.getValoracionAnterior();
        double despues = h.getValoracionNueva();

        String icono;
        String color;

        if (despues > antes) {
            icono = "🟢⬆ Mejora";
            color = "#2ecc71"; // verde
        } else if (despues < antes) {
            icono = "🔴⬇ Bajada";
            color = "#e74c3c"; // rojo
        } else {
            icono = "⚪➖ Sin cambios";
            color = "#bdc3c7"; // gris
        }

        String contenido =
                icono + "\n\n" +
                        "📅 " + h.getFecha().split(" ")[0] + "\n\n" +
                        "Valoración: " + antes + " → " + despues + "\n\n" +
                        "Nota anterior: " + h.getNotaAnterior() + "\n" +
                        "Nota nueva: " + h.getNotaNueva();

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Detalle de valoración");
        alerta.setHeaderText(null);

        // Usamos un Label como contenido para aplicar estilo directamente
        Label contenidoLabel = new Label(contenido);
        contenidoLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-line-spacing: 4px;"
        );
        alerta.getDialogPane().setContent(contenidoLabel);

        // Fondo oscuro del popup
        alerta.getDialogPane().setStyle(
                "-fx-background-color: #1e1e1e;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2;"
        );

        // Estilo del botón Aceptar
        alerta.getDialogPane().lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        alerta.showAndWait();
    }


    private String generarAlerta(List<HistorialValoracion> historial) {

        if (historial.size() < 2) {
            return "Sin datos suficientes para generar alertas.";
        }

        double ultima = historial.get(0).getValoracionNueva();
        double anterior = historial.get(1).getValoracionNueva();

        if (ultima < anterior) {
            return "⚠ El trabajador ha bajado su rendimiento.";
        }

        if (ultima > anterior) {
            return "📈 El trabajador ha mejorado su rendimiento."; }

        if (anterior - ultima >= 2) {
            return "🚨 Caída brusca detectada en la valoración.";
        }

        return "Sin alertas relevantes.";
    }
}
