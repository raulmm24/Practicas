package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.HistorialDAO;
import modelo.HistorialValoracion;

import java.util.List;

public class HistorialValoracionesController {

    @FXML private ComboBox<String> comboTrabajadores;
    @FXML private TableView<HistorialValoracion> tablaHistorial;

    @FXML private TableColumn<HistorialValoracion, String> colFecha;
    @FXML private TableColumn<HistorialValoracion, Double> colValoracionAnterior;
    @FXML private TableColumn<HistorialValoracion, Double> colValoracionNueva;
    @FXML private TableColumn<HistorialValoracion, String> colNotaAnterior;
    @FXML private TableColumn<HistorialValoracion, String> colNotaNueva;
    @FXML private TableColumn<HistorialValoracion, String> colSupervisor;

    @FXML private Button btnCargar;
    @FXML private Button btnVolver;

    private final HistorialDAO dao = new HistorialDAO();

    @FXML
    public void initialize() {

        comboTrabajadores.getItems().setAll(dao.obtenerNombresTrabajadores());

        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colValoracionAnterior.setCellValueFactory(c -> c.getValue().valoracionAnteriorProperty().asObject());
        colValoracionNueva.setCellValueFactory(c -> c.getValue().valoracionNuevaProperty().asObject());
        colNotaAnterior.setCellValueFactory(c -> c.getValue().notaAnteriorProperty());
        colNotaNueva.setCellValueFactory(c -> c.getValue().notaNuevaProperty());
        colSupervisor.setCellValueFactory(c -> c.getValue().supervisorProperty());

        btnCargar.setOnAction(e -> cargarHistorial());
        btnVolver.setOnAction(e -> volver());
    }

    private void cargarHistorial() {
        String nombre = comboTrabajadores.getValue();
        if (nombre == null) return;

        List<HistorialValoracion> lista = dao.obtenerHistorialPorTrabajador(nombre);
        tablaHistorial.getItems().setAll(lista);
    }

    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
