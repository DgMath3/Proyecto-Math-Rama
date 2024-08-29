import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Protoboard {

    private GridPane gridPane;
    private final int numFilas = 14; // Actualizado a 14 filas
    private final int numColumnas = 30; // Número de columnas
    private final double espacio = 15.0; // Espacio entre los puntos

    public Protoboard() {
        gridPane = new GridPane();
        configurarGridPane();
        crearProtoboard();
    }

    private void configurarGridPane() {
        // Ajusta el tamaño del GridPane
        gridPane.setPrefSize(1200, 800); // Tamaño ajustado

        // Configura las columnas
        for (int i = 0; i < numColumnas; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / numColumnas); // Porcentaje de ancho
            gridPane.getColumnConstraints().add(col);
        }

        // Configura las filas
        for (int i = 0; i < numFilas; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / numFilas); // Porcentaje de altura
            gridPane.getRowConstraints().add(row);
        }

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10)); // Espaciado del borde
    }

    private void crearProtoboard() {
        double puntoTamaño = 10; // Tamaño de cada punto
        double puntoEspacio = (espacio - puntoTamaño) / 2; // Espaciado entre puntos

        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                Circle punto = new Circle(puntoTamaño, Color.LIGHTGRAY);

                // Cambiar color de filas específicas
                if (i == 0 || i == numFilas - 1) {
                    punto.setFill(Color.BLUE); // Primer y última fila
                } else if (i == 1 || i == numFilas - 2) {
                    punto.setFill(Color.RED); // Segunda y penúltima fila
                } else if (i == 12 || i == 13) {
                    punto.setFill(Color.GREEN); // Nueva fila 13 y 14
                }

                gridPane.add(punto, j, i);
                GridPane.setMargin(punto, new Insets(puntoEspacio));
            }
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void agregarObjeto(String tipo) {
        Button nuevoObjeto = new Button(tipo); // Botón para agregar objetos
        nuevoObjeto.setOnAction(e -> System.out.println("Objeto " + tipo + " agregado"));
        gridPane.add(nuevoObjeto, 0, 0); // Cambia la posición según sea necesario
    }
}
