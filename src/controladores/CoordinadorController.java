package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

public class CoordinadorController {

    @FXML private TableView<Trabajador> tablaCoordinador;

    @FXML private TableColumn<Trabajador, String> colNombre;
    @FXML private TableColumn<Trabajador, String> colDepartamento;
    @FXML private TableColumn<Trabajador, Double> colValoracion;
    @FXML private TableColumn<Trabajador, String> colNota;

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnDetalles;
    @FXML private Button btnAsignar;
    @FXML private Button btnActualizar;

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

        // Acción del botón buscar
        btnBuscar.setOnAction(e -> buscarTrabajador());

        // Acción del botón actualizar
        btnActualizar.setOnAction(e ->
                tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores())
        );

        // Acción del botón detalles
        btnDetalles.setOnAction(e -> mostrarDetalles());

        // Acción del botón asignar tarea
        btnAsignar.setOnAction(e -> asignarTarea());
    }

    // ------------------------------
    // MÉTODO DE BÚSQUEDA
    // ------------------------------
    private void buscarTrabajador() {
        String filtro = txtBuscar.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores());
            return;
        }

        tablaCoordinador.getItems().setAll(
                dao.obtenerTrabajadores().stream()
                        .filter(t -> t.getNombre().toLowerCase().contains(filtro))
                        .toList()
        );
    }

    // ------------------------------
    // MÉTODO DETALLES
    // ------------------------------
    private void mostrarDetalles() {
        Trabajador seleccionado = tablaCoordinador.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un trabajador para ver detalles.");
            return;
        }

        mostrarAlerta(
                "Detalles del trabajador:\n\n" +
                        "Nombre: " + seleccionado.getNombre() + "\n" +
                        "Departamento: " + seleccionado.getDepartamento() + "\n" +
                        "Valoración: " + seleccionado.getValoracion() + "\n" +
                        "Nota: " + seleccionado.getNota()
        );
    }

    // ------------------------------
    // MÉTODO ASIGNAR TAREA
    // ------------------------------
    private void asignarTarea() {
        Trabajador seleccionado = tablaCoordinador.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un trabajador para asignar una tarea.");
            return;
        }

        mostrarAlerta("Función de asignar tarea pendiente de implementar.");
    }

    // ------------------------------
    // ALERTA SIMPLE
    // ------------------------------
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}


