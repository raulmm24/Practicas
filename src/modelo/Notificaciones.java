package modelo;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Notificaciones {

    public static void show(Stage ownerStage, String mensaje, String success) {
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        // Diseño del Toast
        Label texto = new Label(mensaje);
        texto.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox root = new HBox(texto);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(15, 25, 15, 25));
        root.setStyle("-fx-background-color: #334155; -fx-background-radius: 25;"); // Color pizarra oscuro

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        // Posicionamiento (Esquina inferior derecha relativa a la ventana principal)
        toastStage.setX(ownerStage.getX() + ownerStage.getWidth() - 300);
        toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - 100);

        // Animaciones
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2)); // Tiempo que se queda visible
        fadeOut.setOnFinished(e -> toastStage.close());

        toastStage.show();
        fadeIn.play();
        fadeOut.play();
    }
}