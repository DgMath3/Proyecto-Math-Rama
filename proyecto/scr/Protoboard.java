import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.Node;
import javafx.scene.image.Image;

public class Protoboard {

    private GridPane gridPane;
    private final int numFilas = 14;
    private final int numColumnas = 30;
    private final double espacio = 15.0;
    private String[][] matriz;

    public Protoboard() {
        gridPane = new GridPane();
        configurarGridPane();
        crearProtoboard();
        matriz = new String[numFilas][numColumnas];
        inicializarMatrizEnergia(matriz);
    }

    public void cambiarColor(int fila, int columna, Color color, boolean bateriaEncendida) {
        Circle punto = (Circle) gridPane.getChildren().get(fila * numColumnas + columna);

        // Si la batería está apagada, solo aplicar energía neutra
        if (!bateriaEncendida) {
            punto.setFill(Color.LIGHTGRAY); // Visualmente representamos como neutro
            matriz[fila][columna] = "|"; // Energía neutra en la matriz
            return;
        }

        // Si la batería está encendida, aplica el color normalmente
        punto.setFill(color);

        // Actualizar la matriz dependiendo del color aplicado
        if (color.equals(Color.BLUE)) {
            matriz[fila][columna] = "+";
        } else if (color.equals(Color.RED)) {
            matriz[fila][columna] = "-";
        } else {
            matriz[fila][columna] = "|"; // Neutro si no es ni rojo ni azul
        }
    }

    private void configurarGridPane() {
        gridPane.setPrefSize(800, 600);
    
        // Establecer tamaño fijo para las columnas y filas
        double anchoColumna = 100; // Ancho fijo de cada columna
        double altoFila = 80; // Alto fijo de cada fila
        
        for (int i = 0; i < numColumnas; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(anchoColumna); // Ancho fijo
            gridPane.getColumnConstraints().add(col);
        }
    
        for (int i = 0; i < numFilas; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(altoFila); // Alto fijo
            gridPane.getRowConstraints().add(row);
        }
    
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
    
        // Establecer imagen de fondo
        Image imagenFondo = new Image("/resources/fondo1.png");
        BackgroundImage backgroundImage = new BackgroundImage(imagenFondo,
                null, null, null, new BackgroundSize(1000, 800, false, false, false, true));
        gridPane.setBackground(new Background(backgroundImage));
    }
    
    private void crearProtoboard() {
        double puntoTamaño = 10;
        double puntoEspacio = (espacio - puntoTamaño) / 2;

        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {

                Circle punto = new Circle(puntoTamaño, Color.LIGHTGRAY);

                gridPane.add(punto, j, i);
                GridPane.setMargin(punto, new Insets(puntoEspacio));
            }
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    // Método para actualizar la matriz según el color de los puntos del GridPane
    public void actualizarMatriz(GridPane gridPane, boolean bateriaEncendida) {
        if (matriz == null) {
            System.out.println("Error: la matriz no está inicializada.");
            return;
        }
        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                Node node = obtenerhoyito(gridPane, i, j);
                if (node != null && node instanceof Circle) {
                    Circle punto = (Circle) node;
                    Color color = (Color) punto.getFill();

                    if (bateriaEncendida) {
                        if (color.equals(Color.BLUE)) {
                            matriz[i][j] = "+"; // Energía positiva
                        } else if (color.equals(Color.RED)) {
                            matriz[i][j] = "-"; // Energía negativa
                        } else {
                            matriz[i][j] = "|"; // Energía neutra
                        }
                    }
                    // Si la batería está apagada, no modificar la matriz
                } else {
                    matriz[i][j] = " "; // Espacio en blanco si no hay nodo
                }
            }
        }
    }

    private Node obtenerhoyito(GridPane gridPane, int row, int column) {
        for (Node nodo : gridPane.getChildren()) {
            Integer nodofila = GridPane.getRowIndex(nodo);
            Integer nodocolumna = GridPane.getColumnIndex(nodo);
            if (nodofila != null && nodofila == row && nodocolumna != null && nodocolumna == column) {
                return nodo;
            }
        }
        return null;
    }

    public String[][] getMatriz() {
        return matriz;
    }

    private void inicializarMatrizEnergia(String[][] matrizEnergia) {
        for (int i = 0; i < matrizEnergia.length; i++) {
            for (int j = 0; j < matrizEnergia[i].length; j++) {
                if (matrizEnergia[i][j] == null) {
                    matrizEnergia[i][j] = "|"; // Valor por defecto si no hay energía.
                }
            }
        }
    }

    // Método para cambiar la energía de todo el protoboard según el estado de la
    // batería
    public void cambiarEnergiaDeTodoElProtoboard(boolean bateriaEncendida) {
        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                Node node = obtenerhoyito(gridPane, i, j);
                if (node != null && node instanceof Circle) {
                    Circle punto = (Circle) node;
                    if (!bateriaEncendida) {
                        punto.setFill(Color.LIGHTGRAY); // Color visual para energía neutra
                        // No modificar la matriz de energía
                    } else {
                        // Restaurar el color según el valor en la matriz
                        switch (matriz[i][j]) {
                            case "+":
                                punto.setFill(Color.BLUE);
                                break;
                            case "-":
                                punto.setFill(Color.RED);
                                break;
                            default:
                                punto.setFill(Color.LIGHTGRAY);
                                break;
                        }
                    }
                }
            }
        }
        System.out.println(
                "Energía de todo el protoboard actualizada a: " + (bateriaEncendida ? "Encendida" : "Apagada"));
    }

}