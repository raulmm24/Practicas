package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private ComboBox<String> comboRol;

    @FXML
    public void entrarSistema() {
        String rol = comboRol.getValue();

        if (rol == null) {
            System.out.println("Debes seleccionar un rol.");
            return;
        }

        try {
            String archivoFXML = switch (rol) {
                case "Supervisor" -> "/vistas/SupervisorEquipo.fxml";
                case "Coordinador" -> "/vistas/Coordinador.fxml";
                case "Trabajador" -> "/vistas/Trabajador.fxml";
                default -> null;
            };

            if (archivoFXML == null) {
                System.out.println("Rol no reconocido.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(archivoFXML));
            Parent root = loader.load();

            Stage stage = (Stage) comboRol.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
