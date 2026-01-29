package controladores;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.HistorialValoracion;
import modelo.HistorialValoracionDAO;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

import java.util.List;

public class HistorialValoracionController {

    @FXML private ComboBox<Trabajador> comboTrabajadores;
    @FXML private Button btnBuscar;
    @FXML private Button btnVolver;

    @FXML private TableView<HistorialValoracion> tablaHistorial;
    @FXML private TableColumn<HistorialValoracion, String> colFecha;
    @FXML private TableColumn<HistorialValoracion, Double> colAnterior;
    @FXML private TableColumn<HistorialValoracion, Double> colNueva;
    @FXML private TableColumn<HistorialValoracion, String> colNotaAnterior;
    @FXML private TableColumn<HistorialValoracion, String> colNotaNueva;
    @FXML private Label lblAlerta;

    private final HistorialValoracionDAO historialDAO = new HistorialValoracionDAO();
    private final TrabajadorDAO trabajadorDAO = new TrabajadorDAO();

    @FXML
    public void initialize() {

        // Configurar columnas
        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFecha()));

        colAnterior.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getValoracionAnterior()).asObject());

        colNueva.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getValoracionNueva()).asObject());

        colNotaAnterior.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNotaAnterior()));

        colNotaNueva.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNotaNueva()));

        comboTrabajadores.getItems().setAll(trabajadorDAO.obtenerTrabajadores());

        comboTrabajadores.setOnAction(e -> {
            Trabajador t = comboTrabajadores.getValue();
            if (t != null) cargarHistorial(t.getId());
        });

        btnBuscar.setOnAction(e -> {
            Trabajador t = comboTrabajadores.getValue();
            if (t != null) cargarHistorial(t.getId());
        });

        // Botón Volver (corregido)
        btnVolver.setOnAction(e -> volverAlLogin());
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
        tablaHistorial.getItems().setAll(historial);

        String alerta = generarAlerta(historial);
        lblAlerta.setText(alerta);
    }

    public void setIdTrabajador(int id) {
        for (Trabajador t : comboTrabajadores.getItems()) {
            if (t.getId() == id) {
                comboTrabajadores.setValue(t);
                break;
            }
        }
        cargarHistorial(id);
    }

    private String generarAlerta(List<HistorialValoracion> historial) {

        if (historial.size() < 2) {
            return "Sin datos suficientes para generar alertas.";
        }

        double ultima = historial.get(0).getValoracionNueva();
        double anterior = historial.get(1).getValoracionNueva();

        // 1. Bajada de rendimiento
        if (ultima < anterior) {
            return "⚠ El trabajador ha bajado su rendimiento.";
        }

        // 2. Mejora continua (3 mejoras seguidas)
        if (historial.size() >= 3) {
            double v1 = historial.get(0).getValoracionNueva();
            double v2 = historial.get(1).getValoracionNueva();
            double v3 = historial.get(2).getValoracionNueva();

            if (v1 > v2 && v2 > v3) {
                return "📈 El trabajador está mejorando de forma constante.";
            }
        }

        // 3. Caída brusca (> 2 puntos)
        if (anterior - ultima >= 2) {
            return "🚨 Caída brusca detectada en la valoración.";
        }

        return "Sin alertas relevantes.";
    }

}
