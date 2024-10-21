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
import java.util.concurrent.CopyOnWriteArrayList;

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
    private final List<Cable> cables = new CopyOnWriteArrayList<>();
    private List<Chip> chips;
    public boolean Espera;
    private int[][] matrizConexiones;

    public GestorCables(GridPane gridPane, Loc loc, Protoboard protoboard, Controlador controlador) {
        this.gridPane = gridPane;
        this.loc = loc;
        this.protoboard = protoboard;
        this.controlador = controlador;
        this.drawingPane = new Pane();
        this.drawingPane.setMouseTransparent(false);
        this.gridPane.getChildren().add(drawingPane);
        this.Espera = false;
        this.matrizConexiones = new int[14][30];
        inicializarMatrizConexiones();
        // Inicializar la lista de cables
        this.chips = new ArrayList<>();

        // Asegurarse de que el tamaño del Pane de dibujo sea correcto
        this.configurarEventos();
        this.drawingPane.setPrefSize(gridPane.getWidth(), gridPane.getHeight());
    }

    public void inicializarMatrizConexiones() {
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 30; j++) {
                matrizConexiones[i][j] = 0; // Indica que la posición está vacía
            }
        }
    }

    public void imprimirMatrizConexiones() {
        for (int i = 0; i < matrizConexiones.length; i++) {
            for (int j = 0; j < matrizConexiones[i].length; j++) {
                System.out.print(matrizConexiones[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println();
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

        if (!objetoSeleccionado.getId().equals("chip") && !objetoSeleccionado.getId().equals("Switch2")) {
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
        } else if (!objetoSeleccionado.getId().equals("Switch2")) {
            // llamamos a tu funcion
            double clickX = event.getX();
            double clickY = event.getY();
            drawing = true;
            int[] cl = loc.getfilaccoluma(clickX, clickY);
            colocarChip(cl[0], cl[1]);
            drawing = false;
            cablearActivo = false; // Desactiva la funcionalidad de cablear después de dibujar
            objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
        } else {
            // llamamos a tu funcion
            double clickX = event.getX();
            double clickY = event.getY();
            drawing = true;
            int[] cl = loc.getfilaccoluma(clickX, clickY);
            colocarotro(cl[0], cl[1]);
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
                if ((filaInicio == cableFilaInicio && columnaInicio == cableColumnainicio)
                        || (filaInicio == cableFilaFin && columnaInicio == cableColumnaFin)
                        || (filaFin == cableFilaInicio && columnaFin == cableColumnainicio)
                        || (filaFin == cableFilaFin && columnaFin == cableColumnaFin)) {
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
                if ((Math.abs(cable.getStartX() - startX) < 10 && Math.abs(cable.getStartY() - startY) < 10)
                        || (Math.abs(cable.getEndX() - endX) < 10 && Math.abs(cable.getEndY() - endY) < 10)) {
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

        if (matrizConexiones[filaInicio][columnaInicio] == 1) {
            alerta("hay un objeto #001");
            return false;
        }

        if (matrizConexiones[filaFin][columnaFin] == 1 && (!objetoSeleccionado.getId().equals("cablegen+")
                && !objetoSeleccionado.getId().equals("cablegen-"))) {
            alerta("hay un objeto #002");
            return false;
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

        if (!objetoSeleccionado.getId().equals("cablegen+") && !objetoSeleccionado.getId().equals("cablegen-")) {
            matrizConexiones[filaFin][columnaFin] = 1;
        }
        matrizConexiones[filaInicio][columnaInicio] = 1;

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

        if (!objetoSeleccionado.getId().equals("cablegen+") && !objetoSeleccionado.getId().equals("cablegen-")) {
            matrizConexiones[filaFin][columnaFin] = 1;
        }
        matrizConexiones[filaInicio][columnaInicio] = 1;

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
            Chip chipToRemove = null;
            for (Chip chip : chips) {
                for (int i = 0; i < 12; i++) {
                    ImageView imageView = chip.getImageView(i);
                    if (imageView != null) {
                        // Comprobar si el clic está dentro de los límites del ImageView
                        double imageViewX = imageView.getLayoutX();
                        double imageViewY = imageView.getLayoutY();
                        double imageViewWidth = imageView.getFitWidth();
                        double imageViewHeight = imageView.getFitHeight();

                        if (event.getX() >= imageViewX && event.getX() <= imageViewX + imageViewWidth
                                && event.getY() >= imageViewY && event.getY() <= imageViewY + imageViewHeight) {
                            chipToRemove = chip;
                            break;
                        }
                    }
                }
                if (chipToRemove != null) {
                    break;
                }
            }

            if (chipToRemove != null) {
                eliminarChip(chipToRemove);
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
                    eliminarCable(cableCambiado, false);
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
        redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(), cable.getColumnaFin(),
                cable.getStartX(), cable.getStartY(), cable.getvalor());

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

        if (!cable.getObjeto().getId().equals("cablegen+") && !cable.getObjeto().getId().equals("cablegen-")) {
            matrizConexiones[cable.getFilaInicio()][cable.getColumnaInicio()] = 0;
        }
        matrizConexiones[cable.getFilaFin()][cable.getColumnaFin()] = 0;

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

    public List<Chip> obtenerChips() {
        return chips;
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
        for(int a = 0; a <= 6; a++){
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
            if ((cable.getFilaInicio() == fila && cable.getColumnaInicio() == columna)
                    || (cable.getFilaFin() == fila && cable.getColumnaFin() == columna)) {
                return cable; // Devuelve el cable si coincide con el inicio o el final
            }
        }
        return null; // Retorna null si no hay ningún cable en esa posición
    }

    public boolean Espera() {
        return Espera;
    }

    public void eliminarChip(Chip chip) {
        // Obtener las imágenes del chip
        for (int i = 0; i < 12; i++) {
            ImageView imageView = chip.getImageView(i);
            if (imageView != null) {
                drawingPane.getChildren().remove(imageView);
            }
        }

        // Establecer la matriz de conexiones a 0 en los puntos ocupados por el chip
        for (int i = chip.getFilaInicio(); i <= chip.getFilaInicio() + 1; i++) {
            for (int j = chip.getColumnaInicio(); j <= chip.getColumnaInicio() + 6; j++) {
                if (i >= 0 && i < matrizConexiones.length && j >= 0 && j < matrizConexiones[i].length) {
                    matrizConexiones[i][j] = 0; // Establecer el valor a 0
                }
            }
        }

        // Luego, eliminar el chip de la lista
        chips.remove(chip);
    }

    public void actualizar() {
        // Obtener la lista de cables a partir del método obtenerCables().
        ArrayList<Cable> cables1 = new ArrayList<>(obtenerCables());
        ArrayList<Chip> chips1 = new ArrayList<>(obtenerChips());

        ArrayList<Cable> cablesParaEliminar = new ArrayList<>(cables1);
        ArrayList<Chip> chipsParaEliminar = new ArrayList<>(chips1);

        // Eliminar cables
        for (Cable cable : cablesParaEliminar) {
            eliminarCable(cable, false);
        }

        // Eliminar chips
        for (Chip chip : chipsParaEliminar) {
            eliminarChip(chip);
        }

        // Redibujar los cables después de eliminarlos.
        for (Cable cable : cables1) {
            // Obtener las coordenadas de inicio y fin del cable para redibujarlo.
            int iniciox = cable.getFilaInicio();
            int inicioy = cable.getColumnaInicio();
            int finalX = cable.getFilaFin();
            int finalY = cable.getColumnaFin();

            double startX1 = cable.getStartX();
            double startY1 = cable.getStartY();
            setObjetoSeleccionado(cable.getObjeto());
            // Redibujar el cable utilizando las coordenadas.
            redibujar(iniciox, inicioy, finalX, finalY, startX1, startY1, cable.getvalor());
        }

        // Redibujar los chips después de eliminarlos.
        for (Chip chip : chips1) {
            // Redibujar el chip utilizando las coordenadas.
            colocarChip(chip.getFilaInicio(), chip.getColumnaInicio());
        }
    }

    
    public void colocarChip(int fila, int columna) {

        if (fila == 6 && columna + 6 <= 30) {
            for (int i = fila; i <= fila + 1; i++) {
                for (int j = columna; j <= columna + 5; j++) {
                    if (i >= 0 && i < matrizConexiones.length && j >= 0 && j < matrizConexiones[i].length) {
                        matrizConexiones[i][j] = 1;
                    }
                }
            }
            Image imagenChip = new Image("/resources/chip.png");
            Image imagenChip1 = new Image("/resources/chip1.png");
            Chip nuevoChip = new Chip(fila, columna, fila + 1, columna + 5);

            // Colocar las imágenes en las filas 6 y 7 y guardar los ImageViews
            for (int i = 0; i < 6; i++) {
                ImageView imageViewFila6 = colocarImagenEnPosicion(fila, columna + i, imagenChip, 20);
                ImageView imageViewFila7 = colocarImagenEnPosicion(fila + 1, columna + i, imagenChip1, -10);

                // Guardar las imágenes en el Chip
                nuevoChip.setImageView(i, imageViewFila6); // Fila 6
                nuevoChip.setImageView(i + 6, imageViewFila7); // Fila 7
            }

            // Guardar el nuevo chip en la lista de chips
            chips.add(nuevoChip);
        } else {
            alerta("El objeto no se puede colocar en la posición seleccionada. Solo puedes colocar el chip en la fila 6.");
        }
    }

    public void colocarotro(int fila, int columna) {
        if (fila <= 13 && columna + 2 <= 30) {
            for (int i = fila; i <= fila + 1; i++) {
                for (int j = columna; j <= columna + 1; j++) {
                    if (i >= 0 && i < matrizConexiones.length && j >= 0 && j < matrizConexiones[i].length) {
                        matrizConexiones[i][j] = 1;
                    }
                }
            }
            Image imagenChip = new Image("/resources/chip.png");
            Image imagenChip1 = new Image("/resources/chip1.png");
            Chip nuevoChip = new Chip(fila, columna, fila + 1, columna + 5);

            for (int i = 0; i < 2; i++) {
                ImageView imageViewFila6 = colocarImagenEnPosicion(fila, columna + i, imagenChip, 20);
                ImageView imageViewFila7 = colocarImagenEnPosicion(fila + 1, columna + i, imagenChip1, -10);

                // Guardar las imágenes en el Chip
                nuevoChip.setImageView(i, imageViewFila6); // Fila 6
                nuevoChip.setImageView(i + 6, imageViewFila7); // Fila 7
            }

            // Guardar el nuevo chip en la lista de chips
            chips.add(nuevoChip);
        } else {
            alerta("El objeto no se puede colocar en la posición seleccionada. Solo puedes colocar el Switch (grande)");
        }
    }

    private ImageView colocarImagenEnPosicion(int fila, int columna, Image imagenChip, int margen) {
        Node hoyito = loc.obtenerNodoPorFilaColumna(fila, columna);
        if (hoyito != null) {
            double[] coordenadas = loc.getCoordenadasGridPane(hoyito);
            double posX = coordenadas[0];
            double posY = coordenadas[1];

            ImageView imagenView = new ImageView(imagenChip);
            imagenView.setFitWidth(33);
            imagenView.setFitHeight(45);
            imagenView.setPreserveRatio(false);

            drawingPane.getChildren().add(imagenView);
            imagenView.setLayoutX(posX - 16);
            imagenView.setLayoutY(posY - 29 + margen);
            protoboard.getMatriz()[fila][columna] = "C";

            return imagenView; // Retorna el ImageView para que pueda ser almacenado
        }
        return null; // Si no se encuentra el nodo, retorna null
    }
}