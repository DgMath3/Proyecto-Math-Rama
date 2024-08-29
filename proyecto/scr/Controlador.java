import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Controlador {

    public void actualizarBuses(GridPane gridPane) {
        // Se recorre el GridPane solamente por los buses (fila 1 2 13 14)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                // Se verifica si el punto es de color verde
                if (punto.getFill() == Color.GREEN) {
                    // Se recorre la fila para cambiar el color de los puntos
                    for (int k = 0; k < 30; k++) {
                        Circle puntoBus = (Circle) gridPane.getChildren().get(i * 30 + k);
                        puntoBus.setFill(Color.GREEN);
                    }
                }
            }
        }
        for (int i = 12; i < 14; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                if (punto.getFill() == Color.GREEN) {
                    for (int k = 0; k < 30; k++) {
                        Circle puntoBus = (Circle) gridPane.getChildren().get(i * 30 + k);
                        puntoBus.setFill(Color.GREEN);
                    }
                }
            }
        }
        //lo mismo para el rojo
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                // Se verifica si el punto es de color tojo
                if (punto.getFill() == Color.RED) {
                    // Se recorre la fila para cambiar el color de los puntos
                    for (int k = 0; k < 30; k++) {
                        Circle puntoBus = (Circle) gridPane.getChildren().get(i * 30 + k);
                        puntoBus.setFill(Color.RED);
                    }
                }
            }
        }
        for (int i = 12; i < 14; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                if (punto.getFill() == Color.RED) {
                    for (int k = 0; k < 30; k++) {
                        Circle puntoBus = (Circle) gridPane.getChildren().get(i * 30 + k);
                        puntoBus.setFill(Color.RED);
                    }
                }
            }
        }
    }
}
