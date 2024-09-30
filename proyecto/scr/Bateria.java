import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

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
    private GestorCables gestorcables;
    private double startX, startY; // Coordenadas del botón presionado

    public Bateria(Loc loc, Protoboard protoboard, Controlador controlador, GridPane gridPane, GestorCables cablear) {
        this.loc = loc;
        this.protoboard = protoboard;
        this.controlador = controlador;
        this.gridPane = gridPane;
        this.gestorcables = cablear;

        contenedorBateria = new VBox();
        bateriaImagen = new ImageView(new Image("/resources/bateria.png")); 
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
        botonVerde.setOnAction(e -> seleccionarColor(Color.BLUE, botonVerde));
        botonRojo.setOnAction(e -> seleccionarColor(Color.RED, botonRojo));
    }

    private void seleccionarColor(Color color, Node boton) {
        this.colorEsperado = color;
    
        // Ajusta las coordenadas según el color seleccionado
        if (color == Color.BLUE) {
            startX = 1090;
            startY = 320;
        } else {
            startX = 1090;
            startY = 400;
        }
    
        // Configura el objeto seleccionado para el cable
        Objeto objeto = new Objeto(color == Color.BLUE ? "cablegen+" : "cablegen-");
        gestorcables.setObjetoSeleccionado(objeto);
    
        // Configura el evento de clic en el GridPane
        gridPane.setOnMouseClicked(this::manejarClickGridPane);
    }
    
    private void manejarClickGridPane(MouseEvent evento) {
        if (evento.getButton() != javafx.scene.input.MouseButton.PRIMARY) return;
    
        // Obtener las coordenadas de la celda seleccionada
        int fila = loc.getFilaActual();
        int columna = loc.getColumnaActual();
    
        if (fila >= 0 && columna >= 0) {
            
    
            // Dibuja el cable desde el botón hacia la celda seleccionada
            double endX = evento.getX() - 5;
            double endY = evento.getY() - 5;
    
            // Verifica y dibuja el cable si no hay conflictos
            boolean exito = gestorcables.dibujarCable(startX, startY, endX, endY,fila,columna);
            if (exito) {
                // Aplica el color y actualiza el protoboard
                protoboard.cambiarColor(fila, columna, colorEsperado);
                controlador.actualizarBuses(protoboard.getGridPane());
                controlador.ActualizarProtoboard(protoboard.getGridPane());
                protoboard.actualizarMatriz(gridPane);
                System.out.println("Cable conectado y color aplicado.");
            } else {
                System.out.println("No se pudo conectar el cable (posiblemente hay un cable en la misma posición).");
            }
        } else {
            System.out.println("No se ha seleccionado una celda válida.");
        }
        finalizarAccion(); // Finaliza la acción después de aplicar el color
    }
    

    private void finalizarAccion() {
        gridPane.setOnMouseClicked(null); // Elimina el manejador de eventos del mouse
    }

    public VBox getContenedorBateria() {
        return contenedorBateria;
    }
}