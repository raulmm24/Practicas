package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import modelo.SupervisorDAO;
import modelo.TrabajadorSeleccion;

import java.util.List;
import java.util.stream.Collectors;

public class SupervisorEquipoController {

    @FXML private ComboBox<String> comboDepartamento;
    @FXML private TableView<TrabajadorSeleccion> tablaEquipo;

    @FXML private TableColumn<TrabajadorSeleccion, Boolean> colSeleccion;
    @FXML private TableColumn<TrabajadorSeleccion, String> colNombre;
    @FXML private TableColumn<TrabajadorSeleccion, String> colDepartamento;
    @FXML private TableColumn<TrabajadorSeleccion, Double> colValoracion;
    @FXML private TableColumn<TrabajadorSeleccion, String> colNota;

    @FXML private Button btnCargar;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private final SupervisorDAO dao = new SupervisorDAO();
    private int idSupervisor;

    public void setIdSupervisor(int id) {
        this.idSupervisor = id;
    }

    @FXML
    public void initialize() {

        tablaEquipo.setEditable(true);

        // Cargar departamentos
        comboDepartamento.getItems().setAll(dao.obtenerDepartamentos());

        // Columna selección con tick verde
        colSeleccion.setCellValueFactory(cellData -> cellData.getValue().seleccionadoProperty());
        colSeleccion.setCellFactory(col -> new TableCell<TrabajadorSeleccion, Boolean>() {
            @Override
            protected void updateItem(Boolean seleccionado, boolean empty) {
                super.updateItem(seleccionado, empty);

                if (empty || seleccionado == null) {
                    setGraphic(null);
                    return;
                }

                Label icono = new Label(seleccionado ? "✔" : "");
                icono.setStyle("-fx-font-size: 18px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");

                StackPane contenedor = new StackPane(icono);
                contenedor.setAlignment(Pos.CENTER);
                contenedor.setPrefWidth(Double.MAX_VALUE);

                setGraphic(contenedor);
                setText(null);

                setOnMouseClicked(e -> {
                    TrabajadorSeleccion item = getTableView().getItems().get(getIndex());
                    item.setSeleccionado(!item.isSeleccionado());
                    getTableView().refresh();
                });
            }
        });

        // Columnas normales
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colDepartamento.setCellValueFactory(c -> c.getValue().departamentoProperty());
        colValoracion.setCellValueFactory(c -> c.getValue().valoracionProperty().asObject());
        colNota.setCellValueFactory(c -> c.getValue().notaProperty());

        // Valoración editable
        colValoracion.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colValoracion.setOnEditCommit(event -> {
            TrabajadorSeleccion t = event.getRowValue();
            t.setValoracion(event.getNewValue());
            tablaEquipo.refresh();
        });

        // Nota editable
        colNota.setCellFactory(TextFieldTableCell.forTableColumn());
        colNota.setOnEditCommit(event -> {
            TrabajadorSeleccion t = event.getRowValue();
            t.setNota(event.getNewValue());
            tablaEquipo.refresh();
        });

        btnCargar.setOnAction(e -> cargarTrabajadores());
        btnGuardar.setOnAction(e -> guardarEquipo());
        btnVolver.setOnAction(e -> volver());
    }

    private void cargarTrabajadores() {
        String nombreDpto = comboDepartamento.getSelectionModel().getSelectedItem();
        if (nombreDpto == null) {
            mostrar("Selecciona un departamento.");
            return;
        }

        List<TrabajadorSeleccion> lista = dao.obtenerTrabajadoresPorDepartamento(nombreDpto);
        tablaEquipo.getItems().setAll(lista);
    }

    private void guardarEquipo() {
        String nombreDpto = comboDepartamento.getSelectionModel().getSelectedItem();
        if (nombreDpto == null) {
            mostrar("Selecciona un departamento.");
            return;
        }

        List<Integer> seleccionados = tablaEquipo.getItems().stream()
                .filter(TrabajadorSeleccion::isSeleccionado)
                .map(TrabajadorSeleccion::getId)
                .collect(Collectors.toList());

        dao.asignarEquipo(idSupervisor, seleccionados, nombreDpto);

        for (TrabajadorSeleccion t : tablaEquipo.getItems()) {

            double valoracionAnterior = t.getValoracionOriginal();
            String notaAnterior = t.getNotaOriginal();

            double valoracionNueva = t.getValoracion();
            String notaNueva = t.getNota();

            if (valoracionAnterior != valoracionNueva || !notaAnterior.equals(notaNueva)) {

                dao.guardarHistorial(
                        t.getId(),
                        idSupervisor,
                        valoracionAnterior,
                        valoracionNueva,
                        notaAnterior,
                        notaNueva
                );

                dao.actualizarValoracionYNota(
                        t.getId(),
                        valoracionNueva,
                        notaNueva
                );

                t.setValoracionOriginal(valoracionNueva);
                t.setNotaOriginal(notaNueva);
            }
        }

        tablaEquipo.refresh();
        mostrar("Cambios guardados correctamente.");
    }

    private void volver() {
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

    private void mostrar(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
