package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

public class SupervisorController {

    @FXML private TableView<Trabajador> tablaEquipo;

    @FXML private TableColumn<Trabajador, String> colNombre;
    @FXML private TableColumn<Trabajador, String> colDepartamento;
    @FXML private TableColumn<Trabajador, Double> colValoracion;
    @FXML private TableColumn<Trabajador, String> colNota;

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

        // Cargar trabajadores reales en la tabla
        tablaEquipo.getItems().setAll(dao.obtenerTrabajadores());

        btnGuardar.setOnAction(e -> guardarCambios());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    private void guardarCambios() {
        Trabajador seleccionado = tablaEquipo.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un trabajador para actualizar.");
            return;
        }

        try {
            double valoracionAnterior = seleccionado.getValoracion();
            String notaAnterior = seleccionado.getNota();

            // Aquí podrías abrir un diálogo para editar valoración y nota
            // pero como quitamos los campos, no se edita desde esta pantalla.

            mostrarAlerta("No hay campos para editar. Opción A aplicada.");

        } catch (Exception e) {
            mostrarAlerta("Error al actualizar.");
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
