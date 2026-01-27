package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @FXML
    public void initialize() {

        // Enlazar columnas con propiedades del modelo
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colDepartamento.setCellValueFactory(c -> c.getValue().departamentoProperty());
        colValoracion.setCellValueFactory(c -> c.getValue().valoracionProperty().asObject());
        colNota.setCellValueFactory(c -> c.getValue().notaProperty());

        // Cargar trabajadores reales en la tabla (NO Strings)
        tablaEquipo.getItems().setAll(dao.obtenerTrabajadores());

        // Listener para seleccionar trabajador
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
            if (txtValoracion.getText().isEmpty()) {
                mostrarAlerta("La valoración no puede estar vacía.");
                return;
            }

            double valoracionAnterior = seleccionado.getValoracion();
            String notaAnterior = seleccionado.getNota();

            double nuevaValoracion = Double.parseDouble(txtValoracion.getText());
            String nuevaNota = txtNota.getText();

            // Actualizar en memoria
            seleccionado.setValoracion(nuevaValoracion);
            seleccionado.setNota(nuevaNota);

            // Actualizar en BD
            boolean ok = dao.actualizarValoracionYNota(
                    seleccionado.getId(),
                    nuevaValoracion,
                    nuevaNota
            );

            // Registrar historial
            dao.insertarHistorial(
                    seleccionado.getId(),
                    1, // id_supervisor
                    valoracionAnterior,
                    nuevaValoracion,
                    notaAnterior,
                    nuevaNota
            );

            if (ok) {
                mostrarAlerta("Datos actualizados correctamente.");
                tablaEquipo.refresh();
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
