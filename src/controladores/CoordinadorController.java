package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import modelo.Trabajador;
import modelo.TrabajadorDAO;

import java.util.List;

public class CoordinadorController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Trabajador> tablaCoordinador;
    @FXML private TableColumn<Trabajador, String> colNombre;
    @FXML private TableColumn<Trabajador, String> colDepartamento;
    @FXML private TableColumn<Trabajador, Double> colValoracion;
    @FXML private TableColumn<Trabajador, String> colNota;

    @FXML private Button btnBuscar, btnAsignar, btnActualizar, btnVolver;

    private final TrabajadorDAO dao = new TrabajadorDAO();

    @FXML
    public void initialize() {

        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colDepartamento.setCellValueFactory(c -> c.getValue().departamentoProperty());
        colValoracion.setCellValueFactory(c -> c.getValue().valoracionProperty().asObject());
        colNota.setCellValueFactory(c -> c.getValue().notaProperty());

        tablaCoordinador.setEditable(true);
        colValoracion.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colNota.setCellFactory(TextFieldTableCell.forTableColumn());

        colValoracion.setOnEditCommit(event -> {
            Trabajador t = event.getRowValue();
            double anterior = t.getValoracion();
            double nuevo = event.getNewValue();
            t.setValoracion(nuevo);
            dao.actualizarValoracionYNota(t.getId(), nuevo, t.getNota());
            dao.insertarHistorial(t.getId(), 1, anterior, nuevo, t.getNota(), t.getNota());
            refrescarTabla();
        });

        colNota.setOnEditCommit(event -> {
            Trabajador t = event.getRowValue();
            String anterior = t.getNota();
            String nuevo = event.getNewValue();
            t.setNota(nuevo);
            dao.actualizarValoracionYNota(t.getId(), t.getValoracion(), nuevo);
            dao.insertarHistorial(t.getId(), 1, t.getValoracion(), t.getValoracion(), anterior, nuevo);
            refrescarTabla();
        });

        refrescarTabla();

        btnBuscar.setOnAction(e -> buscarTrabajador());
        btnActualizar.setOnAction(e -> refrescarTabla());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    private void refrescarTabla() {
        tablaCoordinador.getItems().setAll(dao.obtenerTrabajadores());
    }

    private void buscarTrabajador() {
        String filtro = txtBuscar.getText().trim();
        List<Trabajador> resultados = filtro.isEmpty() ? dao.obtenerTrabajadores() : dao.obtenerTrabajadores(filtro);
        tablaCoordinador.getItems().setAll(resultados);
    }

    private void volverAlMenu() {
        try { FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load(); Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root)); stage.setTitle("Login");
            stage.show(); } catch (Exception e) { e.printStackTrace(); }}

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null); alert.setContentText(mensaje);
        alert.showAndWait(); } }