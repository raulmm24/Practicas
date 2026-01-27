package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.HistorialValoracion;
import modelo.HistorialValoracionDAO;
import modelo.TrabajadorDAO;

public class HistorialValoracionController {

    @FXML private ComboBox<String> comboTrabajadores;
    @FXML private TableView<HistorialValoracion> tablaHistorial;

    @FXML private TableColumn<HistorialValoracion, String> colFecha;
    @FXML private TableColumn<HistorialValoracion, Number> colValAnt;
    @FXML private TableColumn<HistorialValoracion, Number> colValNueva;
    @FXML private TableColumn<HistorialValoracion, String> colNotaAnt;
    @FXML private TableColumn<HistorialValoracion, String> colNotaNueva;

    @FXML private Button btnVolver;

    private final HistorialValoracionDAO dao = new HistorialValoracionDAO();

    @FXML
    public void initialize() {

        // Cargar trabajadores en el ComboBox
        TrabajadorDAO tdao = new TrabajadorDAO();
        comboTrabajadores.getItems().setAll(tdao.obtenerTrabajadoresParaCombo());

        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colValAnt.setCellValueFactory(c -> c.getValue().valoracionAnteriorProperty());
        colValNueva.setCellValueFactory(c -> c.getValue().valoracionNuevaProperty());
        colNotaAnt.setCellValueFactory(c -> c.getValue().notaAnteriorProperty());
        colNotaNueva.setCellValueFactory(c -> c.getValue().notaNuevaProperty());

        // ⭐ Solución definitiva para Nota Nueva
        colNotaNueva.setCellFactory(column -> new TableCell<HistorialValoracion, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item);
                setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 13px; -fx-padding: 6px;");
            }
        });
    }

    @FXML
    public void cargarHistorial() {
        String nombre = comboTrabajadores.getSelectionModel().getSelectedItem();
        if (nombre == null) return;

        int id = Integer.parseInt(nombre.split("-")[0].trim());
        tablaHistorial.getItems().setAll(dao.obtenerHistorialPorTrabajador(id));
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
