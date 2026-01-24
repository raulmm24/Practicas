package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CoordinadorController {

    @FXML private Button btnVolver;

    @FXML
    public void initialize() {
        btnVolver.setOnAction(e -> volverAlMenu());
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



