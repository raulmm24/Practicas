package controladores;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Notificaciones;
import modelo.SupervisorDAO;
import modelo.TrabajadorSeleccion;
import modelo.Sesion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupervisorEquipoController {

    @FXML private ComboBox<String> comboDepartamento;
    @FXML private VBox vboxTrabajadores;
    @FXML private Button btnCargar, btnGuardar, btnVolver, btnAñadir, btnEliminarSeleccionado, btnTogglePanel;
    @FXML private TextField txtBuscar;
    @FXML private VBox panelLateral;
    @FXML private ListView<TrabajadorSeleccion> listaTrabajadoresPanel;

    private final SupervisorDAO dao = new SupervisorDAO();
    private int idSupervisor;
    private List<TrabajadorSeleccion> listaActual = new ArrayList<>();
    private FilteredList<TrabajadorSeleccion> listaFiltrada;

    @FXML
    public void initialize() {
        this.idSupervisor = Sesion.getIdUsuarioLogueado();

        // 1. Cargar departamentos (Ahora el DAO tiene este método)
        comboDepartamento.getItems().setAll(dao.obtenerDepartamentos());

        // Configuración visual de la lista lateral
        listaTrabajadoresPanel.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TrabajadorSeleccion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("👤  " + item.getNombre());
                }
            }
        });

        // Eventos
        btnCargar.setOnAction(e -> cargarTrabajadores());
        btnGuardar.setOnAction(e -> guardarEquipo());
        btnVolver.setOnAction(e -> volver());
        btnAñadir.setOnAction(e -> mostrarModalAñadir());
        btnEliminarSeleccionado.setOnAction(e -> eliminarSeleccionadoPanel());
        btnTogglePanel.setOnAction(e -> togglePanelLateral());

        txtBuscar.textProperty().addListener((obs, oldV, newV) -> filtrarTrabajadores(newV));

        Platform.runLater(() -> {
            if (btnCargar.getScene() != null) {
                Stage stage = (Stage) btnCargar.getScene().getWindow();
                Notificaciones.show(stage, "🚀 Gestión de Equipo Lista", "SUCCESS");
            }
        });
    }

    private void cargarTrabajadores() {
        String nombreDpto = comboDepartamento.getSelectionModel().getSelectedItem();
        if (nombreDpto == null) {
            mostrarNotificacion("⚠️ Selecciona un departamento", "INFO");
            return;
        }
        listaActual = dao.obtenerTrabajadoresPorDepartamento(nombreDpto);
        listaFiltrada = new FilteredList<>(FXCollections.observableArrayList(listaActual), t -> true);
        refrescarListaVertical();
        refrescarPanelLateral();
    }

    private void filtrarTrabajadores(String filtro) {
        if (listaFiltrada == null) return;
        listaFiltrada.setPredicate(t ->
                filtro == null || filtro.isEmpty() || t.getNombre().toLowerCase().contains(filtro.toLowerCase())
        );
        refrescarListaVertical();
    }

    private void refrescarListaVertical() {
        vboxTrabajadores.getChildren().clear();
        double delay = 0;
        for (TrabajadorSeleccion t : listaFiltrada) {
            Pane tarjeta = crearTarjetaVisual(t);
            vboxTrabajadores.getChildren().add(tarjeta);

            // Animación de entrada
            tarjeta.setOpacity(0);
            tarjeta.setTranslateY(15);
            FadeTransition ft = new FadeTransition(Duration.millis(300), tarjeta);
            ft.setToValue(1);
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), tarjeta);
            tt.setToY(0);
            ParallelTransition pt = new ParallelTransition(ft, tt);
            pt.setDelay(Duration.millis(delay));
            pt.play();
            delay += 50;
        }
    }

    private Pane crearTarjetaVisual(TrabajadorSeleccion t) {
        VBox tarjeta = new VBox(10);
        tarjeta.getStyleClass().add("tarjeta-visual");

        Label dpto = new Label(t.getDepartamento());
        dpto.getStyleClass().add("subtitulo");
        Label nombre = new Label(t.getNombre());
        nombre.getStyleClass().add("nombre");

        CheckBox cb = new CheckBox();
        cb.setSelected(t.isSeleccionado());
        cb.selectedProperty().addListener((o, ov, nv) -> t.setSeleccionado(nv));

        HBox filaCabecera = new HBox(new VBox(dpto, nombre), new Region(), cb);
        HBox.setHgrow(filaCabecera.getChildren().get(1), Priority.ALWAYS);
        filaCabecera.setAlignment(Pos.CENTER_LEFT);

        HBox filaDatos = new HBox(10);
        filaDatos.getStyleClass().add("contenedor-datos");
        filaDatos.setAlignment(Pos.CENTER_LEFT);

        TextField txtVal = new TextField(String.valueOf(t.getValoracion()));
        txtVal.getStyleClass().add("valoracion-input");
        txtVal.setPrefWidth(50);
        txtVal.textProperty().addListener((o, ov, nv) -> {
            try { t.setValoracion(Double.parseDouble(nv)); } catch(Exception ignored){}
        });

        TextField txtNota = new TextField(t.getNota());
        txtNota.setPromptText("Escribir nota...");
        txtNota.getStyleClass().add("nota-box");
        HBox.setHgrow(txtNota, Priority.ALWAYS);
        txtNota.textProperty().addListener((o, ov, nv) -> t.setNota(nv));

        filaDatos.getChildren().addAll(new Label("⭐"), txtVal, txtNota);
        tarjeta.getChildren().addAll(filaCabecera, filaDatos);
        return tarjeta;
    }

    private void guardarEquipo() {
        String dpto = comboDepartamento.getSelectionModel().getSelectedItem();

        if (dpto == null) {
            mostrarNotificacion("⚠️ Selecciona el departamento antes de guardar", "INFO");
            return;
        }

        List<Integer> seleccionados = listaActual.stream()
                .filter(TrabajadorSeleccion::isSeleccionado)
                .map(TrabajadorSeleccion::getId)
                .collect(Collectors.toList());

        try {
            // Guardar asignación de equipo (Relación Supervisor - Trabajador)
            dao.asignarEquipo(idSupervisor, seleccionados, dpto);

            // Guardar valoraciones individuales
            for (TrabajadorSeleccion t : listaActual) {
                dao.actualizarValoracionYNota(t.getId(), t.getValoracion(), t.getNota());
            }

            mostrarNotificacion("✔️ Cambios guardados con éxito", "SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarNotificacion("❌ Error al guardar en Base de Datos", "ERROR");
        }
    }

    private void mostrarModalAñadir() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Trabajador");
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setPrefWidth(300);

        TextField n = new TextField(); n.setPromptText("Nombre");
        ComboBox<String> d = new ComboBox<>(); d.getItems().setAll(dao.obtenerDepartamentos());
        d.setMaxWidth(Double.MAX_VALUE);
        TextField v = new TextField(); v.setPromptText("Rating (0-10)");

        root.getChildren().addAll(new Label("Nombre"), n, new Label("Departamento"), d, new Label("Valoración"), v);
        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                try {
                    dao.insertarTrabajador(n.getText(), d.getValue(), Double.parseDouble(v.getText()), "");
                    cargarTrabajadores();
                    mostrarNotificacion("👤 Añadido correctamente", "SUCCESS");
                } catch (Exception e) {
                    mostrarNotificacion("❌ Error en los datos", "ERROR");
                }
            }
        });
    }

    private void eliminarSeleccionadoPanel() {
        TrabajadorSeleccion sel = listaTrabajadoresPanel.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarNotificacion("⚠️ Selecciona un trabajador en la lista lateral", "INFO");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar a " + sel.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                dao.eliminarTrabajador(sel.getId());
                cargarTrabajadores();
                mostrarNotificacion("🗑️ Trabajador eliminado", "SUCCESS");
            }
        });
    }

    private void togglePanelLateral() {
        panelLateral.setVisible(!panelLateral.isVisible());
        panelLateral.setManaged(panelLateral.isVisible());
    }

    private void refrescarPanelLateral() {
        listaTrabajadoresPanel.setItems(FXCollections.observableArrayList(listaActual));
    }

    private void volver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vistas/SupervisorHub.fxml"));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrarNotificacion(String msj, String tipo) {
        if (btnVolver.getScene() != null) {
            Notificaciones.show((Stage) btnVolver.getScene().getWindow(), msj, tipo);
        }
    }
}