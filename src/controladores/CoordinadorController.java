package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

import java.io.IOException;
import java.util.List;

public class CoordinadorController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Trabajador> tablaCoordinador;
    @FXML private TableColumn<Trabajador, String> colNombre;
    @FXML private TableColumn<Trabajador, String> colDepartamento;
    @FXML private TableColumn<Trabajador, Double> colValoracion;
    @FXML private TableColumn<Trabajador, String> colNota;
    @FXML private Button btnBuscar, btnDetalles, btnAsignar, btnActualizar, btnVolver;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colDepartamento.setCellValueFactory(cellData -> cellData.getValue().departamentoProperty());
        colValoracion.setCellValueFactory(cellData -> cellData.getValue().valoracionProperty().asObject());
        colNota.setCellValueFactory(cellData -> cellData.getValue().notaProperty());

        tablaCoordinador.setEditable(true);
        colValoracion.setEditable(true);
        colNota.setEditable(true);

        colValoracion.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colNota.setCellFactory(TextFieldTableCell.forTableColumn());

        colValoracion.setOnEditCommit(event -> {
            Trabajador t = event.getRowValue();
            double anterior = t.getValoracion();
            double nuevo = event.getNewValue();
            t.setValoracion(nuevo);
            dao.actualizarValoracionYNota(t.getId(), nuevo, t.getNota());
            dao.insertarHistorial(t.getId(), 1, anterior, nuevo, t.getNota(), t.getNota());
            refrescarTabla();
        });

        colNota.setOnEditCommit(event -> {
            Trabajador t = event.getRowValue();
            String anterior = t.getNota();
            String nuevo = event.getNewValue();
            t.setNota(nuevo);
            dao.actualizarValoracionYNota(t.getId(), t.getValoracion(), nuevo);
            dao.insertarHistorial(t.getId(), 1, t.getValoracion(), t.getValoracion(), anterior, nuevo);
            refrescarTabla();
        });

        refrescarTabla();

        btnBuscar.setOnAction(e -> buscarTrabajador());
        btnDetalles.setOnAction(e -> verDetalles());
        btnAsignar.setOnAction(e -> mostrarAlerta("Función de asignar tarea aún no implementada."));
        btnActualizar.setOnAction(e -> refrescarTabla());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    private void refrescarTabla() {
        tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores());
    }

    private void buscarTrabajador() {
        String filtro = txtBuscar.getText().trim();
        List<Trabajador> resultados = filtro.isEmpty() ? dao.obtenerTrabajadores() : dao.obtenerTrabajadores(filtro);
        tablaCoordinador.getItems().setAll(resultados);
    }

    private void verDetalles() {
        Trabajador seleccionado = tablaCoordinador.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un trabajador para ver sus detalles.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Trabajador.fxml"));
            Parent root = loader.load();
            TrabajadorController controller = loader.getController();
            controller.setTrabajador(seleccionado);
            Stage stage = new Stage();
            stage.setTitle("Ficha del Empleado");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void volverAlMenu() {
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
