package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

        // Hacer tabla editable
        tablaCoordinador.setEditable(true);

        colValoracion.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colNota.setCellFactory(TextFieldTableCell.forTableColumn());

        // Guardar cambios al editar valoración
        colValoracion.setOnEditCommit(event -> {
            Trabajador t = event.getRowValue();
            double nuevaValoracion = event.getNewValue();
            t.setValoracion(nuevaValoracion);
            dao.actualizarValoracionYNota(t.getId(), nuevaValoracion, t.getNota());
        });

        // Guardar cambios al editar nota
        colNota.setOnEditCommit(event -> {
            Trabajador t = event.getRowValue();
            String nuevaNota = event.getNewValue();
            t.setNota(nuevaNota);
            dao.actualizarValoracionYNota(t.getId(), t.getValoracion(), nuevaNota);
        });

        // Cargar datos iniciales
        refrescarTabla();

        // Eventos
        btnBuscar.setOnAction(e -> buscarTrabajador());
        btnDetalles.setOnAction(e -> verDetalles());
        btnAsignar.setOnAction(e -> asignarTarea());
        btnActualizar.setOnAction(e -> refrescarTabla());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    // MÉTODO CENTRAL PARA RECARGAR LA TABLA
    public void refrescarTabla() {
        tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores());
    }

    private void buscarTrabajador() {
        String filtro = txtBuscar.getText().trim();

        if (filtro.isEmpty()) {
            refrescarTabla();
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
