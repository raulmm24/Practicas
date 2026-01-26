package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.SupervisorDAO;
import modelo.TrabajadorSeleccion;

import java.util.List;
import java.util.stream.Collectors;

public class SupervisorEquipoController {

    @FXML private ComboBox<String> comboDepartamento;
    @FXML private TableView<TrabajadorSeleccion> tablaEquipo;
    @FXML private TableColumn<TrabajadorSeleccion, Boolean> colSeleccion;
    @FXML private TableColumn<TrabajadorSeleccion, String> colNombre;
    @FXML private TableColumn<TrabajadorSeleccion, String> colDepartamento;
    @FXML private TableColumn<TrabajadorSeleccion, Double> colValoracion;
    @FXML private TableColumn<TrabajadorSeleccion, String> colNota;

    @FXML private Button btnCargar;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private final SupervisorDAO dao = new SupervisorDAO();
    private int idSupervisor = 1; // Cambiar cuando tengas login

    @FXML
    public void initialize() {

        comboDepartamento.getItems().setAll(dao.obtenerDepartamentos());

        colSeleccion.setCellValueFactory(cellData -> cellData.getValue().seleccionadoProperty());
        colSeleccion.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccion));

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        colValoracion.setCellValueFactory(new PropertyValueFactory<>("valoracion"));
        colNota.setCellValueFactory(new PropertyValueFactory<>("nota"));

        btnCargar.setOnAction(e -> cargarTrabajadores());
        btnGuardar.setOnAction(e -> guardarEquipo());
        btnVolver.setOnAction(e -> volver());
    }

    private void cargarTrabajadores() {
        String nombreDpto = comboDepartamento.getSelectionModel().getSelectedItem();
        if (nombreDpto == null) {
            mostrar("Selecciona un departamento.");
            return;
        }

        Integer idDpto = dao.obtenerIdDepartamentoPorNombre(nombreDpto);
        if (idDpto == null) {
            mostrar("Departamento no encontrado.");
            return;
        }

        List<TrabajadorSeleccion> lista = dao.obtenerTrabajadoresPorDepartamento(idDpto, idSupervisor);
        tablaEquipo.getItems().setAll(lista);
    }

    private void guardarEquipo() {
        String nombreDpto = comboDepartamento.getSelectionModel().getSelectedItem();
        if (nombreDpto == null) {
            mostrar("Selecciona un departamento.");
            return;
        }

        Integer idDpto = dao.obtenerIdDepartamentoPorNombre(nombreDpto);

        List<Integer> seleccionados = tablaEquipo.getItems().stream()
                .filter(TrabajadorSeleccion::isSeleccionado)
                .map(TrabajadorSeleccion::getId)
                .collect(Collectors.toList());

        dao.asignarEquipo(idSupervisor, seleccionados, idDpto);
        mostrar("Equipo guardado correctamente.");
    }

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


    private void mostrar(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}