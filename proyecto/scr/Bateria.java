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
    private double startX, startY;
    private boolean bateriaEncendida;

    // Constructor actualizado para recibir HiloGestorCables
    public Bateria(Loc loc, Protoboard protoboard, Controlador controlador, GridPane gridPane, GestorCables cablear,
            HiloGestorCables hiloGestorCables) {
        this.loc = loc;
        this.protoboard = protoboard;
        this.controlador = controlador;
        this.gridPane = gridPane;
        this.gestorcables = cablear;

        contenedorBateria = new VBox();
        bateriaImagen = new ImageView(new Image("/resources/bateria.png"));
        bateriaImagen.setFitWidth(100);
        bateriaImagen.setPreserveRatio(true);
        bateriaEncendida = true; 
        gestorcables.setEstado(bateriaEncendida);

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
    
    public boolean getestado(){
        return bateriaEncendida;
    }

    private void configurarEventos() {
        botonVerde.setOnAction(e -> seleccionarColor(Color.BLUE, botonVerde));
        botonRojo.setOnAction(e -> seleccionarColor(Color.RED, botonRojo));

        // Evento de clic en la imagen de la batería para cambiar entre encendido y
        // apagado
        bateriaImagen.setOnMouseClicked(event -> {
            bateriaEncendida = !bateriaEncendida; // Alterna el estado de la batería

            // Cambiar la imagen de la batería según su estado
            if (bateriaEncendida) {
                bateriaImagen.setImage(new Image("/resources/bateria.png")); // Imagen de batería encendida
                gestorcables.setEnergia();
                gestorcables.setEstado(bateriaEncendida); 
                gestorcables.resetLed();
            } else {
                bateriaImagen.setImage(new Image("/resources/bateriaOFF.png")); // Imagen de batería apagada
                gestorcables.EliminarEnergia(protoboard.getMatriz());
                gestorcables.setEstado(bateriaEncendida);
                gestorcables.resetLed();
            }
        });
    }

    private void seleccionarColor(Color color, Node boton) {
        this.colorEsperado = color;
    
        // Obtener las coordenadas actuales de la imagen de la batería
        startX = bateriaImagen.localToScene(bateriaImagen.getBoundsInLocal()).getMinX();
        startY = bateriaImagen.localToScene(bateriaImagen.getBoundsInLocal()).getMinY();
    
        Objeto objeto = new Objeto(color == Color.BLUE ? "cablegen+" : "cablegen-");
        gestorcables.setObjetoSeleccionado(objeto);
        gridPane.setOnMouseClicked(this::manejarClickGridPane);
    }
    
    private void manejarClickGridPane(MouseEvent evento) {
        if (evento.getButton() != javafx.scene.input.MouseButton.PRIMARY)
            return;
    
        // Obtener las coordenadas del clic del usuario en el gridPane
        double endX = evento.getX();
        double endY = evento.getY();

        // Convertir las coordenadas del clic en fila y columna del grid
        int[] x2 = loc.getfilaccoluma(endX, endY);
        int filaFin = x2[0];
        int columnaFin = x2[1];

        // Verifica y dibuja el cable desde la batería hasta el punto donde hizo clic el usuario
        boolean exito = gestorcables.dibujarCable(startX, startY, endX, endY, filaFin, columnaFin, 1);
        if (exito) {
            // Aplica el color y actualiza el protoboard si la batería está encendida
            protoboard.cambiarColor(filaFin, columnaFin, colorEsperado);
            controlador.actualizarBuses(protoboard.getGridPane());
            controlador.ActualizarProtoboard(protoboard.getGridPane());
            protoboard.actualizarMatriz(gridPane);
            System.out.println("Cable conectado y color aplicado.");
        }
        finalizarAccion(); // Finaliza la acción después de aplicar el color
    }
    
    private void finalizarAccion() {
        gridPane.setOnMouseClicked(null); // Elimina el manejador de eventos del mouse
        gestorcables.configurarEventos();
        gestorcables.configurarEventos();
    }

    public VBox getContenedorBateria() {
        return contenedorBateria;
    }
}
