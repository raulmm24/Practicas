package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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

    @FXML private Button btnBuscar;
    @FXML private Button btnDetalles;
    @FXML private Button btnAsignar;
    @FXML private Button btnActualizar;
    @FXML private Button btnVolver;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @FXML
    public void initialize() {

        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        colValoracion.setCellValueFactory(new PropertyValueFactory<>("valoracion"));
        colNota.setCellValueFactory(new PropertyValueFactory<>("nota"));

        // Cargar datos iniciales
        tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores());

        // Eventos
        btnBuscar.setOnAction(e -> buscarTrabajador());
        btnDetalles.setOnAction(e -> verDetalles());
        btnAsignar.setOnAction(e -> asignarTarea());
        btnActualizar.setOnAction(e -> actualizarDatos());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    private void buscarTrabajador() {
        String filtro = txtBuscar.getText().trim();

        if (filtro.isEmpty()) {
            tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores());
            return;
        }

        List<Trabajador> resultados = dao.buscarTrabajadores(filtro);
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

    private void asignarTarea() {
        mostrarAlerta("Función de asignar tarea aún no implementada.");
    }

    private void actualizarDatos() {
        tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores());
        mostrarAlerta("Datos actualizados correctamente.");
    }

    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Menu.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Menú Principal");
            stage.setScene(new Scene(root));
            stage.show();

            Stage ventanaActual = (Stage) btnVolver.getScene().getWindow();
            ventanaActual.close();

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
