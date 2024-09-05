import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class Bateria {
    private VBox contenedorBateria;
    private ImageView bateriaImagen;
    private Button botonRojo;
    private Button botonVerde;
    private Loc loc;
    private Protoboard protoboard;
    private Controlador controlador;
    private Color colorEsperado;
    private GridPane gridPane;

    public Bateria(Loc loc, Protoboard protoboard, Controlador controlador, GridPane gridPane) {
        this.loc = loc;
        this.protoboard = protoboard;
        this.controlador = controlador;
        this.gridPane = gridPane;

        contenedorBateria = new VBox();
        bateriaImagen = new ImageView(new Image("file:C:\\Users\\matia\\OneDrive\\Escritorio\\proyecto\\resources\\bateria.png")); 
        bateriaImagen.setFitWidth(100); 
        bateriaImagen.setPreserveRatio(true); 
        
        botonVerde = new Button("+");
        botonVerde.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        
        botonRojo = new Button("-");
        botonRojo.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        
        contenedorBateria.getChildren().addAll(botonVerde, bateriaImagen, botonRojo);

        contenedorBateria.setAlignment(Pos.CENTER);
        contenedorBateria.setSpacing(10);
        contenedorBateria.setPadding(new Insets(10));

        configurarEventos();
    }

    private void configurarEventos() {
        botonVerde.setOnAction(e -> seleccionarColor(Color.GREEN));
        botonRojo.setOnAction(e -> seleccionarColor(Color.RED));
    }

    private void seleccionarColor(Color color) {
        this.colorEsperado = color;
        System.out.println("Esperando selección de celda...");
        gridPane.setOnMouseClicked(this::manejarClickGridPane);
        activarBotones(false); // Desactiva los botones mientras espera la selección de una celda
    }

    private void manejarClickGridPane(MouseEvent evento) {
        if (evento.getButton() != javafx.scene.input.MouseButton.PRIMARY) return;

        int fila = loc.getFilaActual();
        int columna = loc.getColumnaActual();

        if (fila >= 0 && columna >= 0) {
            protoboard.cambiarColor(fila, columna, colorEsperado);
            controlador.actualizarBuses(protoboard.getGridPane());
            controlador.ActualizarProtoboard(protoboard.getGridPane());
            System.out.println("Color aplicado.");
        } else {
            System.out.println("No se ha seleccionado una celda válida.");
        }
        finalizarAccion(); // Finalizar la acción después de aplicar el color
    }

    private void finalizarAccion() {
        gridPane.setOnMouseClicked(null); // Eliminar el manejador de eventos del mouse
        activarBotones(true); // Reactiva los botones después de que se ha realizado la acción
    }

    private void activarBotones(boolean activar) {
        botonVerde.setDisable(!activar);
        botonRojo.setDisable(!activar);
    }

    public VBox getContenedorBateria() {
        return contenedorBateria;
    }
}
