import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        gridPane.setPrefSize(1000, 800);

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
    public String[][] getMatriz(){
        return matriz;
    }
    
    private void inicializarMatrizEnergia(String[][] matrizEnergia) {
        for (int i = 0; i < matrizEnergia.length; i++) {
            for (int j = 0; j < matrizEnergia[i].length; j++) {
                if (matrizEnergia[i][j] == null) {
                    matrizEnergia[i][j] = "|";  // Valor por defecto si no hay energía.
                }
            }
        }
    }


    // Método para cambiar la energía de todo el protoboard según el estado de la batería
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
        System.out.println("Energía de todo el protoboard actualizada a: " + (bateriaEncendida ? "Encendida" : "Apagada"));
    }
    
}