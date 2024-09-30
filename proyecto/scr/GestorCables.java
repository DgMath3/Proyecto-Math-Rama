import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;

public class GestorCables {
    private final GridPane gridPane;
    private final Pane drawingPane;
    private final Loc loc;
    private double startX, startY;
    private boolean drawing = false;
    private boolean cablearActivo = false;
    private Protoboard protoboard;
    private Controlador controlador;
    private Objeto objetoSeleccionado = null;
    private List<Cable> cables; // Lista para almacenar los cables

    private int[][] matrizConexiones; // Matriz para almacenar los puntos de conexión

    public GestorCables(GridPane gridPane, Loc loc, Protoboard protoboard, Controlador controlador) {
        this.gridPane = gridPane;
        this.loc = loc;
        this.protoboard = protoboard;
        this.controlador = controlador;
        this.drawingPane = new Pane();
        this.drawingPane.setMouseTransparent(false);
        this.gridPane.getChildren().add(drawingPane);

        // Inicializar la lista de cables
        this.cables = new ArrayList<>();

        // Inicializar la matriz de conexiones con 14 filas y 30 columnas
        this.matrizConexiones = new int[14][30];

        // Asegurarse de que el tamaño del Pane de dibujo sea correcto
        this.drawingPane.setPrefSize(gridPane.getWidth(), gridPane.getHeight());
    }

