package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Trabajador;

public class TrabajadorController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDepartamento;
    @FXML private TextField txtValoracion;
    @FXML private TextArea txtNota;

    @FXML private Button btnCerrar;
    @FXML private Button btnVolver;

    private Trabajador trabajador;

    @FXML
    public void initialize() {
        btnCerrar.setOnAction(e -> cerrarVentana());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
        cargarDatos();
    }

    private void cargarDatos() {
        if (trabajador == null) return;

        txtNombre.setText(trabajador.getNombre());
        txtDepartamento.setText(trabajador.getDepartamento());
        txtValoracion.setText(String.valueOf(trabajador.getValoracion()));
        txtNota.setText(trabajador.getNota());
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
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
