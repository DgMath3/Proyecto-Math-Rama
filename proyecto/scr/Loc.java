import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.Node;

public class Loc {
    private final GridPane gridPane;
    private final Color colorResaltado;
    private final Color colorDefecto;
    private Circle[][] celdas;
    private int filaActual = -1;
    private int columnaActual = -1;

    public Loc(GridPane gridPane, Color colorResaltado, Color colorDefecto) {
        this.gridPane = gridPane;
        this.colorResaltado = colorResaltado;
        this.colorDefecto = colorDefecto;
        inicializarCeldas();
        configurarEventos();


    }

    private void inicializarCeldas() {
        int filas = gridPane.getRowConstraints().size();
        int columnas = gridPane.getColumnConstraints().size();
        celdas = new Circle[filas][columnas];

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Circle celda = new Circle(10, colorDefecto);  // Ajusta el radio según sea necesario
                gridPane.add(celda, columna, fila);
                celdas[fila][columna] = celda;
            }
        }
    }

    private void configurarEventos() {
        gridPane.setOnMouseMoved(event -> {
            // Actualiza la posición del mouse
            manejarMovimientoMouse(event);
        });
        gridPane.setOnMouseExited(this::manejarSalidaMouse);
    }

    private void manejarMovimientoMouse(MouseEvent evento) {
        // Obtener nodo bajo el mouse
        Node nodo = obtenerNodoBajoMouse(evento.getX(), evento.getY());

        // Restablecer el color de todas las celdas
        restablecerColores();

        if (nodo instanceof Circle) {
            Integer indiceFila = GridPane.getRowIndex(nodo);
            Integer indiceColumna = GridPane.getColumnIndex(nodo);

            if (indiceFila != null && indiceColumna != null) {
                celdas[indiceFila][indiceColumna].setFill(colorResaltado);
                filaActual = indiceFila;
                columnaActual = indiceColumna;
            }
        }
    }

    private void manejarSalidaMouse(MouseEvent evento) {
        restablecerColores(); // Restablecer todos los colores cuando el mouse salga del GridPane
        filaActual = -1;
        columnaActual = -1;
    }

    private void restablecerColores() {
        for (int f = 0; f < celdas.length; f++) {
            for (int c = 0; c < celdas[f].length; c++) {
                if (celdas[f][c] != null) {
                    celdas[f][c].setFill(colorDefecto);
                }
            }
        }
    }

    private Node obtenerNodoBajoMouse(double x, double y) {
        for (Node nodo : gridPane.getChildren()) {
            if (nodo.getBoundsInParent().contains(x, y)) {
                return nodo;
            }
        }
        return null;
    }

    public int getFilaActual() {
        return filaActual;
    }

    public int getColumnaActual() {
        return columnaActual;
    }

    public boolean estaDentroDelGridPane(double x, double y) {
        return x >= 0 && x <= gridPane.getWidth() &&
               y >= 0 && y <= gridPane.getHeight();
    }

    public int[] getfilaccoluma(double x, double y) {
        // Obtener el tamaño del GridPane y el número de filas y columnas
        double anchoCelda = gridPane.getWidth() / gridPane.getColumnConstraints().size();
        double altoCelda = gridPane.getHeight() / gridPane.getRowConstraints().size();
    
        // Calcular la columna y la fila en base a las coordenadas (x, y)
        int columna = (int) (x / anchoCelda);
        int fila = (int) (y / altoCelda);
    
        // Asegurarse de que la columna y fila estén dentro de los límites válidos
        columna = Math.max(0, Math.min(columna, gridPane.getColumnConstraints().size() - 1));
        fila = Math.max(0, Math.min(fila, gridPane.getRowConstraints().size() - 1));
    
        return new int[]{fila, columna};
    }
}