import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.Node;

public class Protoboard {

    private GridPane gridPane;
    private final int numFilas = 14;
    private final int numColumnas = 30;
    private final double espacio = 15.0;

    public Protoboard() {
        gridPane = new GridPane();
        configurarGridPane();
        crearProtoboard();
    }

    public void cambiarColor(int fila, int columna, Color color) {
        Circle punto = (Circle) gridPane.getChildren().get(fila * numColumnas + columna);
        punto.setFill(color);
    }

    private void configurarGridPane() {
        gridPane.setPrefSize(1200, 800);

        for (int i = 0; i < numColumnas; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / numColumnas);
            gridPane.getColumnConstraints().add(col);
        }

        for (int i = 0; i < numFilas; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / numFilas);
            gridPane.getRowConstraints().add(row);
        }

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
    }

    private void crearProtoboard() {
        double puntoTamaño = 10;
        double puntoEspacio = (espacio - puntoTamaño) / 2;

        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                final int filaFinal = i;  // Variable final para lambda
                final int columnaFinal = j;  // Variable final para lambda

                Circle punto = new Circle(puntoTamaño, Color.LIGHTGRAY);

                if (i == 0 || i == 1) {
                    punto.setFill(Color.LIGHTGRAY); // Buses superiores
                } else if (i == 12 || i == 13) {
                    punto.setFill(Color.LIGHTGRAY); // Buses inferiores
                }

                // Asignar evento de clic
                punto.setOnMouseClicked(event -> manejarClickEnBus(punto, filaFinal, columnaFinal));

                gridPane.add(punto, j, i);
                GridPane.setMargin(punto, new Insets(puntoEspacio));
            }
        }
    }

    private void manejarClickEnBus(Circle punto, int fila, int columna) {
        Color colorActual = (Color) punto.getFill();
        System.out.println("Click en bus: Fila " + fila + " Columna " + columna + " Color: " + colorActual);

        if (colorActual.equals(Color.GREEN) || colorActual.equals(Color.RED)) {
            if (fila == 0 || fila == 1) { // Filas de bus superiores
                for (int i = 2; i <= 6; i++) {  // Filas 3 a 6
                    Circle puntoColumna = obtenerPuntoEnFilaColumna(i, columna);
                    if (puntoColumna != null) {
                        puntoColumna.setFill(colorActual);
                    }
                }
            } else if (fila == 12 || fila == 13) { // Filas de bus inferiores
                for (int i = 7; i <= 11; i++) {  // Filas 8 a 11
                    Circle puntoColumna = obtenerPuntoEnFilaColumna(i, columna);
                    if (puntoColumna != null) {
                        puntoColumna.setFill(colorActual);
                    }
                }
            }
        }
    }

    private Circle obtenerPuntoEnFilaColumna(int fila, int columna) {
        for (Node nodo : gridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(nodo);
            Integer colIndex = GridPane.getColumnIndex(nodo);

            if (rowIndex != null && rowIndex.equals(fila) && colIndex != null && colIndex.equals(columna)) {
                return (Circle) nodo;
            }
        }
        return null;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void agregarObjeto(String tipo) {
        Button nuevoObjeto = new Button(tipo);
        nuevoObjeto.setOnAction(e -> System.out.println("Objeto " + tipo + " agregado"));
        gridPane.add(nuevoObjeto, 0, 0);
    }
}
