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

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Circle celda = new Circle(10, colorDefecto); // Ajusta el radio según sea necesario
                celda.setTranslateX(columna * (gridPane.getWidth() / columnas) + (gridPane.getWidth() / (2 * columnas)));
                celda.setTranslateY(fila * (gridPane.getHeight() / filas) + (gridPane.getHeight() / (2 * filas)));
                gridPane.add(celda, columna, fila);
                celdas[fila][columna] = celda;
            }
        }
    }

    private void configurarEventos() {
        gridPane.setOnMouseMoved(this::manejarMovimientoMouse);
    }

    private void manejarMovimientoMouse(MouseEvent evento) {
        int indiceFila = (int) (evento.getY() / (gridPane.getHeight() / celdas.length));
        int indiceColumna = (int) (evento.getX() / (gridPane.getWidth() / celdas[0].length));

        for (int f = 0; f < celdas.length; f++) {
            for (int c = 0; c < celdas[f].length; c++) {
                celdas[f][c].setFill(colorDefecto);
            }
        }

        if (indiceFila >= 0 && indiceFila < celdas.length && indiceColumna >= 0 && indiceColumna < celdas[0].length) {
            celdas[indiceFila][indiceColumna].setFill(colorResaltado);
            filaActual = indiceFila;
            columnaActual = indiceColumna;
        }
    }

    public int getFilaActual() {
        return filaActual;
    }

    public int getColumnaActual() {
        return columnaActual;
    }
}
