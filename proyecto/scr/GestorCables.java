import javafx.scene.image.Image;
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
import java.util.Optional;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

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
    private List<Cable> cables;
    public boolean Espera;

    public GestorCables(GridPane gridPane, Loc loc, Protoboard protoboard, Controlador controlador) {
        this.gridPane = gridPane;
        this.loc = loc;
        this.protoboard = protoboard;
        this.controlador = controlador;
        this.drawingPane = new Pane();
        this.drawingPane.setMouseTransparent(false);
        this.gridPane.getChildren().add(drawingPane);
        this.Espera = false;

        // Inicializar la lista de cables
        this.cables = new ArrayList<>();

        // Asegurarse de que el tamaño del Pane de dibujo sea correcto
        this.configurarEventos();
        this.drawingPane.setPrefSize(gridPane.getWidth(), gridPane.getHeight());
    }

    public void configurarEventos() {
        // Agregar el listener para cuando se presiona el mouse en el gridPane
        gridPane.setOnMouseClicked(event -> {
            clicpresionado(event); // Llama a tu método que maneja el clic en el gridPane
        });

        // Agregar el listener para cuando se detecta un clic en el drawingPane
        drawingPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            manejarclicks(event); // Llama a tu método que maneja los clics en el drawingPane
        });
    }

    private void clicpresionado(MouseEvent event) {
        if (!cablearActivo || objetoSeleccionado == null) {
            return; // Si no está activo o no hay objeto seleccionado, no hace nada
        }

        if (!objetoSeleccionado.getId().equals("chip")) {
            // Esperar brevemente para asegurar que el GridPane se actualice
            PauseTransition pause = new PauseTransition(Duration.millis(50));
            pause.setOnFinished(e -> {
                // Usar el método de Loc para verificar si el clic está dentro del GridPane
                if (loc.estaDentroDelGridPane(event.getX(), event.getY())) {
                    double clickX = event.getX();
                    double clickY = event.getY();
                    int[] cl = loc.getfilaccoluma(clickX, clickY);

                    if (!drawing) {
                        startX = clickX;
                        startY = clickY;
                        drawing = true;
                    } else {
                        try {
                            if (!existeCableEnPosicion(startX, startY, clickX, clickY)) {
                                dibujarCable(startX, startY, clickX, clickY, cl[0], cl[1], 1);
                            } else {
                                System.err
                                        .println("Error: Ya existe un cable en la posición de inicio o fin del cable.");
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
        } else {
            // llamamos a tu funcion
            double clickX = event.getX();
            double clickY = event.getY();
            drawing = true;
            int[] cl = loc.getfilaccoluma(clickX, clickY);
            colocarChip(cl[0], cl[1]);
            drawing = false;
            cablearActivo = false; // Desactiva la funcionalidad de cablear después de dibujar
            objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
        }
    }

    private boolean existeCableEnPosicion(double startX, double startY, double endX, double endY) {
        int[] inicioL = loc.getfilaccoluma(startX + 10, startY);
        int[] finalL = loc.getfilaccoluma(endX + 10, endY);

        int filaInicio = inicioL[0];
        int columnaInicio = inicioL[1];
        int filaFin = finalL[0];
        int columnaFin = finalL[1];

        for (Cable cable : cables) {
            if (!cable.getObjeto().getId().equals("cablegen+") && !cable.getObjeto().getId().equals("cablegen-")) {
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

    private void alerta(String info) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Error al colocar el objeto");
            alerta.setContentText(info);
            alerta.showAndWait();
        });
    }

    public Boolean dibujarCable(double startX, double startY, double endX, double endY, int fila, int columna,
            double valor) {
        // Obtener las posiciones de inicio y fin en la matriz de conexiones
        int[] inicioL = loc.getfilaccoluma(endX, endY);
        int[] finalL = loc.getfilaccoluma(startX, startY);

        int filaInicio = inicioL[0];
        int columnaInicio = inicioL[1];
        int filaFin = finalL[0];
        int columnaFin = finalL[1];

        // Comprobar si ya existe un cable en la posición de inicio o fin
        if (!objetoSeleccionado.getId().equals("cablegen+") && !objetoSeleccionado.getId().equals("cablegen-")) {
            for (Cable cable : cables) {
                // Verificar proximidad en el inicio y el fin
                if ((Math.abs(cable.getStartX() - startX) < 10 && Math.abs(cable.getStartY() - startY) < 10) ||
                        (Math.abs(cable.getEndX() - endX) < 10 && Math.abs(cable.getEndY() - endY) < 10)) {
                    alerta("Error: Ya existe un cable en la posición seleccionada.");

                    return false; // No se dibuja el cable
                }
            }
        }

        // Comprobar si el cable está siendo colocado en el mismo lugar en el inicio y
        // en el fin
        if (filaInicio == filaFin && columnaInicio == columnaFin && !objetoSeleccionado.getId().equals("cablegen+")
                && !objetoSeleccionado.getId().equals("cablegen-")) {
            alerta("Error: El cable no puede ser colocado en el mismo lugar en el inicio y en el fin.");
            return false; // No se dibuja el cable
        }

        // Verifica si el objeto seleccionado es válido y si la posición es válida
        if (objetoSeleccionado != null) {
            if (!verificarPosicion(startX, startY, endX, endY)) {
                // Si la posición no es válida, mostrar un mensaje de error
                alerta("El objeto no se puede colocar en la posición seleccionada. Verifique que esté dentro de un cable válido.");
                // Limpiar el estado de dibujo y seleccionar el objeto
                drawing = false;
                cablearActivo = false;
                objetoSeleccionado = null;
                return false; // No se dibuja el cable
            }
        }

        // Obtener color, imagen y estado de pasa energía del objeto seleccionado
        Paint color = objetoSeleccionado != null ? objetoSeleccionado.getColor() : Color.BLACK;
        ImageView imageView = objetoSeleccionado != null ? new ImageView(objetoSeleccionado.getImagen()) : null;
        boolean pasa = objetoSeleccionado != null && objetoSeleccionado.getpasa();

        // agregar las funcione llamando a loc para obtener el centro del grid pane
        Node nodo1 = loc.obtenerNodoPorFilaColumna(filaFin, columnaFin);
        Node nodo2 = loc.obtenerNodoPorFilaColumna(filaInicio, columnaInicio);

        if ((nodo1 == null
                && (!objetoSeleccionado.getId().equals("cablegen+") && !objetoSeleccionado.getId().equals("cablegen-")))
                || nodo2 == null) {
            return false;
        }

        double[] inicio = loc.getCoordenadasGridPane(nodo1);
        double[] fin = loc.getCoordenadasGridPane(nodo2);

        if (objetoSeleccionado.getId().equals("cablegen+") || objetoSeleccionado.getId().equals("cablegen-")) {
            inicio = new double[] { startX, startY };
        }

        // Crear el cable utilizando las coordenadas centradas
        Cable cable = new Cable(inicio[0], inicio[1], fin[0], fin[1], color, objetoSeleccionado, imageView, pasa,
                filaInicio, columnaInicio, filaFin, columnaFin, valor);

        // Añadir el cable a la lista y al Pane de dibujo
        cables.add(cable);
        drawingPane.getChildren().add(cable.getLinea());
        cable.getLinea().toFront();

        // Ajustar la imagen del cable
        double anchoCelda = gridPane.getWidth() / gridPane.getColumnConstraints().size();
        double altoCelda = gridPane.getHeight() / gridPane.getRowConstraints().size();

        imageView.setFitWidth(anchoCelda);
        imageView.setFitHeight(altoCelda);

        // Calcular la posición central de la imagen del cable
        imageView.setLayoutX((inicio[0] + fin[0]) / 2 - imageView.getFitWidth() / 2);
        imageView.setLayoutY((inicio[1] + fin[1]) / 2 - imageView.getFitHeight() / 2);

        // Calcular el ángulo de rotación
        double deltaX = fin[0] - inicio[0];
        double deltaY = fin[1] - inicio[1];
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        // Establecer la rotación de la imagen
        imageView.setRotate(angle);

        drawingPane.getChildren().add(imageView);
        imageView.toFront();

        objetoSeleccionado = null;
        drawing = false;
        return true;
    }

    public Boolean redibujar(int filaInicio, int columnaInicio, int filaFin, int columnaFin, double startX,
            double startY, double valor) {
        // Obtener color, imagen y estado de pasa energía del objeto seleccionado
        Paint color = objetoSeleccionado != null ? objetoSeleccionado.getColor() : Color.BLACK;
        ImageView imageView = objetoSeleccionado != null ? new ImageView(objetoSeleccionado.getImagen()) : null;
        boolean pasa = objetoSeleccionado != null && objetoSeleccionado.getpasa();

        // Agregar las funciones llamando a loc para obtener el centro del grid pane
        Node nodo1 = loc.obtenerNodoPorFilaColumna(filaFin, columnaFin);
        Node nodo2 = loc.obtenerNodoPorFilaColumna(filaInicio, columnaInicio);

        // Obtener coordenadas de los nodos
        double[] inicio = loc.getCoordenadasGridPane(nodo1);
        double[] fin = loc.getCoordenadasGridPane(nodo2);

        if (objetoSeleccionado.getId().equals("cablegen+") || objetoSeleccionado.getId().equals("cablegen-")) {
            inicio = new double[] { startX, startY };
        }

        // Crear el cable utilizando las coordenadas centradas
        Cable cable = new Cable(inicio[0], inicio[1], fin[0], fin[1], color, objetoSeleccionado, imageView, pasa,
                filaInicio, columnaInicio, filaFin, columnaFin, valor);

        // Añadir el cable a la lista y al Pane de dibujo
        cables.add(cable);
        drawingPane.getChildren().add(cable.getLinea());
        cable.getLinea().toFront();

        // Ajustar la imagen del cable
        double anchoCelda = gridPane.getWidth() / gridPane.getColumnConstraints().size();
        double altoCelda = gridPane.getHeight() / gridPane.getRowConstraints().size();

        imageView.setFitWidth(anchoCelda);
        imageView.setFitHeight(altoCelda);

        // Calcular la posición central de la imagen del cable
        imageView.setLayoutX((inicio[0] + fin[0]) / 2 - imageView.getFitWidth() / 2);
        imageView.setLayoutY((inicio[1] + fin[1]) / 2 - imageView.getFitHeight() / 2);

        // Calcular el ángulo de rotación
        double deltaX = fin[0] - inicio[0];
        double deltaY = fin[1] - inicio[1];
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        // Establecer la rotación de la imagen
        imageView.setRotate(angle);

        drawingPane.getChildren().add(imageView);
        imageView.toFront();

        objetoSeleccionado = null;
        drawing = false;
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
                Espera = true;
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
                    objetoSeleccionado = new Objeto("SwitchOn");
                    cambiarCable(cableCambiado);
                    objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
                } else if (cableCambiado.getObjeto().getId().equals("SwitchOn")) {
                    eliminarEnergiaSinConexiones(protoboard.getMatriz());
                    eliminarEnergiaSinConexiones(protoboard.getMatriz());
                    eliminarEnergiaSinConexiones(protoboard.getMatriz());
                    eliminarEnergiaSinConexiones(protoboard.getMatriz());
                    objetoSeleccionado = new Objeto("Switch");
                    cambiarCable(cableCambiado);
                    objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
                } else if (cableCambiado.getObjeto().getId().equals("resistor")) {
                    objetoSeleccionado = new Objeto("resistor");
                    double valor = solicitarValor("Configuracion resistor", cableCambiado.getvalor());
                    redibujar(cableCambiado.getFilaInicio(), cableCambiado.getColumnaInicio(),
                            cableCambiado.getFilaFin(), cableCambiado.getColumnaFin(), 0, 0, valor);
                    eliminarCable(cableCambiado, false);
                }
                event.consume(); // Evita que el evento se propague
            }
        }
    }

    private static double solicitarValor(String titulo, double valor) {
        // Crear un cuadro de diálogo de entrada
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo + valor);
        dialog.setHeaderText("valor actual: " + valor);
        dialog.setContentText("Valor:");

        // Mostrar el diálogo y esperar a que el usuario ingrese un valor
        Optional<String> result = dialog.showAndWait();

        // Verificar si el usuario ingresó un valor y no lo dejó vacío
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                // Intentar convertir el valor ingresado a un número
                return Double.parseDouble(result.get());
            } catch (NumberFormatException e) {
                // Si el valor no es un número válido, imprimir error
                System.out.println("Error: El valor ingresado no es un número válido.");
            }
        } else {
            // Si no se ingresó ningún valor o se cerró el cuadro de diálogo
            System.out.println("No se ingresó ningún valor para la resistencia.");
        }
        return valor;
    }

    private void cambiarCable(Cable cable) {

        // Eliminar el cable actual
        eliminarCable(cable, false);

        int[] cl = loc.getfilaccoluma(cable.getStartX(), cable.getStartY());

        // Crear un nuevo cable con las mismas coordenadas pero con el nuevo objeto
        dibujarCable(cable.getStartX(), cable.getStartY(), cable.getEndX(), cable.getEndY(), cl[0], cl[1],
                cable.getvalor());

        eliminarEnergiaSinConexiones(protoboard.getMatriz());
        eliminarEnergiaSinConexiones(protoboard.getMatriz());
        eliminarEnergiaSinConexiones(protoboard.getMatriz());
        eliminarEnergiaSinConexiones(protoboard.getMatriz());
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

        if (nose) {
            eliminarEnergiaSinConexiones(protoboard.getMatriz());
            eliminarEnergiaSinConexiones(protoboard.getMatriz());
            eliminarEnergiaSinConexiones(protoboard.getMatriz());
            eliminarEnergiaSinConexiones(protoboard.getMatriz());
        }
    }

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

        // Crear una nueva matriz temporal para almacenar los cambios
        String[][] nuevaMatrizEnergia = new String[matrizEnergia.length][matrizEnergia[0].length];

        // Llenar la nueva matriz con los valores actualizados
        for (int i = 0; i < nuevaMatrizEnergia.length; i++) {
            for (int j = 0; j < nuevaMatrizEnergia[i].length; j++) {
                // Verifica si la celda está en el conjunto de celdas conectadas
                if (celdasConectadas.contains(i + "," + j)) {
                    nuevaMatrizEnergia[i][j] = matrizEnergia[i][j]; // Mantiene el valor original
                } else {
                    nuevaMatrizEnergia[i][j] = "|"; // Indica energía sin conexión
                    cambiarColorCelda(i, j, Color.LIGHTGRAY, gridPane); // Cambia el color
                }
            }
        }

        // Reemplaza la matriz original con la nueva matriz
        for (int i = 0; i < matrizEnergia.length; i++) {
            System.arraycopy(nuevaMatrizEnergia[i], 0, matrizEnergia[i], 0, matrizEnergia[i].length);
        }

        // Actualiza el protoboard después de los cambios
        controlador.actualizarBuses(gridPane);
        controlador.ActualizarProtoboard(gridPane);
        Espera = false;
    }

    // Método para obtener un cable en una posición específica (fila, columna)
    public Cable obtenerCableEnPosicion(int fila, int columna) {
        // Itera sobre la lista de cables y verifica si alguno está en la posición
        // indicada
        for (Cable cable : cables) {
            // Verificar si la posición coincide con el inicio o el final del cable
            if ((cable.getFilaInicio() == fila && cable.getColumnaInicio() == columna) ||
                    (cable.getFilaFin() == fila && cable.getColumnaFin() == columna)) {
                return cable; // Devuelve el cable si coincide con el inicio o el final
            }
        }
        return null; // Retorna null si no hay ningún cable en esa posición
    }

    public boolean Espera() {
        return Espera;
    }

    public void actualizar() {
        // Obtener la lista de cables a partir del método obtenerCables().
        ArrayList<Cable> cables1 = new ArrayList<>(obtenerCables()); // Hacemos una copia para evitar modificaciones
                                                                     // concurrentes

        // Eliminar cada cable de la lista de cables.
        // Creamos una lista temporal para los cables a eliminar.
        ArrayList<Cable> cablesParaEliminar = new ArrayList<>(cables1);

        for (Cable cable : cablesParaEliminar) {
            eliminarCable(cable, false);
        }

        // Redibujar los cables después de eliminarlos.
        for (Cable cable : cables1) {
            // Obtener las coordenadas de inicio y fin del cable para redibujarlo.
            int iniciox = cable.getFilaInicio();
            int inicioy = cable.getColumnaInicio();
            int finalX = cable.getFilaFin();
            int fianlY = cable.getColumnaFin();

            double startX1 = cable.getStartX();
            double startY1 = cable.getStartY();
            setObjetoSeleccionado(cable.getObjeto());
            // Redibujar el cable utilizando las coordenadas.
            redibujar(iniciox, inicioy, finalX, fianlY, startX1, startY1, cable.getvalor());
        }
    }

    private void colocarChip(int fila, int columna) {
        // Verificar que la posición sea válida
        if (fila == 6 && columna + 6 <= 30) {
            // Cargar la imagen del chip
            Image imagenChip = new Image("/resources/chip.png");
    
            // Colocar el chip en las filas 6 y 7
            for (int i = 0; i < 6; i++) {
                // Colocar en fila 6
                colocarImagenEnPosicion(fila, columna + i, imagenChip);
                // Colocar en fila 7
                colocarImagenEnPosicion(fila + 1, columna + i, imagenChip);
            }
    
            // Aquí puedes realizar cualquier actualización visual adicional si es necesario
            System.out.println("Chip colocado en la fila 6 y 7 desde la columna " + columna);
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Error al colocar el objeto");
            alerta.setContentText("El objeto no se puede colocar en la posición seleccionada. Solo puedes colocar el chip en la fila 6.");
            alerta.showAndWait();
        }
    }
    
    private void colocarImagenEnPosicion(int fila, int columna, Image imagenChip) {
        Node hoyito = loc.obtenerNodoPorFilaColumna(fila, columna);
        if (hoyito != null) {
            // Eliminar el nodo del GridPane si es necesario
            drawingPane.getChildren().remove(hoyito);
    
            // Obtener las coordenadas para posicionar la imagen
            double[] coordenadas = loc.getCoordenadasGridPane(hoyito);
            double posX = coordenadas[0];
            double posY = coordenadas[1];
    
            // Crear y configurar el ImageView
            ImageView imagenView = new ImageView(imagenChip);
            imagenView.setFitWidth(30); // Ajusta el ancho según sea necesario
            imagenView.setFitHeight(20); // Ajusta la altura según sea necesario
            imagenView.setPreserveRatio(false); // Mantiene la relación de aspecto
    
            // Posicionar la imagen correctamente en el drawingPane
            drawingPane.getChildren().add(imagenView);
            imagenView.setLayoutX(posX - 16);
            imagenView.setLayoutY(posY - 10);
            protoboard.getMatriz()[fila][columna] = "C"; // Representación del chip en la matriz
        }
    }
}