    public void configurarEventos() {
        gridPane.setOnMouseClicked(this::clicpresionado);
        drawingPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::manejarclicks);
    }

    private void clicpresionado(MouseEvent event) {
        if (!cablearActivo || objetoSeleccionado == null) {
            return; // Si no está activo o no hay objeto seleccionado, no hace nada
        }

        // Esperar brevemente para asegurar que el GridPane se actualice
        PauseTransition pause = new PauseTransition(Duration.millis(50));
        pause.setOnFinished(e -> {
            // Usar el método de Loc para verificar si el clic está dentro del GridPane
            if (loc.estaDentroDelGridPane(event.getX(), event.getY())) {
                double clickX = event.getX() - 5;
                double clickY = event.getY() - 5;

                int fila = loc.getFilaActual();
                int columna = loc.getColumnaActual();

                if (!drawing) {
                    startX = clickX;
                    startY = clickY;
                    drawing = true;
                } else {
                    try {
                        if (!existeCableEnPosicion(startX, startY, clickX, clickY)) {
                            dibujarCable(startX, startY, clickX, clickY, fila, columna);
                        } else {
                            System.err.println("Error: Ya existe un cable en la posición de inicio o fin del cable.");
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.err.println(
                                "Error: El punto de inicio o fin del cable está fuera de los límites de la matriz.");
                    }
                    drawing = false;
                    cablearActivo = false; // Desactiva la funcionalidad de cablear después de dibujar
                    objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
                }
            }
        });
        pause.play();
    }

    private boolean existeCableEnPosicion(double startX, double startY, double endX, double endY) {
        int[] inicioL = loc.getfilaccoluma(startX + 10, startY);
        int[] finalL = loc.getfilaccoluma(endX + 10, endY);

        int filaInicio = inicioL[0];
        int columnaInicio = inicioL[1];
        int filaFin = finalL[0];
        int columnaFin = finalL[1];

        for (Cable cable : cables) {
            if (cable.getStartX() != 1090) {
                inicioL = loc.getfilaccoluma(cable.getStartX() + 10, cable.getStartY());
                finalL = loc.getfilaccoluma(cable.getEndX() + 10, cable.getEndY());

                int cableFilaInicio = inicioL[0];
                int cableColumnainicio = inicioL[1];
                int cableFilaFin = finalL[0];
                int cableColumnaFin = finalL[1];

                // Verificar si el nuevo cable se superpone con algún cable existente
                if ((filaInicio == cableFilaInicio && columnaInicio == cableColumnainicio) ||
                        (filaInicio == cableFilaFin && columnaInicio == cableColumnaFin) ||
                        (filaFin == cableFilaInicio && columnaFin == cableColumnainicio) ||
                        (filaFin == cableFilaFin && columnaFin == cableColumnaFin)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean dibujarCable(double startX, double startY, double endX, double endY, int fila, int columna) {
        int[] inicioL = loc.getfilaccoluma(endX + 10, endY);
        int[] finalL = loc.getfilaccoluma(startX + 10, startY);

        // Calcular las posiciones de inicio y fin en la matriz de conexiones
        int filaInicio = inicioL[0];
        int columnaInicio = inicioL[1];
        int filaFin = finalL[0];
        int columnaFin = finalL[1];

        // Comprobar si ya existe un cable en la posición de inicio o fin
        if (startX != 1090) {
            for (Cable cable : cables) {
                // Verificar proximidad en el inicio y el fin
                if ((Math.abs(cable.getStartX() - startX) < 10 && Math.abs(cable.getStartY() - startY) < 10) ||
                        (Math.abs(cable.getEndX() - endX) < 10 && Math.abs(cable.getEndY() - endY) < 10)) {
                    System.err.println("Error: Ya existe un cable en la posición seleccionada.");
                    return false; // No se dibuja el cable
                }
            }
        }

        // Comprobar si el cable está siendo colocado en el mismo lugar en el inicio y
        // en el fin
        if (filaInicio == filaFin && columnaInicio == columnaFin && startX != 1090) {
            System.err.println("Error: El cable no puede ser colocado en el mismo lugar en el inicio y en el fin.");
            return false; // No se dibuja el cable
        }

        // Obtener color, imagen y estado de pasa energía del objeto seleccionado
        Paint color = objetoSeleccionado != null ? objetoSeleccionado.getColor() : Color.BLACK;
        ImageView imageView = objetoSeleccionado != null ? new ImageView(objetoSeleccionado.getImagen()) : null;
        boolean pasa = objetoSeleccionado != null && objetoSeleccionado.getpasa();

        // Crear el cable con la información proporcionada
        Cable cable = new Cable(startX, startY, endX, endY, color, objetoSeleccionado, imageView, pasa, filaInicio,
                columnaInicio, filaFin, columnaFin);

        // Añadir el cable a la lista y al Pane de dibujo
        cables.add(cable);
        drawingPane.getChildren().add(cable.getLinea());
        cable.getLinea().toFront();

        // Manejar la imagen si está presente
        if (imageView != null) {
            double cellSize = gridPane.getWidth() / gridPane.getColumnConstraints().size();
            imageView.setFitWidth(cellSize);
            imageView.setFitHeight(cellSize);

            imageView.setLayoutX((startX + endX) / 2 - imageView.getFitWidth() / 2);
            imageView.setLayoutY((startY + endY) / 2 - imageView.getFitHeight() / 2);

            drawingPane.getChildren().add(imageView);
            imageView.toFront();
        }

        // Determinar el valor a actualizar en la matriz de conexiones
        int valor;
        if (objetoSeleccionado.getId().equals("cablegen+")) {
            valor = 2; // Cables que generan energía positiva
        } else if (objetoSeleccionado.getId().equals("cablegen-")) {
            valor = 3; // Cables que generan energía negativa
        } else {
            valor = pasa ? 1 : -1; // Determinar el valor basado en pasa
        }

        // Actualizar la matriz de conexiones para el inicio y el fin del cable
        if (startX != 1090) {
            actualizarMatrizConexiones(filaFin, columnaFin, valor);
        }
        actualizarMatrizConexiones(filaInicio, columnaInicio, valor);

        return true; // Cable dibujado exitosamente
    }

    private void manejarclicks(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) { // Detecta clic derecho
            Cable cableToRemove = null;
            for (Cable cable : cables) {
                if (cable.getLinea().contains(event.getX(), event.getY())) {
                    cableToRemove = cable;
                    break;
                }
            }
            if (cableToRemove != null) {
                eliminarCable(cableToRemove, true);
                event.consume(); // Evita que el evento se propague
            }
        } else if (event.getButton() == MouseButton.PRIMARY && cablearActivo && objetoSeleccionado != null) {
            Cable cableToChange = null;
            for (Cable cable : cables) {
                if (cable.getLinea().contains(event.getX(), event.getY())) {
                    cableToChange = cable;
                    break;
                }
            }
            if (cableToChange != null) {
                cambiarCable(cableToChange);
                event.consume(); // Evita que el evento se propague
            }
        } else if (event.getButton() == MouseButton.PRIMARY && objetoSeleccionado == null) {
            Cable cableCambiado = null;
            for (Cable cable : cables) {
                if (cable.getLinea().contains(event.getX(), event.getY())) {
                    cableCambiado = cable;
                    break;
                }
            }
            if (cableCambiado != null && cableCambiado.getObjeto() != null) {
                if (cableCambiado.getObjeto().getId().equals("Switch")) {
                    eliminarEnergiaSinConexiones(protoboard.getMatriz());
                    objetoSeleccionado = new Objeto("SwitchOn");
                    cambiarCable(cableCambiado);
                    objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
                } else if (cableCambiado.getObjeto().getId().equals("SwitchOn")) {
                    eliminarEnergiaSinConexiones(protoboard.getMatriz());
                    objetoSeleccionado = new Objeto("Switch");
                    cambiarCable(cableCambiado);
                    objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
                }
                event.consume(); // Evita que el evento se propague
            }
        }
    }

    // Método para cambiar un cable por otro objeto o estado
    private void cambiarCable(Cable cable) {
        // Obtener las coordenadas del cable existente
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();

        // Eliminar el cable actual
        eliminarCable(cable, false);

        // Crear un nuevo cable con las mismas coordenadas pero con el nuevo objeto
        boolean nose = dibujarCable(cable.getStartX(), cable.getStartY(), cable.getEndX(), cable.getEndY(),
                loc.getFilaActual(), loc.getColumnaActual());

        if (nose) {
            // Actualizar la matriz de conexiones para el nuevo estado del cable
            Cable nuevoCable = cables.get(cables.size() - 1); // El último cable agregado es el nuevo cable
            boolean pasa = nuevoCable.pasa(); // Obtener el nuevo estado de pasa
            int valor = pasa ? 1 : -1; // Determinar el valor basado en pasa
            if (cable.getStartX() != 1090) {
                actualizarMatrizConexiones(filaInicio, columnaInicio, valor);
            }
            actualizarMatrizConexiones(filaFin, columnaFin, valor);
            // Actualizar la matriz para el inicio y el fin del nuevo cable
        }

        cablearActivo = false; // Desactiva la funcionalidad de cablear después de cambiar
        objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
    }

    public void activarCablear(boolean activar) {
        this.cablearActivo = activar;
    }

    public void setObjetoSeleccionado(Objeto objeto) {
        this.objetoSeleccionado = objeto;
        this.cablearActivo = true; // Activar la funcionalidad de cablear al seleccionar un objeto
    }

    public void eliminarCable(Cable cable, boolean nose) {
        if (cable == null) {
            System.err.println("Error: El cable es nulo.");
            return;
        }

        if (!cables.contains(cable)) {
            System.err.println("Error: El cable no está en la lista.");
            return;
        }

        // Eliminar la línea de dibujo
        drawingPane.getChildren().remove(cable.getLinea());

        // Eliminar la imagen asociada al objeto
        if (cable.getObjeto() != null && cable.getObjeto().getImagen() != null) {
            drawingPane.getChildren().remove(cable.getImageView());
        }

        // Eliminar el cable de la lista
        cables.remove(cable);

        // Actualizar la matriz de conexiones para el inicio y el fin del cable
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();

        if (filaInicio >= 0 && filaInicio < matrizConexiones.length && columnaInicio >= 0
                && columnaInicio < matrizConexiones[0].length) {
            actualizarMatrizConexiones(filaInicio, columnaInicio, 0);
        } else {
            System.err.println("Error: Coordenadas de inicio fuera de los límites.");
        }

        if (filaFin >= 0 && filaFin < matrizConexiones.length && columnaFin >= 0
                && columnaFin < matrizConexiones[0].length) {
            actualizarMatrizConexiones(filaFin, columnaFin, 0);
        } else {
            System.err.println("Error: Coordenadas de fin fuera de los límites.");
        }

        if (nose) {
            eliminarEnergiaSinConexiones(protoboard.getMatriz());
        }
    }

    // Método para actualizar la matriz de conexiones
    private void actualizarMatrizConexiones(int fila, int columna, int valor) {
        if (fila >= 0 && fila < matrizConexiones.length && columna >= 0 && columna < matrizConexiones[0].length) {
            matrizConexiones[fila][columna] = valor;
        } else {
            System.err.println("Error: Las coordenadas están fuera de los límites de la matriz.");
        }
    }

    // Metodo que verifica si hay algun objeto en una parte exacta de la matriz y
    // retorna el id del objeto
    public String verificarObjetoEnPosicion(int fila, int columna, Cable cable) {

        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();

        if ((filaInicio == fila && columnaInicio == columna) || (filaFin == fila && columnaFin == columna)) {
            return cable.getObjeto().getId();
        }
        return "No hay objeto";
    }

    public List<Cable> obtenerCables() {
        return cables; // Retorna la lista de cables
    }

    private void cambiarColorCelda(int fila, int columna, Color color, GridPane gridPane) {
        // Encuentra el nodo correspondiente en el GridPane y cambia su color
        Node nodo = getNodeFromGridPane(fila, columna, gridPane);
        if (nodo instanceof Circle) {
            ((Circle) nodo).setFill(color);
        }
    }

    private Node getNodeFromGridPane(int fila, int columna, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == fila && GridPane.getColumnIndex(node) == columna) {
                return node;
            }
        }
        return null;
    }

    public void eliminarEnergiaSinConexiones(String[][] matrizEnergia) {
        // Crea un conjunto para almacenar las celdas conectadas a cables que generan
        // energía
        Set<String> celdasConectadas = new HashSet<>();

        // Recorre los cables para identificar las conexiones
        for (Cable cable : cables) {
            Objeto objeto = cable.getObjeto();
            if (objeto != null && (objeto.getId().equals("cablegen+") || objeto.getId().equals("cablegen-"))) {
                int filaInicio = cable.getFilaInicio();
                int columnaInicio = cable.getColumnaInicio();

                // Agrega las celdas conectadas al conjunto
                celdasConectadas.add(filaInicio + "," + columnaInicio);
            }
        }

        // Recorre la matriz de energía para actualizar las celdas sin conexión
        for (int i = 0; i < matrizEnergia.length; i++) {
            for (int j = 0; j < matrizEnergia[i].length; j++) {
                // Verifica si la celda no está en el conjunto de celdas conectadas
                if (!celdasConectadas.contains(i + "," + j)) {
                    // Si la celda no está conectada, cámbiala a "|" para indicar energía sin
                    // conexión
                    matrizEnergia[i][j] = "|"; // Suponiendo que "|" indica energía sin conexión
                    cambiarColorCelda(i, j, Color.LIGHTGRAY, gridPane);
                }
            }
        }

        // Actualiza el protoboard después de los cambios
        controlador.actualizarBuses(gridPane);
        controlador.ActualizarProtoboard(gridPane);
    }

    public void led(Cable cable, String[][] matrizEnergia) {
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();
        Objeto objeto = cable.getObjeto();
        String idObjeto = objeto.getId();

        // Verificación de LEDs
        if (idObjeto.equals("Led") && !objeto.isLedActivado()) {
            if ((matrizEnergia[filaInicio][columnaInicio].equals("+")
                    && matrizEnergia[filaFin][columnaFin].equals("-")) ||
                    (matrizEnergia[filaInicio][columnaInicio].equals("-")
                            && matrizEnergia[filaFin][columnaFin].equals("+"))) {
                eliminarCable(cable, false);
                objeto.alternarLed();
                objeto.setLedActivado(true);
                // Dibujar un nuevo cable con el LED activado
                dibujarCable(cable.getStartX(), cable.getStartY(), cable.getEndX(), cable.getEndY(),
                        cable.getFilaInicio(), cable.getColumnaInicio());
            }
        }
        objetoSeleccionado = null;
        cablearActivo = false;
        drawing = false;
    }
}