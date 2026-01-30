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

import static javafx.scene.layout.TilePane.setMargin;

public class SupervisorEquipoController {

    @FXML private ComboBox<String> comboDepartamento;
    @FXML private GridPane gridTrabajadores;
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
        // Cargar departamentos al inicio
        comboDepartamento.getItems().setAll(dao.obtenerDepartamentos());

        // --- MEJORA DE LA LISTA LATERAL (Cápsulas de cristal) ---
        listaTrabajadoresPanel.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TrabajadorSeleccion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                } else {
                    setText("👤  " + item.getNombre());
                    // Aseguramos que el estilo de margen se aplique a cada celda para que no se peguen
                    setMargin(this, new Insets(5, 0, 5, 0));
                }
            }
        });

        // Listeners de botones
        btnCargar.setOnAction(e -> cargarTrabajadores());
        btnGuardar.setOnAction(e -> guardarEquipo());
        btnVolver.setOnAction(e -> volver());
        btnAñadir.setOnAction(e -> mostrarModalAñadir());
        btnEliminarSeleccionado.setOnAction(e -> eliminarSeleccionadoPanel());
        btnTogglePanel.setOnAction(e -> togglePanelLateral());

        // Buscador Dinámico
        txtBuscar.textProperty().addListener((obs, oldV, newV) -> filtrarTrabajadores(newV));

        // Animación de expansión para el buscador
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

        refrescarGrid();
        refrescarPanelLateral();
    }

    private void filtrarTrabajadores(String filtro) {
        if (listaFiltrada == null) return;
        String f = filtro == null ? "" : filtro.toLowerCase().trim();
        listaFiltrada.setPredicate(t -> f.isEmpty() || t.getNombre().toLowerCase().contains(f));
        refrescarGrid();
    }

    private void refrescarGrid() {
        gridTrabajadores.getChildren().clear();
        int col = 0, row = 0;
        double delay = 0;

        for (TrabajadorSeleccion t : listaFiltrada) {
            Pane tarjeta = crearTarjetaVisual(t);
            gridTrabajadores.add(tarjeta, col, row);

            // Animación de entrada suave (Fade + Slide)
            tarjeta.setOpacity(0);
            tarjeta.setTranslateY(15);
            FadeTransition ft = new FadeTransition(Duration.millis(400), tarjeta);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(delay));
            TranslateTransition tt = new TranslateTransition(Duration.millis(400), tarjeta);
            tt.setToY(0);
            tt.setDelay(Duration.millis(delay));

            new ParallelTransition(ft, tt).play();

            delay += 50;
            col++;
            if (col == 3) { col = 0; row++; }
        }
    }

    private Pane crearTarjetaVisual(TrabajadorSeleccion t) {
        VBox tarjeta = new VBox(10); // Espaciado vertical definido en CSS (.tarjeta-visual)
        tarjeta.getStyleClass().add("tarjeta-visual");
        tarjeta.setPrefWidth(380);
        tarjeta.setUserData(t);

        // 1. Cabecera (Dpto, Nombre y Checkbox)
        HBox cabecera = new HBox();
        cabecera.setAlignment(Pos.CENTER_LEFT);

        VBox textoNombre = new VBox(-2);
        Label dpto = new Label(t.getDepartamento());
        dpto.getStyleClass().add("subtitulo");
        Label nombre = new Label(t.getNombre());
        nombre.getStyleClass().add("nombre");
        textoNombre.getChildren().addAll(dpto, nombre);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        CheckBox cb = new CheckBox();
        cb.setSelected(t.isSeleccionado());
        cb.selectedProperty().addListener((o, ov, nv) -> t.setSeleccionado(nv));

        cabecera.getChildren().addAll(textoNombre, spacer, cb);

        // 2. Fila de Datos (⭐ + Valoración + Nota) colocada debajo del nombre
        HBox filaDatos = new HBox(10);
        filaDatos.getStyleClass().add("contenedor-datos");

        TextField txtVal = new TextField(String.valueOf(t.getValoracion()));
        txtVal.getStyleClass().add("valoracion-input");
        txtVal.textProperty().addListener((o, ov, nv) -> {
            try { t.setValoracion(Double.parseDouble(nv)); } catch(Exception ignored){}
        });

        TextField txtNota = new TextField(t.getNota());
        txtNota.getStyleClass().add("nota-box");
        txtNota.setPromptText("Escribir nota...");
        HBox.setHgrow(txtNota, Priority.ALWAYS);
        txtNota.textProperty().addListener((o, ov, nv) -> t.setNota(nv));

        filaDatos.getChildren().addAll(new Label("⭐"), txtVal, txtNota);

        // Construir Tarjeta
        tarjeta.getChildren().addAll(cabecera, filaDatos);

        // Hover effect dinámico
        tarjeta.setOnMouseEntered(e -> animateCard(tarjeta, 1.02, 0.2));
        tarjeta.setOnMouseExited(e -> animateCard(tarjeta, 1.0, 0.2));

        return tarjeta;
    }

    private void animateCard(Node node, double scale, double duration) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(duration), node);
        st.setToX(scale);
        st.setToY(scale);
        st.setInterpolator(Interpolator.EASE_BOTH);
        st.play();
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
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), panelLateral);
        tt.setInterpolator(Interpolator.EASE_OUT);

        if (visible) {
            tt.setToX(panelLateral.getWidth() + 50); // Sale de la pantalla
            tt.setOnFinished(e -> panelLateral.setVisible(false));
        } else {
            panelLateral.setVisible(true);
            tt.setFromX(panelLateral.getWidth() + 50);
            tt.setToX(0);
        }
        tt.play();
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
                    mostrar("Error", "Datos inválidos. Revisa la valoración numérica.");
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
                dao.guardarHistorial(t.getId(), idSupervisor, t.getValoracionOriginal(), t.getValoracion(), t.getNotaOriginal(), t.getNota());
                dao.actualizarValoracionYNota(t.getId(), t.getValoracion(), t.getNota());
                t.setValoracionOriginal(t.getValoracion());
                t.setNotaOriginal(t.getNota());
            }
        }
        mostrar("Éxito", "Gestión de equipo actualizada correctamente.");
    }

    private void volver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vistas/Login.fxml"));
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