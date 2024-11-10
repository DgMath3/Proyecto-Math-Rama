import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Controlador {

    public void actualizarBuses(GridPane gridPane) {
        for (int i = 0; i < 2; i++) {
            actualizarFilaBus(gridPane, i, Color.BLUE);
            actualizarFilaBus(gridPane, i, Color.RED);
        }
        for (int i = 12; i < 14; i++) {
            actualizarFilaBus(gridPane, i, Color.BLUE);
            actualizarFilaBus(gridPane, i, Color.RED);
        }
    }

    private void actualizarFilaBus(GridPane gridPane, int fila, Color color) {
        int numColumns = gridPane.getColumnConstraints().size(); // Obtén el número de columnas
        for (int j = 0; j < numColumns; j++) {
            int index = fila * numColumns + j;
            if (index < gridPane.getChildren().size()) { // Verifica si el índice es válido
                Circle punto = (Circle) gridPane.getChildren().get(index);
                if (punto.getFill() == color) {
                    for (int k = 0; k < numColumns; k++) {
                        int indexBus = fila * numColumns + k;
                        if (indexBus < gridPane.getChildren().size()) { // Verifica si el índice es válido
                            Circle puntoBus = (Circle) gridPane.getChildren().get(indexBus);
                            puntoBus.setFill(color);
                        }
                    }
                    break; 
                }
            }
        }
    }

    public void ActualizarProtoboard(GridPane gridPane) {
        for (int i = 2; i < 7; i++) {
            actualizarFila(gridPane, i, Color.BLUE);
            actualizarFila(gridPane, i, Color.RED);
        }
        for (int i = 7; i < 12; i++) {
            actualizarFila(gridPane, i, Color.BLUE);
            actualizarFila(gridPane, i, Color.RED);
        }
    }

    private void actualizarFila(GridPane gridPane, int fila, Color color) {
        int numColumns = gridPane.getColumnConstraints().size(); // Obtén el número de columnas
        for (int j = 0; j < numColumns; j++) {
            int index = fila * numColumns + j;
            if (index < gridPane.getChildren().size()) { // Verifica si el índice es válido
                Circle punto = (Circle) gridPane.getChildren().get(index);
                if (punto.getFill() == color) {
                    for (int m = (fila < 7 ? 2 : 7); m < (fila < 7 ? 7 : 12); m++) {
                        int index2 = m * numColumns + j;
                        if (index2 < gridPane.getChildren().size()) { // Verifica si el índice es válido
                            Circle punto2 = (Circle) gridPane.getChildren().get(index2);
                            punto2.setFill(color);
                        }
                    }
                }
            }
        }
    }
}