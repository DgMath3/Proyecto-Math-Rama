import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

        double cellWidth = gridPane.getWidth() / columnas;
        double cellHeight = gridPane.getHeight() / filas;

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Circle celda = new Circle(10, colorDefecto); // Ajusta el radio según sea necesario
                // Posicionar el círculo en el centro de la celda
                celda.setTranslateX(columna * cellWidth + cellWidth / 2);
                celda.setTranslateY(fila * cellHeight + cellHeight / 2);
                gridPane.add(celda, columna, fila);
                celdas[fila][columna] = celda;
            }
        }
    }

    private void configurarEventos() {
        gridPane.setOnMouseMoved(this::manejarMovimientoMouse);
    }

    private void manejarMovimientoMouse(MouseEvent evento) {
        // Tamaño de cada celda en píxeles
        double cellWidth = gridPane.getWidth() / gridPane.getColumnConstraints().size();
        double cellHeight = gridPane.getHeight() / gridPane.getRowConstraints().size();
        int indiceFila = (int) (evento.getY() / cellHeight);
        int indiceColumna = (int) (evento.getX() / cellWidth);

        // Restablecer el color de todas las celdas
        for (int f = 0; f < celdas.length; f++) {
            for (int c = 0; c < celdas[f].length; c++) {
                if (celdas[f][c] != null) {
                    celdas[f][c].setFill(colorDefecto);
                }
            }
        }

        // Resaltar la celda actual
        if (indiceFila >= 0 && indiceFila < celdas.length &&
            indiceColumna >= 0 && indiceColumna < celdas[0].length) {
            if (celdas[indiceFila][indiceColumna] != null) {
                celdas[indiceFila][indiceColumna].setFill(colorResaltado);
                filaActual = indiceFila;
                columnaActual = indiceColumna;
            }
        }
    }

    public int getFilaActual() {
        return filaActual;
    }

    public int getColumnaActual() {
        return columnaActual;
    }
}
