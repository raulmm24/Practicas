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

public class SupervisorController {

    @FXML private TableView<Trabajador> tablaEquipo;

    @FXML private TableColumn<Trabajador, String> colNombre;
    @FXML private TableColumn<Trabajador, String> colDepartamento;
    @FXML private TableColumn<Trabajador, Double> colValoracion;
    @FXML private TableColumn<Trabajador, String> colNota;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDepartamento;
    @FXML private TextField txtValoracion;
    @FXML private TextArea txtNota;

    @FXML private Button btnVerFicha;
    @FXML private Button btnVolver;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @FXML
    public void initialize() {

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        colValoracion.setCellValueFactory(new PropertyValueFactory<>("valoracion"));
        colNota.setCellValueFactory(new PropertyValueFactory<>("nota"));

        tablaEquipo.getItems().setAll(dao.obtenerTrabajadores());

        tablaEquipo.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombre.setText(newSel.getNombre());
                txtDepartamento.setText(newSel.getDepartamento());
                txtValoracion.setText(String.valueOf(newSel.getValoracion()));
                txtNota.setText(newSel.getNota());
            }
        });

        btnVerFicha.setOnAction(e -> abrirFichaTrabajador());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    private void abrirFichaTrabajador() {
        Trabajador seleccionado = tablaEquipo.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Selecciona un trabajador para ver su ficha.");
            alert.showAndWait();
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
}
