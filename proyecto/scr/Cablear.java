import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class Cablear {
    private final GridPane gridPane;
    private final Pane drawingPane;
    private final Loc loc;
    private double startX, startY;
    private boolean drawing = false;
    private boolean cablearActivo = false;
    private Objeto objetoSeleccionado = null;
    private List<Cable> cables; // Lista para almacenar los cables
    private int[][] matrizConexiones; // Matriz para almacenar los puntos de conexión

    public Cablear(GridPane gridPane, Loc loc) {
        this.gridPane = gridPane;
        this.loc = loc;
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
        gridPane.setOnMouseClicked(this::handleClick);
        drawingPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::manejarclicks);
    }

    private void handleClick(MouseEvent event) {
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

    private boolean verificarPosicion(double startX, double startY, double endX, double endY) {
        // Verifica la distancia máxima permitida entre los puntos de cable
        double diferenciaX = Math.abs(endX - startX);
        double diferenciaY = Math.abs(endY - startY);
    
        // Calcula la distancia en términos de celdas del protoboard
        int distanciaCelulasX = (int) Math
                .round(diferenciaX / (gridPane.getWidth() / gridPane.getColumnConstraints().size()));
        int distanciaCelulasY = (int) Math
                .round(diferenciaY / (gridPane.getHeight() / gridPane.getRowConstraints().size()));
    
        // Verifica que la distancia no exceda el largo del objeto
        if (objetoSeleccionado != null) {
            int largoObjeto = objetoSeleccionado.getLargo();
            return distanciaCelulasX <= largoObjeto && distanciaCelulasY <= largoObjeto;
        }
    
        return distanciaCelulasX <= 2 && distanciaCelulasY <= 2; // Valor por defecto si no hay objeto seleccionado
    }
    

    private boolean existeCableEnPosicion(double startX, double startY, double endX, double endY) {
        int filaInicio = (int) (startY / (gridPane.getHeight() / 14));
        int columnaInicio = (int) (startX / (gridPane.getWidth() / 30));
        int filaFin = (int) (endY / (gridPane.getHeight() / 14));
        int columnaFin = (int) (endX / (gridPane.getWidth() / 30));

        for (Cable cable : cables) {
            int cableFilaInicio = (int) (cable.getStartY() / (gridPane.getHeight() / 14));
            int cableColumnaInicio = (int) (cable.getStartX() / (gridPane.getWidth() / 30));
            int cableFilaFin = (int) (cable.getEndY() / (gridPane.getHeight() / 14));
            int cableColumnaFin = (int) (cable.getEndX() / (gridPane.getWidth() / 30));

            // Verificar si el nuevo cable se superpone con algún cable existente
            if ((filaInicio == cableFilaInicio && columnaInicio == cableColumnaInicio) ||
                    (filaInicio == cableFilaFin && columnaInicio == cableColumnaFin) ||
                    (filaFin == cableFilaInicio && columnaFin == cableColumnaInicio) ||
                    (filaFin == cableFilaFin && columnaFin == cableColumnaFin)) {
                return true;
            }
        }
        return false;
    }

    public Boolean dibujarCable(double startX, double startY, double endX, double endY, int fila, int columna) {
        // Comprobar si el cable está siendo dibujado en el mismo punto
        if (startX == endX && startY == endY) {
            System.err.println("Error: El cable no puede ser dibujado en el mismo punto.");
            return false; // No se dibuja el cable
        }
    
        // Calcular las posiciones de inicio y fin en la matriz de conexiones
        int filaInicio = (int) (startY / (gridPane.getHeight() / gridPane.getRowConstraints().size()));
        int columnaInicio = (int) (startX / (gridPane.getWidth() / gridPane.getColumnConstraints().size()));
        int filaFin = (int) (endY / (gridPane.getHeight() / gridPane.getRowConstraints().size()));
        int columnaFin = (int) (endX / (gridPane.getWidth() / gridPane.getColumnConstraints().size()));
    
        // Comprobar si ya existe un cable en la posición de inicio o fin
        for (Cable cable : cables) {
            if ((Math.abs(cable.getStartX() - startX) < 10 && Math.abs(cable.getStartY() - startY) < 10) ||
                    (Math.abs(cable.getEndX() - endX) < 10 && Math.abs(cable.getEndY() - endY) < 10)) {
                System.err.println("Error: Ya existe un cable en la posición seleccionada.");
                return false; // No se dibuja el cable
            }
        }
    
        // Comprobar si el cable está siendo colocado en el mismo lugar en el inicio y en el fin
        if (filaInicio == filaFin && columnaInicio == columnaFin) {
            System.err.println("Error: El cable no puede ser colocado en el mismo lugar en el inicio y en el fin.");
            return false; // No se dibuja el cable
        }
    
        // Verifica si el objeto seleccionado es válido y si la posición es válida
        if (objetoSeleccionado != null) {
            if (!verificarPosicion(startX, startY, endX, endY)) {
                // Si la posición no es válida, mostrar un mensaje de error
                Platform.runLater(() -> {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error");
                    alerta.setHeaderText("Error al colocar el objeto");
                    alerta.setContentText("El objeto no se puede colocar en la posición seleccionada. Verifique que esté dentro de un cable válido.");
                    alerta.showAndWait();
                });
    
                // Limpiar el estado de dibujo y seleccionar el objeto
                drawing = false;
                cablearActivo = false;
                objetoSeleccionado = null;
                return false; // No se dibuja el cable
            }
        }
    
        Paint color = objetoSeleccionado != null ? objetoSeleccionado.getColor() : Color.BLACK;
        ImageView imageView = objetoSeleccionado != null ? new ImageView(objetoSeleccionado.getImagen()) : null;
        boolean pasa = objetoSeleccionado != null ? objetoSeleccionado.getpasa() : false;
    
        // Crear el cable con la información proporcionada
        Cable cable = new Cable(startX, startY, endX, endY, color, imageView, objetoSeleccionado, pasa);
    
        // Añadir el cable a la lista y al Pane de dibujo
        cables.add(cable);
        drawingPane.getChildren().add(cable.getLinea());
        cable.getLinea().toFront();
    
        if (imageView != null) {
            double cellSize = gridPane.getWidth() / gridPane.getColumnConstraints().size();
            imageView.setFitWidth(cellSize);
            imageView.setFitHeight(cellSize);
    
            imageView.setLayoutX((startX + endX) / 2 - imageView.getFitWidth() / 2);
            imageView.setLayoutY((startY + endY) / 2 - imageView.getFitHeight() / 2);
    
            drawingPane.getChildren().add(imageView);
            imageView.toFront();
        }
    
        // Actualizar la matriz de conexiones para el inicio y el fin del cable
        actualizarMatrizConexiones(filaInicio, columnaInicio, 1);
        actualizarMatrizConexiones(filaFin, columnaFin, 1);
        mostrarMatrizConexiones(); // Mostrar la matriz por consola
        return true;
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
                eliminarCable(cableToRemove);
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
            Cable cableToChange = null;
            for (Cable cable : cables) {
                if (cable.getLinea().contains(event.getX(), event.getY())) {
                    cableToChange = cable;
                    break;
                }
            }
            if (cableToChange != null && cableToChange.getObjeto() != null) {
                // Cambiar el estado del objeto asociado con el cable
                cableToChange.getObjeto().alternarImagen();
                // Actualizar la imagen en el pane de dibujo si es necesario
                if (cableToChange.getImageView() != null) {
                    cableToChange.getImageView().setImage(cableToChange.getObjeto().getImagen());
                }
                event.consume(); // Evita que el evento se propague
            }
        }
    }

    // Método para cambiar un cable por otro objeto o estado
    private void cambiarCable(Cable cable) {
        eliminarCable(cable); // Elimina el cable actual
        // Crear un nuevo cable con las mismas coordenadas pero con el nuevo objeto
        dibujarCable(cable.getStartX(), cable.getStartY(), cable.getEndX(), cable.getEndY(), loc.getFilaActual(),
                loc.getColumnaActual());
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

    // Método para eliminar un cable
    public void eliminarCable(Cable cable) {
        drawingPane.getChildren().remove(cable.getLinea());
        if (cable.getImageView() != null) {
            drawingPane.getChildren().remove(cable.getImageView());
        }
        cables.remove(cable);

        // Actualizar la matriz de conexiones para el inicio y el fin del cable
        int filaInicio = (int) (cable.getStartY() / (gridPane.getHeight() / 14));
        int columnaInicio = (int) (cable.getStartX() / (gridPane.getWidth() / 30));
        int filaFin = (int) (cable.getEndY() / (gridPane.getHeight() / 14));
        int columnaFin = (int) (cable.getEndX() / (gridPane.getWidth() / 30));

        if (filaInicio >= 0 && filaInicio < matrizConexiones.length && columnaInicio >= 0
                && columnaInicio < matrizConexiones[0].length) {
            actualizarMatrizConexiones(filaInicio, columnaInicio, 0);
        }

        if (filaFin >= 0 && filaFin < matrizConexiones.length && columnaFin >= 0
                && columnaFin < matrizConexiones[0].length) {
            actualizarMatrizConexiones(filaFin, columnaFin, 0);
        }

        mostrarMatrizConexiones(); // Mostrar la matriz por consola
    }

    // Método para actualizar la matriz de conexiones
    private void actualizarMatrizConexiones(int fila, int columna, int valor) {
        if (fila >= 0 && fila < matrizConexiones.length && columna >= 0 && columna < matrizConexiones[0].length) {
            matrizConexiones[fila][columna] = valor;
        } else {
            System.err.println("Error: Las coordenadas están fuera de los límites de la matriz.");
        }
    }

    // Método para mostrar la matriz de conexiones por consola
    private void mostrarMatrizConexiones() {
        System.out.println("Matriz de Conexiones:");
        for (int i = 0; i < matrizConexiones.length; i++) {
            for (int j = 0; j < matrizConexiones[i].length; j++) {
                System.out.print(matrizConexiones[i][j] + " ");
            }
            System.out.println();
        }
    }
}
