import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;

public class Cablear {
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

    public Cablear(GridPane gridPane, Loc loc, Protoboard protoboard, Controlador controlador) {
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

    public Boolean dibujarCableSolo(double startX, double startY, double endX, double endY) {
        // Comprobar si el cable está siendo dibujado en el mismo punto
        if (startX == endX && startY == endY) {
            System.err.println("Error: El cable no puede ser dibujado en el mismo punto.");
            return false; // No se dibuja el cable
        }

        int[] inicioL = loc.getfilaccoluma(endX + 10, endY);
        int[] finalL = loc.getfilaccoluma(startX + 10, startY);

        // Calcular las posiciones de inicio y fin en la matriz de conexiones
        int filaInicio = inicioL[0];
        int columnaInicio = inicioL[1];
        int filaFin = finalL[0];
        int columnaFin = finalL[1];

        Paint color = objetoSeleccionado != null ? objetoSeleccionado.getColor() : Color.BLACK;
        ImageView imageView = objetoSeleccionado != null ? new ImageView(objetoSeleccionado.getImagen()) : null;
        boolean pasa = objetoSeleccionado != null ? objetoSeleccionado.getpasa() : false;

        // Crear un Cable con color negro y sin imagen (solo para dibujo visual)
        Cable cable = new Cable(startX, startY, endX, endY, color, imageView, objetoSeleccionado, pasa, filaInicio,
                columnaInicio, filaFin, columnaFin);

        // Añadir el cable a la lista y al Pane de dibujo
        drawingPane.getChildren().add(cable.getLinea());
        cable.getLinea().toFront();

        // Configurar la imagen si existe (en este caso no se agrega imagen)
        if (cable.getImageView() != null) {
            double cellSize = gridPane.getWidth() / gridPane.getColumnConstraints().size();
            cable.getImageView().setFitWidth(cellSize);
            cable.getImageView().setFitHeight(cellSize);

            cable.getImageView().setLayoutX((startX + endX) / 2 - cable.getImageView().getFitWidth() / 2);
            cable.getImageView().setLayoutY((startY + endY) / 2 - cable.getImageView().getFitHeight() / 2);

            drawingPane.getChildren().add(cable.getImageView());
            cable.getImageView().toFront();
        }
        actualizarObjetos(protoboard.getMatriz());

        return true;
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
        int[] inicioL = loc.getfilaccoluma(startX + 10, startY);
        int[] finalL = loc.getfilaccoluma(endX + 10, endY);

        int filaInicio = inicioL[0];
        int columnaInicio = inicioL[1];
        int filaFin = finalL[0];
        int columnaFin = finalL[1];

        for (Cable cable : cables) {
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
        return false;
    }

    public Boolean dibujarCable(double startX, double startY, double endX, double endY, int fila, int columna) {
        // Comprobar si el cable está siendo dibujado en el mismo punto
        if (startX == endX && startY == endY) {
            System.err.println("Error: El cable no puede ser dibujado en el mismo punto.");
            return false; // No se dibuja el cable
        }
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
                if ((Math.abs(cable.getStartX() - startX) < 10 && Math.abs(cable.getStartY() - startY) < 10) ||
                        (Math.abs(cable.getEndX() - endX) < 10 && Math.abs(cable.getEndY() - endY) < 10)) {
                    System.err.println("Error: Ya existe un cable en la posición seleccionada.");
                    return false; // No se dibuja el cable
                }
            }
        }

        // Comprobar si el cable está siendo colocado en el mismo lugar en el inicio y
        // en el fin
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
                    alerta.setContentText(
                            "El objeto no se puede colocar en la posición seleccionada. Verifique que esté dentro de un cable válido.");
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
        Cable cable = new Cable(startX, startY, endX, endY, color, imageView, objetoSeleccionado, pasa, filaInicio,
                columnaInicio, filaFin, columnaFin);

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
        int valor = 0;
        // Actualizar la matriz de conexiones para el inicio y el fin del cable
        if (objetoSeleccionado.getId().equals("cablegen+")) {
            valor = 2;
        } else if (objetoSeleccionado.getId().equals("cablegen-")) {
            valor = 3;
        } else {
            valor = pasa ? 1 : -1; // Determinar el valor basado en pasa
        }
        System.err.println(filaInicio + " " + columnaInicio + " " + filaFin + " " + columnaFin);
        if (startX != 1090) {
            actualizarMatrizConexiones(filaFin, columnaFin, valor);
        }
        actualizarMatrizConexiones(filaInicio, columnaInicio, valor);
        mostrarMatrizConexiones(); // Mostrar la matriz por consola
        if (startX != 1090) {
            actualizarObjetos(protoboard.getMatriz());
        }
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
            Cable cableCambiado = null;
            for (Cable cable : cables) {
                if (cable.getLinea().contains(event.getX(), event.getY())) {
                    cableCambiado = cable;
                    break;
                }
            }
            if (cableCambiado != null && cableCambiado.getObjeto() != null) {
                // Cambiar el estado del objeto asociado con el cable
                Objeto objeto = cableCambiado.getObjeto();
                objeto.alternarImagen();
                // Actualizar el valor de pasa
                boolean nuevoPasa = objeto.getpasa();
                // Actualizar la imagen en el pane de dibujo si es necesario
                if (cableCambiado.getImageView() != null) {
                    cableCambiado.getImageView().setImage(objeto.getImagen());
                }
                // Actualizar la matriz de conexiones
                int[] inicioL = loc.getfilaccoluma(cableCambiado.getStartX() + 10, cableCambiado.getStartY());
                int[] finalL = loc.getfilaccoluma(cableCambiado.getEndX() + 10, cableCambiado.getEndY());

                int filaInicio = inicioL[0];
                int columnaInicio = inicioL[1];
                int filaFin = finalL[0];
                int columnaFin = finalL[1];
                int valor = nuevoPasa ? 1 : -1; // Determinar el valor basado en pasa
                if (cableCambiado.getStartX() != 1090) {
                    actualizarMatrizConexiones(filaInicio, columnaInicio, valor);
                }
                actualizarMatrizConexiones(filaFin, columnaFin, valor);
                mostrarMatrizConexiones(); // Mostrar la matriz por consola

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
        eliminarCable(cable);

        // Crear un nuevo cable con las mismas coordenadas pero con el nuevo objeto
        boolean nose = dibujarCable(cable.getStartX(), cable.getStartY(), cable.getEndX(), cable.getEndY(),
                loc.getFilaActual(),
                loc.getColumnaActual());

        if (nose) {
            // Actualizar la matriz de conexiones para el nuevo estado del cable
            Cable nuevoCable = cables.get(cables.size() - 1); // El último cable agregado es el nuevo cable
            boolean pasa = nuevoCable.pasa(); // Obtener el nuevo estado de pasa
            int valor = pasa ? 1 : -1; // Determinar el valor basado en pasa
            if (cable.getStartX() != 1090) {
                actualizarMatrizConexiones(filaInicio, columnaInicio, valor);
            }
            actualizarMatrizConexiones(filaFin, columnaFin, valor);
            mostrarMatrizConexiones(); // Mostrar la matriz por consola
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

    // Método para eliminar un cable
    public void eliminarCable(Cable cable) {
        drawingPane.getChildren().remove(cable.getLinea());
        if (cable.getImageView() != null) {
            drawingPane.getChildren().remove(cable.getImageView());
        }
        cables.remove(cable);

        // Actualizar la matriz de conexiones para el inicio y el fin del cable
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();

        if (filaInicio >= 0 && filaInicio < matrizConexiones.length && columnaInicio >= 0
                && columnaInicio < matrizConexiones[0].length) {
            actualizarMatrizConexiones(filaInicio, columnaInicio, 0);
        }

        if (filaFin >= 0 && filaFin < matrizConexiones.length && columnaFin >= 0
                && columnaFin < matrizConexiones[0].length) {
            actualizarMatrizConexiones(filaFin, columnaFin, 0);
        }
        eliminarSectoresSinCable(protoboard.getMatriz(),gridPane);
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

    public void actualizarObjetos(String[][] matrizEnergia) {
        int size = cables.size();

        for (int i = size - 1; i >= 0; i--) {
            Cable cable = cables.get(i);
            if (cable.getStartX() != 1090) {
                procesarCable(cable, matrizEnergia);
            }
        }

        for (Cable cable : cables) {
            if (cable.getStartX() != 1090) {
                procesarCable(cable, matrizEnergia);
            }
        }
        protoboard.imprimirMatriz();
    }

    private void procesarCable(Cable cable, String[][] matrizEnergia) {
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();
        Objeto objeto = cable.getObjeto();

        if (objeto != null) {
            String idObjeto = objeto.getId();
            objetoSeleccionado = objeto;
            System.out.println("Objeto en fila " + filaInicio + " columna " + columnaInicio + ": " + idObjeto);

            // Verificación de cables
            if (idObjeto.equals("Cable_azul") || idObjeto.equals("Cable_rojo")) {
                actualizarEnergia(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
            }

            // Verificación de LEDs
            if (idObjeto.equals("Led") && !objeto.isLedActivado()) {
                if ((matrizEnergia[filaInicio][columnaInicio].equals("+")
                        && matrizEnergia[filaFin][columnaFin].equals("-")) ||
                        (matrizEnergia[filaInicio][columnaInicio].equals("-")
                                && matrizEnergia[filaFin][columnaFin].equals("+"))) {
                    System.out.println("Activando LED");
                    eliminarCable(cable);
                    objeto.alternarLed();
                    objeto.setLedActivado(true);
                    // Dibujar un nuevo cable con el LED activado
                    dibujarCable(cable.getStartX(), cable.getStartY(), cable.getEndX(), cable.getEndY(),
                            cable.getFilaInicio(), cable.getColumnaInicio());
                }
            }
        }
    }

    private void actualizarEnergia(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
            String[][] matrizEnergia) {
        // Actualizar energía en la matriz según el estado de los puntos de inicio y fin
        if (matrizEnergia[filaInicio][columnaInicio].equals("+") && matrizEnergia[filaFin][columnaFin].equals("|")) {
            matrizEnergia[filaFin][columnaFin] = "+";
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")
                && matrizEnergia[filaFin][columnaFin].equals("|")) {
            matrizEnergia[filaFin][columnaFin] = "-";
        } else if (matrizEnergia[filaFin][columnaFin].equals("+")
                && matrizEnergia[filaInicio][columnaInicio].equals("|")) {
            matrizEnergia[filaInicio][columnaInicio] = "+";
        } else if (matrizEnergia[filaFin][columnaFin].equals("-")
                && matrizEnergia[filaInicio][columnaInicio].equals("|")) {
            matrizEnergia[filaInicio][columnaInicio] = "-";
        }
        // Después de actualizar la matriz de energía, actualizar los colores en el
        // protoboard
        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        protoboard.actualizarMatriz(gridPane);
    }

    private void aplicarColoresProtoboard(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
            String[][] matrizEnergia) {
        // Aplicar el color en el punto de inicio
        if (matrizEnergia[filaInicio][columnaInicio].equals("+")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.BLUE);
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.RED);
        }

        // Aplicar el color en el punto de fin
        if (matrizEnergia[filaFin][columnaFin].equals("+")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.BLUE);
        } else if (matrizEnergia[filaFin][columnaFin].equals("-")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.RED);
        }

        // Actualizar el protoboard y los buses para que reflejen los cambios en las
        // filas y columnas
        controlador.actualizarBuses(protoboard.getGridPane());
        controlador.ActualizarProtoboard(protoboard.getGridPane());
    }

    public void eliminarSectoresSinCable(String[][] matrizEnergia, GridPane gridPane) {
        // Primero, crea un conjunto para almacenar las celdas que están conectadas por
        // cables
        Set<String> celdasConectadas = new HashSet<>();

        // Recorre los cables y marca las celdas conectadas
        for (Cable cable : cables) {
            int filaInicio = cable.getFilaInicio();
            int columnaInicio = cable.getColumnaInicio();
            int filaFin = cable.getFilaFin();
            int columnaFin = cable.getColumnaFin();

            // Marca las celdas conectadas en el conjunto
            celdasConectadas.add(filaInicio + "," + columnaInicio);
            celdasConectadas.add(filaFin + "," + columnaFin);
        }

        // Recorre la matriz de energía
        for (int i = 0; i < matrizEnergia.length; i++) {
            for (int j = 0; j < matrizEnergia[i].length; j++) {
                // Verifica si la celda está en el conjunto de celdas conectadas
                if (!celdasConectadas.contains(i + "," + j)) {
                    // Si la celda no está conectada, cámbiala a lightgray
                    matrizEnergia[i][j] = "|"; // Suponiendo que "|" indica energía sin conexión
                    cambiarColorCelda(i, j, Color.LIGHTGRAY, gridPane);
                }
            }
        }

        // Actualiza el protoboard después de los cambios
        controlador.actualizarBuses(gridPane);
        controlador.ActualizarProtoboard(gridPane);
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
}
