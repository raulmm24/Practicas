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

    @FXML private TextField txtNombre;
    @FXML private TextField txtDepartamento;
    @FXML private TextField txtValoracion;
    @FXML private TextArea txtNota;

    @FXML private Button btnBuscar;
    @FXML private Button btnVolver;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnBuscar.setOnAction(e -> buscarEmpleado());

        btnVolver.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Menu.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Menú Principal");
                stage.setScene(new Scene(root));
                stage.show();

                // Cerrar la ventana actual
                Stage ventanaActual = (Stage) btnVolver.getScene().getWindow();
                ventanaActual.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    private void buscarEmpleado() {
        String filtro = txtBuscar.getText().trim();

        if (filtro.isEmpty()) {
            mostrarAlerta("Escribe un nombre para buscar.");
            return;
        }

        List<Trabajador> resultados = dao.buscarTrabajadores(filtro);

        if (resultados.isEmpty()) {
            mostrarAlerta("No se encontró ningún empleado con ese nombre.");
            return;
        }

        // Tomamos el primer resultado
        setTrabajador(resultados.get(0));
    }

    public void setTrabajador(Trabajador t) {
        if (t == null) return;

        txtNombre.setText(t.getNombre());
        txtDepartamento.setText(t.getDepartamento());
        txtValoracion.setText(String.valueOf(t.getValoracion()));
        txtNota.setText(t.getNota());
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

