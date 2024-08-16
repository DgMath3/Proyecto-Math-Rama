import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Componentes {

    private GridPane protoboard;

    public Componentes(GridPane protoboard) {
        this.protoboard = protoboard;
    }

    public void CComponente(ImageView imagen, int columna, int fila) {
        // Configura la imagen y añádela al protoboard
        GridPane.setColumnIndex(imagen, columna);
        GridPane.setRowIndex(imagen, fila);
        protoboard.getChildren().add(imagen);
    }
}
