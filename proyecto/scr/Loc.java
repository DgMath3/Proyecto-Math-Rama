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
                Circle celda = new Circle(11, colorDefecto); // Ajusta el radio según sea necesario
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
            }
        }
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

    public Node obtenerNodoBajoMouse(double x, double y) {
        for (Node nodo : gridPane.getChildren()) {
            if (nodo.getBoundsInParent().contains(x, y)) {
                return nodo;
            }
        }
        return null;
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
        int columna = (int) Math.floor(x / anchoCelda);
        int fila = (int) Math.floor(y / altoCelda);
    
        // Asegurarse de que la columna y fila estén dentro de los límites válidos
        columna = Math.max(0, Math.min(columna, gridPane.getColumnConstraints().size() - 1));
        fila = Math.max(0, Math.min(fila, gridPane.getRowConstraints().size() - 1));
    
        return new int[] { fila, columna };
    }
    

    // Función para obtener las coordenadas absolutas del GridPane
    public double[] getCoordenadasGridPane(Node nodo) {
        if (nodo != null) {
            // Convertir coordenadas del nodo a la escena
            javafx.geometry.Point2D puntoEnEscena = nodo.localToScene(nodo.getBoundsInLocal().getMinX(),
                    nodo.getBoundsInLocal().getMinY());

            // Obtener las coordenadas de la esquina superior izquierda del GridPane en la
            // escena
            javafx.geometry.Point2D puntoGridPane = gridPane.localToScene(0.0, 0.0);

            // Calcular las coordenadas relativas del nodo al GridPane
            double xRelativa = puntoEnEscena.getX() - puntoGridPane.getX();
            double yRelativa = puntoEnEscena.getY() - puntoGridPane.getY();

            return new double[] { xRelativa, yRelativa };
        }
        return null;
    }

    public Node obtenerNodoPorFilaColumna(int fila, int columna) {
        // Asegúrate de que los índices estén dentro de los límites del GridPane
        if (fila < 0 || fila >= gridPane.getRowConstraints().size() || columna < 0 || columna >= gridPane.getColumnConstraints().size()) {
            throw new IndexOutOfBoundsException("Fila o columna fuera de límites");
        }
    
        // Busca el nodo directamente en el GridPane usando GridPane.getChildren()
        for (Node nodo : gridPane.getChildren()) {
            Integer indiceFila = GridPane.getRowIndex(nodo);
            Integer indiceColumna = GridPane.getColumnIndex(nodo);
    
            // Si el nodo se encuentra en la fila y columna especificadas, lo retornamos
            if (indiceFila != null && indiceColumna != null && indiceFila == fila && indiceColumna == columna) {
                return nodo;
            }
        }
        // Si no se encontró el nodo, retorna null (puedes manejar esto según sea necesario)
        return null;
    }
}