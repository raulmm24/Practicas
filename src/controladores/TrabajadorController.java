package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TrabajadorController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVolver;

    @FXML private TableView<Trabajador> tablaTrabajadores;
    @FXML private TableColumn<Trabajador, Number> colId;
    @FXML private TableColumn<Trabajador, String> colNombre;
    @FXML private TableColumn<Trabajador, String> colDepartamento;
    @FXML private TableColumn<Trabajador, Number> colValoracion;
    @FXML private TableColumn<Trabajador, String> colNota;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnBuscar.setOnAction(e -> buscarEmpleado());
        btnVolver.setOnAction(e -> volver());

        // Configurar columnas
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colDepartamento.setCellValueFactory(c -> c.getValue().departamentoProperty());
        colValoracion.setCellValueFactory(c -> c.getValue().valoracionProperty());
        colNota.setCellValueFactory(c -> c.getValue().notaProperty());
    }

    private void buscarEmpleado() {
        String filtro = txtBuscar.getText().trim();

        if (filtro.isEmpty()) {
            mostrarAlerta("Escribe un nombre para buscar.");
            return;
        }

        List<Trabajador> resultados = dao.obtenerTrabajadores(filtro);

        if (resultados.isEmpty()) {
            mostrarAlerta("No se encontró ningún empleado con ese nombre.");
            tablaTrabajadores.getItems().clear();
            return;
        }

        tablaTrabajadores.getItems().setAll(resultados);
    }

    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("No se pudo volver al login.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
