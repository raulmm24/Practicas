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
    @FXML private Button btnGuardar;
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
        btnGuardar.setOnAction(e -> guardarCambios());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    private void abrirFichaTrabajador() {
        Trabajador seleccionado = tablaEquipo.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un trabajador para ver su ficha.");
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

    private void guardarCambios() {
        Trabajador seleccionado = tablaEquipo.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un trabajador para actualizar.");
            return;
        }

        try {
            double nuevaValoracion = Double.parseDouble(txtValoracion.getText());
            String nuevaNota = txtNota.getText();

            boolean ok = dao.actualizarValoracionYNota(
                    seleccionado.getId(),
                    nuevaValoracion,
                    nuevaNota
            );

            if (ok) {
                mostrarAlerta("Datos actualizados correctamente.");
                tablaEquipo.getItems().setAll(dao.obtenerTrabajadores());
            } else {
                mostrarAlerta("No se pudo actualizar.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("La valoración debe ser un número válido.");
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
