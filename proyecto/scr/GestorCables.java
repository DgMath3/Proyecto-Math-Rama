import javafx.scene.image.Image;
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
import java.util.Optional;
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
    private int[][] matrizConexiones;
    private boolean estado;
    private int largo;
    private double bateriax;
    private double bateriay;

    public GestorCables(GridPane gridPane, Loc loc, Protoboard protoboard, Controlador controlador) {
        this.gridPane = gridPane;
        this.loc = loc;
        this.protoboard = protoboard;
        this.controlador = controlador;
        this.drawingPane = new Pane();
        this.drawingPane.setMouseTransparent(false);
        this.gridPane.getChildren().add(drawingPane);
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
        gridPane.setOnMouseClicked(event -> {
            clicpresionado(event);
        });
        drawingPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            manejarclicks(event);
        });

    }

    public void setlargo(int largo) {
        this.largo = largo;
    }

    private void clicpresionado(MouseEvent event) {
        if (!cablearActivo || objetoSeleccionado == null) {
            return; // Si no está activo o no hay objeto seleccionado, no hace nada
        }

        if (objetoSeleccionado == null) {
            System.out.println("objetoSeleccionado es null");
            return; // Evitar que el código siga si el objeto es null
        }

        String objetoId = objetoSeleccionado.getId();

        // Verificar si el objeto seleccionado no es un chip o switch especial
        if (!objetoId.equals("chip") && !objetoId.equals("AND") && !objetoId.equals("OR") && !objetoId.equals("NOT")) {
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
                        dibujarCable(startX, startY, clickX, clickY, cl[0], cl[1], 1);
                        drawing = false;
                        cablearActivo = false; // Desactiva la funcionalidad de cablear después de dibujar
                        objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
                        resetcablegen();
                    }
                }
            });
            pause.play();
        } else if (objetoId.equals("AND") || objetoId.equals("OR") || objetoId.equals("NOT")) {
            // Colocar el chip según su ID específico
            double clickX = event.getX();
            double clickY = event.getY();
            int[] cl = loc.getfilaccoluma(clickX, clickY);

            // Verificar el tipo de chip usando el tipo guardado en `objetoSeleccionado`
            String tipoChip = objetoSeleccionado.getId(); // Obtener el tipo de chip

            System.out.println("Tipo de chip: " + tipoChip);
            switch (tipoChip) {
            case "AND":
                colocarChip(cl[0], cl[1], "AND", largo);
                break;
            case "OR":
                colocarChip(cl[0], cl[1], "OR", largo);
                break;
            case "NOT":
                colocarChip(cl[0], cl[1], "NOT", largo);
                break;
            default:
                // Si el chip no es uno de los tres tipos conocidos
                System.out.println("Tipo de chip desconocido: " + tipoChip);
                break;
            }

            drawing = false;
            cablearActivo = false;
            objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo

        }
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

        if (matrizConexiones[filaInicio][columnaInicio] == 1 && (!objetoSeleccionado.getId().equals("cablegen+")
                && !objetoSeleccionado.getId().equals("cablegen-"))) {
            alerta("hay un objeto #001");
            return false;
        }

        if (matrizConexiones[filaFin][columnaFin] == 1) {
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
                filaInicio, columnaInicio, filaFin, columnaFin, valor, objetoSeleccionado.getLed());

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
        resetcablegen();
        return true;
    }

    public Boolean redibujar(int filaInicio, int columnaInicio, int filaFin, int columnaFin, double startX,
            double startY, double valor, Objeto objeto) {
        // Obtener color, imagen y estado de pasa energía del objeto seleccionado
        Paint color = objeto != null ? objeto.getColor() : Color.BLACK;
        ImageView imageView = objeto != null ? new ImageView(objeto.getImagen()) : null;
        boolean pasa = objeto != null && objeto.getpasa();

        // Agregar las funciones llamando a loc para obtener el centro del grid pane
        Node nodo1 = loc.obtenerNodoPorFilaColumna(filaFin, columnaFin);
        Node nodo2 = loc.obtenerNodoPorFilaColumna(filaInicio, columnaInicio);

        // Obtener coordenadas de los nodos
        double[] inicio = loc.getCoordenadasGridPane(nodo1);
        double[] fin = loc.getCoordenadasGridPane(nodo2);

        if (objeto.getId().equals("cablegen+") || objeto.getId().equals("cablegen-")) {
            inicio = new double[] { startX, startY };
        }

        // Crear el cable utilizando las coordenadas centradas
        Cable cable = new Cable(inicio[0], inicio[1], fin[0], fin[1], color, objeto, imageView, pasa, filaInicio,
                columnaInicio, filaFin, columnaFin, valor, objeto.getLed());

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

        if (!objeto.getId().equals("cablegen+") && !objeto.getId().equals("cablegen-")) {
            matrizConexiones[filaFin][columnaFin] = 1;
        }
        matrizConexiones[filaInicio][columnaInicio] = 1;
        return true; // Cable dibujado exitosamente
    }

    public void resetLed() {
        for (Cable cable2 : obtenerCables()) {
            if (cable2.getObjeto().getId().equals("Led_on")) {
                redibujar(cable2.getFilaInicio(), cable2.getColumnaInicio(), cable2.getFilaFin(),
                        cable2.getColumnaFin(), cable2.getStartX(), cable2.getStartY(), cable2.getvalor(),
                        new Objeto("Led", cable2.getColorled()));
                eliminarCable(cable2, false);
            }
        }
    }

    public void resetcablegen() {
        for (Cable cable2 : obtenerCables()) {
            if (cable2.getObjeto().getId().equals("cablegen+") || cable2.getObjeto().getId().equals("cablegen-")) {
                redibujar(cable2.getFilaInicio(), cable2.getColumnaInicio(), cable2.getFilaFin(),
                        cable2.getColumnaFin(), cable2.getStartX(), cable2.getStartY(), cable2.getvalor(),
                        cable2.getObjeto());
                eliminarCable(cable2, false);
            }
        }
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
                eliminarCable(cableToRemove, false);
                resetLed();
                resetcablegen();
                eliminarEnergiaConRetraso(protoboard.getMatriz(), 100);
                setEnergia();
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
                cambiarCable(cableToChange, objetoSeleccionado);
                resetLed();
                resetcablegen();
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
            if (cableCambiado != null) {
                if (cableCambiado.getObjeto().getId().equals("Switch")) {
                    cambiarCable(cableCambiado, new Objeto("SwitchOn", "x"));
                    eliminarCable(cableCambiado, false);
                } else if (cableCambiado.getObjeto().getId().equals("SwitchOn")) {
                    cables.remove(cableCambiado);
                    cambiarCable(cableCambiado, new Objeto("Switch", "x"));
                    event.consume();
                    eliminarEnergiaConRetraso(protoboard.getMatriz(), 50);
                    eliminarEnergiaConRetraso(protoboard.getMatriz(), 100);
                    eliminarEnergiaConRetraso(protoboard.getMatriz(), 150);
                    eliminarEnergiaConRetraso(protoboard.getMatriz(), 200);
                } else if (cableCambiado.getObjeto().getId().equals("resistor")) {
                    double valor = solicitarValor("Configuracion resistor", "valor actual: ", cableCambiado.getvalor());
                    redibujar(cableCambiado.getFilaInicio(), cableCambiado.getColumnaInicio(),
                            cableCambiado.getFilaFin(), cableCambiado.getColumnaFin(), 0, 0, valor,
                            new Objeto("resistor", "x"));
                    eliminarCable(cableCambiado, false);
                    event.consume();

                }

                event.consume(); // Evita que el evento se propague
            }
        }

    }

    public void eliminarEnergiaConRetraso(String[][] matrizEnergia, long delayMilisegundos) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMilisegundos);
                EliminarEnergia(matrizEnergia);
                EliminarEnergia(matrizEnergia);
                EliminarEnergia(matrizEnergia);
                setEnergia();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static double solicitarValor(String titulo, String texto, double valor) {
        // Crear un cuadro de diálogo de entrada
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(texto + valor);
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

    private void cambiarCable(Cable cable, Objeto objeto) {

        // Eliminar el cable actual
        eliminarCable(cable, false);
        redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(), cable.getColumnaFin(),
                cable.getStartX(), cable.getStartY(), cable.getvalor(), objeto);

        EliminarEnergia(protoboard.getMatriz());
        setEnergia();
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

    public void eliminarChip(Chip chip) {
        // Obtener las imágenes del chip
        for (int i = 0; i < chip.getlargo() * 2 + 2; i++) {
            ImageView imageView = chip.getImageView(i);
            if (imageView != null) {
                drawingPane.getChildren().remove(imageView);
            }
        }

        // Establecer la matriz de conexiones a 0 en los puntos ocupados por el chip
        for (int i = chip.getFilaInicio(); i <= chip.getFilaInicio() + 1; i++) {
            for (int j = chip.getColumnaInicio(); j <= chip.getColumnaInicio() + chip.getlargo(); j++) {
                if (i >= 0 && i < matrizConexiones.length && j >= 0 && j < matrizConexiones[i].length) {
                    matrizConexiones[i][j] = 0; // Establecer el valor a 0
                }
            }
        }

        // Luego, eliminar el chip de la lista
        chips.remove(chip);
    }

    public void setbateria(double bateriax, double bateriay) {
        this.bateriax = bateriax;
        this.bateriay = bateriay;
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
            redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(), cable.getColumnaFin(),
                    bateriax, bateriay, cable.getvalor(), cable.getObjeto());
        }

        for (Chip chip : chips1) {
            colocarChip(chip.getFilaInicio(), chip.getColumnaInicio(), chip.getId(), chip.getlargo());
        }
    }

    public void colocarChip(int fila, int columna, String tipo, int largo) {
        System.err.println(tipo);

        // Verificar que no haya chips en las filas 6 y 7
        for (int i = columna; i < columna + largo; i++) {
            if (i >= 0 && i < matrizConexiones[0].length) {
                // Verificar en la fila 6 y 7 si ya hay un chip
                if (matrizConexiones[6][i] == 1 || matrizConexiones[7][i] == 1) {
                    alerta("Ya hay un chip en esta posición, por favor selecciona otra.");
                    return; // Salir si hay un chip en la posición
                }
            }
        }

        // Si la posición está libre, proceder a colocar el chip
        if (fila == 6 && columna + largo <= 30) {
            // Marcar las posiciones de las filas 6 y 7 como ocupadas
            for (int i = fila; i <= fila + 1; i++) {
                for (int j = columna; j <= columna + largo - 1; j++) {
                    if (i >= 0 && i < matrizConexiones.length && j >= 0 && j < matrizConexiones[i].length) {
                        matrizConexiones[i][j] = 1; // Marcar como ocupado
                    }
                }
            }

            // Crear las imágenes y el chip
            Image imagenChip = new Image("/resources/chip.png");
            Image imagenChip1 = new Image("/resources/chip1.png");
            Chip nuevoChip = new Chip(fila, columna, fila + 1, columna + 6, largo, tipo);

            // Colocar las imágenes en las filas 6 y 7 y guardar los ImageViews
            for (int i = 0; i < largo; i++) {
                ImageView imageViewFila6 = colocarImagenEnPosicion(fila, columna + i, imagenChip, 20);
                ImageView imageViewFila7 = colocarImagenEnPosicion(fila + 1, columna + i, imagenChip1, -10);

                // Guardar las imágenes en el Chip
                nuevoChip.setImageView(i, imageViewFila6); // Fila 6
                nuevoChip.setImageView(i + largo, imageViewFila7); // Fila 7
            }

            // Guardar el nuevo chip en la lista de chips
            chips.add(nuevoChip);
        } else {
            alerta("El objeto no se puede colocar en la posición seleccionada. Solo puedes colocar el chip en la fila 6.");
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

    public void EliminarEnergia(String[][] matrizEnergia) {
        for (int i = 0; i < matrizEnergia.length; i++) {
            for (int j = 0; j < matrizEnergia[i].length; j++) {
                if (matrizEnergia[i][j].equals("+") || matrizEnergia[i][j].equals("-")) {
                    matrizEnergia[i][j] = "|";
                    protoboard.cambiarColor(i, j, Color.LIGHTGRAY);
                }
            }
        }
        controlador.actualizarBuses(gridPane);
        controlador.ActualizarProtoboard(gridPane);
    }

    public void setEnergia() {
        for (Cable cable : cables) {
            if (cable.getObjeto().getId().equals("cablegen+")) {
                protoboard.cambiarColor(cable.getFilaInicio(), cable.getColumnaInicio(), Color.BLUE);
            } else if (cable.getObjeto().getId().equals("cablegen-")) {
                protoboard.cambiarColor(cable.getFilaInicio(), cable.getColumnaInicio(), Color.RED);
            }
            controlador.actualizarBuses(protoboard.getGridPane());
            controlador.ActualizarProtoboard(protoboard.getGridPane());
        }
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public boolean getestado() {
        return estado;
    }
}