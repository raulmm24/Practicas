package controladores;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import modelo.HistorialValoracion;
import modelo.HistorialValoracionDAO;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

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

    private final HistorialValoracionDAO historialDAO = new HistorialValoracionDAO();
    private final TrabajadorDAO trabajadorDAO = new TrabajadorDAO();

    @FXML
    public void initialize() {

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

        btnVolver.setOnAction(e -> {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.close();
        });
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

    private void cargarHistorial(int idTrabajador) {
        tablaHistorial.getItems().setAll(
                historialDAO.obtenerHistorialPorTrabajador(idTrabajador)
        );
    }
}
