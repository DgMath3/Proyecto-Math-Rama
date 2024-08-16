import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

    private GridPane protoboard;
    private VistaPrevia vistaPrevia;
    private BarraMenu barraMenu;
    private final int numFilas = 10;
    private final int numColumnas = 30;
    private final double espacio = 15.0;

    @Override
    public void start(Stage escenarioPrincipal) {
        BorderPane root = new BorderPane();
        protoboard = new GridPane();
        vistaPrevia = new VistaPrevia(protoboard, numFilas, numColumnas);
        barraMenu = new BarraMenu(vistaPrevia);

        configurarGridPane(); // Configura las restricciones del GridPane
        crearProtoboard(); // Crea los puntos del protoboard

        root.setCenter(protoboard);
        root.setTop(barraMenu.getBarraMenu());

        Scene escena = new Scene(root, 800, 600);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.setTitle("Simulador de Protoboard");
        escenarioPrincipal.show();
    }

    private void configurarGridPane() {
        // Configura las columnas
        for (int i = 0; i < numColumnas; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / numColumnas); // Distribuir el ancho de manera uniforme
            protoboard.getColumnConstraints().add(col);
        }

        // Configura las filas
        for (int i = 0; i < numFilas; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / numFilas); // Distribuir la altura de manera uniforme
            protoboard.getRowConstraints().add(row);
        }

        // Ajusta el alineamiento y el margen
        protoboard.setAlignment(Pos.CENTER);
        protoboard.setPadding(new Insets(10));
    }

    private void crearProtoboard() {
        // Configura el tamaño de los puntos
        double puntoTamaño = 10;
        double puntoEspacio = (espacio - puntoTamaño) / 2;

        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                Circle punto = new Circle(puntoTamaño, Color.LIGHTGRAY);
                protoboard.add(punto, j, i);

                // Ajusta el margen para espaciar los puntos
                GridPane.setMargin(punto, new Insets(puntoEspacio));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
