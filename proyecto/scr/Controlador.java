import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Controlador {

    public void actualizarBuses(GridPane gridPane) {
        // Implementación del método actualizarBuses
        for (int i = 0; i < 2; i++) {
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
    }
    public void ActualizarProtoboard(GridPane gridPane) {
        // Actualizar filas del 2 al 6
        for (int i = 2; i < 7; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                if (punto.getFill() == Color.GREEN) {
                    for (int m = 2; m < 7; m++) {
                        Circle punto2 = (Circle) gridPane.getChildren().get(m * 30 + j);
                        punto2.setFill(Color.GREEN);
                    }
                }
            }
        }
        
        // Actualizar filas del 7 al 12
        for (int i = 7; i < 12; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                if (punto.getFill() == Color.GREEN) {
                    for (int m = 7; m < 12; m++) {
                        Circle punto2 = (Circle) gridPane.getChildren().get(m * 30 + j);
                        punto2.setFill(Color.GREEN);
                    }
                }
            }
        }
        // Actualizar filas del 2 al 6
        for (int i = 2; i < 7; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                if (punto.getFill() == Color.RED) {
                    for (int m = 2; m < 7; m++) {
                        Circle punto2 = (Circle) gridPane.getChildren().get(m * 30 + j);
                        punto2.setFill(Color.RED);
                    }
                }
            }
        }
        
        // Actualizar filas del 7 al 12
        for (int i = 7; i < 12; i++) {
            for (int j = 0; j < 30; j++) {
                Circle punto = (Circle) gridPane.getChildren().get(i * 30 + j);
                if (punto.getFill() == Color.RED) {
                    for (int m = 7; m < 12; m++) {
                        Circle punto2 = (Circle) gridPane.getChildren().get(m * 30 + j);
                        punto2.setFill(Color.RED);
                    }
                }
            }
        }
    }
}

