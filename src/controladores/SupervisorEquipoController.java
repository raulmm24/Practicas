package controladores;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.SupervisorDAO;
import modelo.TrabajadorSeleccion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupervisorEquipoController {

    @FXML private ComboBox<String> comboDepartamento;

    // CAMBIO: Ahora usamos un VBox para la lista vertical
    @FXML private VBox vboxTrabajadores;

    @FXML private Button btnCargar, btnGuardar, btnVolver, btnAñadir, btnEliminarSeleccionado, btnTogglePanel;
    @FXML private TextField txtBuscar;
    @FXML private VBox panelLateral;
    @FXML private ListView<TrabajadorSeleccion> listaTrabajadoresPanel;

    private final SupervisorDAO dao = new SupervisorDAO();
    private int idSupervisor;
    private List<TrabajadorSeleccion> listaActual = new ArrayList<>();
    private FilteredList<TrabajadorSeleccion> listaFiltrada;

    public void setIdSupervisor(int id) { this.idSupervisor = id; }

    @FXML
    public void initialize() {
        comboDepartamento.getItems().setAll(dao.obtenerDepartamentos());

        // Mejora visual de la lista lateral
        listaTrabajadoresPanel.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TrabajadorSeleccion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText("👤  " + item.getNombre());
                }
            }
        });

        btnCargar.setOnAction(e -> cargarTrabajadores());
        btnGuardar.setOnAction(e -> guardarEquipo());
        btnVolver.setOnAction(e -> volver());
        btnAñadir.setOnAction(e -> mostrarModalAñadir());
        btnEliminarSeleccionado.setOnAction(e -> eliminarSeleccionadoPanel());
        btnTogglePanel.setOnAction(e -> togglePanelLateral());

        txtBuscar.textProperty().addListener((obs, oldV, newV) -> filtrarTrabajadores(newV));

        // Animación buscador
        txtBuscar.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(txtBuscar.prefWidthProperty(), isFocused ? 300 : 220);
            KeyFrame kf = new KeyFrame(Duration.millis(250), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
        });
    }

    private void cargarTrabajadores() {
        String nombreDpto = comboDepartamento.getSelectionModel().getSelectedItem();
        if (nombreDpto == null) {
            mostrar("Atención", "Selecciona un departamento primero.");
            return;
        }

        listaActual = dao.obtenerTrabajadoresPorDepartamento(nombreDpto);
        listaFiltrada = new FilteredList<>(FXCollections.observableArrayList(listaActual), t -> true);

        refrescarListaVertical();
        refrescarPanelLateral();
    }

    private void filtrarTrabajadores(String filtro) {
        if (listaFiltrada == null) return;
        String f = filtro == null ? "" : filtro.toLowerCase().trim();
        listaFiltrada.setPredicate(t -> f.isEmpty() || t.getNombre().toLowerCase().contains(f));
        refrescarListaVertical();
    }

    // CAMBIO: Lógica para llenar el VBox verticalmente
    private void refrescarListaVertical() {
        vboxTrabajadores.getChildren().clear();
        double delay = 0;

        for (TrabajadorSeleccion t : listaFiltrada) {
            Pane tarjeta = crearTarjetaVisual(t);
            vboxTrabajadores.getChildren().add(tarjeta);

            // Animación de cascada
            tarjeta.setOpacity(0);
            tarjeta.setTranslateY(10);
            FadeTransition ft = new FadeTransition(Duration.millis(300), tarjeta);
            ft.setToValue(1);
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), tarjeta);
            tt.setToY(0);

            ParallelTransition pt = new ParallelTransition(ft, tt);
            pt.setDelay(Duration.millis(delay));
            pt.play();

            delay += 40;
        }
    }

    private Pane crearTarjetaVisual(TrabajadorSeleccion t) {
        VBox tarjeta = new VBox(12);
        tarjeta.getStyleClass().add("card-objetivo"); // Usamos tu clase de estilo premium
        tarjeta.setMaxWidth(Double.MAX_VALUE); // Para que ocupe todo el ancho del VBox
        tarjeta.setPadding(new Insets(15, 20, 15, 20));
        tarjeta.setUserData(t);

        // 1. Fila superior: Info y Selección
        HBox filaCabecera = new HBox();
        filaCabecera.setAlignment(Pos.CENTER_LEFT);

        VBox textoNombre = new VBox(-2);
        Label dpto = new Label(t.getDepartamento().toUpperCase());
        dpto.setStyle("-fx-text-fill: #f97316; -fx-font-weight: bold; -fx-font-size: 10px;");
        Label nombre = new Label(t.getNombre());
        nombre.getStyleClass().add("label-titulo-meta"); // Reutilizamos tu clase de título visible
        textoNombre.getChildren().addAll(dpto, nombre);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        CheckBox cb = new CheckBox();
        cb.setSelected(t.isSeleccionado());
        cb.selectedProperty().addListener((o, ov, nv) -> t.setSeleccionado(nv));

        filaCabecera.getChildren().addAll(textoNombre, spacer, cb);

        // 2. Fila inferior: Inputs de datos
        HBox filaDatos = new HBox(15);
        filaDatos.setAlignment(Pos.CENTER_LEFT);

        Label iconStar = new Label("⭐");

        TextField txtVal = new TextField(String.valueOf(t.getValoracion()));
        txtVal.setPrefWidth(55);
        txtVal.getStyleClass().add("valoracion-input");
        txtVal.textProperty().addListener((o, ov, nv) -> {
            try { t.setValoracion(Double.parseDouble(nv)); } catch(Exception ignored){}
        });

        TextField txtNota = new TextField(t.getNota());
        txtNota.setPromptText("Añadir observaciones sobre el rendimiento...");
        txtNota.getStyleClass().add("nota-box");
        HBox.setHgrow(txtNota, Priority.ALWAYS); // Expandir la nota a todo el ancho disponible
        txtNota.textProperty().addListener((o, ov, nv) -> t.setNota(nv));

        filaDatos.getChildren().addAll(iconStar, txtVal, txtNota);

        tarjeta.getChildren().addAll(filaCabecera, filaDatos);

        // Efecto hover
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-border-color: #3b82f6; -fx-background-color: #f8fafc;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle(""));

        return tarjeta;
    }

    private void refrescarPanelLateral() {
        listaTrabajadoresPanel.setItems(FXCollections.observableArrayList(listaActual));
    }

    private void eliminarSeleccionadoPanel() {
        TrabajadorSeleccion sel = listaTrabajadoresPanel.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrar("Error", "Selecciona a alguien en la lista lateral."); return; }
        dao.eliminarTrabajador(sel.getId());
        cargarTrabajadores();
    }

    private void togglePanelLateral() {
        boolean visible = panelLateral.isVisible();
        if (visible) {
            panelLateral.setVisible(false);
            panelLateral.setManaged(false);
        } else {
            panelLateral.setVisible(true);
            panelLateral.setManaged(true);
        }
    }

    private void mostrarModalAñadir() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Trabajador");
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        TextField n = new TextField(); n.setPromptText("Nombre completo");
        ComboBox<String> d = new ComboBox<>(); d.getItems().setAll(dao.obtenerDepartamentos());
        TextField v = new TextField(); v.setPromptText("Valoración (0-10)");
        TextField nt = new TextField(); nt.setPromptText("Observaciones...");

        root.getChildren().addAll(new Label("Nombre"), n, new Label("Departamento"), d, new Label("Rating inicial"), v, new Label("Nota"), nt);
        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.insertarTrabajador(n.getText(), d.getValue(), Double.parseDouble(v.getText()), nt.getText());
                    cargarTrabajadores();
                } catch (Exception ex) {
                    mostrar("Error", "Datos inválidos.");
                }
            }
        });
    }

    private void guardarEquipo() {
        String dpto = comboDepartamento.getSelectionModel().getSelectedItem();
        if (dpto == null) return;

        List<Integer> seleccionados = listaActual.stream()
                .filter(TrabajadorSeleccion::isSeleccionado)
                .map(TrabajadorSeleccion::getId)
                .collect(Collectors.toList());

        dao.asignarEquipo(idSupervisor, seleccionados, dpto);

        for (TrabajadorSeleccion t : listaActual) {
            if (t.getValoracion() != t.getValoracionOriginal() || !t.getNota().equals(t.getNotaOriginal())) {
                dao.actualizarValoracionYNota(t.getId(), t.getValoracion(), t.getNota());
            }
        }
        mostrar("Éxito", "Gestión de equipo actualizada.");
    }

    private void volver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vistas/SupervisorHub.fxml"));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrar(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}